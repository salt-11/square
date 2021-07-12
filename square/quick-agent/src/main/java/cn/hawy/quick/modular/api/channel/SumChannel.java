package cn.hawy.quick.modular.api.channel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hawy.quick.config.properties.SumProperties;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.util.PayUtil;
import cn.hawy.quick.modular.api.channel.sumDto.BindedCardForm;
import cn.hawy.quick.modular.api.channel.sumDto.UserStatusInfo;
import cn.hawy.quick.modular.api.dto.sum.BalanceQueryDto;
import cn.hawy.quick.modular.api.dto.sum.BindCardDto;
import cn.hawy.quick.modular.api.dto.sum.ModifyMchInfoDto;
import cn.hawy.quick.modular.api.dto.sum.RechargeDto;
import cn.hawy.quick.modular.api.dto.sum.RegisterDto;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import fosun.sumpay.merchant.integration.core.context.FileContext;
import fosun.sumpay.merchant.integration.core.request.Request;
import fosun.sumpay.merchant.integration.core.request.outer.perfectbill.ModifyUserInfoRequest;
import fosun.sumpay.merchant.integration.core.request.outer.perfectbill.QueryAvaliableAmountRequest;
import fosun.sumpay.merchant.integration.core.request.outer.perfectbill.QueryAvaliableBankRequest;
import fosun.sumpay.merchant.integration.core.request.outer.perfectbill.QueryOrderStatusRequest;
import fosun.sumpay.merchant.integration.core.request.outer.perfectbill.QueryUserStatusRequest;
import fosun.sumpay.merchant.integration.core.request.outer.perfectbill.RechargeRequest;
import fosun.sumpay.merchant.integration.core.request.outer.perfectbill.RegisterRequest;
import fosun.sumpay.merchant.integration.core.request.outer.perfectbill.SignSendMessageRequest;
import fosun.sumpay.merchant.integration.core.request.outer.perfectbill.SignVerifyMessageRequest;
import fosun.sumpay.merchant.integration.core.request.outer.perfectbill.WithdrawRequest;
import fosun.sumpay.merchant.integration.core.service.SumpayService;
import fosun.sumpay.merchant.integration.core.service.SumpayServiceImpl;

@Service
public class SumChannel {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SumProperties sumProperties;


	public String register(RegisterDto registerDto,String merAppId,String channelNo) {
		//基础参数信息
		RegisterRequest req = new RegisterRequest();
		req.setService("cn.sumpay.bill.management.trade.register");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id(channelNo);
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id(channelNo);
		req.setMer_app_id(merAppId);
		req.setMobile_no(registerDto.getMobile());
		req.setRealname(registerDto.getRealName());
		req.setId_type("1");
		req.setId_no(registerDto.getIdNo());
		req.setId_start_date(registerDto.getIdStartDate());
		req.setId_end_date(registerDto.getIdEndDate());
		req.setLogin_password(registerDto.getLoginPassword());
		req.setPay_password(registerDto.getPayPassword());
		req.setGender(registerDto.getGender());
		req.setCareer(registerDto.getCareer());
		req.setAddress(registerDto.getAddress());//100104967197
		//图片参数信息
		List<FileContext> fileContexts = new ArrayList<FileContext>();
		FileContext id_card_front_context = new FileContext();
		//String idCardFront = "http://qpay.shineroon.com:6001/img/idCard.png";
		String idCardFront = registerDto.getIdCardFront();
		id_card_front_context.setOriginalFilename(idCardFront.substring(idCardFront.lastIndexOf("/")+1));//
		id_card_front_context.setFieldName("id_card_front");
		id_card_front_context.setFileBytes(ImageToBase64(idCardFront));
		fileContexts.add(id_card_front_context);
		FileContext id_card_back_context = new FileContext();
		//String idCardBackFront = "http://qpay.shineroon.com:6001/img/idCardBack.png";
		String idCardBackFront = registerDto.getIdCardBack();
		id_card_back_context.setOriginalFilename(idCardBackFront.substring(idCardBackFront.lastIndexOf("/")+1));//
		id_card_back_context.setFieldName("id_card_back");
		id_card_back_context.setFileBytes(ImageToBase64(idCardBackFront));
		fileContexts.add(id_card_back_context);
		//接口调用
		Request request = new Request();
		request.setFileParams(fileContexts);
		request.setUserMultipart(true);
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword(sumProperties.getPassword()); //
		request.setPrivateKeyPath(sumProperties.getPrivateKeyPath()+"/"+channelNo+".pfx");
		request.setPublicKeyPath(sumProperties.getPublicKeyPath());
		request.setUrl(sumProperties.getUrl());
		request.setDomain(sumProperties.getDomain());
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		//System.setProperty("https.protocols","TLS1.2");
		log.info("商盟请求报文-register:[request={}]", JSON.toJSONString(req));
		Map<String, String> res = ss.execute(request);
		log.info("商盟返回报文-register:[response={}]", JSON.toJSONString(res));
		if("000000".equals(res.get("resp_code"))) {
			return String.valueOf(res.get("user_id"));
		}else {
			throw new RestException(501, String.valueOf(res.get("resp_msg")));
		}
	}

