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

import cn.hawy.quick.core.common.RestResponse;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.util.IdGenerator;
import cn.hawy.quick.core.util.MapUtils;
import cn.hawy.quick.core.util.PayUtil;
import cn.hawy.quick.core.util.RSA;
import cn.hawy.quick.modular.api.channel.EfpsChannel;
import cn.hawy.quick.modular.api.channel.efpsDto.OrderNotify;
import cn.hawy.quick.modular.api.channel.efpsDto.SplitNotify;
import cn.hawy.quick.modular.api.dto.PayPreConfirmDto;
import cn.hawy.quick.modular.api.dto.PayPreDto;
import cn.hawy.quick.modular.api.dto.QueryDto;
import cn.hawy.quick.modular.api.entity.TDeptRateChannel;
import cn.hawy.quick.modular.api.entity.TMchCard;
import cn.hawy.quick.modular.api.entity.TMchCardChannel;
import cn.hawy.quick.modular.api.entity.TMchInfo;
import cn.hawy.quick.modular.api.entity.TMchInfoChannel;
import cn.hawy.quick.modular.api.entity.TPayOrder;
import cn.hawy.quick.modular.api.mq.MqPayNotify;
import cn.hawy.quick.modular.api.service.TDeptRateChannelService;
import cn.hawy.quick.modular.api.service.TMchCardChannelService;
import cn.hawy.quick.modular.api.service.TMchCardService;
import cn.hawy.quick.modular.api.service.TMchInfoChannelService;
import cn.hawy.quick.modular.api.service.TMchInfoService;
import cn.hawy.quick.modular.api.service.TPayOrderService;
import cn.hawy.quick.modular.api.validate.PayValidate;
import cn.hawy.quick.modular.system.entity.Dept;
import cn.hawy.quick.modular.system.service.DeptService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

