package cn.hawy.quick.modular.api.validate;


import java.util.Date;

import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.modular.api.dto.AddMerchantDto;
import cn.hawy.quick.modular.api.dto.BindCardConfirmDto;
import cn.hawy.quick.modular.api.dto.BindCardDto;
import cn.hawy.quick.modular.api.dto.MchBalanceQueryDto;
import cn.hawy.quick.modular.api.dto.MchCashDto;
import cn.hawy.quick.modular.api.dto.MchCashQueryDto;
import cn.hawy.quick.modular.api.dto.QueryDto;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

public class MchValidate {
	
	private static String[] bankCardType = {"debit","credit"};
	
	private static String[] isSendIssuer = {"true","false"};
 	
	
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
	public static void addMerchantValidate(AddMerchantDto addMerchantDto) {
		if(StrUtil.isEmpty(addMerchantDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(addMerchantDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(addMerchantDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
		if(StrUtil.isEmpty(addMerchantDto.getMchName())) {
			throw new RestException(401, "mchName不能为空!");
		}
		if(StrUtil.isEmpty(addMerchantDto.getMchShortName())) {
			throw new RestException(401, "mchShortName不能为空!");
		}
		if(StrUtil.isEmpty(addMerchantDto.getAreaCode())) {
			throw new RestException(401, "areaCode不能为空!");
		}
		if(StrUtil.isEmpty(addMerchantDto.getMchAddress())) {
			throw new RestException(401, "mchAddress不能为空!");
		}
		if(StrUtil.isEmpty(addMerchantDto.getMobile())) {
			throw new RestException(401, "mobile不能为空!");
		}
		if(StrUtil.isEmpty(addMerchantDto.getEmail())) {
			throw new RestException(401, "email不能为空!");
		}
		if(StrUtil.isEmpty(addMerchantDto.getCustomerName())) {
			throw new RestException(401, "customerName不能为空!");
		}
		if(StrUtil.isEmpty(addMerchantDto.getCustomerIdentNo())) {
			throw new RestException(401, "customerIdentNo不能为空!");
		}
		if(StrUtil.isEmpty(addMerchantDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}
	
	/**
	 * 请求参数校验
	 * @param qrcodeDto
	 */
	public static void bindCardValidate(BindCardDto bindCardDto) {
		if(StrUtil.isEmpty(bindCardDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(bindCardDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(bindCardDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
		if(StrUtil.isEmpty(bindCardDto.getMchId())) {
			throw new RestException(401, "mchId不能为空!");
		}
		if(StrUtil.isEmpty(bindCardDto.getMobile())) {
			throw new RestException(401, "mobile不能为空!");
		}
		if(StrUtil.isEmpty(bindCardDto.getBankCardNo())) {
			throw new RestException(401, "bankCardNo不能为空!");
		}
		if(StrUtil.isEmpty(bindCardDto.getMchRate())) {
			throw new RestException(401, "mchRate不能为空!");
		}
		if(!NumberUtil.isDouble(bindCardDto.getMchRate())) {
			throw new RestException(401, "mchRate格式错误!");
		}
		if(StrUtil.isEmpty(bindCardDto.getBankCardType())) {
			throw new RestException(401, "bankCardType不能为空!");
		}
		if(!ArrayUtil.contains(bankCardType, bindCardDto.getBankCardType())) {
			throw new RestException(401, "bankCardType类型错误!");
		}
		if(StrUtil.isEmpty(bindCardDto.getIsSendIssuer())) {
			throw new RestException(401, "isSendIssuer不能为空!");
		}
		if(!ArrayUtil.contains(isSendIssuer, bindCardDto.getIsSendIssuer())) {
			throw new RestException(401, "isSendIssuer类型错误!");
		}
		if("credit".equals(bindCardDto.getBankCardType())) {
			if(StrUtil.isEmpty(bindCardDto.getExpired())) {
				throw new RestException(401, "expired不能为空!");
			}
			if(StrUtil.isEmpty(bindCardDto.getCvn())) {
				throw new RestException(401, "cvn不能为空!");
			}
			if("false".equals(bindCardDto.getIsSendIssuer())) {
				throw new RestException(401, "信用卡暂不支持绑定成结算卡!");
			}
		}
		if("debit".equals(bindCardDto.getBankCardType())) {
			if("true".equals(bindCardDto.getIsSendIssuer())) {
				throw new RestException(401, "储蓄卡暂不支持绑定成交易卡!");
			}
		}
		if(StrUtil.isEmpty(bindCardDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}
	
	/**
	 * 请求参数校验
	 * @param qrcodeDto
	 */
	public static void bindCardConfirmValidate(BindCardConfirmDto bindCardConfirmDto) {
		if(StrUtil.isEmpty(bindCardConfirmDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(bindCardConfirmDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(bindCardConfirmDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
		if(StrUtil.isEmpty(bindCardConfirmDto.getMchId())) {
			throw new RestException(401, "mchId不能为空!");
		}
		if(StrUtil.isEmpty(bindCardConfirmDto.getBankCardNo())) {
			throw new RestException(401, "bankCardNo不能为空!");
		}
		if(StrUtil.isEmpty(bindCardConfirmDto.getSmsCode())) {
			throw new RestException(401, "smsCode不能为空!");
		}
		if(StrUtil.isEmpty(bindCardConfirmDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}
	
	/**
	 * 请求参数校验
	 * @param qrcodeDto
	 */
	public static void mchBalanceQueryValidate(MchBalanceQueryDto mchBalanceQueryDto) {
		if(StrUtil.isEmpty(mchBalanceQueryDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(mchBalanceQueryDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(mchBalanceQueryDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
		if(StrUtil.isEmpty(mchBalanceQueryDto.getMchId())) {
			throw new RestException(401, "mchId不能为空!");
		}
		if(StrUtil.isEmpty(mchBalanceQueryDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}
	
	/**
	 * 请求参数校验
	 * @param qrcodeDto
	 */
	public static void mchCashValidate(MchCashDto mchCashDto) {
		if(StrUtil.isEmpty(mchCashDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(mchCashDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(mchCashDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
		if(StrUtil.isEmpty(mchCashDto.getMchId())) {
			throw new RestException(401, "mchId不能为空!");
		}
		if(StrUtil.isEmpty(mchCashDto.getBankCardNo())) {
			throw new RestException(401, "bankCardNo不能为空!");
		}
		if(StrUtil.isEmpty(mchCashDto.getOutTradeNo())) {
			throw new RestException(401, "outTradeNo不能为空!");
		}
		if(StrUtil.isEmpty(mchCashDto.getCashAmount())) {
			throw new RestException(401, "cashAmount不能为空!");
		}
		if(!NumberUtil.isLong(mchCashDto.getCashAmount())) {
			throw new RestException(401, "cashAmount格式错误!");
		}
		if(StrUtil.isEmpty(mchCashDto.getCashFee())) {
			throw new RestException(401, "cashFee不能为空!");
		}
		if(!NumberUtil.isLong(mchCashDto.getCashFee())) {
			throw new RestException(401, "cashFee格式错误!");
		}
		if(NumberUtil.parseLong(mchCashDto.getCashAmount()) <= NumberUtil.parseLong(mchCashDto.getCashFee())){
			throw new RestException(401, "提现金额必须大于提现手续费!");
		}
		if(StrUtil.isEmpty(mchCashDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}
	
	/**
	 * 请求参数校验
	 * @param qrcodeDto
	 */
	public static void mchCashQueryValidate(MchCashQueryDto mchCashQueryDto) {
		if(StrUtil.isEmpty(mchCashQueryDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(mchCashQueryDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(mchCashQueryDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
		if(StrUtil.isEmpty(mchCashQueryDto.getMchId())) {
			throw new RestException(401, "mchId不能为空!");
		}
		if(StrUtil.isEmpty(mchCashQueryDto.getOutTradeNo())) {
			throw new RestException(401, "outTradeNo不能为空!");
		}
		if(StrUtil.isEmpty(mchCashQueryDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}
	
	
}