	public boolean modifyUserInfo(ModifyMchInfoDto modifyMchInfoDto,String merAppId,String channelNo,String userId) {
		//基础参数信息
		ModifyUserInfoRequest req = new ModifyUserInfoRequest();
		req.setService("cn.sumpay.bill.management.trade.modify.user.info");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id(channelNo);
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id(channelNo);
		req.setMer_app_id(merAppId);
		req.setUser_id(userId);
		req.setMobile_no(modifyMchInfoDto.getMobile());
		req.setId_start_date(modifyMchInfoDto.getIdStartDate());
		req.setId_end_date(modifyMchInfoDto.getIdEndDate());
		req.setCareer(modifyMchInfoDto.getCareer());
		req.setAddress(modifyMchInfoDto.getAddress());//100104967197
		//图片参数信息
		List<FileContext> fileContexts = new ArrayList<FileContext>();

		//String idCardFront = "http://qpay.shineroon.com:6001/img/idCard.png";
		if(StrUtil.isNotEmpty(modifyMchInfoDto.getIdCardFront())) {
			FileContext id_card_front_context = new FileContext();
			String idCardFront = modifyMchInfoDto.getIdCardFront();
			id_card_front_context.setOriginalFilename(idCardFront.substring(idCardFront.lastIndexOf("/")+1));//
			id_card_front_context.setFieldName("id_card_front");
			id_card_front_context.setFileBytes(ImageToBase64(idCardFront));
			fileContexts.add(id_card_front_context);
		}
		if(StrUtil.isNotEmpty(modifyMchInfoDto.getIdCardBack())) {
			FileContext id_card_back_context = new FileContext();
			//String idCardBackFront = "http://qpay.shineroon.com:6001/img/idCardBack.png";
			String idCardBackFront = modifyMchInfoDto.getIdCardBack();
			id_card_back_context.setOriginalFilename(idCardBackFront.substring(idCardBackFront.lastIndexOf("/")+1));//
			id_card_back_context.setFieldName("id_card_back");
			id_card_back_context.setFileBytes(ImageToBase64(idCardBackFront));
			fileContexts.add(id_card_back_context);
		}

		//接口调用
		Request request = new Request();
		request.setFileParams(fileContexts);
		request.setUserMultipart(true);
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword(sumProperties.getPassword()); //
		request.setPrivateKeyPath(sumProperties.getPrivateKeyPath()+"/"+channelNo+".pfx");
		request.setPublicKeyPath(sumProperties.getPublicKeyPath());
		request.setUrl(sumProperties.getUrl());
		request.setDomain(sumProperties.getDomain());
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		log.info("商盟请求报文-modifyUserInfo:[request={}]", JSON.toJSONString(req));
		Map<String, String> res = ss.execute(request);
		log.info("商盟返回报文-modifyUserInfo:[response={}]", JSON.toJSONString(res));
		if("000000".equals(res.get("resp_code"))) {
			return true;
		}else {
			throw new RestException(501, String.valueOf(res.get("resp_msg")));
		}
	}

	public Map<String,String> queryUserStatus(String merAppId,String channelNo,String userId) {
		//基础参数信息
		QueryUserStatusRequest req = new QueryUserStatusRequest();
		req.setService("cn.sumpay.bill.management.trade.query.user.status");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id(channelNo);
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id(channelNo);
		req.setMer_app_id(merAppId);
		req.setPage_index("1");
		req.setUser_id(userId);

		//接口调用
		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword(sumProperties.getPassword()); //
		request.setPrivateKeyPath(sumProperties.getPrivateKeyPath()+"/"+channelNo+".pfx");
		request.setPublicKeyPath(sumProperties.getPublicKeyPath());
		request.setUrl(sumProperties.getUrl());
		request.setDomain(sumProperties.getDomain());
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		log.info("商盟请求报文-queryUserStatus:[request={}]", JSON.toJSONString(req));
		Map<String, String> res = ss.execute(request);
		log.info("商盟返回报文-queryUserStatus:[response={}]", JSON.toJSONString(res));
		if("000000".equals(res.get("resp_code"))) {
			if("1".equals(res.get("total_count"))) {
				Map<String,String> respMap = new HashMap<String,String>();
				List<UserStatusInfo> userStatusList = JSON.parseArray(res.get("user_status_list"), UserStatusInfo.class);
				UserStatusInfo userStatusInfo = userStatusList.get(0);
				respMap.put("user_status", userStatusInfo.getUser_status());
				respMap.put("check_remarks", userStatusInfo.getCheck_remarks());
				return respMap;
			}else {
				throw new RestException(501, "查询总记录数错误");
			}
		}else {
			throw new RestException(501, String.valueOf(res.get("resp_msg")));
		}
	}

