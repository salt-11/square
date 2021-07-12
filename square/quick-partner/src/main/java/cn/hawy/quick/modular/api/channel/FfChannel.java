package cn.hawy.quick.modular.api.channel;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hawy.quick.config.properties.FfProperties;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.util.PayUtil;
import cn.hawy.quick.modular.api.channel.ff.FfResponseDto;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;


@Service
public class FfChannel {
	
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	FfProperties ffProperties;
	
	public static void main(String[] args) {
		//sendSmsOpenToken();
		//checkSmsOpenToken();
		//queryOpenToken();
		//queryUserBanlance();
	}
	
	public Map<String,String> sendSmsOpenToken(String accountNumber,String tel,String holderName,String idcard,String cvv2,String expired,String merOrderNumber) {
		HashMap<String, Object> contentMap = new HashMap<>();
		contentMap.put("apiVersion", "1");
		contentMap.put("curDate", System.currentTimeMillis());
		contentMap.put("method", "sendSms2OpenToken");
		contentMap.put("merchantId", ffProperties.getMerchantId());
		contentMap.put("accountNumber", accountNumber);
		contentMap.put("tel", tel);
		contentMap.put("channelType", "ffkj");
		contentMap.put("holderName", holderName);
		contentMap.put("idcard", idcard);
		contentMap.put("cvv2", cvv2);
		contentMap.put("expired", expired);
		contentMap.put("merOrderNumber", merOrderNumber); 
		log.info("FF请求报文-sendSmsOpenToken:[request={}]", JSONUtil.toJsonStr(contentMap));
		LinkedHashMap<String, Object> paramMap = signMap(contentMap);
		log.info("FF请求报文-sendSmsOpenToken:[request={}]", JSONUtil.toJsonStr(paramMap));
		String result= HttpUtil.post(ffProperties.getUrl()+"/sendSms2OpenToken", paramMap);
		log.info("FF返回报文-sendSmsOpenToken:[response={}]", result);
		HashMap<String, String> resMap = form2Map(result);
		boolean flag = checkSign(resMap);
		if(flag) {
			if(resMap.get("code").equals("00")) {
				String dataDecrypt = rsaDecrypt(resMap.get("data"));
				log.info("FF返回报文-sendSmsOpenToken:[response={}]", dataDecrypt);
				FfResponseDto ffResponseDto = JSONUtil.toBean(dataDecrypt, FfResponseDto.class);
				Map<String,String> respMap = new HashMap<String,String>();
				if(ffResponseDto.getCode().equals("00")) {
					respMap.put("signCode", "1");
				}else if(ffResponseDto.getCode().equals("02")) {
					respMap.put("signCode", "2");
				}else {
					throw new RestException(501, ffResponseDto.getMessage());
				}
				return respMap;
			}else {
				throw new RestException(501, resMap.get("message"));
			}
		}else {
			throw new RestException(501, "通道延签失败!");
		}
	}
	
	public boolean checkSmsOpenToken(String accountNumber,String tel,String holderName,String idcard,String cvv2,String expired,String merOrderNumber,String smsCode) {
		HashMap<String, Object> contentMap = new HashMap<>();
		contentMap.put("apiVersion", "1");
		contentMap.put("curDate", System.currentTimeMillis());
		contentMap.put("method", "checkSms2OpenToken");
		contentMap.put("merchantId", ffProperties.getMerchantId());
		contentMap.put("accountNumber", accountNumber);
		contentMap.put("tel", tel);
		contentMap.put("channelType", "ffkj");
		contentMap.put("holderName", holderName);
		contentMap.put("idcard", idcard);
		contentMap.put("cvv2", cvv2);
		contentMap.put("expired", expired);
		contentMap.put("merOrderNumber", merOrderNumber);
		contentMap.put("smsCode", smsCode);
		log.info("FF请求报文-checkSmsOpenToken:[request={}]", JSONUtil.toJsonStr(contentMap));
		LinkedHashMap<String, Object> paramMap = signMap(contentMap);
		log.info("FF请求报文-checkSmsOpenToken:[request={}]", JSONUtil.toJsonStr(paramMap));
		String result= HttpUtil.post(ffProperties.getUrl()+"/checkSms2OpenToken", paramMap);
		log.info("FF返回报文-checkSmsOpenToken:[response={}]", result);
		HashMap<String, String> resultMap = form2Map(result);
		boolean flag = checkSign(resultMap);
		if(flag) {
			if(resultMap.get("code").equals("00")) {
				String dataDecrypt = rsaDecrypt(resultMap.get("data"));
				log.info("FF返回报文-checkSmsOpenToken:[response={}]", dataDecrypt);
				FfResponseDto ffResponseDto = JSONUtil.toBean(dataDecrypt, FfResponseDto.class);
				if(ffResponseDto.getCode().equals("00")) {
					return true;
				}else {
					throw new RestException(501, ffResponseDto.getMessage());
				}
			}else {
				throw new RestException(501, resultMap.get("message"));
			}
		}else {
			throw new RestException(501, "通道延签失败!");
		}
	}
	
