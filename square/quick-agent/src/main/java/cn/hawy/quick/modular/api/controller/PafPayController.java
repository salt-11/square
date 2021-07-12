package cn.hawy.quick.modular.api.controller;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

import cn.hawy.quick.config.properties.PafProperties;
import cn.hawy.quick.core.common.RestResponse;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.util.IdGenerator;
import cn.hawy.quick.core.util.MapUtils;
import cn.hawy.quick.core.util.RSA;
import cn.hawy.quick.modular.api.channel.PafChannel;
import cn.hawy.quick.modular.api.dto.paf.OrderNotifyDto;
import cn.hawy.quick.modular.api.dto.paf.QueryDto;
import cn.hawy.quick.modular.api.dto.paf.RechargeDto;
import cn.hawy.quick.modular.api.entity.TDeptRateChannel;
import cn.hawy.quick.modular.api.entity.TMchCard;
import cn.hawy.quick.modular.api.entity.TMchInfo;
import cn.hawy.quick.modular.api.entity.TPayOrder;
import cn.hawy.quick.modular.api.mq.MqMchCashNotify;
import cn.hawy.quick.modular.api.mq.MqPayNotify;
import cn.hawy.quick.modular.api.service.TDeptRateChannelService;
import cn.hawy.quick.modular.api.service.TFestivalHolidayService;
import cn.hawy.quick.modular.api.service.TMchCardChannelService;
import cn.hawy.quick.modular.api.service.TMchCardService;
import cn.hawy.quick.modular.api.service.TMchCashFlowService;
import cn.hawy.quick.modular.api.service.TMchInfoChannelService;
import cn.hawy.quick.modular.api.service.TMchInfoService;
import cn.hawy.quick.modular.api.service.TMerPoolService;
import cn.hawy.quick.modular.api.service.TPayOrderService;
import cn.hawy.quick.modular.api.validate.paf.PafPayValidate;
import cn.hawy.quick.modular.system.entity.Dept;
import cn.hawy.quick.modular.system.service.DeptService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;

@RestController
@RequestMapping("/api/paf/pay")
public class PafPayController {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	TMchInfoService mchInfoService;
	@Autowired
	TMchInfoChannelService mchInfoChannelService;
	@Autowired
	TMchCardService mchCardService;
	@Autowired
	TMchCardChannelService mchCardChannelService;
	@Autowired
	TPayOrderService payOrderService;
	@Autowired
	TMerPoolService merPoolServic;
	@Autowired
	MqPayNotify mqPayNotify;
	@Autowired
	TDeptRateChannelService deptRateChannelService;
	@Autowired
	DeptService deptService;
	@Autowired
	PafChannel pafChannel;
	@Autowired
	PafProperties pafProperties;
	@Autowired
	TMchCashFlowService mchCashFlowService;
	@Autowired
	MqMchCashNotify mqMchCashNotify;
	@Autowired
	TFestivalHolidayService festivalHolidayService;

