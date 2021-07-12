package cn.hawy.quick.modular.test;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hawy.quick.modular.test.util.HttpClient;
import cn.hawy.quick.modular.test.util.MD5;
import cn.hawy.quick.modular.test.vo.GetTokenResp;
import cn.hawy.quick.modular.test.vo.QueryReq;
import cn.hawy.quick.modular.test.vo.ResultMsg;
import cn.hawy.quick.modular.test.vo.SmsReq;
import cn.hawy.quick.modular.test.vo.VariantSmsReq;

/**
 * 短信服务相关方法
 */
public class SmsClient {
	
	/**
	 * 获取token信息
	 * @param custCode 客户账号
	 * @param serviceBaseUrl http://ip:port
	 * @return ResultMsg
	 */
	private ResultMsg getToken(String custCode, String serviceBaseUrl){
		ResultMsg resultMsg = new ResultMsg();
		try {
			//发送token请求
			QueryReq getTokenReq = new QueryReq();
			
			getTokenReq.setCust_code(custCode);
			
			String postData = JSON.toJSONString(getTokenReq);
			String getTokenResp = HttpClient.post(serviceBaseUrl + "/getToken", postData,
					"application/json", "utf-8");
			
			JSONObject jsonObject = JSON.parseObject(getTokenResp);
			
			if (!"failed".equals(jsonObject.getString("status"))) {//判断是否获取token信息成功
				resultMsg.setSuccess(true);
				resultMsg.setData(getTokenResp);
			} else {
				resultMsg.setSuccess(false);
				resultMsg.setCode(jsonObject.getString("respCode"));
				resultMsg.setMsg(jsonObject.getString("respMsg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg.setSuccess(false);
			resultMsg.setCode("1000");
			resultMsg.setMsg("服务器出现未知异常");
		}
		return resultMsg;
	}
	
	/**
	 * 发送短信
	 * @param smsReq SmsReq实体类
	 * @param password 客户密码
	 * @param serviceBaseUrl http://ip:port
	 * @return ResultMsg
	 */
	public ResultMsg sendSms(SmsReq smsReq, String password, String serviceBaseUrl){

		SmsClient smsClient = new SmsClient();
		ResultMsg resultMsg = smsClient.getToken(smsReq.getCust_code(), serviceBaseUrl);
		
		try {
			String sign = MD5.getMD5((smsReq.getContent() + password).getBytes("utf-8"));
			smsReq.setSign(sign);
			String postData = JSON.toJSONString(smsReq);
			String sendSmsResp = HttpClient.post(serviceBaseUrl + "/sendSms", postData, "application/json", "utf-8");
			
			JSONObject jsonObject = JSON.parseObject(sendSmsResp);
			if (!"failed".equals(jsonObject.getString("status"))) {//判断是否获取token信息成功
				resultMsg.setSuccess(true);
				resultMsg.setData(sendSmsResp);
			} else {
				resultMsg.setSuccess(false);
				resultMsg.setCode(jsonObject.getString("respCode"));
				resultMsg.setMsg(jsonObject.getString("respMsg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg.setSuccess(false);
			resultMsg.setCode("1000");
			resultMsg.setMsg("服务器出现未知异常");
		}
		return resultMsg;
	}
	
	/**
	 * 发送短信
	 * @param uid 			[选填] 业务标识，由贵司自定义32为数字透传至我司
	 * @param custCode 		[必填] 用户账号
	 * @param content 		[必填] 短信内容
	 * @param destMobiles 	[必填] 接收号码，同时发送给多个号码时,号码之间用英文半角逗号分隔(,)
	 * @param needReport 	[选填] 状态报告需求与否，是 yes 否 no 默认yes
	 * @param spCode 		[选填] 长号码
	 * @param msgFmt 		[选填] 信息格式，0：ASCII串；3：短信写卡操作；4：二进制信息；8：UCS2编码；默认8
	 * @param serviceBaseUrl 			[必填] http://ip:port
	 * @param password 		[必填] 账号密码
	 * @return ResultMsg
	 */
	public ResultMsg sendSms(String uid, String custCode, String content, String destMobiles,
			String needReport, String spCode, String msgFmt, String serviceBaseUrl,String password){

		SmsClient smsClient = new SmsClient();
		ResultMsg resultMsg = smsClient.getToken(custCode, serviceBaseUrl);
		try {
			SmsReq req = new SmsReq();
			req.setUid(uid);
			req.setCust_code(custCode);
			req.setContent(content);
			req.setDestMobiles(destMobiles);
			req.setNeed_report(needReport);
			req.setSp_code(spCode);
			req.setMsgFmt(msgFmt);
			
			String sign = MD5.getMD5((req.getContent() + password).getBytes("utf-8"));
			req.setSign(sign);
			String postData = JSON.toJSONString(req);
			String sendSmsResp = HttpClient.post(serviceBaseUrl + "/sendSms", postData, "application/json", "utf-8");
			
			JSONObject jsonObject = JSON.parseObject(sendSmsResp);
			if (!"failed".equals(jsonObject.getString("status"))) {//判断是否获取token信息成功
				resultMsg.setSuccess(true);
				resultMsg.setData(sendSmsResp);
			} else {
				resultMsg.setSuccess(false);
				resultMsg.setCode(jsonObject.getString("respCode"));
				resultMsg.setMsg(jsonObject.getString("respMsg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg.setSuccess(false);
			resultMsg.setCode("1000");
			resultMsg.setMsg("服务器出现未知异常");
		}
		return resultMsg;
	}
	
	
	/**
	 * 发送变量短信
	 * @param variantSmsReq VariantSmsReq实体类
	 * @param password 客户密码
	 * @param serviceBaseUrl http://ip:port
	 * @return ResultMsg
	 */
	public ResultMsg sendVariantSms(VariantSmsReq variantSmsReq, String serviceBaseUrl,String password){

		SmsClient smsClient = new SmsClient();
		ResultMsg resultMsg = smsClient.getToken(variantSmsReq.getCust_code(), serviceBaseUrl);
		try {
			String sign = MD5.getMD5((variantSmsReq.getContent() + password).getBytes("utf-8"));
			variantSmsReq.setSign(sign);
			String postData = JSON.toJSONString(variantSmsReq);
			String sendSmsResp = HttpClient.post(serviceBaseUrl + "/sendVariantSms", postData, "application/json", "utf-8");
			
			JSONObject jsonObject = JSON.parseObject(sendSmsResp);
			if (!"failed".equals(jsonObject.getString("status"))) {//判断是否获取token信息成功
				resultMsg.setSuccess(true);
				resultMsg.setData(sendSmsResp);
			} else {
				resultMsg.setSuccess(false);
				resultMsg.setCode(jsonObject.getString("respCode"));
				resultMsg.setMsg(jsonObject.getString("respMsg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg.setSuccess(false);
			resultMsg.setCode("1000");
			resultMsg.setMsg("服务器出现未知异常");
		}
		return resultMsg;
	}
	
	
	/**
	 * 查询账户余额
	 * @param custCode 客户账号
	 * @param password 客户密码
	 * @param serviceBaseUrl http://ip:port
	 * @return ResultMsg
	 */
	public ResultMsg queryAccount(String custCode, String password, String serviceBaseUrl){

		SmsClient smsClient = new SmsClient();
		ResultMsg resultMsg = smsClient.getToken(custCode, serviceBaseUrl);
		try {
			if (resultMsg.isSuccess()) {
				GetTokenResp gtResp = JSON.parseObject(resultMsg.getData(), GetTokenResp.class);
				QueryReq queryAccountReq = new QueryReq();
				String sign = MD5.getMD5((gtResp.getToken() + password).getBytes("utf-8"));
				
				queryAccountReq.setToken_id(gtResp.getToken_id());
				queryAccountReq.setCust_code(custCode);
				queryAccountReq.setSign(sign);
				
				String postData = JSON.toJSONString(queryAccountReq);
				String queryAccountResp = HttpClient.post(serviceBaseUrl + "/queryAccount",
						postData, "application/json", "utf-8");
				
				JSONObject jsonObject = JSON.parseObject(queryAccountResp);

				if (!"failed".equals(jsonObject.getString("status"))) {
					resultMsg.setSuccess(true);
					resultMsg.setData(queryAccountResp);
				} else {
					resultMsg.setSuccess(false);
					resultMsg.setCode(jsonObject.getString("respCode"));
					resultMsg.setMsg(jsonObject.getString("respMsg"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg.setSuccess(false);
			resultMsg.setCode("1000");
			resultMsg.setMsg("服务器出现未知异常");
		}
		return resultMsg;
	}
	
	
	/**
	 * 获取上行记录
	 * @param custCode 客户账号
	 * @param password 客户密码
	 * @param serviceBaseUrl http://ip:port
	 * @return ResultMsg
	 */
	public ResultMsg getMo(String custCode, String password, String serviceBaseUrl){

		SmsClient smsClient = new SmsClient();
		ResultMsg resultMsg = smsClient.getToken(custCode, serviceBaseUrl);
		try {
			if (resultMsg.isSuccess()) {
				GetTokenResp gtResp = JSON.parseObject(resultMsg.getData(), GetTokenResp.class);
				QueryReq queryAccountReq = new QueryReq();
				String sign = MD5.getMD5((gtResp.getToken() + password).getBytes("utf-8"));
				
				queryAccountReq.setToken_id(gtResp.getToken_id());
				queryAccountReq.setCust_code(custCode);
				queryAccountReq.setSign(sign);
				
				String postData = JSON.toJSONString(queryAccountReq);
				String getMoResp = HttpClient.post(serviceBaseUrl + "/getMO",
						postData, "application/json", "utf-8");
				
				JSONObject jsonObject;
				try {
					JSON.parseArray(getMoResp);//无异常代表是json数组，即正常返回数据
					resultMsg.setSuccess(true);
					resultMsg.setData(getMoResp);
				} catch (Exception e) {
					jsonObject = JSON.parseObject(getMoResp);
					if (!"failed".equals(jsonObject.getString("status"))) {
						resultMsg.setSuccess(true);
						resultMsg.setData(getMoResp);
					} else {
						resultMsg.setSuccess(false);
						resultMsg.setCode(jsonObject.getString("respCode"));
						resultMsg.setMsg(jsonObject.getString("respMsg"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg.setSuccess(false);
			resultMsg.setCode("1000");
			resultMsg.setMsg("服务器出现未知异常");
		}
		return resultMsg;
	}
	
	/**
	 * 获取状态报告
	 * @param custCode 客户账号
	 * @param password 客户密码
	 * @param serviceBaseUrl http://ip:port
	 * @return ResultMsg
	 */
	public ResultMsg getReport(String custCode, String password, String serviceBaseUrl){

		SmsClient smsClient = new SmsClient();
		ResultMsg resultMsg = smsClient.getToken(custCode, serviceBaseUrl);
		try {
			if (resultMsg.isSuccess()) {
				GetTokenResp gtResp = JSON.parseObject(resultMsg.getData(), GetTokenResp.class);
				QueryReq queryAccountReq = new QueryReq();
				String sign = MD5.getMD5((gtResp.getToken() + password).getBytes("utf-8"));
				
				queryAccountReq.setToken_id(gtResp.getToken_id());
				queryAccountReq.setCust_code(custCode);
				queryAccountReq.setSign(sign);
				
				String postData = JSON.toJSONString(queryAccountReq);
				String getReportResp = HttpClient.post(serviceBaseUrl + "/getReport",
						postData, "application/json", "utf-8");
				
				JSONObject jsonObject;
				try {
					JSON.parseArray(getReportResp);//无异常代表是json数组，即正常返回数据
					resultMsg.setSuccess(true);
					resultMsg.setData(getReportResp);
				} catch (Exception e) {
					jsonObject = JSON.parseObject(getReportResp);
					if (!"failed".equals(jsonObject.getString("status"))) {
						resultMsg.setSuccess(true);
						resultMsg.setData(getReportResp);
					} else {
						resultMsg.setSuccess(false);
						resultMsg.setCode(jsonObject.getString("respCode"));
						resultMsg.setMsg(jsonObject.getString("respMsg"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg.setSuccess(false);
			resultMsg.setCode("1000");
			resultMsg.setMsg("服务器出现未知异常");
		}
		return resultMsg;
	}
	
	/**
	 * 获取某手机号码的黑名单类型
	 * @param custCode 客户账号
	 * @param password 客户密码
	 * @param serviceBaseUrl http://ip:port
	 * @param mobile 手机号码
	 * @return ResultMsg
	 */
	public ResultMsg getBlacklist(String custCode, String password, String serviceBaseUrl, String mobile){

		SmsClient smsClient = new SmsClient();
		ResultMsg resultMsg = smsClient.getToken(custCode, serviceBaseUrl);
		try {
			if (resultMsg.isSuccess()) {
				GetTokenResp gtResp = JSON.parseObject(resultMsg.getData(), GetTokenResp.class);
				QueryReq queryAccountReq = new QueryReq();
				String sign = MD5.getMD5((gtResp.getToken() + password).getBytes("utf-8"));
				
				queryAccountReq.setToken_id(gtResp.getToken_id());
				queryAccountReq.setCust_code(custCode);
				queryAccountReq.setSign(sign);
				queryAccountReq.setMobile(mobile);
				
				String postData = JSON.toJSONString(queryAccountReq);
				String getReportResp = HttpClient.post(serviceBaseUrl + "/getBlacklist",
						postData, "application/json", "utf-8");
				
				JSONObject jsonObject;
				try {
					JSON.parseArray(getReportResp);//无异常代表是json数组，即正常返回数据
					resultMsg.setSuccess(true);
					resultMsg.setData(getReportResp);
				} catch (Exception e) {
					System.out.println(getReportResp);
					jsonObject = JSON.parseObject(getReportResp);
					if (!"failed".equals(jsonObject.getString("status"))) {
						resultMsg.setSuccess(true);
						resultMsg.setData(getReportResp);
					} else {
						resultMsg.setSuccess(false);
						resultMsg.setCode(jsonObject.getString("respCode"));
						resultMsg.setMsg(jsonObject.getString("respMsg"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg.setSuccess(false);
			resultMsg.setCode("1000");
			resultMsg.setMsg("服务器出现未知异常");
		}
		return resultMsg;
	}

}