	public Map<String,String> sendMessage(BindCardDto bindCardDto,String userId,String orderNo,String realName,String idNo,String channelNo,String merAppId) { //pro-100106373982  test-100104967197
		SignSendMessageRequest req = new SignSendMessageRequest();
		req.setService("cn.sumpay.bill.management.trade.send.message");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id(channelNo);
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id(channelNo);
		req.setMer_app_id(merAppId);
		req.setUser_id(userId);
		req.setOrder_no(orderNo);
		req.setCard_no(bindCardDto.getCardNo());
		req.setCvv(bindCardDto.getCvv());
		req.setValid_year(bindCardDto.getValidYear());
		req.setValid_month(bindCardDto.getValidMonth());
		req.setBank_code(bindCardDto.getBankCode());
		req.setCard_type("1");
		req.setMobile_no(bindCardDto.getMobile());
		req.setRealname(realName);
		req.setId_type("1");
		req.setId_no(idNo);
		req.setReturn_url(bindCardDto.getReturnUrl());

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword(sumProperties.getPassword()); //
		request.setPrivateKeyPath(sumProperties.getPrivateKeyPath()+"/"+channelNo+".pfx");
		request.setPublicKeyPath(sumProperties.getPublicKeyPath());
		request.setUrl(sumProperties.getUrl());
		request.setDomain(sumProperties.getDomain());
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		log.info("商盟请求报文-sendMessage:[request={}]", JSON.toJSONString(req));
		Map<String, String> res = ss.execute(request);
		System.out.println(res.toString());
		log.info("商盟返回报文-sendMessage:[response={}]", JSON.toJSONString(res));
		Map<String,String> respMap = new HashMap<String,String>();
		if("000000".equals(res.get("resp_code"))) {
			respMap.put("sign_code", String.valueOf(res.get("sign_code")));
			respMap.put("form", String.valueOf(res.get("form")));
			respMap.put("bind_card_id", String.valueOf(res.get("bind_card_id")));
			return respMap;
		}else {
			throw new RestException(501, String.valueOf(res.get("resp_msg")));
		}
	}

	public String validMessage(String verifyCode,String userId,String orderNo,String channelNo,String merAppId) {
		SignVerifyMessageRequest req = new SignVerifyMessageRequest();
		req.setService("cn.sumpay.bill.management.trade.submit.message");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id(channelNo);
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id(channelNo);
		req.setMer_app_id(merAppId);
		req.setUser_id(userId);
		req.setOrder_no(orderNo);
		req.setVerify_code(verifyCode);

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword(sumProperties.getPassword()); //
		request.setPrivateKeyPath(sumProperties.getPrivateKeyPath()+"/"+channelNo+".pfx");
		request.setPublicKeyPath(sumProperties.getPublicKeyPath());
		request.setUrl(sumProperties.getUrl());
		request.setDomain(sumProperties.getDomain());
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		log.info("商盟请求报文-validMessage:[request={}]", JSON.toJSONString(req));
		Map<String, String> res = ss.execute(request);
		log.info("商盟返回报文-validMessage:[response={}]", JSON.toJSONString(res));
		if("000000".equals(res.get("resp_code"))) {
			return String.valueOf(res.get("bind_card_id"));
		}else {
			throw new RestException(501, String.valueOf(res.get("resp_msg")));
		}
	}

	public String queryAvaliableAmount(BalanceQueryDto balanceQueryDto,String userId,String bindCardId,String channelNo,String merAppId) {
		QueryAvaliableAmountRequest req = new QueryAvaliableAmountRequest();
		req.setService("cn.sumpay.bill.management.trade.query.avaliable.amount");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id(channelNo);
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id(channelNo);
		req.setMer_app_id(merAppId);
		req.setUser_id(userId);
		req.setBind_card_id(bindCardId);

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword(sumProperties.getPassword()); //
		request.setPrivateKeyPath(sumProperties.getPrivateKeyPath()+"/"+channelNo+".pfx");
		request.setPublicKeyPath(sumProperties.getPublicKeyPath());
		request.setUrl(sumProperties.getUrl());
		request.setDomain(sumProperties.getDomain());
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		log.info("商盟请求报文-queryAvaliableAmount:[request={}]", JSON.toJSONString(req));
		Map<String, String> res = ss.execute(request);
		log.info("商盟返回报文-queryAvaliableAmount:[response={}]", JSON.toJSONString(res));
		if("000000".equals(res.get("resp_code"))) {
			JSONObject SumpayAvaliableAmountResponse = JSON.parseObject(res.get("sumpay_avaliable_amount_response"));
			return String.valueOf(SumpayAvaliableAmountResponse.get("avaliable_balance"));
		}else {
			throw new RestException(501, String.valueOf(res.get("resp_msg")));
		}
	}

