package cn.hawy.quick.modular.api.controller;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hawy.quick.modular.api.entity.*;
import cn.hawy.quick.modular.api.service.*;
import cn.hutool.core.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hawy.quick.config.properties.SumBtProperties;
import cn.hawy.quick.core.common.RestResponse;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.util.IdGenerator;
import cn.hawy.quick.core.util.MapUtils;
import cn.hawy.quick.core.util.RSA;
import cn.hawy.quick.modular.api.channel.SumBtChannel;
import cn.hawy.quick.modular.api.dto.sumbt.ChangeAgentCardDto;
import cn.hawy.quick.modular.api.dto.sumbt.OrderAppleyDto;
import cn.hawy.quick.modular.api.dto.sumbt.OrderNotifyDto;
import cn.hawy.quick.modular.api.dto.sumbt.QueryDto;
import cn.hawy.quick.modular.api.dto.sumbt.VerifyMessageDto;
import cn.hawy.quick.modular.api.mq.MqPayNotify;
import cn.hawy.quick.modular.api.validate.sumbt.SumBtPayValidate;
import cn.hawy.quick.modular.system.entity.Dept;
import cn.hawy.quick.modular.system.service.DeptService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

@RestController
@RequestMapping("/api/vs/bt/pay")
public class SumBtPayController {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private static final String Channel = "sumbt";
	private static final String PlatformCode = "tts";

	@Autowired
	SumBtProperties sumbtProperties;
	@Autowired
	TDeptInfoService deptInfoService;
	@Autowired
	TMchInfoService mchInfoService;
	@Autowired
	TMchInfoChannelService mchInfoChannelService;
	@Autowired
	TMchCardService mchCardService;
	@Autowired
	TMchCardChannelService mchCardChannelService;
	@Autowired
	TDeptRateChannelService deptRateChannelService;
	@Autowired
	TPayOrderService payOrderService;
	@Autowired
	TPayOrderBcService payOrderBcService;
	@Autowired
	TMchCashFlowService mchCashFlowService;
	@Autowired
	SumBtChannel sumbtChannel;
	@Autowired
	MqPayNotify mqPayNotify;
	@Autowired
	TMerPoolService merPoolServic;
	@Autowired
	TAgentInfoService agentInfoService;
	@Autowired
	TPlatformRateChannelService platformRateChannelService;
	@Autowired
	TAgentRateChannelService agentRateChannelService;

