package cn.hawy.quick.modular.api.controller;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hawy.quick.modular.api.entity.*;
import cn.hawy.quick.modular.api.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hawy.quick.config.properties.SumProperties;
import cn.hawy.quick.core.common.RestResponse;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.util.IdGenerator;
import cn.hawy.quick.core.util.MapUtils;
import cn.hawy.quick.core.util.RSA;
import cn.hawy.quick.modular.api.channel.SumChannel;
import cn.hawy.quick.modular.api.dto.sum.OrderNotifyDto;
import cn.hawy.quick.modular.api.dto.sum.QueryDto;
import cn.hawy.quick.modular.api.dto.sum.RechargeDto;
import cn.hawy.quick.modular.api.mq.MqMchCashNotify;
import cn.hawy.quick.modular.api.mq.MqPayNotify;
import cn.hawy.quick.modular.api.validate.sum.SumPayValidate;
import cn.hawy.quick.modular.system.entity.Dept;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

@RestController
@RequestMapping("/api/vs/pay")
public class SumPayController {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private static final String Channel = "sum";

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
	TAgentRateChannelService agentRateChannelService;
	@Autowired
	TPlatformRateChannelService platformRateChannelService;
	@Autowired
	TDeptInfoService deptInfoService;
	@Autowired
	TAgentInfoService agentInfoService;
	@Autowired
	SumChannel sumChannel;
	@Autowired
	SumProperties sumProperties;
	@Autowired
	TMchCashFlowService mchCashFlowService;
	@Autowired
	MqMchCashNotify mqMchCashNotify;

