package cn.hawy.quick.modular.test;

import cn.hawy.quick.modular.test.vo.ResultMsg;
import cn.hawy.quick.modular.test.vo.SmsReq;

/**
 * 普通短信发送示例
 * 两种方式：
 * 		1.通过SmsReq对象传参
 * 		2.直接传参
 */
public class SendSms {
	public static void main(String[] args) {
		String custCode = "301093";							 //[必填] 用户账号
		String password = "WHO7R97F6N";						 //[必填] 账号密码
		String serviceBaseUrl = "http://123.58.255.70:8860"; 			 //[必填] http://ip:port

		/**
		 * 1.通过SmsReq对象传参
		 */
		
		SmsReq smsReq = new SmsReq();
		smsReq.setUid("");							//[选填] 业务标识，由贵司自定义32为数字透传至我司
		smsReq.setCust_code(custCode);				//[必填] 用户账号
		smsReq.setContent("123456");				//[必填] 短信内容
		smsReq.setDestMobiles("18305975931");		//[必填] 接收号码，同时发送给多个号码时,号码之间用英文半角逗号分隔(,)
		smsReq.setNeed_report("yes");				//[选填] 状态报告需求与否，是 yes 否 no 默认yes
		smsReq.setSp_code("");						//[选填] 长号码
		smsReq.setMsgFmt("8");						//[选填] 信息格式，0：ASCII串；3：短信写卡操作；4：二进制信息；8：UCS2编码；默认8

		SmsClient smsClient = new SmsClient();
		ResultMsg resultMsg = smsClient.sendSms(smsReq, password, serviceBaseUrl);
		if (resultMsg.isSuccess()) {
			/**
			 * 成功返回json对象字符串，data数据如下：
			 * {
				    "uid": "1123344567",
				    "status": "success",
				    "respCode": "0",
				    "respMsg": "提交成功！",
				    "totalChargeNum": 1,
				    "result": [
				        {
				            "msgid": "59106312221352221524",
				            "mobile": "1348908xxxx",
				            "code": "0",
				            "msg": "提交成功.",
				            "chargeNum": 1
				        }
				    ]
				}
			 */
			System.out.println(resultMsg.getData());
		} else {
			/**
			 *  1000：服务器出现未知异常！
			 *  1001 操作不合法，操作前未获取Token，或Token已过时
			 *	1002 签名验证不通过！
			 *	1003 Json参数解析出错
			 *	1004 操作不合法，cust_code: xxxxxx不存在
			 *	1005 客户端IP鉴权不通过
			 *	1006 客户账号已停用！
			 *	1008 客户提交接口协议HTTP, 与客户参数允许的协议不一致！
			 *	1009 提交的短信内容超过规定的长度！
			 *	1011 客户账户不存在！
			 *	1012 账户没有足够的余额
			 *	1013 扩展号码(sp_code)不符合规范！
			 */
			System.out.println(resultMsg.getCode());
			System.out.println(resultMsg.getMsg());
		}
		
		/**
		 * 2.直接传参
		 */
//		String uid = "1123344567";
//		String content = "短信内容";
//		String destMobiles = "13489080110,13489080111";
//		String needReport = "yes";
//		String spCode = "";
//		String msgFmt = "8";
//
//		SmsClient smsClient1 = new SmsClient();
//		ResultMsg resultMsg1 = smsClient1.sendSms(uid, custCode, content, destMobiles,
//				needReport, spCode, msgFmt, serviceBaseUrl, password);
//		if (resultMsg1.isSuccess()) {
//			System.out.println(resultMsg1.getData());
//		} else {
//			System.out.println(resultMsg1.getCode());
//			System.out.println(resultMsg1.getMsg());
//		}
		
	}
}