	public boolean queryOpenToken() {
		HashMap<String, Object> contentMap = new HashMap<>();
		contentMap.put("apiVersion", "1");
		contentMap.put("curDate", System.currentTimeMillis());
		contentMap.put("method", "queryOpenToken");
		contentMap.put("merchantId", ffProperties.getMerchantId());
		contentMap.put("accountNumber", "6225768721583068");
		contentMap.put("channelType", "ffkj");
		contentMap.put("idcard", "429001198911125912");
		LinkedHashMap<String, Object> paramMap = signMap(contentMap);
		System.out.println(JSONUtil.toJsonStr(paramMap));
		String result= HttpUtil.post("https://47.92.100.165/service/gateway/queryOpenToken", paramMap);
		System.out.println(result);
		HashMap<String, String> resultMap = form2Map(result);
		boolean flag = checkSign(resultMap);
		if(flag) {
			if(resultMap.get("code").equals("00")) {
				String dataDecrypt = rsaDecrypt(resultMap.get("data"));
				System.out.println(dataDecrypt);
				FfResponseDto ffResponseDto = JSONUtil.toBean(dataDecrypt, FfResponseDto.class);
				if(ffResponseDto.getCode().equals("00")) {
					return true;
				}else {
					throw new RestException(501, ffResponseDto.getMessage());
				}
			}else {
				throw new RestException(501, resultMap.get("message"));
			}
		}else {
			throw new RestException(501, "通道延签失败!");
		}
	}
	
	public String queryUserBanlance(String accountNumber) {
		HashMap<String, Object> contentMap = new HashMap<>();
		contentMap.put("apiVersion", "1");
		contentMap.put("curDate", System.currentTimeMillis());
		contentMap.put("method", "queryUserBanlance");
		contentMap.put("merchantId", ffProperties.getMerchantId());
		contentMap.put("accountNumber", accountNumber);
		contentMap.put("channelType", "ffkj");
		log.info("FF请求报文-queryUserBanlance:[request={}]", JSONUtil.toJsonStr(contentMap));
		LinkedHashMap<String, Object> paramMap = signMap(contentMap);
		log.info("FF请求报文-queryUserBanlance:[request={}]", JSONUtil.toJsonStr(paramMap));
		String result= HttpUtil.post(ffProperties.getUrl()+"/queryUserBanlance", paramMap);
		log.info("FF返回报文-queryUserBanlance:[response={}]", result);
		HashMap<String, String> resultMap = form2Map(result);
		boolean flag = checkSign(resultMap);
		if(flag) {
			if(resultMap.get("code").equals("00")) {
				String dataDecrypt = rsaDecrypt(resultMap.get("data"));
				log.info("FF返回报文-queryUserBanlance:[response={}]", dataDecrypt);
				FfResponseDto ffResponseDto = JSONUtil.toBean(dataDecrypt, FfResponseDto.class);
				if(ffResponseDto.getCode().equals("00")) {
					return String.valueOf(ffResponseDto.getContent());
				}else {
					throw new RestException(501, ffResponseDto.getMessage());
				}
			}else {
				throw new RestException(501, resultMap.get("message"));
			}
		}else {
			throw new RestException(501, "通道延签失败!");
		}
	}
	