@RestController
@RequestMapping("/api/pay")
public class PayController {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	DeptService deptService;
	@Autowired
	EfpsChannel efpsChannel;
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
	MqPayNotify mqPayNotify;
	@Autowired
	TDeptRateChannelService deptRateChannelService;
	
	
	@RequestMapping(value = "/payPre",method = RequestMethod.POST)
	@ResponseBody
	public Object payPre(PayPreDto payPreDto) {
		log.info("下游请求报文-payPre:request={}",JSON.toJSONString(payPreDto));
		//参数校验
		PayValidate.payPreValidate(payPreDto);
		//渠道商信息
		Dept dept = deptService.getById(payPreDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(payPreDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, payPreDto.getSignature(), dept.getPartnerPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(payPreDto.getPartnerId(),payPreDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TMchCard mchCard = mchCardService.findBybankCardNo(payPreDto.getMchId(), payPreDto.getBankCardNo());
		if(mchCard == null) {
			throw new RestException(401, "商户卡信息错误");
		}
		TMchCardChannel mchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),2,"efps");
		if(mchCardChannel == null) {
			throw new RestException(401, "商户卡信息渠道错误");
		}
		TDeptRateChannel deptRateChannel = deptRateChannelService.findByDeptIdAndBankNameAndChannel(payPreDto.getPartnerId(), mchCard.getBankName(), "efps");
		if(deptRateChannel == null) {
			throw new RestException(401, "未找到渠道费率信息");
		}
		/*TDeptRateChannel costRateChannel = deptRateChannelService.findByDeptIdAndBankNameAndChannel("2000000", mchCard.getBankName(), "efps");
		if(costRateChannel == null) {
			throw new RestException(401, "未找到平台费率信息");
		}*/
		TPayOrder payOrder = payOrderService.findByMchIdAndOutTradeNo(payPreDto.getMchId(), payPreDto.getOutTradeNo());
		if(payOrder != null) {
			throw new RestException(401, "订单号已存在!");
		}
		Long orderId = IdGenerator.getIdLong();
		String subCustomerCode = efpsChannel.querySubCustomer(mchCardChannel.getOutMchId(), String.valueOf(orderId), payPreDto.getAreaCode(), payPreDto.getMccCode());
		Long orderAmount = NumberUtil.parseLong(payPreDto.getOrderAmount());
		//费率计算方式和后端通道保持一致
		//商户
		//Long mchFee = NumberUtil.parseLong(NumberUtil.roundStr(orderAmount*(Double.valueOf(mchCardChannel.getMchRate())), 0));
		Long mchFee = NumberUtil.round(NumberUtil.mul(payPreDto.getOrderAmount(), mchCardChannel.getMchRate()),0).longValue();
		Long mchAmount = orderAmount-mchFee;
		//渠道
		//Long deptFee = NumberUtil.parseLong(NumberUtil.roundStr(orderAmount*(Double.valueOf(deptRateChannel.getCostRate())), 0));
		Long deptFee = NumberUtil.round(NumberUtil.mul(payPreDto.getOrderAmount(), deptRateChannel.getCostRate()),0).longValue();
		Long deptAmount = mchFee - deptFee;
		//平台
		//Long costFee = NumberUtil.parseLong(NumberUtil.roundStr(orderAmount*(Double.valueOf(deptRateChannel.getChannelCostRate())), 0));
		Long costFee = NumberUtil.round(NumberUtil.mul(payPreDto.getOrderAmount(), deptRateChannel.getChannelCostRate()),0).longValue();
		Long costAmount = deptFee -  costFee;
		payOrder = new TPayOrder();
		payOrder.setOrderId(orderId);
		payOrder.setMchId(payPreDto.getMchId());
		payOrder.setMchName(mchInfo.getMchName());
		payOrder.setDeptId(payPreDto.getPartnerId());
		payOrder.setChannel("efps");
		payOrder.setChannelNo(deptRateChannel.getChannelNo());
		payOrder.setBankCardNo(payPreDto.getBankCardNo());
		payOrder.setOutTradeNo(payPreDto.getOutTradeNo());
		payOrder.setOrderAmount(orderAmount);
		payOrder.setOutMchId(mchCardChannel.getOutMchId());
		payOrder.setMchRate(mchCardChannel.getMchRate());
		payOrder.setMchFee(mchFee);
		payOrder.setMchAmount(mchAmount);
		payOrder.setDeptRate(deptRateChannel.getCostRate());
		payOrder.setDeptAmount(deptAmount);
		payOrder.setCostRate(deptRateChannel.getChannelCostRate());
		payOrder.setCostAmount(costAmount);
		payOrder.setOrderStatus(1);
		payOrder.setSplitStatus(1);
		payOrder.setNotifyUrl(payPreDto.getNotifyUrl());
		payOrder.setNotifyCount(0);
		payOrder.setOrderTime(LocalDateTime.now());
		payOrderService.save(payOrder);
		payPreDto.setOrderId(String.valueOf(orderId));
		String token = efpsChannel.protocolPayPre(payPreDto,orderAmount, mchAmount,subCustomerCode,mchCardChannel.getOutMchId(), mchCardChannel.getProtocol(),payPreDto.getIsSendSmsCode());
		payOrder.setToken(token);
		payOrderService.updateById(payOrder);
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", payPreDto.getPartnerId());
		map.put("mchId", payPreDto.getMchId());
		map.put("bankCardNo", payPreDto.getBankCardNo());
		map.put("outTradeNo", payPreDto.getOutTradeNo());
		map.put("orderStatus", "1");
		if(StrUtil.isEmpty(token)) {
			map.put("isConfirm", "1");//不需要
		}else {
			map.put("isConfirm", "2");//需要
		}
		
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-payPre:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}
	
	@RequestMapping(value = "/payPreConfirm",method = RequestMethod.POST)
	@ResponseBody
	public Object payPreConfirm(PayPreConfirmDto payPreConfirmDto) {
		log.info("下游请求报文-payPreConfirm:request={}",JSON.toJSONString(payPreConfirmDto));
		//参数校验
		PayValidate.payPreConfirmValidate(payPreConfirmDto);
		//渠道商信息
		Dept dept = deptService.getById(payPreConfirmDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(payPreConfirmDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, payPreConfirmDto.getSignature(), dept.getPartnerPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(payPreConfirmDto.getPartnerId(),payPreConfirmDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TPayOrder payOrder = payOrderService.findByMchIdAndOutTradeNo(payPreConfirmDto.getMchId(), payPreConfirmDto.getOutTradeNo());
		if(payOrder == null) {
			throw new RestException(401, "订单号不存在!");
		}
		if(StrUtil.isEmpty(payOrder.getToken())) {
			throw new RestException(401, "订单不需要确认!");
		}
		TMchCard mchCard = mchCardService.findBybankCardNo(payPreConfirmDto.getMchId(), payOrder.getBankCardNo());
		if(mchCard == null) {
			throw new RestException(401, "商户卡信息错误");
		}
		TMchCardChannel mchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),2,"efps");
		if(mchCardChannel == null) {
			throw new RestException(401, "商户卡信息渠道错误");
		}
		efpsChannel.protocolPayConfirm(payOrder.getToken(), payOrder.getOutMchId(), mchCardChannel.getProtocol(), payPreConfirmDto.getSmsCode());
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", payPreConfirmDto.getPartnerId());
		map.put("mchId", payPreConfirmDto.getMchId());
		map.put("outTradeNo", payPreConfirmDto.getOutTradeNo());
		map.put("orderStatus", "1");
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-payPreConfirm:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}
	
	@RequestMapping(value = "/query",method = RequestMethod.POST)
	@ResponseBody
	public Object query(QueryDto queryDto) {
		log.info("下游请求报文-query:request={}",JSON.toJSONString(queryDto));
		//参数校验
		PayValidate.queryValidate(queryDto);
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
		if(payOrder.getOrderStatus() == 1) {
			int orderStatus = efpsChannel.paymentQuery(String.valueOf(payOrder.getOrderId()));
			if(orderStatus == 1) {
				map.put("orderStatus", "1");
			}else if(orderStatus == 2) {
				boolean orderStatusFlag = payOrderService.updateOrderStatusSuccess(payOrder, dept);
				if(orderStatusFlag) {
					map.put("orderStatus", "2");
				}else {
					map.put("orderStatus", "1");
				}
			}else if(orderStatus == 3) {
				boolean orderStatusFlag = payOrderService.updateOrderStatusFail(payOrder.getOrderId());
				if(orderStatusFlag) {
					map.put("orderStatus", "3");
				}else {
					map.put("orderStatus", "1");
				}
			}else {
				throw new RestException(401, "订单查询失败!");
			}
		}else {//非处理中则直接返回
			map.put("orderStatus", String.valueOf(payOrder.getOrderStatus()));
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
	
	
	@RequestMapping(value = "/epspSplitNofify",method = RequestMethod.POST)
	@ResponseBody
	public Object epspSplitNofify(@RequestBody SplitNotify splitNotify) {
		log.info("EPSP支付通知-epspSplitNofify:request={}",JSON.toJSONString(splitNotify));
		Map<String,String> restMap = new HashMap<String,String>();
		//获取订单信息
		TPayOrder payOrder = payOrderService.getById(splitNotify.getOutTradeNo());
		if(payOrder == null) {
			//什么都不做
			restMap.put("returnCode", "0001");
			restMap.put("returnMsg", "查无订单");
			log.info("EPSP支付通知返回-epspSplitNofify:response={}",JSON.toJSONString(restMap));
			return restMap;
		}
		if(payOrder.getSplitStatus() != 1) {
			restMap.put("returnCode", "0001");
			restMap.put("returnMsg", "订单已处理");
			log.info("EPSP支付通知返回-epspSplitNofify:response={}",JSON.toJSONString(restMap));
			return restMap;
		}
		if("00".equals(splitNotify.getSplitState())) {
			boolean flag = payOrderService.updateSplitStatusSuccess(payOrder.getOrderId());
			if(flag) {
				restMap.put("returnCode", "0000");
				restMap.put("returnMsg", "");
				log.info("EPSP支付通知返回-epspSplitNofify:response={}",JSON.toJSONString(restMap));
				return restMap;
			}else {
				restMap.put("returnCode", "0001");
				restMap.put("returnMsg", "订单修改失败");
				log.info("EPSP支付通知返回-epspSplitNofify:response={}",JSON.toJSONString(restMap));
				return restMap;
			}
		}else {
			//什么都不做
			restMap.put("returnCode", "0001");
			restMap.put("returnMsg", "通知分账状态异常");
			log.info("EPSP支付通知返回-epspSplitNofify:response={}",JSON.toJSONString(restMap));
			return restMap;
		}
	}
	
	@RequestMapping(value = "/epspOrderNofify",method = RequestMethod.POST)
	@ResponseBody
	public Object epspOrderNofify(@RequestBody OrderNotify orderNotify) {
		log.info("EPSP支付通知-epspOrderNofify:request={}",JSON.toJSONString(orderNotify));
		Map<String,String> restMap = new HashMap<String,String>();
		//获取订单信息
		TPayOrder payOrder = payOrderService.getById(orderNotify.getOutTradeNo());
		if(payOrder == null) {
			//什么都不做
			restMap.put("returnCode", "0001");
			restMap.put("returnMsg", "查无订单");
			log.info("EPSP支付通知返回-epspOrderNofify:response={}",JSON.toJSONString(restMap));
			return restMap;
		}
		if(payOrder.getOrderStatus() != 1) {
			restMap.put("returnCode", "0001");
			restMap.put("returnMsg", "订单已处理");
			log.info("EPSP支付通知返回-epspOrderNofify:response={}",JSON.toJSONString(restMap));
			return restMap;
		}
		if("00".equals(orderNotify.getPayState())) {
			//渠道商信息
			Dept dept = deptService.getById(payOrder.getDeptId());
			if(dept == null) {
				restMap.put("returnCode", "0001");
				restMap.put("returnMsg", "订单渠道异常");
				log.info("EPSP支付通知返回-epspOrderNofify:response={}",JSON.toJSONString(restMap));
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
			boolean flag = payOrderService.updateOrderStatusSuccess(payOrder, dept);
			if(flag) {
				map.put("orderStatus", "2");
				String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
				String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
				map.put("signature", signature);
				log.info("下游返回报文-epspOrderNofify:response={}",JSON.toJSONString(map));
				msgObj.put("notifyMsg", RestResponse.success(map));
				mqPayNotify.send(JSON.toJSONString(msgObj));
				restMap.put("returnCode", "0000");
				restMap.put("returnMsg", "");
				log.info("EPSP支付通知返回-epspOrderNofify:response={}",JSON.toJSONString(restMap));
				return restMap;
			}else {
				restMap.put("returnCode", "0001");
				restMap.put("returnMsg", "订单修改失败");
				log.info("EPSP支付通知返回-epspOrderNofify:response={}",JSON.toJSONString(restMap));
				return restMap;
			}
		}else {
			//什么都不做
			restMap.put("returnCode", "0001");
			restMap.put("returnMsg", "通知分账状态异常");
			log.info("EPSP支付通知返回-epspOrderNofify:response={}",JSON.toJSONString(restMap));
			return restMap;
		}
	}
	
	@RequestMapping(value = "/test",method = RequestMethod.POST)
	@ResponseBody
	public Object test(@RequestBody String message) {
		System.out.println(message);
		return "success";
	}

}
