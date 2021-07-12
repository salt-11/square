package cn.hawy.quick.modular.api.channel;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hawy.quick.config.properties.PafProperties;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.util.PayUtil;
import cn.hawy.quick.modular.api.channel.paf.PafResponseDto;
import cn.hawy.quick.modular.api.channel.paf.PafResultDto;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

@Service
public class PafChannel {


	@Autowired
	PafProperties pafProperties;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public static void main(String[] args) {
		System.out.println();
	}


	public Map<String,String> fastpayOpenToken(String idcard,String accountNumber,String holderName,String tel,String bizOrderNumber,String frontUrl){
		HashMap<String, Object> contentMap = new HashMap<>();
		contentMap.put("encryptId", pafProperties.getEncryptId());
		contentMap.put("apiVersion", "1");
		contentMap.put("txnDate", System.currentTimeMillis());

		contentMap.put("method", "fastpayOpenToken2");
		contentMap.put("mid", pafProperties.getMid());
		contentMap.put("idcard", idcard);
		contentMap.put("agencyType", "paf");
		contentMap.put("accountNumber", accountNumber);
		contentMap.put("holderName", holderName);
		contentMap.put("tel", tel);
		contentMap.put("bizOrderNumber", bizOrderNumber);
		contentMap.put("frontUrl", frontUrl);
		contentMap.put("notifyUrl", pafProperties.getBindCardNotify());

		LinkedHashMap<String, Object> paramMap = new LinkedHashMap<>();
		paramMap.put("content", JSONUtil.toJsonStr(contentMap));
		paramMap.put("key", pafProperties.getPublicKey());
		String sign = SecureUtil.md5(JSONUtil.toJsonStr(paramMap));
		paramMap.put("sign", sign);
		paramMap.remove("key");
		log.info("PAF请求报文-fastpayPrecreate:[request={}]", JSONUtil.toJsonStr(paramMap));
		String result= HttpUtil.post("https://www.dh0102.com/oss-transaction/gateway/fastpayOpenToken2", JSONUtil.toJsonStr(paramMap));
		log.info("PAF返回报文-fastpayTransferCreate:[response={}]", result);
		PafResponseDto pafResponse = JSONUtil.toBean(result, PafResponseDto.class);
		LinkedHashMap<String, Object> signMap = new LinkedHashMap<>();
		signMap.put("key", pafProperties.getPublicKey());
		signMap.put("result", pafResponse.getResult());
		String checkSign = SecureUtil.md5(JSONUtil.toJsonStr(signMap));
		if(checkSign.equals(pafResponse.getSign())) {
			Map<String,String> resMap = new HashMap<String,String>();
			PafResultDto pafResult = JSONUtil.toBean(pafResponse.getResult(), PafResultDto.class);
			if(pafResult.getCode().equals("000000")){
				JSONObject dataRes = (JSONObject)pafResult.getData();
				if(dataRes.getStr("isSign").equals("t")){
					resMap.put("sign_code", "2");
					resMap.put("form", "");
				}else if(dataRes.getStr("isSign").equals("f")){
					resMap.put("sign_code", "1");
					resMap.put("form", dataRes.getStr("openUrl"));
				}else{
					throw new RestException(501, "通道绑卡状态错误!");
				}
			}else{
				resMap.put("orderStatus", "3");
				resMap.put("returnMsg", pafResult.getMessage());
			}
			return resMap;
		}else {
			throw new RestException(501, "通道延签失败!");
		}
	}