	public Map<String,String> preOrderCreate(String merOrderNumber,String money,String accountNumber,String tel,String fee,String holderName,String idcard,String city,String cvv2,String expired,String mcc) {
		HashMap<String, Object> contentMap = new HashMap<>();
		contentMap.put("apiVersion", "1");
		contentMap.put("curDate", System.currentTimeMillis());
		contentMap.put("method", "preOrderCreate");
		contentMap.put("merchantId", ffProperties.getMerchantId());
		contentMap.put("money", PayUtil.transFenToYuan(money));
		contentMap.put("merOrderNumber", merOrderNumber);
		contentMap.put("accountNumber", accountNumber);
		contentMap.put("tel", tel);
		contentMap.put("fee", PayUtil.transYuanToFen(fee));
		contentMap.put("channelType", "ffkj");
		contentMap.put("holderName", holderName);
		contentMap.put("idcard", idcard);
		contentMap.put("city", city);
		contentMap.put("notifyUrl", ffProperties.getOrderNotify());
		contentMap.put("cvv2", cvv2);
		contentMap.put("expired", expired);
		contentMap.put("mcc", mcc);
		log.info("FF请求报文-preOrderCreate:[request={}]", JSONUtil.toJsonStr(contentMap));
		LinkedHashMap<String, Object> paramMap = signMap(contentMap);
		log.info("FF请求报文-preOrderCreate:[request={}]", JSONUtil.toJsonStr(paramMap));
		String result= HttpUtil.post(ffProperties.getUrl()+"/preOrderCreate", paramMap);
		log.info("FF返回报文-preOrderCreate:[response={}]", result);
		HashMap<String, String> resultMap = form2Map(result);
		boolean flag = checkSign(resultMap);
		if(flag) {
			Map<String,String> resMap = new HashMap<String,String>();
			if(resultMap.get("code").equals("00")) {
				String dataDecrypt = rsaDecrypt(resultMap.get("data"));
				log.info("FF返回报文-preOrderCreate:[response={}]", dataDecrypt);
				FfResponseDto ffResponseDto = JSONUtil.toBean(dataDecrypt, FfResponseDto.class);
				if(ffResponseDto.getCode().equals("00")) {
					JSONObject contentRes = (JSONObject)ffResponseDto.getContent();
					if("00".equals(contentRes.getStr("orderStatus"))) {
						resMap.put("orderStatus", "2");
						resMap.put("returnMsg", "交易成功");
					}else if("01".equals(contentRes.getStr("orderStatus"))) {
						resMap.put("orderStatus", "3");
						resMap.put("returnMsg", contentRes.getStr("orderDesc"));
					}else if("02".equals(contentRes.getStr("orderStatus"))) {
						resMap.put("orderStatus", "1");
					}else {
						resMap.put("orderStatus", "1");
					}
				}else {
					resMap.put("orderStatus", "3");
					resMap.put("returnMsg", ffResponseDto.getMessage());
				}
			}else {
				resMap.put("orderStatus", "3");
				resMap.put("returnMsg", resultMap.get("message"));
			}
			return resMap;
		}else {
			throw new RestException(501, "通道延签失败!");
		}
	}
	
	public Map<String,String> queryOrderStatus(String merOrderNumber) {
		HashMap<String, Object> contentMap = new HashMap<>();
		contentMap.put("apiVersion", "1");
		contentMap.put("curDate", System.currentTimeMillis());
		contentMap.put("method", "queryOrderStatus");
		contentMap.put("merchantId", ffProperties.getMerchantId());
		contentMap.put("merOrderNumber", merOrderNumber);
		log.info("FF请求报文-queryOrderStatus:[request={}]", JSONUtil.toJsonStr(contentMap));
		LinkedHashMap<String, Object> paramMap = signMap(contentMap);
		log.info("FF请求报文-queryOrderStatus:[request={}]", JSONUtil.toJsonStr(paramMap));
		String result= HttpUtil.post(ffProperties.getUrl()+"/queryOrderStatus", paramMap);
		log.info("FF返回报文-queryOrderStatus:[response={}]", result);
		HashMap<String, String> resultMap = form2Map(result);
		boolean flag = checkSign(resultMap);
		if(flag) {
			Map<String,String> resMap = new HashMap<String,String>();
			if(resultMap.get("code").equals("00")) {
				String dataDecrypt = rsaDecrypt(resultMap.get("data"));
				log.info("FF返回报文-queryOrderStatus:[response={}]", dataDecrypt);
				FfResponseDto ffResponseDto = JSONUtil.toBean(dataDecrypt, FfResponseDto.class);
				if(ffResponseDto.getCode().equals("00")) {
					JSONObject contentRes = (JSONObject)ffResponseDto.getContent();
					if("00".equals(contentRes.getStr("orderStatus"))) {
						resMap.put("orderStatus", "2");
						resMap.put("returnMsg", "交易成功");
					}else if("01".equals(contentRes.getStr("orderStatus"))) {
						resMap.put("orderStatus", "3");
						resMap.put("returnMsg", contentRes.getStr("orderDesc"));
					}else if("02".equals(contentRes.getStr("orderStatus"))) {
						resMap.put("orderStatus", "1");
					}else {
						resMap.put("orderStatus", "1");
					}
				}else {
					resMap.put("orderStatus", "3");
					resMap.put("returnMsg", ffResponseDto.getMessage());
				}
			}else {
				resMap.put("orderStatus", "3");
				resMap.put("returnMsg", resultMap.get("message"));
			}
			return resMap;
		}else {
			throw new RestException(501, "通道延签失败!");
		}
	}
	