	@RequestMapping(value = "/recharge",method = RequestMethod.POST)
	@ResponseBody
	public Object recharge(RechargeDto rechargeDto) {
		log.info("下游请求报文-recharge:request={}",JSON.toJSONString(rechargeDto));
		//参数校验
		SumPayValidate.recharge(rechargeDto);

		if(NumberUtil.compare(Double.parseDouble(rechargeDto.getOrderAmount()),Double.parseDouble(sumProperties.getMinOrderAmount())) < 0) {
			throw new RestException(401, "支付金额不能低于最低交易限额!");
		}
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(rechargeDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(rechargeDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, rechargeDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(rechargeDto.getPartnerId(),rechargeDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TMchInfoChannel mchInfoChannel = mchInfoChannelService.findByMchIdAndChannel(rechargeDto.getMchId(), Channel);
		if(mchInfoChannel == null) {
			throw new RestException(401, "商户信息渠道错误!");
		}
		TMchCard mchCard = mchCardService.findBybankCardNo(rechargeDto.getMchId(), rechargeDto.getCardNo());
		if(mchCard == null) {
			throw new RestException(401, "商户卡信息错误");
		}
		TMchCardChannel mchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),2,Channel);
		if(mchCardChannel == null) {
			throw new RestException(401, "商户卡信息渠道错误");
		}
		TPlatformRateChannel platformRateChannel = platformRateChannelService.findByChannel(Channel);
		if(platformRateChannel == null) {
			throw new RestException(401, "未找到平台信息");
		}
		TDeptRateChannel deptRateChannel = deptRateChannelService.findByDeptIdAndChannel(rechargeDto.getPartnerId(), Channel);
		if(deptRateChannel == null) {
			throw new RestException(401, "未找到渠道信息");
		}
		TPayOrder payOrder = payOrderService.findByMchIdAndOutTradeNo(rechargeDto.getMchId(), rechargeDto.getOutTradeNo());
		if(payOrder != null) {
			throw new RestException(401, "订单号已存在!");
		}

		String channelMerNo = "";
		String userIpAddr = "";
		String longitude = "";
		String latitude = "";
		Integer orderStatus = 1;
		String returnMsg = "";
		Long orderAmount = NumberUtil.parseLong(rechargeDto.getOrderAmount());
		//交易限额
		if(dept.getDayMaxAmount() != 0) {
			Long currentAmount = payOrderService.getCurrentAmount(rechargeDto.getPartnerId());
			if(currentAmount + orderAmount > dept.getDayMaxAmount()){
				orderStatus = 3;
				returnMsg = "超过每日限定交易额度!";
			}
		}
		if(platformRateChannel.getLuod() !=0){
			List<TMerPool> merPoolList = null;
			if(platformRateChannel.getLuod() == 1) {
				merPoolList = merPoolServic.findByPC(rechargeDto.getProvinceCode(), rechargeDto.getCityCode(),1);
			}else if(platformRateChannel.getLuod() == 2){
				merPoolList = merPoolServic.findByPC(rechargeDto.getProvinceCode(), rechargeDto.getCityCode(),2);
			}
			if(merPoolList.size() == 0) {
				orderStatus = orderStatus==1?3:orderStatus;
				returnMsg = returnMsg==""?"订单上送的地区码交易不支持":returnMsg;
			}else {
				TMerPool merPool =  merPoolList.get(0); //正常随机，不计算权重
				channelMerNo = merPool.getMerNo();
				userIpAddr = merPool.getIp();
				longitude = merPool.getLongitude();
				latitude = merPool.getLatitude();
			}
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

		//计算方式和后端通道保持一致
		//商户
		Long mchAmount = orderAmount - mchFee;
		//渠道
		Long deptFee = NumberUtil.round(NumberUtil.mul(rechargeDto.getOrderAmount(), deptRateChannel.getCostRate()),0,RoundingMode.UP).longValue();
		Long deptAmount = mchFee - deptFee;
		String agentRate = "";
		Long agentFee = 0L;
		Long agentAmount = 0L;
		Long costFee = 0L;
		Long costAmount = 0L;
		if(StrUtil.isEmpty(dept.getAgentId())) { //没有代理,则平台利润是渠道差
			//平台利润
			costFee = NumberUtil.round(NumberUtil.mul(rechargeDto.getOrderAmount(), platformRateChannel.getCostRate()),0).longValue();
			costAmount = deptFee -  costFee;
		}else { //有代理，则平台利润是代理差
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
			agentFee = NumberUtil.round(NumberUtil.mul(rechargeDto.getOrderAmount(), agentRate),0).longValue();
			agentAmount = deptFee - agentFee;
			//平台利润
			costFee = NumberUtil.round(NumberUtil.mul(rechargeDto.getOrderAmount(), platformRateChannel.getCostRate()),0).longValue();
			costAmount = agentFee -  costFee;
		}

		//通道相关
		Long shareAmount = mchFee - costFee;
		payOrder = new TPayOrder();
		payOrder.setOrderId(orderId);
		payOrder.setMchId(rechargeDto.getMchId());
		payOrder.setMchName(mchInfo.getMchName());
		payOrder.setDeptId(rechargeDto.getPartnerId());
		payOrder.setChannel("sum");
		payOrder.setChannelNo(platformRateChannel.getChannelNo());
		payOrder.setBankCardNo(rechargeDto.getCardNo());
		payOrder.setOutTradeNo(rechargeDto.getOutTradeNo());
		payOrder.setOrderAmount(orderAmount);
		payOrder.setOutMchId(mchInfoChannel.getOutMchId());
		payOrder.setMerPoolNo(channelMerNo);
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
		payOrder.setOrderStatus(orderStatus);
		payOrder.setReturnMsg(returnMsg);
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
		map.put("orderStatus", String.valueOf(payOrder.getOrderStatus()));
		map.put("returnMsg", payOrder.getReturnMsg());
		if(payOrder.getOrderStatus() == 1) {
			Map<String,String> resMap = sumChannel.recharge(rechargeDto, orderId.toString(), mchInfoChannel.getOutMchId(), mchCardChannel.getProtocol(),channelMerNo,shareAmount.toString(),platformRateChannel.getChannelNo(),platformRateChannel.getChannelMerAppId(),userIpAddr,longitude,latitude);
			if("1".equals(resMap.get("orderStatus"))) {
				//处理中不做处理
			}else if("2".equals(resMap.get("orderStatus"))) {
				boolean orderStatusFlag = payOrderService.updateOrderStatusSuccess(payOrder,resMap.get("returnMsg"));
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
		SumPayValidate.query(queryDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(queryDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		TPlatformRateChannel platformRateChannel = platformRateChannelService.findByChannel(Channel);
		if(platformRateChannel == null) {
			throw new RestException(401, "未找到平台信息");
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
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", queryDto.getPartnerId());
		map.put("mchId", queryDto.getMchId());
		map.put("outTradeNo", queryDto.getOutTradeNo());
		map.put("orderAmount", String.valueOf(payOrder.getOrderAmount()));
		map.put("orderStatus", String.valueOf(payOrder.getOrderStatus()));
		map.put("returnMsg", payOrder.getReturnMsg());
		if(payOrder.getOrderStatus() == 1) {
			Map<String,String> resMap = sumChannel.queryOrderStatus(String.valueOf(payOrder.getOrderId()),platformRateChannel.getChannelNo(),platformRateChannel.getChannelMerAppId());
			if("1".equals(resMap.get("orderStatus"))) {
				//不做处理
			}else if("2".equals(resMap.get("orderStatus"))) {
				boolean orderStatusFlag = payOrderService.updateOrderStatusSuccess(payOrder,resMap.get("returnMsg"));
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
	public Object orderNotify(@RequestBody OrderNotifyDto orderNotifyDto) {
		log.info("SUM订单通知-orderNotify:request={}",JSON.toJSONString(orderNotifyDto));
		Map<String,String> restMap = new HashMap<String,String>();
		if(orderNotifyDto.getOrder_type().equals("0")) {
			restMap = payNotify(orderNotifyDto.getOrder_no(),orderNotifyDto.getStatus(),orderNotifyDto.getError_msg());
		}else if(orderNotifyDto.getOrder_type().equals("1")) {
			restMap = cashNotify(orderNotifyDto.getOrder_no(),orderNotifyDto.getStatus(),orderNotifyDto.getError_msg());
		}else {
			restMap.put("resp_code", "0001");
			restMap.put("resp_msg", "订单类型错误");
		}
		log.info("SUM订单通知返回-orderNotify:response={}",JSON.toJSONString(restMap));
		return restMap;
	}


	/**
	 * 支付通知业务
	 * @param orderNo
	 * @param status
	 * @return
	 */
	public Map<String,String> payNotify(String orderNo,String status,String returnMsg){
		Map<String,String> restMap = new HashMap<String,String>();
		//获取订单信息
		TPayOrder payOrder = payOrderService.getById(orderNo);
		if(payOrder == null) {
			//什么都不做
			restMap.put("resp_code", "0001");
			restMap.put("resp_msg", "查无订单");
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
		map.put("returnMsg", payOrder.getReturnMsg());
		if(payOrder.getOrderStatus() == 1) {
			if(status.equals("0")) {
				boolean flag = payOrderService.updateOrderStatusFail(payOrder.getOrderId(),returnMsg);
				if(flag) {
					map.put("orderStatus", "3");
					map.put("returnMsg", returnMsg);
				}else {
					restMap.put("resp_code", "0001");
					restMap.put("resp_msg", "订单修改失败");
					return restMap;
				}
			}else if(status.equals("1")) {
				boolean flag = payOrderService.updateOrderStatusSuccess(payOrder, "交易成功");
				if(flag) {
					map.put("orderStatus", "2");
					map.put("returnMsg", "交易成功");
				}else {
					restMap.put("resp_code", "0001");
					restMap.put("resp_msg", "订单修改失败");
					return restMap;
				}
			}else {
				restMap.put("resp_code", "0001");
				restMap.put("resp_msg", "订单支付状态异常");
				return restMap;
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
		return restMap;
	}


	/**
	 * 提现通知业务
	 * @param orderNo
	 * @param status
	 * @return
	 */
	public Map<String,String> cashNotify(String orderNo,String status,String returnMsg) {
		Map<String,String> restMap = new HashMap<String,String>();
		//获取订单信息
		TMchCashFlow mchCashFlow = mchCashFlowService.getById(orderNo);
		if(mchCashFlow == null) {
			//什么都不做
			restMap.put("resp_code", "0001");
			restMap.put("resp_msg", "查无订单");
			return restMap;
		}
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(mchCashFlow.getDeptId());
		if(dept == null) {
			restMap.put("resp_code", "0001");
			restMap.put("resp_msg", "提现订单渠道异常");
			return restMap;
		}
		JSONObject msgObj = new JSONObject();
		msgObj.put("notifyUrl", mchCashFlow.getNotifyUrl());
		msgObj.put("notifyCount", mchCashFlow.getNotifyCount());
		msgObj.put("cashId", mchCashFlow.getCashId());
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", mchCashFlow.getDeptId());
		map.put("mchId", mchCashFlow.getMchId());
		map.put("outTradeNo", mchCashFlow.getOutTradeNo());
		map.put("cashAmount", String.valueOf(mchCashFlow.getCashAmount()));
		map.put("outAmount", String.valueOf(mchCashFlow.getOutAmount()));
		map.put("cashStatus", String.valueOf(mchCashFlow.getCashStatus()));
		map.put("returnMsg", mchCashFlow.getReturnMsg());
		if(mchCashFlow.getCashStatus() == 1) {
			if(status.equals("0")) {
				boolean flag = mchCashFlowService.updateCashStatusFail(mchCashFlow.getCashId(), returnMsg);
				if(flag) {
					map.put("cashStatus", "3");
					map.put("returnMsg", returnMsg);
				}else {
					restMap.put("resp_code", "0001");
					restMap.put("resp_msg", "订单修改失败");
					return restMap;
				}
			}else if(status.equals("1")) {
				boolean flag = mchCashFlowService.updateCashStatusSuccess(mchCashFlow, "交易成功");
				if(flag) {
					map.put("cashStatus", "2");
					map.put("returnMsg", "交易成功");
				}else {
					restMap.put("resp_code", "0001");
					restMap.put("resp_msg", "订单修改失败");
					return restMap;
				}
			}else {
				restMap.put("resp_code", "0001");
				restMap.put("resp_msg", "订单支付状态异常");
				return restMap;
			}
		}
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-cashNotify:response={}",JSON.toJSONString(map));
		msgObj.put("notifyMsg", RestResponse.success(map));
		mqMchCashNotify.send(JSON.toJSONString(msgObj));
		restMap.put("resp_code", "000000");
		restMap.put("resp_msg", "成功");
		return restMap;
	}

	@PostMapping("/mqTest")
    @ResponseBody
	public ResponseData mqTest() {
		mqPayNotify.send("1123");
		return ResponseData.success();
	}

}