	public Map<String,String> fastpayPrecreate(String bizOrderNumber,String srcAmt,String accountNumber,String tel,String fastpayFee,String holderName,String idcard,String cvv2,String expired,String city,String mcc) {
		HashMap<String, Object> contentMap = new HashMap<>();
		contentMap.put("encryptId", pafProperties.getEncryptId());
		contentMap.put("apiVersion", "1");
		contentMap.put("txnDate", System.currentTimeMillis());

		contentMap.put("method", "fastpayPrecreate2");
		contentMap.put("mid", pafProperties.getMid());
		contentMap.put("bizOrderNumber", bizOrderNumber);
		contentMap.put("srcAmt", PayUtil.transFenToYuan(srcAmt));
		contentMap.put("notifyUrl", pafProperties.getOrderNotify());
		contentMap.put("accountNumber", accountNumber);
		contentMap.put("tel", tel);
		contentMap.put("fastpayFee", PayUtil.transFenToYuan(fastpayFee));
		contentMap.put("feeType", "1");
		contentMap.put("agencyType", "paf");
		contentMap.put("holderName", holderName);
		contentMap.put("idcard", idcard);
		contentMap.put("settAccountNumber", "");
		contentMap.put("settAccountTel", "");
		contentMap.put("cvv2", cvv2);
		contentMap.put("expired", expired);
		contentMap.put("city", city);
		contentMap.put("mcc", mcc);

		LinkedHashMap<String, Object> paramMap = new LinkedHashMap<>();
		paramMap.put("content", JSONUtil.toJsonStr(contentMap));
		paramMap.put("key", pafProperties.getPublicKey());
		String sign = SecureUtil.md5(JSONUtil.toJsonStr(paramMap));
		paramMap.put("sign", sign);
		paramMap.remove("key");
		log.info("PAF请求报文-fastpayPrecreate:[request={}]", JSONUtil.toJsonStr(paramMap));
		String result= HttpUtil.post("https://www.dh0102.com/oss-transaction/gateway/fastpayPrecreate2", JSONUtil.toJsonStr(paramMap));
		log.info("PAF返回报文-fastpayTransferCreate:[response={}]", result);
		PafResponseDto pafResponse = JSONUtil.toBean(result, PafResponseDto.class);
		LinkedHashMap<String, Object> signMap = new LinkedHashMap<>();
		signMap.put("key", pafProperties.getPublicKey());
		signMap.put("result", pafResponse.getResult());
		String checkSign = SecureUtil.md5(JSONUtil.toJsonStr(signMap));
		if(checkSign.equals(pafResponse.getSign())) {
			Map<String,String> resMap = new HashMap<String,String>();
			PafResultDto pafResult = JSONUtil.toBean(pafResponse.getResult(), PafResultDto.class);
			if(pafResult.getCode().equals("000000")){
				JSONObject dataRes = (JSONObject)pafResult.getData();
				if("p".equals(dataRes.getStr("txnStatus"))) {
					resMap.put("orderStatus", "1");
				}else if("s".equals(dataRes.getStr("txnStatus"))) {
					resMap.put("orderStatus", "2");
					resMap.put("returnMsg", "交易成功");
				}else if("c".equals(dataRes.getStr("txnStatus"))) {
					resMap.put("orderStatus", "3");
					resMap.put("returnMsg", "交易失败");
				}else {
					resMap.put("orderStatus", "1");
				}
	        }else{
	        	resMap.put("orderStatus", "3");
				resMap.put("returnMsg", pafResult.getMessage());
	        }
			return resMap;
		}else {
			throw new RestException(501, "通道延签失败!");
		}
	}

	public Map<String,String> fastpayQuery(String bizOrderNumber) {
		HashMap<String, Object> contentMap = new HashMap<>();
		contentMap.put("encryptId", pafProperties.getEncryptId());
		contentMap.put("apiVersion", "1");
		contentMap.put("txnDate", System.currentTimeMillis());

		contentMap.put("method", "fastpayQuery");
		contentMap.put("mid", pafProperties.getMid());
		contentMap.put("bizOrderNumber", bizOrderNumber);

		LinkedHashMap<String, Object> paramMap = new LinkedHashMap<>();
		paramMap.put("content", JSONUtil.toJsonStr(contentMap));
		paramMap.put("key", pafProperties.getPublicKey());
		String sign = SecureUtil.md5(JSONUtil.toJsonStr(paramMap));
		paramMap.put("sign", sign);
		paramMap.remove("key");
		log.info("PAF请求报文-fastpayQuery:[request={}]", JSONUtil.toJsonStr(paramMap));
		String result= HttpUtil.post("https://www.dh0102.com/oss-transaction/gateway/fastpayQuery", JSONUtil.toJsonStr(paramMap));
		log.info("PAF返回报文-fastpayTransferCreate:[response={}]", result);
		PafResponseDto pafResponse = JSONUtil.toBean(result, PafResponseDto.class);
		LinkedHashMap<String, Object> signMap = new LinkedHashMap<>();
		signMap.put("key", pafProperties.getPublicKey());
		signMap.put("result", pafResponse.getResult());
		String checkSign = SecureUtil.md5(JSONUtil.toJsonStr(signMap));
		if(checkSign.equals(pafResponse.getSign())) {
			Map<String,String> resMap = new HashMap<String,String>();
			PafResultDto pafResult = JSONUtil.toBean(pafResponse.getResult(), PafResultDto.class);
			if(pafResult.getCode().equals("000000")){
				JSONObject dataRes = (JSONObject)pafResult.getData();
				if("p".equals(dataRes.getStr("txnStatus"))) {
					resMap.put("orderStatus", "1");
				}else if("s".equals(dataRes.getStr("txnStatus"))) {
					resMap.put("orderStatus", "2");
					resMap.put("returnMsg", "交易成功");
				}else if("c".equals(dataRes.getStr("txnStatus"))) {
					resMap.put("orderStatus", "3");
					resMap.put("returnMsg", "交易失败");
				}else {
					resMap.put("orderStatus", "1");
				}
	        }else{
	        	resMap.put("orderStatus", "3");
				resMap.put("returnMsg", pafResult.getMessage());
	        }
			return resMap;
		}else {
			throw new RestException(501, "通道延签失败!");
		}
	}



