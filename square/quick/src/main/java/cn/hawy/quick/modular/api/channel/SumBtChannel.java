package cn.hawy.quick.modular.api.channel;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.hawy.quick.config.properties.SumBtProperties;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.util.PayUtil;
import cn.hawy.quick.modular.api.channel.sumDto.BindedCardForm;
import cn.hawy.quick.modular.api.dto.sumbt.CardAuthDto;
import cn.hawy.quick.modular.api.dto.sumbt.OrderAppleyDto;
import cn.hawy.quick.modular.api.entity.TMchCard;
import cn.hawy.quick.modular.api.entity.TMchInfo;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import fosun.sumpay.merchant.integration.core.request.Request;
import fosun.sumpay.merchant.integration.core.request.outer.BankCardAuthRequest;
import fosun.sumpay.merchant.integration.core.request.outer.GetAvaliableBanksAndBindedCardsRequest;
import fosun.sumpay.merchant.integration.core.request.outer.perfectbill.QueryAvaliableBankRequest;
import fosun.sumpay.merchant.integration.core.request.outer.samecard.ChangeAgentCardRequest;
import fosun.sumpay.merchant.integration.core.request.outer.samecard.PayAndAgentRequest;
import fosun.sumpay.merchant.integration.core.request.outer.samecard.QueryTradeInfoRequest;
import fosun.sumpay.merchant.integration.core.request.outer.samecard.SubmitVerifyMessageRequest;
import fosun.sumpay.merchant.integration.core.service.SumpayService;
import fosun.sumpay.merchant.integration.core.service.SumpayServiceImpl;

