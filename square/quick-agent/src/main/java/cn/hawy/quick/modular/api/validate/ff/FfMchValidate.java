package cn.hawy.quick.modular.api.validate.ff;


import java.util.Date;

import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.modular.api.dto.ff.BalanceQueryDto;
import cn.hawy.quick.modular.api.dto.ff.BindCardConfirmDto;
import cn.hawy.quick.modular.api.dto.ff.BindCardDto;
import cn.hawy.quick.modular.api.dto.ff.RegisterDto;
import cn.hawy.quick.modular.api.dto.ff.WithdrawDto;
import cn.hawy.quick.modular.api.dto.ff.WithdrawQueryDto;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

public class FfMchValidate {
	
	
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
	public static void register(RegisterDto registerDto) {
		if(StrUtil.isEmpty(registerDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(registerDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(registerDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
		if(StrUtil.isEmpty(registerDto.getMobile())) {
			throw new RestException(401, "mobile不能为空!");
		}
		if(StrUtil.isEmpty(registerDto.getRealName())) {
			throw new RestException(401, "realName不能为空!");
		}
		if(StrUtil.isEmpty(registerDto.getIdNo())) {
			throw new RestException(401, "idNo不能为空!");
		}
		if(StrUtil.isEmpty(registerDto.getAddress())) {
			throw new RestException(401, "address不能为空!");
		}
		if(StrUtil.isEmpty(registerDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}
	
	
	
	public static void binkCard(BindCardDto bindCardDto) {
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
		if(StrUtil.isEmpty(bindCardDto.getCardNo())) {
			throw new RestException(401, "cardNo不能为空!");
		}
		if(StrUtil.isEmpty(bindCardDto.getMobile())) {
			throw new RestException(401, "mobile不能为空!");
		}
		if(StrUtil.isEmpty(bindCardDto.getCvv())) {
			throw new RestException(401, "cvv不能为空!");
		}
		if(StrUtil.isEmpty(bindCardDto.getValidYear())) {
			throw new RestException(401, "validYear不能为空!");
		}
		if(StrUtil.isEmpty(bindCardDto.getValidMonth())) {
			throw new RestException(401, "validMonth不能为空!");
		}
		if(StrUtil.isEmpty(bindCardDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}
	
	public static void binkCardConfirm(BindCardConfirmDto bindCardConfirmDto) {
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
		if(StrUtil.isEmpty(bindCardConfirmDto.getCardNo())) {
			throw new RestException(401, "cardNo不能为空!");
		}
		if(StrUtil.isEmpty(bindCardConfirmDto.getVerifyCode())) {
			throw new RestException(401, "verifyCode不能为空!");
		}
		if(StrUtil.isEmpty(bindCardConfirmDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}
	
	public static void balanceQuery(BalanceQueryDto balanceQueryDto) {
		if(StrUtil.isEmpty(balanceQueryDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(balanceQueryDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(balanceQueryDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
		if(StrUtil.isEmpty(balanceQueryDto.getMchId())) {
			throw new RestException(401, "mchId不能为空!");
		}
		if(StrUtil.isEmpty(balanceQueryDto.getCardNo())) {
			throw new RestException(401, "cardNo不能为空!");
		}
		if(StrUtil.isEmpty(balanceQueryDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}
	
	
	public static void withdraw(WithdrawDto withdrawDto) {
		if(StrUtil.isEmpty(withdrawDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(withdrawDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(withdrawDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
		if(StrUtil.isEmpty(withdrawDto.getMchId())) {
			throw new RestException(401, "mchId不能为空!");
		}
		if(StrUtil.isEmpty(withdrawDto.getOutTradeNo())) {
			throw new RestException(401, "outTradeNo不能为空!");
		}
		if(StrUtil.isEmpty(withdrawDto.getCardNo())) {
			throw new RestException(401, "cardNo不能为空!");
		}
		if(StrUtil.isEmpty(withdrawDto.getCashAmount())) {
			throw new RestException(401, "cashAmount不能为空!");
		}
		if(!NumberUtil.isLong(withdrawDto.getCashAmount())) {
			throw new RestException(401, "cashAmount格式错误!");
		}
		if(StrUtil.isEmpty(withdrawDto.getCashFee())) {
			throw new RestException(401, "cashFee不能为空!");
		}
		if(!NumberUtil.isLong(withdrawDto.getCashFee())) {
			throw new RestException(401, "cashFee格式错误!");
		}
		if(NumberUtil.parseLong(withdrawDto.getCashAmount()) <= NumberUtil.parseLong(withdrawDto.getCashFee())){
			throw new RestException(401, "提现金额必须大于提现手续费!");
		}
		if(StrUtil.isEmpty(withdrawDto.getNotifyUrl())) {
			throw new RestException(401, "notifyUrl不能为空!");
		}
		if(StrUtil.isEmpty(withdrawDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}
	
	
	public static void withdrawQuery(WithdrawQueryDto withdrawQueryDto) {
		if(StrUtil.isEmpty(withdrawQueryDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(withdrawQueryDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(withdrawQueryDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
		if(StrUtil.isEmpty(withdrawQueryDto.getMchId())) {
			throw new RestException(401, "mchId不能为空!");
		}
		if(StrUtil.isEmpty(withdrawQueryDto.getOutTradeNo())) {
			throw new RestException(401, "outTradeNo不能为空!");
		}
		if(StrUtil.isEmpty(withdrawQueryDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}
	
	
}
