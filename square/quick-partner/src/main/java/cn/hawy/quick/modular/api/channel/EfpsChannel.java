package cn.hawy.quick.modular.api.channel;

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

import cn.hawy.quick.config.properties.EfpsProperties;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.util.IdGenerator;
import cn.hawy.quick.core.util.RsaUtils;
import cn.hawy.quick.modular.api.channel.efpsDto.Business;
import cn.hawy.quick.modular.api.channel.efpsDto.Contact;
import cn.hawy.quick.modular.api.channel.efpsDto.OrderGoods;
import cn.hawy.quick.modular.api.channel.efpsDto.OrderInfo;
import cn.hawy.quick.modular.api.channel.efpsDto.SplitInfo;
import cn.hawy.quick.modular.api.channel.efpsDto.SubCode;
import cn.hawy.quick.modular.api.dto.AddMerchantDto;
import cn.hawy.quick.modular.api.dto.BindCardConfirmDto;
import cn.hawy.quick.modular.api.dto.BindCardDto;
import cn.hawy.quick.modular.api.dto.PayPreDto;
import cn.hawy.quick.modular.api.entity.TMchCardChannel;
import cn.hawy.quick.modular.api.entity.TMchInfo;
import cn.hawy.quick.modular.api.entity.TMchInfoChannel;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;

@Service
public class EfpsChannel {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public static String privateKeyPath = "E:\\works\\efps.pfx";
	
	public static String publicKeyPath = "E:\\works\\efps.cer";
	
	@Autowired
	EfpsProperties efpsProperties;
	
	
	public static void main(String[] args) throws Exception{
		//addMerchant();
		//bindCard();
		//bindCardConfirm();
		//queryProtocol();
		//protocolPayPre();
		//protocolPayConfirm();
		//paymentQuery();
		//splitOrder();
		//subCustomerAccountQuery();//
		//withdrawalForSubMerchant();
		//withdrawalQueryForSubMerchant();
		//bindCardConfirm();
		//System.out.println(DateUtil.today());
		querySubCustomer();
	}
	