	@RequestMapping(value = "/orderAppley",method = RequestMethod.POST)
	@ResponseBody
	public Object orderAppley(OrderAppleyDto orderAppleyDto) {
		log.info("下游请求报文-orderAppley:request={}",JSON.toJSONString(orderAppleyDto));
		//参数校验
		SumBtPayValidate.orderAppley(orderAppleyDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(orderAppleyDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(orderAppleyDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, orderAppleyDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		if(NumberUtil.compare(Double.parseDouble(orderAppleyDto.getOrderAmount()),Double.parseDouble(sumbtProperties.getMinOrderAmount())) < 0) {
			throw new RestException(401, "支付金额不能低于最低交易限额!");
		}
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(orderAppleyDto.getPartnerId(),orderAppleyDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TMchInfoChannel mchInfoChannel = mchInfoChannelService.findByMchIdAndChannel(orderAppleyDto.getMchId(), Channel);
		if(mchInfoChannel == null) {
			throw new RestException(401, "商户信息渠道错误!");
		}
		TMchCard mchCard = mchCardService.findBybankCardNo(orderAppleyDto.getMchId(), orderAppleyDto.getCardNo());
		if(mchCard == null) {
			throw new RestException(401, "商户卡信息错误");
		}
		TPlatformRateChannel platformRateChannel = platformRateChannelService.findByBankCodeAndChannel(mchCard.getBankCode(),Channel);
		if(platformRateChannel == null) {
			throw new RestException(401, "未找到平台信息");
		}
		TDeptRateChannel deptRateChannel = deptRateChannelService.findByDeptIdAndBankCodeAndChannel(orderAppleyDto.getPartnerId(), mchCard.getBankCode(), Channel);
		if(deptRateChannel == null) {
			throw new RestException(401, "未找到渠道费率信息");
		}
		if(NumberUtil.compare(Double.parseDouble(orderAppleyDto.getFeeAmount()),Double.parseDouble(deptRateChannel.getCashRate())) < 0) {
			throw new RestException(401, "提现手续费不能低于渠道成本手续费!");
		}
		//商户费率不能低于渠道商费率
		if(NumberUtil.compare(Double.parseDouble(orderAppleyDto.getMchRate()),Double.parseDouble(deptRateChannel.getCostRate())) < 0) {
			throw new RestException(401, "商户费率不能低于渠道商费率!");
		}
		TPayOrder payOrder = payOrderService.findByMchIdAndOutTradeNo(orderAppleyDto.getMchId(), orderAppleyDto.getOutTradeNo());
		if(payOrder != null) {
			throw new RestException(401, "订单号已存在!");
		}

		String bandCardId  = null;
		String channelMerNo = "";
		String userIpAddr = "";
		String longitude = "";
		String latitude = "";
		if(platformRateChannel.getLuod() != 0){
				List<TMerPool> merPoolList = null;
				if(platformRateChannel.getLuod() == 1) {
					merPoolList = merPoolServic.findByPC(orderAppleyDto.getProvinceCode(), orderAppleyDto.getCityCode(),1);
				}else if(platformRateChannel.getLuod() == 2){
					merPoolList = merPoolServic.findByPC(orderAppleyDto.getProvinceCode(), orderAppleyDto.getCityCode(),2);
				}
				if(merPoolList.size() == 0) {
					throw new RestException(401, "订单上送的地区码交易不支持!");
				}else {
					TMerPool merPool = merPoolList.get(0); //正常随机，不计算权重
					channelMerNo = merPool.getMerNo();
					userIpAddr = merPool.getIp();
					longitude = merPool.getLongitude();
					latitude = merPool.getLatitude();
				}
		}


		String mchRate = "";
		if(StrUtil.isEmpty(orderAppleyDto.getMchRate())) {
			mchRate = mchInfoChannel.getMchRate();
		}else {
			mchRate = orderAppleyDto.getMchRate();
		}
		Long orderId = IdGenerator.getIdLong();
		Long orderAmount = NumberUtil.parseLong(orderAppleyDto.getOrderAmount());
		Long cashFee = NumberUtil.parseLong(orderAppleyDto.getFeeAmount());
		//计算方式和后端通道保持一致
		//商户
		Long mchFee = NumberUtil.round(NumberUtil.mul(orderAppleyDto.getOrderAmount(), mchRate),0).longValue();
		Long mchAmount = orderAmount-mchFee;
		//渠道
		Long deptFee = NumberUtil.round(NumberUtil.mul(orderAppleyDto.getOrderAmount(), deptRateChannel.getCostRate()),0).longValue();
		Long deptAmount = mchFee - deptFee;
		//代理
		String agentRate = "";
		Long agentFee = 0L;
		Long agentAmount = 0L;
		//平台
		Long costFee = 0L;
		Long costAmount = 0L;
		if(StrUtil.isEmpty(dept.getAgentId())){ //没有代理,则平台利润是渠道差
			//平台利润
			costFee = NumberUtil.round(NumberUtil.mul(orderAppleyDto.getOrderAmount(), platformRateChannel.getCostRate()),0).longValue();
			costAmount = deptFee -  costFee;
		}else{ //有代理，则平台利润是代理差
			TAgentInfo agent = agentInfoService.getById(dept.getAgentId());
			if(agent == null) {
				throw new RestException(401, "代理商信息错误!");
			}
			TAgentRateChannel agentRateChannel = agentRateChannelService.findByAgentIdAndChannel(dept.getAgentId(), Channel);
			if(agentRateChannel == null) {
				throw new RestException(401, "未找到代理商信息");
			}
			if(StrUtil.isEmpty(agentRateChannel.getCostRate())){
				throw new RestException(401, "代理商费率为空!");
			}
			agentRate = agentRateChannel.getCostRate();
			//代理利润
			agentFee = NumberUtil.round(NumberUtil.mul(orderAppleyDto.getOrderAmount(), agentRate),0).longValue();
			agentAmount = deptFee - agentFee;
			//平台利润
			costFee = NumberUtil.round(NumberUtil.mul(orderAppleyDto.getOrderAmount(), platformRateChannel.getCostRate()),0).longValue();
			costAmount = agentFee -  costFee;
		}

		//通道相关
		payOrder = new TPayOrder();
		payOrder.setOrderId(orderId);
		payOrder.setMchId(orderAppleyDto.getMchId());
		payOrder.setMchName(mchInfo.getMchName());
		payOrder.setDeptId(orderAppleyDto.getPartnerId());
		payOrder.setChannel(Channel);
		payOrder.setChannelNo(platformRateChannel.getChannelNo());
		payOrder.setBankCardNo(orderAppleyDto.getCardNo());
		payOrder.setOutTradeNo(orderAppleyDto.getOutTradeNo());
		payOrder.setOrderAmount(orderAmount);
		payOrder.setOutMchId(mchInfoChannel.getOutMchId());
		payOrder.setMchRate(mchRate);
		payOrder.setMchFee(mchFee);
		payOrder.setMchAmount(mchAmount);
		payOrder.setDeptRate(deptRateChannel.getCostRate());
		payOrder.setDeptAmount(deptAmount);
		payOrder.setAgentId(dept.getAgentId());
		payOrder.setAgentRate(agentRate);
		payOrder.setAgentAmount(agentAmount);
		payOrder.setCostRate(platformRateChannel.getCostRate());
		payOrder.setCostAmount(costAmount);
		payOrder.setOrderStatus(1);
		payOrder.setSplitStatus(1);
		payOrder.setNotifyUrl(orderAppleyDto.getNotifyUrl());
		payOrder.setNotifyCount(0);
		payOrder.setOrderTime(LocalDateTime.now());

		Long cashDeptAmount = cashFee - NumberUtil.parseLong(deptRateChannel.getCashRate());
		Long cashCostAmount = NumberUtil.parseLong(deptRateChannel.getCashRate()) - NumberUtil.parseLong(platformRateChannel.getCashRate());
		TMchCashFlow mchCashFlow = new TMchCashFlow();
		mchCashFlow.setCashId(orderId);
		mchCashFlow.setMchId(orderAppleyDto.getMchId());
		mchCashFlow.setMchName(mchInfo.getMchName());
		mchCashFlow.setDeptId(orderAppleyDto.getPartnerId());
		mchCashFlow.setBankCardNo(orderAppleyDto.getAgentCardNo());
		mchCashFlow.setOutTradeNo(orderAppleyDto.getOutTradeNo());
		mchCashFlow.setOutMchId(mchInfoChannel.getOutMchId());
		mchCashFlow.setCashAmount(mchAmount);
		mchCashFlow.setCashFee(cashFee);
		mchCashFlow.setOutAmount(mchAmount-cashFee);
		mchCashFlow.setCashStatus(1);
		mchCashFlow.setReturnMsg("");
		mchCashFlow.setCashRate(deptRateChannel.getCashRate());
		mchCashFlow.setDeptAmount(cashDeptAmount);
		mchCashFlow.setCostFee(platformRateChannel.getCashRate());
		mchCashFlow.setCostAmount(cashCostAmount);
		mchCashFlow.setNotifyUrl("www.xxx.com");
		mchCashFlow.setNotifyCount(0);
		mchCashFlow.setCreateTime(LocalDateTime.now());
		payOrderService.orderAppley(payOrder,mchCashFlow);

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", orderAppleyDto.getPartnerId());
		map.put("mchId", orderAppleyDto.getMchId());
		map.put("cardNo", orderAppleyDto.getCardNo());
		map.put("outTradeNo", orderAppleyDto.getOutTradeNo());
		Map<String,String> resMap = sumbtChannel.orderAppley(orderAppleyDto, String.valueOf(orderId), mchCard, mchInfo,mchRate,platformRateChannel.getChannelNo(),bandCardId,channelMerNo,userIpAddr,longitude,latitude);
		if("1".equals(resMap.get("orderStatus"))) {
			map.put("orderStatus", "1");
		}else if("2".equals(resMap.get("orderStatus"))) {
			boolean orderStatusFlag = payOrderService.updateOrderStatusSuccessOfSumBt(payOrder,mchCashFlow);
			map.put("orderStatus", orderStatusFlag?"2":"1");
		}else if("3".equals(resMap.get("orderStatus"))) {
			boolean orderStatusFlag = payOrderService.updateOrderStatusFail(payOrder.getOrderId(),resMap.get("returnMsg"));
			map.put("orderStatus", orderStatusFlag?"3":"1");
		}else if("4".equals(resMap.get("orderStatus"))) {
			boolean orderStatusFlag = payOrderService.updateOrderStatusAndReturnMsg(payOrder.getOrderId(), 4, resMap.get("returnMsg"));
			map.put("orderStatus", orderStatusFlag?"4":"1");
		}else if("5".equals(resMap.get("orderStatus"))) {
			boolean orderStatusFlag = payOrderService.updateOrderStatusOther(payOrder.getOrderId(), 5);
			map.put("orderStatus", orderStatusFlag?"5":"1");
		}else {
			throw new RestException(401, "订单查询失败!");
		}
		map.put("returnMsg", resMap.get("returnMsg"));
		map.put("resultType", resMap.get("resultType"));
		//组装返回报文
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-recharge:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}

	@RequestMapping(value = "/verifyMessage",method = RequestMethod.POST)
	@ResponseBody
	public Object verifyMessage(VerifyMessageDto verifyMessageDto) {
		log.info("下游请求报文-verifyMessage:request={}",JSON.toJSONString(verifyMessageDto));
		//参数校验
		SumBtPayValidate.verifyMessage(verifyMessageDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(verifyMessageDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(verifyMessageDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, verifyMessageDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(verifyMessageDto.getPartnerId(),verifyMessageDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TPayOrder payOrder = payOrderService.findByMchIdAndOutTradeNo(verifyMessageDto.getMchId(), verifyMessageDto.getOutTradeNo());
		if(payOrder == null) {
			throw new RestException(401, "订单号不存在!");
		}
		TMchCashFlow mchCashFlow = mchCashFlowService.getById(payOrder.getOrderId());
		if(mchCashFlow == null) {
			throw new RestException(401, "提现单号不存在!");
		}
		TMchCard mchCard = mchCardService.findBybankCardNo(verifyMessageDto.getMchId(), payOrder.getBankCardNo());
		if(mchCard == null) {
			throw new RestException(401, "商户卡信息错误");
		}
		TPlatformRateChannel platformRateChannel = platformRateChannelService.findByBankCodeAndChannel(PlatformCode,Channel);
		if(platformRateChannel == null) {
			throw new RestException(401, "未找到平台信息");
		}

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", verifyMessageDto.getPartnerId());
		map.put("mchId", verifyMessageDto.getMchId());
		map.put("outTradeNo", verifyMessageDto.getOutTradeNo());
		map.put("orderStatus", String.valueOf(payOrder.getOrderStatus()));
		map.put("returnMsg", payOrder.getReturnMsg());
		if(payOrder.getOrderStatus() == 1) {
			Map<String,String> resMap = sumbtChannel.verifyMessage(String.valueOf(payOrder.getOrderId()), verifyMessageDto.getVerifyCode(),platformRateChannel.getChannelNo());
			if("1".equals(resMap.get("orderStatus"))) {
				map.put("orderStatus", "1");
			}else if("2".equals(resMap.get("orderStatus"))) {
				boolean orderStatusFlag = payOrderService.updateOrderStatusSuccessOfSumBt(payOrder,mchCashFlow);
				map.put("orderStatus", orderStatusFlag?"2":"1");
			}else if("3".equals(resMap.get("orderStatus"))) {
				boolean orderStatusFlag = payOrderService.updateOrderStatusFail(payOrder.getOrderId(),resMap.get("returnMsg"));
				map.put("orderStatus", orderStatusFlag?"3":"1");
			}else if("4".equals(resMap.get("orderStatus"))) {
				boolean orderStatusFlag = payOrderService.updateOrderStatusAndReturnMsg(payOrder.getOrderId(), 4, resMap.get("returnMsg"));
				map.put("orderStatus", orderStatusFlag?"4":"1");
			}else if("5".equals(resMap.get("orderStatus"))) {
				boolean orderStatusFlag = payOrderService.updateOrderStatusOther(payOrder.getOrderId(), 5);
				map.put("orderStatus", orderStatusFlag?"5":"1");
			}else {
				throw new RestException(401, "订单查询失败!");
			}
			map.put("returnMsg", resMap.get("returnMsg"));
		}
		//组装返回报文
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-verifyMessage:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}

	@RequestMapping(value = "/changeAgentCard",method = RequestMethod.POST)
	@ResponseBody
	public Object changeAgentCard(ChangeAgentCardDto changeAgentCardDto) {
		log.info("下游请求报文-verifyMessage:request={}",JSON.toJSONString(changeAgentCardDto));
		//参数校验
		SumBtPayValidate.changeAgentCard(changeAgentCardDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(changeAgentCardDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(changeAgentCardDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, changeAgentCardDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(changeAgentCardDto.getPartnerId(),changeAgentCardDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TPayOrder payOrder = payOrderService.findByMchIdAndOutTradeNo(changeAgentCardDto.getMchId(), changeAgentCardDto.getOutTradeNo());
		if(payOrder == null) {
			throw new RestException(401, "订单号不存在!");
		}
		if(payOrder.getOrderStatus() != 4) {
			throw new RestException(401, "订单状态错误!");
		}
		TMchCashFlow mchCashFlow = mchCashFlowService.getById(payOrder.getOrderId());
		if(mchCashFlow == null) {
			throw new RestException(401, "提现单号不存在!");
		}
		TMchCard mchCard = mchCardService.findBybankCardNo(changeAgentCardDto.getMchId(), payOrder.getBankCardNo());
		if(mchCard == null) {
			throw new RestException(401, "商户卡信息错误");
		}
		TPlatformRateChannel platformRateChannel = platformRateChannelService.findByBankCodeAndChannel(PlatformCode,Channel);
		if(platformRateChannel == null) {
			throw new RestException(401, "未找到平台信息");
		}

		mchCashFlow.setBankCardNo(changeAgentCardDto.getCardNo());
		//mchCashFlowService.updateById(mchCashFlow);
		Map<String,Object> map = new HashMap<String,Object>();
		payOrderService.changeAgentCard(payOrder,mchCashFlow);
		map.put("orderStatus", "1");
		map.put("returnMsg", "");
		Map<String,String> resMap = sumbtChannel.changeAgentCard(String.valueOf(payOrder.getOrderId()), changeAgentCardDto.getBankCode(), changeAgentCardDto.getCardNo(),platformRateChannel.getChannelNo());
		if("1".equals(resMap.get("orderStatus"))) {
			map.put("orderStatus", "1");
		}else if("2".equals(resMap.get("orderStatus"))) {
			boolean orderStatusFlag = payOrderService.updateOrderStatusSuccessOfSumBt(payOrder,mchCashFlow);
			map.put("orderStatus", orderStatusFlag?"2":"1");
		}else if("3".equals(resMap.get("orderStatus"))) {
			boolean orderStatusFlag = payOrderService.updateOrderStatusFailOfSumBt(payOrder.getOrderId(),resMap.get("returnMsg"));
			map.put("orderStatus", orderStatusFlag?"3":"1");
		}else if("4".equals(resMap.get("orderStatus"))) {
			boolean orderStatusFlag = payOrderService.updateOrderStatusAndReturnMsgOfSumBt(payOrder.getOrderId(), 4, resMap.get("returnMsg"));
			map.put("orderStatus", orderStatusFlag?"4":"1");
		}else if("5".equals(resMap.get("orderStatus"))) {
			boolean orderStatusFlag = payOrderService.updateOrderStatusOther(payOrder.getOrderId(), 5);
			map.put("orderStatus", orderStatusFlag?"5":"1");
		}
		//组装返回报文
		map.put("partnerId", changeAgentCardDto.getPartnerId());
		map.put("mchId", changeAgentCardDto.getMchId());
		map.put("outTradeNo", changeAgentCardDto.getOutTradeNo());
		map.put("orderAmount", String.valueOf(payOrder.getOrderAmount()));
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-changeAgentCard:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}


	@RequestMapping(value = "/query",method = RequestMethod.POST)
	@ResponseBody
	public Object query(QueryDto queryDto) {
		log.info("下游请求报文-query:request={}",JSON.toJSONString(queryDto));
		//参数校验
		SumBtPayValidate.query(queryDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(queryDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(queryDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, queryDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(queryDto.getPartnerId(),queryDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TPayOrder payOrder = payOrderService.findByMchIdAndOutTradeNo(queryDto.getMchId(), queryDto.getOutTradeNo());
		if(payOrder == null) {
			throw new RestException(401, "订单号不存在!");
		}
		TMchCashFlow mchCashFlow = mchCashFlowService.getById(payOrder.getOrderId());
		if(mchCashFlow == null) {
			throw new RestException(401, "提现单号不存在!");
		}
		TMchCard mchCard = mchCardService.findBybankCardNo(queryDto.getMchId(), payOrder.getBankCardNo());
		if(mchCard == null) {
			throw new RestException(401, "商户卡信息错误");
		}
		TPlatformRateChannel platformRateChannel = platformRateChannelService.findByBankCodeAndChannel(PlatformCode,Channel);
		if(platformRateChannel == null) {
			throw new RestException(401, "未找到平台信息");
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("orderStatus", String.valueOf(payOrder.getOrderStatus()));
		map.put("returnMsg", payOrder.getReturnMsg());
		if(payOrder.getOrderStatus() == 1 || payOrder.getOrderStatus() == 5) {
			Map<String,String> resMap = sumbtChannel.query(String.valueOf(payOrder.getOrderId()),platformRateChannel.getChannelNo());
			if("1".equals(resMap.get("orderStatus"))) {
				map.put("orderStatus", "1");
			}else if("2".equals(resMap.get("orderStatus"))) {
				boolean orderStatusFlag = payOrderService.updateOrderStatusSuccessOfSumBt(payOrder,mchCashFlow);
				map.put("orderStatus", orderStatusFlag?"2":"1");
			}else if("3".equals(resMap.get("orderStatus"))) {
				boolean orderStatusFlag = payOrderService.updateOrderStatusFailOfSumBt(payOrder.getOrderId(),resMap.get("returnMsg"));
				map.put("orderStatus", orderStatusFlag?"3":"1");
			}else if("4".equals(resMap.get("orderStatus"))) {
				boolean orderStatusFlag = payOrderService.updateOrderStatusAndReturnMsgOfSumBt(payOrder.getOrderId(), 4, resMap.get("returnMsg"));
				map.put("orderStatus", orderStatusFlag?"4":"1");
			}else if("5".equals(resMap.get("orderStatus"))) {
				if(payOrder.getOrderStatus() == 1) {
					boolean orderStatusFlag = payOrderService.updateOrderStatusOther(payOrder.getOrderId(), 5);
					map.put("orderStatus", orderStatusFlag?"5":"1");
				}else {
					map.put("orderStatus", "5");
				}
			}else {
				throw new RestException(401, "订单查询失败!");
			}
			map.put("returnMsg", resMap.get("returnMsg"));
		}
		map.put("partnerId", queryDto.getPartnerId());
		map.put("mchId", queryDto.getMchId());
		map.put("outTradeNo", queryDto.getOutTradeNo());
		map.put("orderAmount", String.valueOf(payOrder.getOrderAmount()));
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-query:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}


	@RequestMapping(value = "/orderNotify",method = RequestMethod.POST)
	@ResponseBody
	public Object orderNotify(@RequestBody OrderNotifyDto orderNotifyDto) {
		log.info("SUMBT订单通知-orderNotify:request={}",JSON.toJSONString(orderNotifyDto));
		Map<String,String> restMap = new HashMap<String,String>();
		//获取订单信息
		TPayOrder payOrder = payOrderService.getById(orderNotifyDto.getOrder_no());
		if(payOrder == null) {
			//什么都不做
			restMap.put("resp_code", "0001");
			restMap.put("resp_msg", "查无订单");
			return restMap;
		}
		TMchCashFlow mchCashFlow = mchCashFlowService.getById(payOrder.getOrderId());
		if(mchCashFlow == null) {
			//什么都不做
			restMap.put("resp_code", "0001");
			restMap.put("resp_msg", "查无订单2");
			return restMap;
		}
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(payOrder.getDeptId());
		if(dept == null) {
			restMap.put("resp_code", "0001");
			restMap.put("resp_msg", "订单渠道异常");
			return restMap;
		}
		JSONObject msgObj = new JSONObject();
		msgObj.put("notifyUrl", payOrder.getNotifyUrl());
		msgObj.put("notifyCount", payOrder.getNotifyCount());
		msgObj.put("orderId", payOrder.getOrderId());
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", payOrder.getDeptId());
		map.put("mchId", payOrder.getMchId());
		map.put("outTradeNo", payOrder.getOutTradeNo());
		map.put("orderAmount", String.valueOf(payOrder.getOrderAmount()));
		map.put("orderStatus", String.valueOf(payOrder.getOrderStatus()));
		if(payOrder.getOrderStatus() == 1) {
			if(orderNotifyDto.getStatus().equals("1")) {
				boolean flag = payOrderService.updateOrderStatusSuccessOfSumBt(payOrder, mchCashFlow);
				if(flag) {
					map.put("orderStatus", "2");
				}else {
					restMap.put("resp_code", "0001");
					restMap.put("resp_msg", "订单修改失败");
					return restMap;
				}
			}
		}
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-payNotify:response={}",JSON.toJSONString(map));
		msgObj.put("notifyMsg", RestResponse.success(map));
		mqPayNotify.send(JSON.toJSONString(msgObj));
		restMap.put("resp_code", "000000");
		restMap.put("resp_msg", "成功");
		log.info("SUMBT订单通知返回-orderNotify:response={}",JSON.toJSONString(restMap));
		return restMap;
	}


	@RequestMapping(value = "/test",method = RequestMethod.POST)
	@ResponseBody
	public Object test(@RequestBody String message) {
		System.out.println(message);
		return "success";
	}
}