@Service
public class SumBtChannel {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SumBtProperties sumBtProperties;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String a = PayUtil.transFenToYuan("100");
		System.out.println(a);
		//orderAppley();
		//verifyMessage();
		//query();
	}


	public Map<String,String> orderAppley(OrderAppleyDto orderAppleyDto,String orderNo,TMchCard mchCard,TMchInfo mchInfo,String feeRate,String channelNo,String bandCardId,String channelMerNo,String userIpAddr,String longitude,String latitude) {
		PayAndAgentRequest req = new PayAndAgentRequest();
		//基础参数
		req.setVersion("1.0");
		req.setService("cn.sumpay.credit.management.trade.order.apply");
		req.setFormat("JSON");
		req.setApp_id(sumBtProperties.getAppId());
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		req.setUser_ip_addr(userIpAddr);
		req.setLongitude(longitude);
		req.setLatitude(latitude);
		req.setChannel_mer_no(channelMerNo);
		//业务参数
		req.setMer_no(sumBtProperties.getMerNo());
		req.setSub_mer_no(channelNo);
		req.setUser_id(orderAppleyDto.getMchId());
		req.setGoods_name(orderAppleyDto.getGoodsName());
		req.setGoods_num("1");
		req.setGoods_type("1");
		req.setOrder_no(orderNo);
		req.setOrder_time(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setOrder_amount(PayUtil.transFenToYuan(orderAppleyDto.getOrderAmount()));
		req.setReturn_url("http://www.xxx.com");
		req.setNotify_url(sumBtProperties.getOrderNotify());
		if(bandCardId != null) {
			req.setBinded_card_id(bandCardId);
		}else {
			req.setBusiness_code("03");
			req.setBank_code(mchCard.getBankCode());
			req.setCard_type("1");
			req.setCard_no(mchCard.getBankCardNo());
			req.setMobile_no(mchCard.getMobile());
		}
		req.setCvv(mchCard.getCvn());
		req.setValid_year(mchCard.getExpired().substring(0,4));
		req.setValid_month(mchCard.getExpired().substring(4));
		req.setRealname(mchInfo.getCustomerName());
		req.setId_no(mchInfo.getCustomerIdentNo());
		req.setId_type("1");
		req.setAgent_bank_code(orderAppleyDto.getAgentBankCode());
		req.setAgent_card_no(orderAppleyDto.getAgentCardNo());
		req.setAgent_card_type("0");
		req.setFee_rate(feeRate);
		req.setFee_amount(PayUtil.transFenToYuan(orderAppleyDto.getFeeAmount()));

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword(sumBtProperties.getPassword()); //
		request.setPrivateKeyPath(sumBtProperties.getPrivateKeyPath());
		//request.setPrivateKeyPath(sumBtProperties.getPrivateKeyPath()+"/"+channelNo+".pfx");
		request.setPublicKeyPath(sumBtProperties.getPublicKeyPath());
		request.setUrl(sumBtProperties.getUrl());
		request.setDomain(sumBtProperties.getDomain());
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		log.info("商盟请求报文-orderAppley:[request={}]", JSON.toJSONString(req));
		Map<String, String> res = ss.execute(request);
		log.info("商盟返回报文-orderAppley:[response={}]", JSON.toJSONString(res));
		Map<String,String> resMap = new HashMap<String,String>();
		if("000000".equals(res.get("resp_code"))) {
			String status = String.valueOf(res.get("status"));
			resMap.put("orderStatus", "1");
			resMap.put("returnMsg", "");
			resMap.put("resultType", String.valueOf(res.get("result_type")));
			if(status.equals("0")) {
				resMap.put("orderStatus", "3");
				resMap.put("returnMsg", String.valueOf(res.get("error_msg")));
				//orderStatus = 3;
			}else if(status.equals("1")) {
				resMap.put("orderStatus", "2");
				resMap.put("returnMsg", "交易成功");
				//orderStatus = 2;
			}else if(status.equals("2")) {
				//resMap.put("orderStatus", "1");
				//orderStatus = 1;
			}else if(status.equals("3")) {
				resMap.put("orderStatus", "4");
				resMap.put("returnMsg", String.valueOf(res.get("error_msg")));
				//orderStatus = 4;
			}else if(status.equals("4")) {
				throw new RestException(501, "转账失败，请联系运维人员");
			}else if(status.equals("5")) {
				resMap.put("orderStatus", "5");
			}else if(status.equals("9")) {
				throw new RestException(501, "订单关闭，请联系运维人员");
			}
		}else {
			resMap.put("orderStatus", "3");
			resMap.put("returnMsg", String.valueOf(res.get("resp_msg")));
			resMap.put("resultType", "");
		}
		return resMap;
	}

	public Map<String,String> verifyMessage(String orderNo,String verifyCode,String channelNo) {
		SubmitVerifyMessageRequest req = new SubmitVerifyMessageRequest();
		//基础参数
		req.setVersion("1.0");
		req.setService("cn.sumpay.credit.management.trade.verify.message");
		req.setFormat("JSON");
		req.setApp_id(sumBtProperties.getAppId());
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		//业务参数
		req.setMer_no(sumBtProperties.getMerNo());
		req.setSub_mer_no(channelNo);
		req.setOrder_no(orderNo);
		req.setVerify_code(verifyCode);

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword(sumBtProperties.getPassword()); //
		request.setPrivateKeyPath(sumBtProperties.getPrivateKeyPath());
		//request.setPrivateKeyPath(sumBtProperties.getPrivateKeyPath()+"/"+channelNo+".pfx");
		request.setPublicKeyPath(sumBtProperties.getPublicKeyPath());
		request.setUrl(sumBtProperties.getUrl());
		request.setDomain(sumBtProperties.getDomain());
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		log.info("商盟请求报文-verifyMessage:[request={}]", JSON.toJSONString(req));
		Map<String, String> res = ss.execute(request);
		log.info("商盟返回报文-verifyMessage:[response={}]", JSON.toJSONString(res));
		Map<String,String> resMap = new HashMap<String,String>();
		if("000000".equals(res.get("resp_code"))) {
			String status = String.valueOf(res.get("status"));
			resMap.put("orderStatus", "1");
			resMap.put("returnMsg", "");
			if(status.equals("0")) {
				resMap.put("orderStatus", "3");
				resMap.put("returnMsg", String.valueOf(res.get("error_msg")));
				//orderStatus = 3;
			}else if(status.equals("1")) {
				resMap.put("orderStatus", "2");
				resMap.put("returnMsg", "交易成功");
				//orderStatus = 2;
			}else if(status.equals("2")) {
				//resMap.put("orderStatus", "1");
				//orderStatus = 1;
			}else if(status.equals("3")) {
				resMap.put("orderStatus", "4");
				resMap.put("returnMsg", String.valueOf(res.get("error_msg")));
				//orderStatus = 4;
			}else if(status.equals("4")) {
				throw new RestException(501, "转账失败，请联系运维人员");
			}else if(status.equals("5")) {
				resMap.put("orderStatus", "5");
			}else if(status.equals("9")) {
				throw new RestException(501, "订单关闭，请联系运维人员");
			}
		}else {
			resMap.put("orderStatus", "3");
			resMap.put("returnMsg", String.valueOf(res.get("resp_msg")));
		}
		return resMap;
	}

	public Map<String,String> query(String orderNo,String channelNo) {
		QueryTradeInfoRequest req = new QueryTradeInfoRequest();
		//基础参数
		req.setVersion("1.0");
		req.setService("cn.sumpay.credit.management.query.trade.info");
		req.setFormat("JSON");
		req.setApp_id(sumBtProperties.getAppId());
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		//业务参数
		req.setMer_no(sumBtProperties.getMerNo());
		req.setSub_mer_no(channelNo);
		req.setOrder_no(orderNo);

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword(sumBtProperties.getPassword()); //
		request.setPrivateKeyPath(sumBtProperties.getPrivateKeyPath());
		//request.setPrivateKeyPath(sumBtProperties.getPrivateKeyPath()+"/"+channelNo+".pfx");
		request.setPublicKeyPath(sumBtProperties.getPublicKeyPath());
		request.setUrl(sumBtProperties.getUrl());
		request.setDomain(sumBtProperties.getDomain());
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		log.info("商盟请求报文-query:[request={}]", JSON.toJSONString(req));
		Map<String, String> res = ss.execute(request);
		log.info("商盟返回报文-query:[response={}]", JSON.toJSONString(res));
		Map<String,String> resMap = new HashMap<String,String>();
		if("000000".equals(res.get("resp_code"))) {
			String status = String.valueOf(res.get("status"));
			resMap.put("orderStatus", "1");
			resMap.put("returnMsg", "");
			if(status.equals("0")) {
				resMap.put("orderStatus", "3");
				resMap.put("returnMsg", String.valueOf(res.get("error_msg")));
				//orderStatus = 3;
			}else if(status.equals("1")) {
				resMap.put("orderStatus", "2");
				resMap.put("returnMsg", "交易成功");
				//orderStatus = 2;
			}else if(status.equals("2")) {
				//resMap.put("orderStatus", "1");
				//orderStatus = 1;
			}else if(status.equals("3")) {
				resMap.put("orderStatus", "4");
				resMap.put("returnMsg", String.valueOf(res.get("error_msg")));
				//orderStatus = 4;
			}else if(status.equals("4")) {
				throw new RestException(501, "转账失败，请联系运维人员");
			}else if(status.equals("5")) {
				resMap.put("orderStatus", "5");
			}else if(status.equals("9")) {
				throw new RestException(501, "订单关闭，请联系运维人员");
			}
		}else {
			resMap.put("orderStatus", "3");
			resMap.put("returnMsg", String.valueOf(res.get("resp_msg")));
		}
		return resMap;
	}

	public String avaliableBank(String userId,String bankCode,String cardNo,String channelNo) {
		GetAvaliableBanksAndBindedCardsRequest req = new GetAvaliableBanksAndBindedCardsRequest();
		//基础参数
		req.setVersion("1.0");
		req.setService("fosun.sumpay.api.quickpay.avaliable.bank");
		req.setFormat("JSON");
		req.setApp_id(sumBtProperties.getAppId());
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		//业务参数
		req.setMer_no(sumBtProperties.getMerNo());
		req.setSub_mer_no(channelNo);
		req.setUser_id(userId);
		req.setBusiness_code("03");

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword(sumBtProperties.getPassword()); //
		request.setPrivateKeyPath(sumBtProperties.getPrivateKeyPath());
		request.setPublicKeyPath(sumBtProperties.getPublicKeyPath());
		request.setUrl(sumBtProperties.getUrl());
		request.setDomain(sumBtProperties.getDomain());
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


	public Map<String,String> changeAgentCard(String orderNo,String bankCode,String cardNo,String channelNo) {
		ChangeAgentCardRequest req = new ChangeAgentCardRequest();
		//基础参数
		req.setVersion("1.0");
		req.setService("cn.sumpay.credit.management.trade.change.agent.card");
		req.setFormat("JSON");
		req.setApp_id(sumBtProperties.getAppId());
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		//业务参数
		req.setMer_no(sumBtProperties.getMerNo());
		req.setSub_mer_no(channelNo);
		req.setOrder_no(orderNo);
		req.setBank_code(bankCode);
		req.setCard_no(cardNo);
		req.setCard_type("0");

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword(sumBtProperties.getPassword()); //
		//request.setPrivateKeyPath(sumBtProperties.getPrivateKeyPath()+"/"+channelNo+".pfx");
		request.setPrivateKeyPath(sumBtProperties.getPrivateKeyPath());
		request.setPublicKeyPath(sumBtProperties.getPublicKeyPath());
		request.setUrl(sumBtProperties.getUrl());
		request.setDomain(sumBtProperties.getDomain());
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		log.info("商盟请求报文-changeAgentCard:[request={}]", JSON.toJSONString(req));
		Map<String, String> res = ss.execute(request);
		log.info("商盟返回报文-changeAgentCard:[response={}]", JSON.toJSONString(res));
		Map<String,String> resMap = new HashMap<String,String>();
		if("000000".equals(res.get("resp_code"))) {
			String status = String.valueOf(res.get("status"));
			resMap.put("orderStatus", "1");
			resMap.put("returnMsg", "");
			if(status.equals("0")) {
				resMap.put("orderStatus", "3");
				resMap.put("returnMsg", String.valueOf(res.get("error_msg")));
			}else if(status.equals("1")) {
				resMap.put("orderStatus", "2");
				resMap.put("returnMsg", "交易成功");
				//orderStatus = 2;
			}else if(status.equals("2")) {
				//resMap.put("orderStatus", "1");
				//orderStatus = 1;
			}else if(status.equals("3")) {
				resMap.put("orderStatus", "4");
				resMap.put("returnMsg", String.valueOf(res.get("error_msg")));
				//orderStatus = 4;
			}else if(status.equals("4")) {
				throw new RestException(501, "转账失败，请联系运维人员");
			}else if(status.equals("5")) {
				resMap.put("orderStatus", "5");
			}else if(status.equals("9")) {
				throw new RestException(501, "订单关闭，请联系运维人员");
			}
		}else {
			resMap.put("orderStatus", "3");
			resMap.put("returnMsg", String.valueOf(res.get("resp_msg")));
		}
		return resMap;
	}

	public Map<String,String> cardAuth(CardAuthDto cardAuthDto) {
		BankCardAuthRequest req = new BankCardAuthRequest();
		//基础参数
		req.setVersion("1.0");
		req.setService("fosun.sumpay.api.public.card.auth");
		req.setFormat("JSON");
		req.setApp_id(sumBtProperties.getCardAuthAppId());
		req.setTimestamp(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		req.setTerminal_type("wap");
		//业务参数
		req.setMer_no(sumBtProperties.getCardAuthAppId());
		req.setRequest_id(IdWorker.getIdStr());
		req.setAuth_type(cardAuthDto.getAuthType());
		req.setId_type(cardAuthDto.getIdType());
		req.setId_no(cardAuthDto.getIdNo());
		req.setRealname(cardAuthDto.getRealname());
		req.setMobile_no(cardAuthDto.getMobile());
		req.setCard_no(cardAuthDto.getCardNo());
		req.setCard_type(cardAuthDto.getCardType());
		req.setCvv(cardAuthDto.getCvv());
		req.setValid_date(cardAuthDto.getValidDate());

		Request request = new Request();
		request.setCharset("UTF-8");// 取jsp的请求编码
		request.setContent(JSON.toJSONString(req)); // 业务参数的json字段
		request.setPassword(sumBtProperties.getPassword()); //
		//request.setPrivateKeyPath(sumBtProperties.getPrivateKeyPath()+"/"+channelNo+".pfx");
		request.setPrivateKeyPath(sumBtProperties.getCardAuthPrivateKeyPath());
		request.setPublicKeyPath(sumBtProperties.getPublicKeyPath());
		request.setUrl(sumBtProperties.getUrl());
		request.setDomain(sumBtProperties.getDomain());
		request.setAesEncodedWords(req.getAesEncodedWords());
		request.setBase64EncodedWords(req.getBase64EncodedWords());
		request.setCharsetChangeWords(req.getCharsetChangeWords());
		SumpayService ss = new SumpayServiceImpl();
		log.info("商盟请求报文-cardAuth:[request={}]", JSON.toJSONString(req));
		Map<String, String> res = ss.execute(request);
		log.info("商盟返回报文-cardAuth:[response={}]", JSON.toJSONString(res));
		Map<String,String> resMap = new HashMap<String,String>();
		if("000000".equals(res.get("resp_code"))) {
			String status = String.valueOf(res.get("status"));
			if(status.equals("1")) {
				resMap.put("status", "1");
			}else if(status.equals("2")) {
				resMap.put("status", "2");
			}else if(status.equals("3")) {
				resMap.put("status", "3");
			}
		}else {
			resMap.put("status", "4");
			resMap.put("returnMsg", String.valueOf(res.get("resp_msg")));
		}
		return resMap;
	}

}