	public String addMerchant(AddMerchantDto addMerchantDto) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerCode", efpsProperties.getCustomerCode());
		paramMap.put("version", "2.0");
		paramMap.put("name", addMerchantDto.getMchName());
		paramMap.put("shortName", addMerchantDto.getMchShortName());
		paramMap.put("mobile", addMerchantDto.getMobile());
		paramMap.put("type", "50");
		paramMap.put("areaCode", addMerchantDto.getAreaCode());
		paramMap.put("lealPersonName", addMerchantDto.getCustomerName());
		paramMap.put("lealPersonIdentificationType", "0");
		paramMap.put("lealPersonIdentificationNo", addMerchantDto.getCustomerIdentNo());
		paramMap.put("settMode", "D0"); 
		paramMap.put("settCircle", "0"); 
		paramMap.put("bankAccountType", "2"); 
		paramMap.put("settTarget", "2");
		paramMap.put("notifyURL", "www.xxx.com");
		List<Business> businessList = new ArrayList<Business>();
		businessList.add(new Business("SPLITTED",  "20190709",  "20991231",  "1",  "0"));
		//businessList.add(new Business("WITHDRAW_CREDIT_CARD",  "20190709",  "20991231",  "1",  "0"));
		paramMap.put("businessList", businessList);
		List<Contact> contactList = new ArrayList<Contact>();
		contactList.add(new Contact(1, addMerchantDto.getCustomerName(), addMerchantDto.getMobile(),  addMerchantDto.getEmail(),  ""));
		paramMap.put("contactList", contactList);
		paramMap.put("signModel", "0");
		paramMap.put("nonceStr", RandomUtil.randomNumbers(10));
		String paramMapBody = JSONObject.toJSONString(paramMap);
		Map<String, String> headers = new HashMap<String, String>();
		try {
			String sign = RsaUtils.sign(efpsProperties.getPrivateKeyPath(),efpsProperties.getPassword(), paramMapBody);//签名
			headers.put("x-efps-sign", sign);
			headers.put("x-efps-sign-no", efpsProperties.getSignNo());
		}catch (Exception e) {
			// TODO: handle exception
			throw new RestException(401, "通道签名异常!");
		}
		log.info("易票联请求报文-creatMchnt:[request={}]",paramMapBody);
		String result = HttpRequest.post(efpsProperties.getUrl()+"/api/pas/CustomerInfo/AddMerchant")
				.addHeaders(headers)
				.body(paramMapBody)
				.execute()
				.body();
		log.info("易票联请求报文-creatMchnt:[response={}]",result);
		Map<String,Object> responseMap =JSON.parseObject(result,Map.class);
		if("0000".equals(responseMap.get("returnCode"))) {
			return String.valueOf(responseMap.get("memberId"));
		}else {
			throw new RestException(501, String.valueOf(responseMap.get("returnMsg")));
		}
	}
	
	/**
	 * 绑卡
	 * @throws Exception
	 */
	public Map<String,Object> bindCard(BindCardDto bindCardDto,TMchInfo mchInfo,String memberId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerCode", efpsProperties.getCustomerCode());
		paramMap.put("version", "2.0");
		paramMap.put("mchtOrderNo", RandomUtil.randomNumbers(10));
		paramMap.put("memberId", memberId);
		try {
			paramMap.put("userName", RsaUtils.encryptByPublicKey(mchInfo.getCustomerName(), RsaUtils.getPublicKey(efpsProperties.getPublicKeyPath())));
			paramMap.put("phoneNum", RsaUtils.encryptByPublicKey(bindCardDto.getMobile(), RsaUtils.getPublicKey(efpsProperties.getPublicKeyPath())));
			paramMap.put("bankCardNo", RsaUtils.encryptByPublicKey(bindCardDto.getBankCardNo(), RsaUtils.getPublicKey(efpsProperties.getPublicKeyPath())));
			paramMap.put("certificatesNo", RsaUtils.encryptByPublicKey(mchInfo.getCustomerIdentNo(), RsaUtils.getPublicKey(efpsProperties.getPublicKeyPath())));
			paramMap.put("certificatesType", "01");
			paramMap.put("businessCategory", "efpsNocardService");
			if(bindCardDto.getIsSendIssuer().equals("true")) {
				paramMap.put("isSendIssuer", true);
			}else if(bindCardDto.getIsSendIssuer().equals("false")) {
				paramMap.put("isSendIssuer", false);
			}else {
				throw new RestException(401, "isSendIssuer异常!");
			}
			
			paramMap.put("bankCardType", bindCardDto.getBankCardType());
			paramMap.put("nonceStr", RandomUtil.randomNumbers(10));
			if("credit".equals(bindCardDto.getBankCardType())) {
				paramMap.put("expired", RsaUtils.encryptByPublicKey(bindCardDto.getExpired(),RsaUtils.getPublicKey(efpsProperties.getPublicKeyPath())));
				paramMap.put("cvn", RsaUtils.encryptByPublicKey(bindCardDto.getCvn(),RsaUtils.getPublicKey(efpsProperties.getPublicKeyPath())));
			}
		}catch (Exception e) {
			// TODO: handle exception
			throw new RestException(401, "通道签名异常!");
		}
		
		String paramMapBody  = JSONObject.toJSONString(paramMap);
		Map<String, String> headers = new HashMap<String, String>();
		try {
			String sign = RsaUtils.sign(efpsProperties.getPrivateKeyPath(),efpsProperties.getPassword(), paramMapBody);//签名
			headers.put("x-efps-sign", sign);
			headers.put("x-efps-sign-no", efpsProperties.getSignNo());
		}catch (Exception e) {
			// TODO: handle exception
			throw new RestException(401, "通道签名异常!");
		}
		log.info("易票联请求报文-bindCard:[request={}]",paramMapBody);
		String result = HttpRequest.post(efpsProperties.getUrl()+"/api/txs/protocol/bindCard")
				.addHeaders(headers)
				.body(paramMapBody)
				.execute()
				.body();
		log.info("易票联请求报文-bindCard:[response={}]",result);
		Map<String,Object> responseMap =JSON.parseObject(result,Map.class);
		Map<String,Object> restMap = new HashMap<>();
		if("0000".equals(responseMap.get("returnCode"))) {
			restMap.put("smsNo", responseMap.get("smsNo"));
			restMap.put("protocol", responseMap.get("protocol"));
			return restMap;
		}else {
			throw new RestException(501, String.valueOf(responseMap.get("returnMsg")));
		}
	}
	
	/**
	 * 绑卡确认
	 * @throws Exception
	 */
	public String bindCardConfirm(String smsCode,String smsNo,String memberId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerCode", efpsProperties.getCustomerCode());
		paramMap.put("version", "2.0");
		paramMap.put("smsNo", smsNo);
		paramMap.put("memberId", memberId);
		paramMap.put("smsCode", smsCode);
		paramMap.put("nonceStr", RandomUtil.randomNumbers(10));
		String paramMapBody  = JSONObject.toJSONString(paramMap);
		Map<String, String> headers = new HashMap<String, String>();
		try {
			String sign = RsaUtils.sign(efpsProperties.getPrivateKeyPath(),efpsProperties.getPassword(), paramMapBody);//签名
			headers.put("x-efps-sign", sign);
			headers.put("x-efps-sign-no", efpsProperties.getSignNo());
		}catch (Exception e) {
			// TODO: handle exception
			throw new RestException(401, "通道签名异常!");
		}
		log.info("易票联请求报文-bindCardConfirm:[request={}]",paramMapBody);
		String result = HttpRequest.post(efpsProperties.getUrl()+"/api/txs/protocol/bindCardConfirm")
				.addHeaders(headers)
				.body(paramMapBody)
				.execute()
				.body();
		log.info("易票联请求报文-bindCardConfirm:[response={}]",result);
		Map<String,Object> responseMap =JSON.parseObject(result,Map.class);
		if("0000".equals(responseMap.get("returnCode"))) {
			return String.valueOf(responseMap.get("protocol"));
		}else {
			throw new RestException(501, String.valueOf(responseMap.get("returnMsg")));
		}
	}
	
	
	public static void querySubCustomer() throws Exception{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerCode", "5651300003039000");
		paramMap.put("version", "2.0");
		paramMap.put("memberId", "5651300003063746");
		paramMap.put("outTradeNo", "20190716001");
		paramMap.put("areaCode", "1021");
		paramMap.put("mccCode", "5812");
		paramMap.put("nonceStr", "1234567890");
		String paramMapBody  = JSONObject.toJSONString(paramMap);
		String sign = RsaUtils.sign(privateKeyPath,"Epaylinks@EFPS2018", paramMapBody);//签名
		System.out.println(paramMapBody);
		System.out.println(sign);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("x-efps-sign", sign);
		headers.put("x-efps-sign-no", "20190115yhys01");
		String result = HttpRequest.post("http://test-efps.epaylinks.cn/api/cum/querySubCustomer")
				.addHeaders(headers)
				.body(paramMapBody)
				.execute()
				.body();
		Map<String,Object> responseMap =JSON.parseObject(result,Map.class);
		if("0000".equals(responseMap.get("returnCode"))) {
			List<SubCode> subCodeList = JSON.parseArray(String.valueOf(responseMap.get("subCodeList")), SubCode.class);
			if(subCodeList.size()>0) {
				SubCode subCode = RandomUtil.randomEle(subCodeList);
				
			}else {
				System.out.println("null");
			}
		}else {
			throw new RestException(501, String.valueOf(responseMap.get("returnMsg")));
		}
	}
	
	/**
	 * 查询子商户
	 * @param memberId
	 * @param outTradeNo
	 * @param areaCode
	 * @param mccCode
	 * @return
	 */
	public String querySubCustomer(String memberId,String outTradeNo,String areaCode,String mccCode){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerCode", efpsProperties.getCustomerCode());
		paramMap.put("version", "2.0");
		paramMap.put("memberId", memberId);
		paramMap.put("outTradeNo", outTradeNo);
		paramMap.put("areaCode", areaCode);
		paramMap.put("mccCode", mccCode);
		paramMap.put("nonceStr", RandomUtil.randomNumbers(10));
		String paramMapBody  = JSONObject.toJSONString(paramMap);
		Map<String, String> headers = new HashMap<String, String>();
		try {
			String sign = RsaUtils.sign(efpsProperties.getPrivateKeyPath(),efpsProperties.getPassword(), paramMapBody);//签名
			headers.put("x-efps-sign", sign);
			headers.put("x-efps-sign-no", efpsProperties.getSignNo());
		}catch (Exception e) {
			// TODO: handle exception
			throw new RestException(401, "通道签名异常!");
		}
		log.info("易票联请求报文-querySubCustomer:[request={}]",paramMapBody);
		String result = HttpRequest.post(efpsProperties.getUrl()+"/api/cum/querySubCustomer")
				.addHeaders(headers)
				.body(paramMapBody)
				.execute()
				.body();
		log.info("易票联返回报文-querySubCustomer:[response={}]",result);
		Map<String,Object> responseMap =JSON.parseObject(result,Map.class);
		if("0000".equals(responseMap.get("returnCode"))) {
			List<SubCode> subCodeList = JSON.parseArray(String.valueOf(responseMap.get("subCodeList")), SubCode.class);
			if(subCodeList.size()>0) {
				SubCode subCode = RandomUtil.randomEle(subCodeList);
				return subCode.getSubCustomerCode();
			}else {
				if(efpsProperties.getIsQuerySubCustomer()) {
					throw new RestException(501, "交易子商户查询失败!");
				}else {
					return "";
				}
			}
		}else {
			throw new RestException(501, String.valueOf(responseMap.get("returnMsg")));
		}
	}
	
	public static void unBindCard() throws Exception{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerCode", "5651300003039000");
		paramMap.put("version", "2.0");
		paramMap.put("protocol", "p201907084580943308069");
		paramMap.put("memberId", "5651300003063746");
		paramMap.put("nonceStr", "1234567890");
		String paramMapBody  = JSONObject.toJSONString(paramMap);
		String sign = RsaUtils.sign(privateKeyPath,"Epaylinks@EFPS2018", paramMapBody);//签名
		System.out.println(paramMapBody);
		System.out.println(sign);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("x-efps-sign", sign);
		headers.put("x-efps-sign-no", "20190115yhys01");
		String result = HttpRequest.post("http://test-efps.epaylinks.cn/api/txs/protocol/unBindCard")
				.addHeaders(headers)
				.body(paramMapBody)
				.execute()
				.body();
		System.out.println(result);
	}
	
	public static void queryProtocol() throws Exception{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerCode", "5651300003039000");
		paramMap.put("memberId", "5651300003063784");
		paramMap.put("protocol", "p201907089857209302121");
		paramMap.put("bankCardNo", "6214855914759952");
		paramMap.put("certificatesType", "01");
		paramMap.put("nonceStr", "1234567890");
		String paramMapBody  = JSONObject.toJSONString(paramMap);
		String sign = RsaUtils.sign(privateKeyPath,"Epaylinks@EFPS2018", paramMapBody);//签名
		System.out.println(paramMapBody);
		System.out.println(sign);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("x-efps-sign", sign);
		headers.put("x-efps-sign-no", "20190115yhys01");
		String result = HttpRequest.post("http://test-efps.epaylinks.cn/api/txs/protocol/queryProtocol")
				.addHeaders(headers)
				.body(paramMapBody)
				.execute()
				.body();
		System.out.println(result);
	}
	
	

	/**
	 * 交易
	 * @param payPreDto
	 * @param orderAmount
	 * @param mchAmount
	 * @param subCustomerCode
	 * @param memberId
	 * @param protocol
	 * @return
	 */
	public String protocolPayPre(PayPreDto payPreDto,Long orderAmount,Long mchAmount,String subCustomerCode,String memberId,String protocol,String isSendSmsCode) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerCode", efpsProperties.getCustomerCode());
		paramMap.put("version", "2.0");
		paramMap.put("subCustomerCode", subCustomerCode);
		paramMap.put("memberId", memberId);
		paramMap.put("outTradeNo", payPreDto.getOrderId());
		paramMap.put("protocol", protocol);
		if(StrUtil.isEmpty(isSendSmsCode)) {
			paramMap.put("isSendSmsCode", "2");
		}else {
			paramMap.put("isSendSmsCode", isSendSmsCode);
		}
		OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(payPreDto.getGoodsId());
        orderInfo.setBusinessType(payPreDto.getGoodsType());
        orderInfo.addGood(new OrderGoods(payPreDto.getGoodsName(), "1", orderAmount));
		paramMap.put("orderInfo", orderInfo);
		paramMap.put("payAmount",orderAmount);
		paramMap.put("payCurrency", "CNY");
		paramMap.put("notifyUrl", efpsProperties.getOrderNotifyUrl());
		paramMap.put("transactionStartTime", DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		paramMap.put("needSplit", "true");
		List<SplitInfo> splitInfoList = new ArrayList<SplitInfo>();
		if(orderAmount-mchAmount > 0) {
			splitInfoList.add(new SplitInfo(efpsProperties.getCustomerCode(),orderAmount-mchAmount,1));
			splitInfoList.add(new SplitInfo(memberId,mchAmount));
		}else {
			splitInfoList.add(new SplitInfo(memberId,mchAmount,1));
		}
		paramMap.put("splitInfoList", splitInfoList);
		paramMap.put("splitNotifyUrl", efpsProperties.getSplitNotifyUrl());
		paramMap.put("nonceStr", RandomUtil.randomNumbers(10));
		String paramMapBody  = JSONObject.toJSONString(paramMap);
		Map<String, String> headers = new HashMap<String, String>();
		try {
			String sign = RsaUtils.sign(efpsProperties.getPrivateKeyPath(),efpsProperties.getPassword(), paramMapBody);//签名
			headers.put("x-efps-sign", sign);
			headers.put("x-efps-sign-no", efpsProperties.getSignNo());
		}catch (Exception e) {
			// TODO: handle exception
			throw new RestException(401, "通道签名异常!");
		}
		log.info("易票联请求报文-protocolPayPre:[request={}]",paramMapBody);
		String result = HttpRequest.post(efpsProperties.getUrl()+"/api/txs/protocol/protocolPayPre")
				.addHeaders(headers)
				.body(paramMapBody)
				.execute()
				.body();
		log.info("易票联返回报文-protocolPayPre:[response={}]",result);
		Map<String,Object> responseMap =JSON.parseObject(result,Map.class);
		if("0000".equals(responseMap.get("returnCode"))) {
			if(responseMap.get("token") == null) {
				return "";
			}else {
				return String.valueOf(responseMap.get("token"));
			}
			//return String.valueOf();
		}else {
			throw new RestException(501, String.valueOf(responseMap.get("returnMsg")));
		}
	}
	
	/**
	 * 交易确认
	 * @param token
	 * @param memberId
	 * @param protocol
	 * @param smsCode
	 */
	public void protocolPayConfirm(String token,String memberId,String protocol,String smsCode) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerCode", efpsProperties.getCustomerCode());
		paramMap.put("version", "2.0");
		paramMap.put("token", token);
		paramMap.put("memberId", memberId);
		paramMap.put("protocol", protocol);
		paramMap.put("smsCode", smsCode);
		paramMap.put("nonceStr", RandomUtil.randomNumbers(10));
		String paramMapBody  = JSONObject.toJSONString(paramMap);
		Map<String, String> headers = new HashMap<String, String>();
		try {
			String sign = RsaUtils.sign(efpsProperties.getPrivateKeyPath(),efpsProperties.getPassword(), paramMapBody);//签名
			headers.put("x-efps-sign", sign);
			headers.put("x-efps-sign-no", efpsProperties.getSignNo());
		}catch (Exception e) {
			// TODO: handle exception
			throw new RestException(401, "通道签名异常!");
		}
		log.info("易票联请求报文-protocolPayConfirm:[request={}]",paramMapBody);
		String result = HttpRequest.post(efpsProperties.getUrl()+"/api/txs/protocol/protocolPayConfirm")
				.addHeaders(headers)
				.body(paramMapBody)
				.execute()
				.body();
		log.info("易票联返回报文-protocolPayConfirm:[response={}]",result);
		Map<String,Object> responseMap =JSON.parseObject(result,Map.class);
		if("0000".equals(responseMap.get("returnCode"))) {
			//return String.valueOf();
		}else {
			throw new RestException(501, String.valueOf(responseMap.get("returnMsg")));
		}
	}

	
	/**
	 * 交易查询
	 * @param outTradeNo
	 * @return
	 */
	public int paymentQuery(String outTradeNo) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerCode", efpsProperties.getCustomerCode());
		paramMap.put("outTradeNo", outTradeNo);
		paramMap.put("nonceStr", RandomUtil.randomNumbers(10));
		String paramMapBody  = JSONObject.toJSONString(paramMap);
		Map<String, String> headers = new HashMap<String, String>();
		try {
			String sign = RsaUtils.sign(efpsProperties.getPrivateKeyPath(),efpsProperties.getPassword(), paramMapBody);//签名
			headers.put("x-efps-sign", sign);
			headers.put("x-efps-sign-no", efpsProperties.getSignNo());
		}catch (Exception e) {
			// TODO: handle exception
			throw new RestException(401, "通道签名异常!");
		}
		log.info("易票联请求报文-paymentQuery:[request={}]",paramMapBody);
		String result = HttpRequest.post(efpsProperties.getUrl()+"/api/txs/pay/PaymentQuery")
				.addHeaders(headers)
				.body(paramMapBody)
				.execute()
				.body();
		log.info("易票联返回报文-paymentQuery:[response={}]",result);
		Map<String,Object> responseMap =JSON.parseObject(result,Map.class);
		if("0000".equals(responseMap.get("returnCode"))) {
			int orderStatus = 1;
			String payState = String.valueOf(responseMap.get("payState"));
			if("00".equals(payState)) {
				orderStatus = 2;
			}else if("01".equals(payState)) {
				orderStatus = 3;
			}else {
				orderStatus = 1;
			}
			return orderStatus;
		}else {
			throw new RestException(501, String.valueOf(responseMap.get("returnMsg")));
		}
	}
	
	
	/**
	 * 分账
	 * @throws Exception
	 */
	public static void splitOrder() throws Exception{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerCode", "5651300003039000");
		paramMap.put("version", "2.0");
		paramMap.put("outTradeNo", "20190708007");
		paramMap.put("nonceStr", "1234567890");
		List<SplitInfo> splitInfoList = new ArrayList<SplitInfo>();
		splitInfoList.add(new SplitInfo("5651300003039000",30,1));
        splitInfoList.add(new SplitInfo("5651300003063761",70));
		paramMap.put("splitInfoList", splitInfoList);
		paramMap.put("notifyUrl", "www.test.com");
		paramMap.put("nonceStr", "1234567890");
		String paramMapBody  = JSONObject.toJSONString(paramMap);
		String sign = RsaUtils.sign(privateKeyPath,"Epaylinks@EFPS2018", paramMapBody);//签名
		System.out.println(paramMapBody);
		System.out.println(sign);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("x-efps-sign", sign);
		headers.put("x-efps-sign-no", "20190115yhys01");
		String result = HttpRequest.post("http://test-efps.epaylinks.cn/api/txs/pay/SplitOrder")
				.addHeaders(headers)
				.body(paramMapBody)
				.execute()
				.body();
		System.out.println(result);
	}
	
	
	 /**
	  * 余额查询
	  * @param memberId
	  * @return
	  */
	public String subCustomerAccountQuery(String memberId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerCode", memberId); //分账子商户编号
		paramMap.put("nonceStr", RandomUtil.randomNumbers(10));
		String paramMapBody  = JSONObject.toJSONString(paramMap);
		Map<String, String> headers = new HashMap<String, String>();
		try {
			String sign = RsaUtils.sign(efpsProperties.getPrivateKeyPath(),efpsProperties.getPassword(), paramMapBody);//签名
			headers.put("x-efps-sign", sign);
			headers.put("x-efps-sign-no", efpsProperties.getSignNo());
		}catch (Exception e) {
			// TODO: handle exception
			throw new RestException(401, "通道签名异常!");
		}
		log.info("易票联请求报文-subCustomerAccountQuery:[request={}]",paramMapBody);
		String result = HttpRequest.post(efpsProperties.getUrl()+"/api/acc/account/SubCustomerAccountQuery")
				.addHeaders(headers)
				.body(paramMapBody)
				.execute()
				.body();
		log.info("易票联请求报文-subCustomerAccountQuery:[response={}]",result);
		Map<String,Object> responseMap =JSON.parseObject(result,Map.class);
		if("0000".equals(responseMap.get("returnCode"))) {
			return String.valueOf(responseMap.get("availableBalance"));
		}else {
			throw new RestException(501, String.valueOf(responseMap.get("returnMsg")));
		}
	}
	
	
	/**
	 * 提现
	 * @param outTradeNo
	 * @param memberId
	 * @param protocol
	 * @param payAmount
	 * @param serviceFee
	 * @throws Exception
	 */
	public void withdrawalForSubMerchant(String outTradeNo,String memberId,String protocol,Long payAmount,Long serviceFee){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerCode", efpsProperties.getCustomerCode()); 
		paramMap.put("version", "2.0");
		paramMap.put("outTradeNo", outTradeNo);
		paramMap.put("memberId", memberId);
		paramMap.put("protocol", protocol);
		paramMap.put("payAmount", payAmount);
		if(efpsProperties.getIsServiceFee()) {
			paramMap.put("serviceFee", serviceFee-efpsProperties.getCashRate());
		}else {
			paramMap.put("serviceFee", 0);
		}
		paramMap.put("payCurrency", "CNY");
		//paramMap.put("notifyUrl", efpsProperties.getMchCashNotifyUrl());
		paramMap.put("nonceStr", RandomUtil.randomNumbers(10));
		String paramMapBody  = JSONObject.toJSONString(paramMap);
		Map<String, String> headers = new HashMap<String, String>();
		try {
			String sign = RsaUtils.sign(efpsProperties.getPrivateKeyPath(),efpsProperties.getPassword(), paramMapBody);//签名
			headers.put("x-efps-sign", sign);
			headers.put("x-efps-sign-no", efpsProperties.getSignNo());
		}catch (Exception e) {
			// TODO: handle exception
			throw new RestException(401, "通道签名异常!");
		}
		log.info("易票联请求报文-withdrawalForSubMerchant:[request={}]",paramMapBody);
		String result = HttpRequest.post(efpsProperties.getUrl()+"/api/txs/pay/WithdrawalForSubMerchant")
				.addHeaders(headers)
				.body(paramMapBody)
				.execute()
				.body();
		log.info("易票联请求报文-withdrawalForSubMerchant:[response={}]",result);
		Map<String,Object> responseMap =JSON.parseObject(result,Map.class);
		if("0000".equals(responseMap.get("returnCode"))) {
			
		}else {
			throw new RestException(501, String.valueOf(responseMap.get("returnMsg")));
		}
	}
	
	/**
	 * 提现查询
	 * @throws Exception
	 */
	public int withdrawalQueryForSubMerchant(String memberId,String outTradeNo){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerCode", efpsProperties.getCustomerCode()); 
		paramMap.put("memberId", memberId);
		paramMap.put("version", "2.0");
		paramMap.put("outTradeNo", outTradeNo);
		paramMap.put("nonceStr", RandomUtil.randomNumbers(10));
		String paramMapBody  = JSONObject.toJSONString(paramMap);
		Map<String, String> headers = new HashMap<String, String>();
		try {
			String sign = RsaUtils.sign(efpsProperties.getPrivateKeyPath(),efpsProperties.getPassword(), paramMapBody);//签名
			headers.put("x-efps-sign", sign);
			headers.put("x-efps-sign-no", efpsProperties.getSignNo());
		}catch (Exception e) {
			// TODO: handle exception
			throw new RestException(401, "通道签名异常!");
		}
		log.info("易票联请求报文-withdrawalQueryForSubMerchant:[request={}]",paramMapBody);
		String result = HttpRequest.post(efpsProperties.getUrl()+"/api/txs/pay/WithdrawalQueryForSubMerchant")
				.addHeaders(headers)
				.body(paramMapBody)
				.execute()
				.body();
		log.info("易票联请求报文-withdrawalQueryForSubMerchant:[response={}]",result);
		Map<String,Object> responseMap =JSON.parseObject(result,Map.class);
		if("0000".equals(responseMap.get("returnCode"))) {
			int cashStatus = 1;
			String payState = String.valueOf(responseMap.get("payState"));
			if("00".equals(payState)) {
				cashStatus = 2;
			}else if("01".equals(payState)) {
				cashStatus = 3;
			}else {
				cashStatus = 1;
			}
			return cashStatus;
		}else {
			throw new RestException(501, String.valueOf(responseMap.get("returnMsg")));
		}
	}
}