	public Map<String,String> fastpayTransferCreate(String bizOrderNumber,String accountNumber,String extraFee,String srcAmt,String idcard,String holderName,String tel) {
		HashMap<String, Object> contentMap = new HashMap<>();
		contentMap.put("encryptId", pafProperties.getEncryptId());
		contentMap.put("apiVersion", "1");
		contentMap.put("txnDate", System.currentTimeMillis());
		contentMap.put("method", "fastpayTransferCreate");

		contentMap.put("mid", pafProperties.getMid());
		contentMap.put("bizOrderNumber", bizOrderNumber);
		contentMap.put("notifyUrl", pafProperties.getWithdrawNotify());
		contentMap.put("accountNumber", accountNumber);
		contentMap.put("extraFee", PayUtil.transFenToYuan(extraFee));
		contentMap.put("srcAmt", PayUtil.transFenToYuan(srcAmt));
		contentMap.put("idcard", idcard);
		contentMap.put("holderName", holderName);
		contentMap.put("tel", tel);
		contentMap.put("agencyType", "paf");

		LinkedHashMap<String, Object> paramMap = new LinkedHashMap<>();
		paramMap.put("content", JSONUtil.toJsonStr(contentMap));
		paramMap.put("key", pafProperties.getPublicKey());
		String sign = SecureUtil.md5(JSONUtil.toJsonStr(paramMap));
		paramMap.put("sign", sign);
		paramMap.remove("key");
		log.info("PAF请求报文-fastpayTransferCreate:[request={}]", JSONUtil.toJsonStr(paramMap));
		String result= HttpUtil.post("https://www.dh0102.com/oss-transaction/gateway/fastpayTransferCreate", JSONUtil.toJsonStr(paramMap));
		log.info("PAF返回报文-fastpayTransferCreate:[response={}]", result);
		PafResponseDto pafResponse = JSONUtil.toBean(result, PafResponseDto.class);
		LinkedHashMap<String, Object> signMap = new LinkedHashMap<>();
		signMap.put("key", pafProperties.getPublicKey());
		signMap.put("result", pafResponse.getResult());
		String checkSign = SecureUtil.md5(JSONUtil.toJsonStr(signMap));
		if(checkSign.equals(pafResponse.getSign())) {
			Map<String,String> resMap = new HashMap<String,String>();
			PafResultDto pafResult = JSONUtil.toBean(pafResponse.getResult(), PafResultDto.class);
			if(pafResult.getCode().equals("000000")){
				JSONObject dataRes = (JSONObject)pafResult.getData();
				if("p".equals(dataRes.getStr("txnStatus"))) {
					resMap.put("orderStatus", "1");
				}else if("s".equals(dataRes.getStr("txnStatus"))) {
					resMap.put("orderStatus", "2");
					resMap.put("returnMsg", "交易成功");
				}else if("c".equals(dataRes.getStr("txnStatus"))) {
					resMap.put("orderStatus", "3");
					resMap.put("returnMsg", "交易失败");
				}else {
					resMap.put("orderStatus", "1");
				}
	        }else{
	        	resMap.put("orderStatus", "3");
				resMap.put("returnMsg", pafResult.getMessage());
	        }
			return resMap;
		}else {
			throw new RestException(501, "通道延签失败!");
		}
	}