	public Map<String,String> recharge(RechargeDto rechargeDto,String orderNo,String userId,String bindCardId,String channelMerNo,String shareAmount,String channelNo,String merAppId,String userIpAddr,String longitude,String latitude) {//pro-115106091154
		RechargeRequest req = new RechargeRequest();
		req.setService("cn.sumpay.bill.management.trade.recharge");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id(channelNo);
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setLongitude(longitude);
		req.setLatitude(latitude);
		req.setAgent_id(channelNo);
		req.setOrder_no(orderNo);
		req.setOrder_amount(PayUtil.transFenToYuan(rechargeDto.getOrderAmount()));
		req.setOrder_time(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setMer_app_id(merAppId);
		req.setUser_id(userId);
		req.setBind_card_id(bindCardId);
		req.setPay_password(rechargeDto.getPayPassword());
		req.setCvv(rechargeDto.getCvv());
		req.setValid_year(rechargeDto.getValidYear());
		req.setValid_month(rechargeDto.getValidMonth());
		req.setNeed_notify("1");
		req.setNotify_url(sumProperties.getOrderNotify());
		req.setGoods_name(rechargeDto.getGoodsName());
		req.setGoods_num("1");
		req.setGoods_type("1");
		req.setChannel_mer_no(channelMerNo);
		req.setUser_ip_addr(userIpAddr);
		Map<String,Object> shareBenefitBean = new HashMap<String, Object>();
		shareBenefitBean.put("share_type", "1");
		shareBenefitBean.put("prior", "1");
		List<Map<String,Object>> benefitBeanList = new ArrayList<Map<String,Object>>();
		Map<String,Object> benefitBean = new HashMap<String, Object>();
		benefitBean.put("mer_no", sumProperties.getShareMerNo());
		benefitBean.put("share_type", "1");
		benefitBean.put("prior", "1");
		benefitBean.put("amount", PayUtil.transFenToYuan(shareAmount));
		benefitBeanList.add(benefitBean);
		shareBenefitBean.put("benefit_bean_list", benefitBeanList);
		req.setShare_benefit_exp(JSON.toJSONString(shareBenefitBean));

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword(sumProperties.getPassword()); //
		request.setPrivateKeyPath(sumProperties.getPrivateKeyPath()+"/"+channelNo+".pfx");
		request.setPublicKeyPath(sumProperties.getPublicKeyPath());
		request.setUrl(sumProperties.getUrl());
		request.setDomain(sumProperties.getDomain());
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		log.info("商盟请求报文-recharge:[request={}]", JSON.toJSONString(req));
		Map<String, String> res = ss.execute(request);
		log.info("商盟返回报文-recharge:[response={}]", JSON.toJSONString(res));
		Map<String,String> resMap = new HashMap<String,String>();
		if("000000".equals(res.get("resp_code"))) {
			//return String.valueOf(res.get("Available_balance"));
			JSONObject SumpayRechargeResponse = JSON.parseObject(res.get("sumpay_recharge_response"));
			/*if("0".equals(SumpayRechargeResponse.getString("status"))) {
				throw new RestException(501, String.valueOf(SumpayRechargeResponse.getString("error_msg")));
			}*/
			if("0".equals(SumpayRechargeResponse.getString("status"))) {
				resMap.put("orderStatus", "3");
				resMap.put("returnMsg", String.valueOf(SumpayRechargeResponse.getString("error_msg")));
				//throw new RestException(501, String.valueOf(SumpayRechargeResponse.getString("error_msg")));
			}else if("1".equals(SumpayRechargeResponse.getString("status"))) {
				resMap.put("orderStatus", "2");
				resMap.put("returnMsg", "交易成功");
			}else if("2".equals(SumpayRechargeResponse.getString("status"))) {
				resMap.put("orderStatus", "1");
			}else {
				resMap.put("orderStatus", "1");
			}
		}else {
			resMap.put("orderStatus", "3");
			resMap.put("returnMsg", String.valueOf(res.get("resp_msg")));
			//throw new RestException(501, String.valueOf(res.get("resp_msg")));
		}
		return resMap;
	}

	public Map<String,String> queryOrderStatus(String orderNo,String channelNo,String merAppId) {
		QueryOrderStatusRequest req = new QueryOrderStatusRequest();
		req.setService("cn.sumpay.bill.management.trade.query.order.status");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id(channelNo);
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id(channelNo);
		req.setMer_app_id(merAppId);
		req.setOrder_no(orderNo);

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword(sumProperties.getPassword()); //
		request.setPrivateKeyPath(sumProperties.getPrivateKeyPath()+"/"+channelNo+".pfx");
		request.setPublicKeyPath(sumProperties.getPublicKeyPath());
		request.setUrl(sumProperties.getUrl());
		request.setDomain(sumProperties.getDomain());
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		log.info("商盟请求报文-queryOrderStatus:[request={}]", JSON.toJSONString(req));
		Map<String, String> res = ss.execute(request);
		log.info("商盟返回报文-queryOrderStatus:[response={}]", JSON.toJSONString(res));
		Map<String,String> resMap = new HashMap<String,String>();
		if("000000".equals(res.get("resp_code"))) {
			JSONObject SumpayOrderSearchMerchantResponse = JSON.parseObject(res.get("sumpay_order_search_merchant_response"));
			//int orderStatus;
			if(SumpayOrderSearchMerchantResponse.getString("status").equals("0")) {
				resMap.put("orderStatus", "3");
				resMap.put("returnMsg", String.valueOf(SumpayOrderSearchMerchantResponse.getString("error_msg")));
				//orderStatus = 3;
			}else if(SumpayOrderSearchMerchantResponse.getString("status").equals("1")) {
				resMap.put("orderStatus", "2");
				resMap.put("returnMsg", "交易成功");
				//orderStatus = 2;
			}else if(SumpayOrderSearchMerchantResponse.getString("status").equals("2")) {
				resMap.put("orderStatus", "1");
				//orderStatus = 1;
			}else {
				//当处理中
				resMap.put("orderStatus", "1");
			}

		}else {
			resMap.put("orderStatus", "3");
			resMap.put("returnMsg", String.valueOf(res.get("resp_msg")));
			//throw new RestException(501, String.valueOf(res.get("resp_msg")));
		}
		return resMap;
	}

	public Map<String,String> withdraw(String orderNo,String orderAmount,String userId,String bindCardId,String payPassword,String shareAmount,String channelNo,String merAppId) {
		WithdrawRequest req = new WithdrawRequest();
		req.setService("cn.sumpay.bill.management.trade.withdraw");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id(channelNo);
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id(channelNo);
		req.setOrder_no(orderNo);
		req.setOrder_amount(PayUtil.transFenToYuan(orderAmount));
		req.setOrder_time(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setMer_app_id(merAppId);
		req.setUser_id(userId);
		req.setBind_card_id(bindCardId);
		req.setPay_password(payPassword);
		req.setNeed_notify("1");
		req.setNotify_url(sumProperties.getOrderNotify());
		Map<String,Object> shareBenefitBean = new HashMap<String, Object>();
		shareBenefitBean.put("share_type", "1");
		shareBenefitBean.put("prior", "1");
		List<Map<String,Object>> benefitBeanList = new ArrayList<Map<String,Object>>();
		Map<String,Object> benefitBean = new HashMap<String, Object>();
		benefitBean.put("mer_no", sumProperties.getShareMerNo());
		benefitBean.put("share_type", "1");
		benefitBean.put("prior", "1");
		benefitBean.put("amount", PayUtil.transFenToYuan(shareAmount));
		benefitBeanList.add(benefitBean);
		shareBenefitBean.put("benefit_bean_list", benefitBeanList);
		req.setShare_benefit_exp(JSON.toJSONString(shareBenefitBean));

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword(sumProperties.getPassword()); //
		request.setPrivateKeyPath(sumProperties.getPrivateKeyPath()+"/"+channelNo+".pfx");
		request.setPublicKeyPath(sumProperties.getPublicKeyPath());
		request.setUrl(sumProperties.getUrl());
		request.setDomain(sumProperties.getDomain());
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		log.info("商盟请求报文-withdraw:[request={}]", JSON.toJSONString(req));
		Map<String, String> res = ss.execute(request);
		log.info("商盟返回报文-withdraw:[response={}]", JSON.toJSONString(res));
		Map<String,String> resMap = new HashMap<String,String>();
		if("000000".equals(res.get("resp_code"))) {
			JSONObject SumpayWithdrawResponse = JSON.parseObject(res.get("sumpay_withdraw_response"));
			/*if("0".equals(SumpayWithdrawResponse.getString("status"))) {
				throw new RestException(501, String.valueOf(SumpayWithdrawResponse.getString("error_msg")));
			}*/
			if("0".equals(SumpayWithdrawResponse.getString("status"))) {
				resMap.put("orderStatus", "3");
				resMap.put("returnMsg", String.valueOf(SumpayWithdrawResponse.getString("error_msg")));
			}else if("1".equals(SumpayWithdrawResponse.getString("status"))) {
				resMap.put("orderStatus", "2");
				resMap.put("returnMsg", "交易成功");
			}else if("2".equals(SumpayWithdrawResponse.getString("status"))) {
				resMap.put("orderStatus", "1");
			}else {
				resMap.put("orderStatus", "1");
			}
		}else {
			resMap.put("orderStatus", "3");
			resMap.put("returnMsg", String.valueOf(res.get("resp_msg")));
			//throw new RestException(501, String.valueOf(res.get("resp_msg")));
		}
		return resMap;
	}


	public String avaliableBank(String userId,String bankCode,String cardNo,String channelNo,String merAppId) {
		QueryAvaliableBankRequest req = new QueryAvaliableBankRequest();
		req.setService("cn.sumpay.bill.management.trade.avaliable.bank");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id(channelNo);
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id(channelNo);
		req.setMer_app_id(merAppId);
		req.setUser_id(userId);

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword(sumProperties.getPassword()); //
		request.setPrivateKeyPath(sumProperties.getPrivateKeyPath()+"/"+channelNo+".pfx");
		request.setPublicKeyPath(sumProperties.getPublicKeyPath());
		request.setUrl(sumProperties.getUrl());
		request.setDomain(sumProperties.getDomain());
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		log.info("商盟请求报文-avaliableBank:[request={}]", JSON.toJSONString(req));
		Map<String, String> res = ss.execute(request);
		log.info("商盟返回报文-avaliableBank:[response={}]", JSON.toJSONString(res));
		if("000000".equals(res.get("resp_code"))) {
			List<BindedCardForm> bankList = JSON.parseArray(res.get("binded_card_list"), BindedCardForm.class);
			String bind_card_id = null;
			if(bankList == null) {
				return null;
			}
			for(BindedCardForm bindedCardForm : bankList) {
				if(bindedCardForm.getBank_code().equals(bankCode)
						&& bindedCardForm.getCard_no().equals(cardNo.substring(cardNo.length()-4))) {
					bind_card_id = bindedCardForm.getBind_card_id();
				}
			}
			if(bind_card_id == null) {
				return null;
			}
			return bind_card_id;
		}else {
			throw new RestException(501, String.valueOf(res.get("resp_msg")));
		}
	}

	//private static final String TEST_URL = "http://101.71.243.74:8180/entrance/gateway.htm";
	private static final String pro_url = "https://entrance.sumpay.cn/gateway.htm";




	public static void main(String[] args) {
		//String cardNo = "1234567890";
		//System.out.println(cardNo.substring(cardNo.length()-4));
		//register();
		//sendMessageTest();
		//validMessage();
		//avaliableBankTest("CMB","6225768721583068");
		//rechargeTest();
		//queryOrderStatusTest();
		queryAvaliableAmountTest();
		//tradeWithdrawTest();
		//String a = "http://123/456.jpg";
		//System.out.println(a.substring(a.lastIndexOf("/")+1));
	}


	public static byte[] ImageToBase64(String imgPath) {
		File imgFile = new File(imgPath);
		InputStream in = null;
		byte[] data = null;
		//读取图片字节数组
		try {
			in = new FileInputStream(imgFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 对字节数组Base64编码
		return data;
	}


	public static byte[] ImageToBase64ByOnline(String imgURL) {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			// 创建URL
			URL url = new URL(imgURL);
			byte[] by = new byte[1024];
			// 创建链接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			InputStream is = conn.getInputStream();
			// 将内容读取内存中
			int len = -1;
			while ((len = is.read(by)) != -1) {
				data.write(by, 0, len);
			}
			// 关闭流
			is.close();
		} catch (IOException e) {
			throw new RestException(401, "图片地址解析出错");
		}
		// 对字节数组Base64编码
		return data.toByteArray();
	}



	public static void registerTest() {
		//基础参数信息
		RegisterRequest req = new RegisterRequest();
		req.setService("cn.sumpay.perfect.bill.trade.register");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id("101243663");
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id("101243663");
		req.setMer_app_id("123456");
		req.setMobile_no("18305975931");
		req.setRealname("肖会");
		req.setId_type("1");
		req.setId_no("429001198911125912");
		req.setId_start_date("20160503");
		req.setId_end_date("20360503");
		req.setLogin_password("xh@18305975931");
		req.setPay_password("xh@123456789");
		req.setGender("1");
		req.setCareer("码农");
		req.setAddress("测试地址0000001");//100104967197
		//图片参数信息
		List<FileContext> fileContexts = new ArrayList<FileContext>();
		FileContext id_card_front_context = new FileContext();
		id_card_front_context.setOriginalFilename("1148456765455417345.jpg");//
		id_card_front_context.setFieldName("id_card_front");
		id_card_front_context.setFileBytes(ImageToBase64ByOnline("http://39.100.6.242/img/p10000153/1148456051677151234/1148456765455417345.jpg"));
		fileContexts.add(id_card_front_context);
		FileContext id_card_back_context = new FileContext();
		id_card_back_context.setOriginalFilename("1148475448974196737.jpg");//
		id_card_back_context.setFieldName("id_card_back");
		id_card_back_context.setFileBytes(ImageToBase64ByOnline("http://39.100.6.242/img/p10000153/1148475271592886273/1148475448974196737.jpg"));
		fileContexts.add(id_card_back_context);
		//接口调用
		Request request = new Request();
		request.setFileParams(fileContexts);
		request.setUserMultipart(true);
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword("qwer1234"); //
		request.setPrivateKeyPath("E:\\temp\\yle\\101243663.pfx");
		request.setPublicKeyPath("E:\\temp\\yle\\TTFPublicKey.cer");
		request.setUrl(pro_url);
		request.setDomain("127.0.0.1");
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		Map<String, String> res = ss.execute(request);
		System.out.println(res.toString());
		System.out.println(JSON.toJSONString(req));
	}

	public static void sendMessageTest() { //pro-100106373982  test-100104967197
		SignSendMessageRequest req = new SignSendMessageRequest();
		req.setService("cn.sumpay.perfect.bill.trade.send.message");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id("101243663");
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id("101243663");
		req.setMer_app_id("123456");
		req.setUser_id("100106373982");
		req.setOrder_no("20190801001");
		req.setCard_no("6225768721583068");
		req.setCvv("694");
		req.setValid_year("2021");
		req.setValid_month("07");
		req.setBank_code("CMB");
		req.setCard_type("1");
		req.setMobile_no("18305975931");
		req.setRealname("肖会");
		req.setId_type("1");
		req.setId_no("429001198911125912");
		req.setReturn_url("http://39.100.6.242:7010/quick/api/vs/mch/signNotify");

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword("qwer1234"); //
		request.setPrivateKeyPath("E:\\temp\\yle\\101243663.pfx");
		request.setPublicKeyPath("E:\\temp\\yle\\TTFPublicKey.cer");
		request.setUrl(pro_url);
		request.setDomain("127.0.0.1");
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		Map<String, String> res = ss.execute(request);
		System.out.println(res.toString());
		System.out.println(JSON.toJSONString(req));
	}

	public static void validMessageTest() {
		SignVerifyMessageRequest req = new SignVerifyMessageRequest();
		req.setService("cn.sumpay.perfect.bill.trade.submit.message");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id("101243663");
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id("101243663");
		req.setMer_app_id("123456");
		req.setUser_id("100106373982");
		req.setOrder_no("20190731001");
		req.setVerify_code("875413");

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword("qwer1234"); //
		request.setPrivateKeyPath("E:\\temp\\yle\\101243663.pfx");
		request.setPublicKeyPath("E:\\temp\\yle\\TTFPublicKey.cer");
		request.setUrl(pro_url);
		request.setDomain("127.0.0.1");
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		Map<String, String> res = ss.execute(request);
		System.out.println(res.toString());
		System.out.println(JSON.toJSONString(req));
	}



	public static void rechargeTest() {//pro-115106091154
		RechargeRequest req = new RechargeRequest();
		req.setService("cn.sumpay.perfect.bill.trade.recharge");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id("101243663");
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id("101243663");
		req.setOrder_no("20190805000000015");
		req.setOrder_amount("1.00");
		req.setOrder_time(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setMer_app_id("123456");
		req.setUser_id("100106373982");
		req.setBind_card_id("114998038659");
		req.setCvv("684");
		req.setValid_year("21");
		req.setValid_month("07");
		req.setPay_password("xh@123456789");
		req.setNeed_notify("0");
		req.setGoods_name("test001");
		req.setGoods_num("1");
		req.setGoods_type("1");
		req.setChannel_mer_no("998330155330020");
		Map<String,Object> shareBenefitBean = new HashMap<String, Object>();
		shareBenefitBean.put("share_type", "1");
		shareBenefitBean.put("prior", "1");
		List<Map<String,Object>> benefitBeanList = new ArrayList<Map<String,Object>>();
		Map<String,Object> benefitBean = new HashMap<String, Object>();
		benefitBean.put("mer_no", "101253659");
		benefitBean.put("share_type", "1");
		benefitBean.put("prior", "1");
		benefitBean.put("amount", "0.50");
		benefitBeanList.add(benefitBean);
		shareBenefitBean.put("benefit_bean_list", benefitBeanList);
		req.setShare_benefit_exp(JSON.toJSONString(shareBenefitBean));

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword("qwer1234"); //
		request.setPrivateKeyPath("E:\\temp\\yle\\101243663.pfx");
		request.setPublicKeyPath("E:\\temp\\yle\\TTFPublicKey.cer");
		request.setUrl(pro_url);
		request.setDomain("127.0.0.1");
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		Map<String, String> res = ss.execute(request);
		System.out.println(res.toString());
		System.out.println(JSON.toJSONString(req));
	}

	public static void queryAvaliableAmountTest() {
		QueryAvaliableAmountRequest req = new QueryAvaliableAmountRequest();
		req.setService("cn.sumpay.bill.management.trade.query.avaliable.amount");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id("101243664");
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id("101243664");
		req.setMer_app_id("PBsT3Tmbvyf179MipN3ULtXvfEXEPU");
		req.setUser_id("100106528218");
		req.setBind_card_id("116748328112");

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword("qwer1234"); //
		request.setPrivateKeyPath("E:\\temp\\yle\\101243664.pfx");
		request.setPublicKeyPath("E:\\temp\\yle\\TTFPublicNew.cer");
		request.setUrl(pro_url);
		request.setDomain("127.0.0.1");
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		Map<String, String> res = ss.execute(request);
		System.out.println(res.toString());
		System.out.println(JSON.toJSONString(req));
	}

	public static void avaliableBankTest(String bankCode,String cardNo) {
		QueryAvaliableBankRequest req = new QueryAvaliableBankRequest();
		req.setService("cn.sumpay.perfect.bill.trade.avaliable.bank");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id("101243663");
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id("101243663");
		req.setMer_app_id("123456");
		req.setUser_id("100106373982");

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword("qwer1234"); //
		request.setPrivateKeyPath("E:\\temp\\yle\\101243663.pfx");
		request.setPublicKeyPath("E:\\temp\\yle\\TTFPublicKey.cer");
		request.setUrl(pro_url);
		request.setDomain("127.0.0.1");
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		Map<String, String> res = ss.execute(request);
		System.out.println(res);
		List<BindedCardForm> bankList = JSON.parseArray(res.get("binded_card_list"), BindedCardForm.class);
		String bind_card_id = null;
		for(BindedCardForm bindedCardForm : bankList) {
			if(bindedCardForm.getBank_code().equals(bankCode)
					&& bindedCardForm.getCard_no().equals(cardNo.substring(cardNo.length()-4))) {
				bind_card_id = bindedCardForm.getBind_card_id();
			}
		}
		System.out.println(bind_card_id);
	}

	public static void queryOrderStatusTest() {
		QueryOrderStatusRequest req = new QueryOrderStatusRequest();
		req.setService("cn.sumpay.perfect.bill.trade.query.order.status");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id("101243663");
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id("101243663");
		req.setMer_app_id("123456");
		req.setOrder_no("t20190805007");

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword("qwer1234"); //
		request.setPrivateKeyPath("E:\\temp\\yle\\101243663.pfx");
		request.setPublicKeyPath("E:\\temp\\yle\\TTFPublicKey.cer");
		request.setUrl(pro_url);
		request.setDomain("127.0.0.1");
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		Map<String, String> res = ss.execute(request);
		System.out.println(res.toString());
		System.out.println(JSON.toJSONString(req));
	}


	public static void signChannelTest() {
		QueryOrderStatusRequest req = new QueryOrderStatusRequest();
		req.setService("cn.sumpay.bill.management.trade.sign.channel.info");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id("101563664");
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id("101563664");
		req.setMer_app_id("123456");
		req.setOrder_no("t20190805007");

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword("qwer1234"); //
		request.setPrivateKeyPath("E:\\temp\\yle\\101563664.pfx");
		request.setPublicKeyPath("E:\\temp\\yle\\TTFPublicKey.cer");
		request.setUrl(pro_url);
		request.setDomain("127.0.0.1");
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		Map<String, String> res = ss.execute(request);
		System.out.println(res.toString());
		System.out.println(JSON.toJSONString(req));
	}

	public static void withdrawTest() {
		WithdrawRequest req = new WithdrawRequest();
		req.setService("cn.sumpay.perfect.bill.trade.withdraw");
		req.setVersion("1.0");
		req.setFormat("JSON");
		req.setApp_id("101243663");
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setAgent_id("101243663");
		req.setOrder_no("t20190805007");
		req.setOrder_amount("0.09");
		req.setOrder_time(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setMer_app_id("123456");
		req.setUser_id("100106373982");
		req.setBind_card_id("114998038659");
		req.setPay_password("xh@123456789");
		req.setNeed_notify("0");

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword("qwer1234"); //
		request.setPrivateKeyPath("E:\\temp\\yle\\101243663.pfx");
		request.setPublicKeyPath("E:\\temp\\yle\\TTFPublicKey.cer");
		request.setUrl(pro_url);
		request.setDomain("127.0.0.1");
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		Map<String, String> res = ss.execute(request);
		System.out.println(res.toString());
		System.out.println(JSON.toJSONString(req));
	}
}
