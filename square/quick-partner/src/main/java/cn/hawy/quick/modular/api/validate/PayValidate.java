package cn.hawy.quick.modular.api.validate;


import java.util.Date;

import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.modular.api.dto.AddMerchantDto;
import cn.hawy.quick.modular.api.dto.BindCardConfirmDto;
import cn.hawy.quick.modular.api.dto.BindCardDto;
import cn.hawy.quick.modular.api.dto.PayPreConfirmDto;
import cn.hawy.quick.modular.api.dto.PayPreDto;
import cn.hawy.quick.modular.api.dto.QueryDto;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

public class PayValidate {
	
	private static String[] bankCardType = {"debit","credit"};
	private static String[] isSendSmsCode = {"1","2"};
 	
	
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
	public static void payPreValidate(PayPreDto payPreDto) {
		Date date = DateUtil.date();
		int nowHour = DateUtil.hour(date, true);
		if(nowHour<8 || nowHour>22) {
			throw new RestException(401, "交易开放时间:8:00-23:00");
		}
		if(StrUtil.isEmpty(payPreDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(payPreDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(payPreDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
		if(StrUtil.isEmpty(payPreDto.getMchId())) {
			throw new RestException(401, "mchId不能为空!");
		}
		if(StrUtil.isEmpty(payPreDto.getOrderAmount())) {
			throw new RestException(401, "orderAmount不能为空!");
		}
		if(!NumberUtil.isLong(payPreDto.getOrderAmount())) {
			throw new RestException(401, "orderAmount格式错误!");
		}
		if(StrUtil.isEmpty(payPreDto.getOutTradeNo())) {
			throw new RestException(401, "outTradeNo不能为空!");
		}
		if(StrUtil.isEmpty(payPreDto.getGoodsId())) {
			throw new RestException(401, "goodsId不能为空!");
		}
		if(StrUtil.isEmpty(payPreDto.getGoodsName())) {
			throw new RestException(401, "goodsName不能为空!");
		}
		if(StrUtil.isEmpty(payPreDto.getGoodsType())) {
			throw new RestException(401, "goodsType不能为空!");
		}
		if(StrUtil.isEmpty(payPreDto.getAreaCode())) {
			throw new RestException(401, "areaCode不能为空!");
		}
		/*if(StrUtil.isEmpty(payPreDto.getMccCode())) {
			throw new RestException(401, "mccCode不能为空!");
		}*/
		if(StrUtil.isEmpty(payPreDto.getNotifyUrl())) {
			throw new RestException(401, "notifyUrl不能为空!");
		}
		if(!StrUtil.isEmpty(payPreDto.getIsSendSmsCode())) {
			if(!ArrayUtil.contains(isSendSmsCode, payPreDto.getIsSendSmsCode())) {
				throw new RestException(401, "isSendSmsCode类型错误!");
			}
		}
		if(StrUtil.isEmpty(payPreDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}
	
	/**
	 * 请求参数校验
	 * @param qrcodeDto
	 */
	public static void queryValidate(QueryDto queryDto) {
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
	
	/**
	 * 请求参数校验
	 * @param qrcodeDto
	 */
	public static void payPreConfirmValidate(PayPreConfirmDto payPreConfirmDto) {
		if(StrUtil.isEmpty(payPreConfirmDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(payPreConfirmDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(payPreConfirmDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
		if(StrUtil.isEmpty(payPreConfirmDto.getMchId())) {
			throw new RestException(401, "mchId不能为空!");
		}
		if(StrUtil.isEmpty(payPreConfirmDto.getOutTradeNo())) {
			throw new RestException(401, "outTradeNo不能为空!");
		}
		if(StrUtil.isEmpty(payPreConfirmDto.getSmsCode())) {
			throw new RestException(401, "smsCode不能为空!");
		}
		if(StrUtil.isEmpty(payPreConfirmDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}
	
	
}