	public Map<String,String> fastpayTransferQuery(String bizOrderNumber) {
		HashMap<String, Object> contentMap = new HashMap<>();
		contentMap.put("encryptId", pafProperties.getEncryptId());
		contentMap.put("apiVersion", "1");
		contentMap.put("txnDate", System.currentTimeMillis());
		contentMap.put("method", "fastpayTransferQuery");
		contentMap.put("mid", pafProperties.getMid());
		contentMap.put("bizOrderNumber", bizOrderNumber);

		LinkedHashMap<String, Object> paramMap = new LinkedHashMap<>();
		paramMap.put("content", JSONUtil.toJsonStr(contentMap));
		paramMap.put("key", pafProperties.getPublicKey());
		String sign = SecureUtil.md5(JSONUtil.toJsonStr(paramMap));
		paramMap.put("sign", sign);
		paramMap.remove("key");
		log.info("PAF请求报文-fastpayTransferQuery:[request={}]", JSONUtil.toJsonStr(paramMap));
		String result= HttpUtil.post("https://www.dh0102.com/oss-transaction/gateway/fastpayTransferQuery", JSONUtil.toJsonStr(paramMap));
		log.info("PAF返回报文-fastpayTransferQuery:[response={}]", result);
		PafResponseDto pafResponse = JSONUtil.toBean(result, PafResponseDto.class);
		LinkedHashMap<String, Object> signMap = new LinkedHashMap<>();
		signMap.put("key", pafProperties.getPublicKey());
		signMap.put("result", pafResponse.getResult());
		String checkSign = SecureUtil.md5(JSONUtil.toJsonStr(signMap));
		if(checkSign.equals(pafResponse.getSign())) {
			Map<String,String> resMap = new HashMap<String,String>();
			PafResultDto pafResult = JSONUtil.toBean(pafResponse.getResult(), PafResultDto.class);
			if(pafResult.getCode().equals("000000")){
				JSONObject dataRes = (JSONObject)pafResult.getData();
				if("p".equals(dataRes.getStr("txnStatus"))) {
					resMap.put("orderStatus", "1");
				}else if("s".equals(dataRes.getStr("txnStatus"))) {
					resMap.put("orderStatus", "2");
					resMap.put("returnMsg", "交易成功");
				}else if("c".equals(dataRes.getStr("txnStatus"))) {
					resMap.put("orderStatus", "3");
					resMap.put("returnMsg", dataRes.getStr("dataMessage"));
				}else {
					resMap.put("orderStatus", "1");
				}
	        }else{
	        	resMap.put("orderStatus", "3");
				resMap.put("returnMsg", pafResult.getMessage());
	        }
			return resMap;
		}else {
			throw new RestException(501, "通道延签失败!");
		}
	}


	public String fastpayTransferBalanceQuery(String idcard) {
		HashMap<String, Object> contentMap = new HashMap<>();
		//通用请求参数
		contentMap.put("encryptId", pafProperties.getEncryptId());
		contentMap.put("apiVersion", "1");
		contentMap.put("txnDate", System.currentTimeMillis());
		contentMap.put("method", "fastpayTransferBalanceQuery");
		//业务请求参数
		contentMap.put("mid", pafProperties.getMid());
		contentMap.put("idcard", idcard);
		contentMap.put("agencyType", "paf");

		LinkedHashMap<String, Object> paramMap = new LinkedHashMap<>();
		paramMap.put("content", JSONUtil.toJsonStr(contentMap));
		paramMap.put("key", pafProperties.getPublicKey());
		String sign = SecureUtil.md5(JSONUtil.toJsonStr(paramMap));
		paramMap.put("sign", sign);
		paramMap.remove("key");
		log.info("PAF请求报文-fastpayTransferBalanceQuery:[request={}]", JSONUtil.toJsonStr(paramMap));
		String result= HttpUtil.post("https://www.dh0102.com/oss-transaction/gateway/fastpayTransferBalanceQuery", JSONUtil.toJsonStr(paramMap));
		log.info("PAF返回报文-fastpayTransferBalanceQuery:[response={}]", result);
		PafResponseDto pafResponse = JSONUtil.toBean(result, PafResponseDto.class);
		LinkedHashMap<String, Object> signMap = new LinkedHashMap<>();
		signMap.put("key", pafProperties.getPublicKey());
		signMap.put("result", pafResponse.getResult());
		String checkSign = SecureUtil.md5(JSONUtil.toJsonStr(signMap));
		if(checkSign.equals(pafResponse.getSign())) {
			PafResultDto pafResult = JSONUtil.toBean(pafResponse.getResult(), PafResultDto.class);
			if(pafResult.getCode().equals("000000")){
				if(pafResult.getData().equals(-1)) {
					return String.valueOf("0");
				}else {
					return String.valueOf(pafResult.getData());
				}
	        }else{
	            throw new RestException(501, pafResult.getMessage());
	        }
		}else {
			throw new RestException(501, "通道延签失败!");
		}
	}
}