	public Map<String,String> checkOutOrder(String merOrderNumber,String money,String accountNumber,String tel,String extraFee,String holderName,String idcard) {
		HashMap<String, Object> contentMap = new HashMap<>();
		contentMap.put("apiVersion", "1");
		contentMap.put("curDate", System.currentTimeMillis());
		contentMap.put("method", "checkOutOrder");
		contentMap.put("merchantId", ffProperties.getMerchantId());
		contentMap.put("money", PayUtil.transFenToYuan(money));
		contentMap.put("merOrderNumber", merOrderNumber);
		contentMap.put("accountNumber", accountNumber);
		contentMap.put("tel", tel);
		contentMap.put("extraFee", PayUtil.transFenToYuan(extraFee));
		contentMap.put("channelType", "ffkj");
		contentMap.put("holderName", holderName);
		contentMap.put("idcard", idcard);
		contentMap.put("notifyUrl", ffProperties.getWithdrawNotify());
		log.info("FF请求报文-checkOutOrder:[request={}]", JSONUtil.toJsonStr(contentMap));
		LinkedHashMap<String, Object> paramMap = signMap(contentMap);
		log.info("FF请求报文-checkOutOrder:[request={}]", JSONUtil.toJsonStr(paramMap));
		String result= HttpUtil.post(ffProperties.getUrl()+"/checkOutOrder", paramMap);
		log.info("FF返回报文-checkOutOrder:[response={}]", result);
		HashMap<String, String> resultMap = form2Map(result);
		boolean flag = checkSign(resultMap);
		if(flag) {
			Map<String,String> resMap = new HashMap<String,String>();
			if(resultMap.get("code").equals("00")) {
				String dataDecrypt = rsaDecrypt(resultMap.get("data"));
				log.info("FF返回报文-checkOutOrder:[response={}]", dataDecrypt);
				FfResponseDto ffResponseDto = JSONUtil.toBean(dataDecrypt, FfResponseDto.class);
				if(ffResponseDto.getCode().equals("00")) {
					JSONObject contentRes = (JSONObject)ffResponseDto.getContent();
					if("00".equals(contentRes.getStr("orderStatus"))) {
						resMap.put("orderStatus", "2");
						resMap.put("returnMsg", "交易成功");
					}else if("01".equals(contentRes.getStr("orderStatus"))) {
						resMap.put("orderStatus", "3");
						resMap.put("returnMsg", contentRes.getStr("orderDesc"));
					}else if("02".equals(contentRes.getStr("orderStatus"))) {
						resMap.put("orderStatus", "1");
					}else {
						resMap.put("orderStatus", "1");
					}
				}else {
					resMap.put("orderStatus", "3");
					resMap.put("returnMsg", ffResponseDto.getMessage());
				}
			}else {
				resMap.put("orderStatus", "3");
				resMap.put("returnMsg", resultMap.get("message"));
			}
			return resMap;
		}else {
			throw new RestException(501, "通道延签失败!");
		}
	}
	
