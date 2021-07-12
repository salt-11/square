package cn.hawy.quick.modular.api.validate.ff;


import java.util.Date;

import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.modular.api.dto.ff.QueryDto;
import cn.hawy.quick.modular.api.dto.ff.RechargeDto;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

public class FfPayValidate {
	
	
	public static void main(String[] args) {
		System.out.println(NumberUtil.compare(Double.parseDouble("0.3"),Double.parseDouble("0.3")));
		/*String dateStr = "20190527220345";
		Date date = DateUtil.parse(dateStr);
		long betweenMinute = DateUtil.between(date,DateUtil.date(),DateUnit.MINUTE);
		System.out.println(betweenMinute);*/
	}
	
	/**
	 * 请求参数校验
	 * @param qrcodeDto
	 */
	public static void recharge(RechargeDto rechargeDto) {
		Date date = DateUtil.date();
		int nowHour = DateUtil.hour(date, true);
		if(nowHour<8 || nowHour>22) {
			throw new RestException(401, "交易开放时间:8:00-23:00");
		}
		if(StrUtil.isEmpty(rechargeDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(rechargeDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(rechargeDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
		if(StrUtil.isEmpty(rechargeDto.getMchId())) {
			throw new RestException(401, "mchId不能为空!");
		}
		if(StrUtil.isEmpty(rechargeDto.getCardNo())) {
			throw new RestException(401, "cardNo不能为空!");
		}
		if(StrUtil.isEmpty(rechargeDto.getOutTradeNo())) {
			throw new RestException(401, "outTradeNo不能为空!");
		}
		if(StrUtil.isEmpty(rechargeDto.getOrderAmount())) {
			throw new RestException(401, "orderAmount不能为空!");
		}
		if(!NumberUtil.isLong(rechargeDto.getOrderAmount())) {
			throw new RestException(401, "orderAmount格式错误!");
		}
		if(StrUtil.isEmpty(rechargeDto.getNotifyUrl())) {
			throw new RestException(401, "notifyUrl不能为空!");
		}
		if(!StrUtil.isEmpty(rechargeDto.getMchRate())) {
			if(!NumberUtil.isDouble(rechargeDto.getMchRate())) {
				throw new RestException(401, "mchRate格式错误!");
			}
		}
		if(StrUtil.isEmpty(rechargeDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}
	
	/**
	 * 请求参数校验
	 * @param qrcodeDto
	 */
	public static void query(QueryDto queryDto) {
		if(StrUtil.isEmpty(queryDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(queryDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(queryDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
		if(StrUtil.isEmpty(queryDto.getMchId())) {
			throw new RestException(401, "mchId不能为空!");
		}
		if(StrUtil.isEmpty(queryDto.getOutTradeNo())) {
			throw new RestException(401, "outTradeNo不能为空!");
		}
		if(StrUtil.isEmpty(queryDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}
	
}