	@RequestMapping(value = "/recharge",method = RequestMethod.POST)
	@ResponseBody
	public Object recharge(RechargeDto rechargeDto) {
		log.info("下游请求报文-recharge:request={}",JSON.toJSONString(rechargeDto));
		//参数校验
		PafPayValidate.recharge(rechargeDto);

		if(NumberUtil.compare(Double.parseDouble(rechargeDto.getOrderAmount()),Double.parseDouble(pafProperties.getMinOrderAmount())) < 0) {
			throw new RestException(401, "支付金额不能低于最低交易限额!");
		}
		//渠道商信息
		Dept dept = deptService.getById(rechargeDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(rechargeDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, rechargeDto.getSignature(), dept.getPartnerPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		boolean openFlag = festivalHolidayService.isOpenFlag();
		if(!openFlag) {
			throw new RestException(401, "通道关闭中!");
		}
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(rechargeDto.getPartnerId(),rechargeDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TMchCard mchCard = mchCardService.findBybankCardNo(rechargeDto.getMchId(), rechargeDto.getCardNo());
		if(mchCard == null) {
			throw new RestException(401, "商户卡信息错误");
		}
		TDeptRateChannel deptRateChannel = null;
		if(dept.getChannelType() == 1){
			deptRateChannel = deptRateChannelService.findByDeptIdAndChannel(rechargeDto.getPartnerId(), "paf");
		}else {
			deptRateChannel = deptRateChannelService.findByDeptIdAndBankNameAndChannel(rechargeDto.getPartnerId(), mchCard.getBankCode(), "paf");
		}
		if(deptRateChannel == null) {
			throw new RestException(401, "未找到通道信息");
		}
		TPayOrder payOrder = payOrderService.findByMchIdAndOutTradeNo(rechargeDto.getMchId(), rechargeDto.getOutTradeNo());
		if(payOrder != null) {
			throw new RestException(401, "订单号已存在!");
		}
		String mchRate = "";
		
		Long mchFee = 0L;
		if(StrUtil.isEmpty(rechargeDto.getMchFee())) {
			if(StrUtil.isEmpty(rechargeDto.getMchRate())) {
				throw new RestException(401, "MchFee和MchRate不能同时为空");
			}else {
				mchRate = rechargeDto.getMchRate();
			}
			mchFee = NumberUtil.round(NumberUtil.mul(rechargeDto.getOrderAmount(), mchRate),0,RoundingMode.UP).longValue();
		}else {
			mchRate = NumberUtil.div(rechargeDto.getMchFee(), rechargeDto.getOrderAmount(),4,RoundingMode.DOWN).toString();
			mchFee = NumberUtil.parseLong(rechargeDto.getMchFee());
		}
		if(NumberUtil.compare(Double.parseDouble(mchRate),Double.parseDouble(deptRateChannel.getCostRate())) < 0) {
			throw new RestException(401, "商户费率不能低于渠道商费率!");
		}
		Long orderId = IdGenerator.getIdLong();
		Long orderAmount = NumberUtil.parseLong(rechargeDto.getOrderAmount());
		//计算方式和后端通道保持一致
		//商户
		Long mchAmount = orderAmount - mchFee;
		//渠道
		Long deptFee = NumberUtil.round(NumberUtil.mul(rechargeDto.getOrderAmount(), deptRateChannel.getCostRate()),0,RoundingMode.UP).longValue();
		Long deptAmount = mchFee - deptFee;
		Long agentFee = 0L;
		Long agentAmount = 0L;
		Long costFee = 0L;
		Long costAmount = 0L;
		if(StrUtil.isEmpty(dept.getAgentId())) { //没有代理,则平台利润是渠道差
			//平台利润
			costFee = NumberUtil.round(NumberUtil.mul(rechargeDto.getOrderAmount(), deptRateChannel.getChannelCostRate()),0).longValue();
			costAmount = deptFee -  costFee;
		}else { //有代理，则平台利润是代理差
			if(StrUtil.isEmpty(deptRateChannel.getAgentRate())){
				throw new RestException(401, "代理商费率为空!");
			}
			//代理利润
			agentFee = NumberUtil.round(NumberUtil.mul(rechargeDto.getOrderAmount(), deptRateChannel.getAgentRate()),0).longValue();
			agentAmount = deptFee - agentFee;
			//平台利润
			costFee = NumberUtil.round(NumberUtil.mul(rechargeDto.getOrderAmount(), deptRateChannel.getChannelCostRate()),0).longValue();
			costAmount = agentFee -  costFee;
		}

		//通道相关
		payOrder = new TPayOrder();
		payOrder.setOrderId(orderId);
		payOrder.setMchId(rechargeDto.getMchId());
		payOrder.setMchName(mchInfo.getMchName());
		payOrder.setDeptId(rechargeDto.getPartnerId());
		payOrder.setChannel("paf");
		payOrder.setChannelNo(deptRateChannel.getChannelNo());
		payOrder.setBankCardNo(rechargeDto.getCardNo());
		payOrder.setOutTradeNo(rechargeDto.getOutTradeNo());
		payOrder.setOrderAmount(orderAmount);
		payOrder.setOutMchId("00000000");
		payOrder.setMerPoolNo("00000000");
		payOrder.setMchRate(mchRate);
		payOrder.setMchFee(mchFee);
		payOrder.setMchAmount(mchAmount);
		payOrder.setDeptRate(deptRateChannel.getCostRate());
		payOrder.setDeptAmount(deptAmount);
		payOrder.setAgentId(dept.getAgentId());
		payOrder.setAgentRate(deptRateChannel.getAgentRate());
		payOrder.setAgentAmount(agentAmount);
		payOrder.setCostRate(deptRateChannel.getChannelCostRate());
		payOrder.setCostAmount(costAmount);
		payOrder.setOrderStatus(1);
		payOrder.setReturnMsg("");
		payOrder.setSplitStatus(1);
		payOrder.setNotifyUrl(rechargeDto.getNotifyUrl());
		payOrder.setNotifyCount(0);
		payOrder.setOrderTime(LocalDateTime.now());
		payOrderService.save(payOrder);

		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", rechargeDto.getPartnerId());
		map.put("mchId", rechargeDto.getMchId());
		map.put("cardNo", rechargeDto.getCardNo());
		map.put("outTradeNo", rechargeDto.getOutTradeNo());
		Map<String,String> resMap = pafChannel.fastpayPrecreate(String.valueOf(orderId), rechargeDto.getOrderAmount(), rechargeDto.getCardNo(), mchCard.getMobile(), String.valueOf(mchFee), mchInfo.getCustomerName(), mchInfo.getCustomerIdentNo(), mchCard.getCvn(), mchCard.getExpired(), rechargeDto.getCity(), rechargeDto.getMcc());
		if("1".equals(resMap.get("orderStatus"))) {
			map.put("orderStatus", "1");
			//处理中不做处理
		}else if("2".equals(resMap.get("orderStatus"))) {
			boolean orderStatusFlag = payOrderService.updateOrderStatusSuccess(payOrder, dept,resMap.get("returnMsg"));
			if(orderStatusFlag) {
				map.put("orderStatus", "2");
				map.put("returnMsg", resMap.get("returnMsg"));
			}
		}else if("3".equals(resMap.get("orderStatus"))){
			boolean orderStatusFlag = payOrderService.updateOrderStatusFail(payOrder.getOrderId(),resMap.get("returnMsg"));
			if(orderStatusFlag) {
				map.put("orderStatus", "3");
				map.put("returnMsg", resMap.get("returnMsg"));
			}
		}else {
			throw new RestException(401, "交易状态异常，请联系运维人员!");
		}
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-recharge:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}

	@RequestMapping(value = "/query",method = RequestMethod.POST)
	@ResponseBody
	public Object query(QueryDto queryDto) {
		log.info("下游请求报文-query:request={}",JSON.toJSONString(queryDto));
		//参数校验
		PafPayValidate.query(queryDto);
		//渠道商信息
		Dept dept = deptService.getById(queryDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(queryDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, queryDto.getSignature(), dept.getPartnerPublickey());
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
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", queryDto.getPartnerId());
		map.put("mchId", queryDto.getMchId());
		map.put("outTradeNo", queryDto.getOutTradeNo());
		map.put("orderAmount", String.valueOf(payOrder.getOrderAmount()));
		map.put("orderStatus", String.valueOf(payOrder.getOrderStatus()));
		map.put("returnMsg", payOrder.getReturnMsg());
		if(payOrder.getOrderStatus() == 1) {
			Map<String,String> resMap = pafChannel.fastpayQuery(String.valueOf(payOrder.getOrderId()));
			if("1".equals(resMap.get("orderStatus"))) {
				//不做处理
			}else if("2".equals(resMap.get("orderStatus"))) {
				boolean orderStatusFlag = payOrderService.updateOrderStatusSuccess(payOrder, dept,resMap.get("returnMsg"));
				if(orderStatusFlag) {
					map.put("orderStatus", "2");
					map.put("returnMsg", resMap.get("returnMsg"));
				}
			}else if("3".equals(resMap.get("orderStatus"))) {
				boolean orderStatusFlag = payOrderService.updateOrderStatusFail(payOrder.getOrderId(),resMap.get("returnMsg"));
				if(orderStatusFlag) {
					map.put("orderStatus", "3");
					map.put("returnMsg", resMap.get("returnMsg"));
				}
			}else {
				throw new RestException(401, "订单查询失败!");
			}
		}
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-query:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}


	@RequestMapping(value = "/orderNotify",method = RequestMethod.POST)
	@ResponseBody
	public Object orderNotify(OrderNotifyDto orderNotifyDto) {
		log.info("PAF交易通知请求-orderNotify={}",JSON.toJSONString(orderNotifyDto));
		String md5Str = "bizOrderNumber="+orderNotifyDto.getBizOrderNumber()
						+"&completedTime="+orderNotifyDto.getCompletedTime()
						+"&mid="+orderNotifyDto.getMid()
						+"&srcAmt="+orderNotifyDto.getSrcAmt()
						+"&key="+pafProperties.getPublicKey();
		String checkSign = SecureUtil.md5(md5Str);
		if(!checkSign.equals(orderNotifyDto.getSign())) {
			log.info("PAF提现通知返回-orderNotify={}","验签失败返回fail");
			return "fail";
		}
		//获取订单信息
		TPayOrder payOrder = payOrderService.getById(orderNotifyDto.getBizOrderNumber());
		if(payOrder == null) {
			//什么都不做
			log.info("PAF交易通知返回-orderNotify={}","订单不存在返回fail");
			return "fail";
		}
		//渠道商信息
        Dept dept = deptService.getById(payOrder.getDeptId());
        if(dept == null) {
            log.info("PAF交易通知返回-orderNotify={}","渠道信息不存在返回fail");
            return "fail";
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
		map.put("returnMsg", payOrder.getReturnMsg());
		if(payOrder.getOrderStatus() == 1) {
			boolean flag = payOrderService.updateOrderStatusSuccess(payOrder, dept, "交易成功");
			if(flag) {
				map.put("orderStatus", "2");
				map.put("returnMsg", "交易成功");
			}else {
				log.info("PAF交易通知返回-orderNotify={}","状态修改失败返回fail");
                return "fail";
			}
		}
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-orderNotify={}",JSON.toJSONString(map));
		msgObj.put("notifyMsg", RestResponse.success(map));
		mqPayNotify.send(JSON.toJSONString(msgObj));
	    log.info("PAF交易通知返回-orderNotify={}","success");
	    return "success";
	}

}