	public Map<String,String> queryCheckOutOrderStatus(String merOrderNumber) {
		HashMap<String, Object> contentMap = new HashMap<>();
		contentMap.put("apiVersion", "1");
		contentMap.put("curDate", System.currentTimeMillis());
		contentMap.put("method", "queryCheckOutOrderStatus");
		contentMap.put("merchantId", ffProperties.getMerchantId());
		contentMap.put("merOrderNumber", merOrderNumber);
		log.info("FF请求报文-queryCheckOutOrderStatus:[request={}]", JSONUtil.toJsonStr(contentMap));
		LinkedHashMap<String, Object> paramMap = signMap(contentMap);
		log.info("FF请求报文-queryCheckOutOrderStatus:[request={}]", JSONUtil.toJsonStr(paramMap));
		String result= HttpUtil.post(ffProperties.getUrl()+"/queryCheckOutOrderStatus", paramMap);
		log.info("FF返回报文-queryCheckOutOrderStatus:[response={}]", result);
		HashMap<String, String> resultMap = form2Map(result);
		boolean flag = checkSign(resultMap);
		if(flag) {
			Map<String,String> resMap = new HashMap<String,String>();
			if(resultMap.get("code").equals("00")) {
				String dataDecrypt = rsaDecrypt(resultMap.get("data"));
				log.info("FF返回报文-queryCheckOutOrderStatus:[response={}]", dataDecrypt);
				FfResponseDto ffResponseDto = JSONUtil.toBean(dataDecrypt, FfResponseDto.class);
				if(ffResponseDto.getCode().equals("00")) {
					JSONObject contentRes = (JSONObject)ffResponseDto.getContent();
					if("00".equals(contentRes.getStr("orderStatus"))) {
						resMap.put("orderStatus", "2");
						resMap.put("returnMsg", "交易成功");
					}else if("01".equals(contentRes.getStr("orderStatus"))) {
						resMap.put("orderStatus", "3");
						resMap.put("returnMsg", contentRes.getStr("orderDesc"));
					}else if("02".equals(contentRes.getStr("orderStatus"))) {
						resMap.put("orderStatus", "1");
					}else {
						resMap.put("orderStatus", "1");
					}
				}else {
					resMap.put("orderStatus", "3");
					resMap.put("returnMsg", ffResponseDto.getMessage());
				}
			}else {
				resMap.put("orderStatus", "3");
				resMap.put("returnMsg", resultMap.get("message"));
			}
			return resMap;
		}else {
			throw new RestException(501, "通道延签失败!");
		}
	}
	
	public LinkedHashMap<String, Object> signMap(HashMap<String, Object> contentMap){
		LinkedHashMap<String, Object> paramMap = new LinkedHashMap<>();
		try {
			String contentStr = MapUtil.joinIgnoreNull(MapUtil.sort(contentMap), "&", "=");
			String contentEncrypt = rsaEncrypt(contentStr);
			paramMap.put("data", contentEncrypt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RestException(501, "通道加密异常");
		}
		paramMap.put("merchantId", ffProperties.getMerchantId());
		paramMap.put("key", ffProperties.getKey());
		String sign = SecureUtil.md5(MapUtil.joinIgnoreNull(paramMap, "&", "="));
		paramMap.put("sign", sign);
		paramMap.remove("key");
		return paramMap;
	}
	
	public boolean checkSign(HashMap<String, String> resMap) {
		LinkedHashMap<String, Object> signMap = new LinkedHashMap<>();
		signMap.put("code", resMap.get("code"));
		signMap.put("message", resMap.get("message"));
		signMap.put("data", resMap.get("data"));
		signMap.put("merchantId", resMap.get("merchantId"));
		signMap.put("key", ffProperties.getKey());
		String checkSignStr = MapUtil.joinIgnoreNull(signMap, "&", "=");
		String checkSign = SecureUtil.md5(checkSignStr);
		if(checkSign.equals(resMap.get("sign"))) {
			return true;
		}else {
			return false;
		}
	}
	
	public String rsaEncrypt(String contentStr) {
		String contentEncrypt = SecureUtil.rsa(ffProperties.getPrivateKey(), ffProperties.getCostPublicKey()).encryptBase64(contentStr, KeyType.PublicKey);
		return contentEncrypt;
	}
	
	public String rsaDecrypt(String data) {
		String dataDecrypt = SecureUtil.rsa(ffProperties.getPrivateKey(), ffProperties.getCostPublicKey()).decryptStr(data,KeyType.PrivateKey);
		return dataDecrypt;
	}
	
	
	public static HashMap<String, String> form2Map( String orderinfo) {
        String listinfo[];
        HashMap<String, String> map = new HashMap<String, String>();
        listinfo = orderinfo.split("&");
        for(String s : listinfo)
        {
            String list[]  = s.split("=",2);
            if(list.length>1)
            {
                map.put(list[0], list[1]);
            }
        }
        return map;
    }

}
