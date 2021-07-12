package cn.hawy.quick.modular.api.validate.sumbt;


import java.util.Date;

import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.modular.api.dto.sumbt.ChangeAgentCardDto;
import cn.hawy.quick.modular.api.dto.sumbt.OrderAppleyDto;
import cn.hawy.quick.modular.api.dto.sumbt.QueryDto;
import cn.hawy.quick.modular.api.dto.sumbt.VerifyMessageDto;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

public class SumBtPayValidate {

	private static String[] cardType = {"debit"};

	public static void main(String[] args) {
		System.out.println(NumberUtil.compare(Double.parseDouble("0.3"),Double.parseDouble("0.3")));
		/*String dateStr = "20190527220345";
		Date date = DateUtil.parse(dateStr);
		long betweenMinute = DateUtil.between(date,DateUtil.date(),DateUnit.MINUTE);
		System.out.println(betweenMinute);*/
	}

	/**
	 * 请求参数校验
	 * @param
	 */
	public static void orderAppley(OrderAppleyDto orderAppleyDto) {
		Date date = DateUtil.date();
		int nowHour = DateUtil.hour(date, true);
		if(nowHour<8 || nowHour>22) {
			throw new RestException(401, "交易开放时间:8:00-23:00");
		}
		if(StrUtil.isEmpty(orderAppleyDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(orderAppleyDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(orderAppleyDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
		if(StrUtil.isEmpty(orderAppleyDto.getMchId())) {
			throw new RestException(401, "mchId不能为空!");
		}
		if(StrUtil.isEmpty(orderAppleyDto.getCardNo())) {
			throw new RestException(401, "cardNo不能为空!");
		}
		if(StrUtil.isEmpty(orderAppleyDto.getOutTradeNo())) {
			throw new RestException(401, "outTradeNo不能为空!");
		}
		if(StrUtil.isEmpty(orderAppleyDto.getOrderAmount())) {
			throw new RestException(401, "orderAmount不能为空!");
		}
		if(!NumberUtil.isLong(orderAppleyDto.getOrderAmount())) {
			throw new RestException(401, "orderAmount格式错误!");
		}
		if(StrUtil.isEmpty(orderAppleyDto.getGoodsName())) {
			throw new RestException(401, "goodsName不能为空!");
		}
		if(StrUtil.isEmpty(orderAppleyDto.getNotifyUrl())) {
			throw new RestException(401, "notifyUrl不能为空!");
		}
		if(StrUtil.isEmpty(orderAppleyDto.getAgentBankCode())) {
			throw new RestException(401, "agentBankCode不能为空!");
		}
		if(StrUtil.isEmpty(orderAppleyDto.getAgentCardNo())) {
			throw new RestException(401, "agentCardNo不能为空!");
		}
		if(StrUtil.isEmpty(orderAppleyDto.getAgentCardType())) {
			throw new RestException(401, "agentCardType不能为空!");
		}
		if(!ArrayUtil.contains(cardType, orderAppleyDto.getAgentCardType())) {
			throw new RestException(401, "agentCardType类型错误!");
		}
		if(StrUtil.isEmpty(orderAppleyDto.getFeeAmount())) {
			throw new RestException(401, "feeAmount不能为空!");
		}
		if(!NumberUtil.isLong(orderAppleyDto.getFeeAmount())) {
			throw new RestException(401, "feeAmount格式错误!");
		}
		if(!StrUtil.isEmpty(orderAppleyDto.getMchRate())) {
			if(!NumberUtil.isDouble(orderAppleyDto.getMchRate())) {
				throw new RestException(401, "mchRate格式错误!");
			}
			//throw new RestException(401, "validMonth不能为空!");
		}
		if(StrUtil.isEmpty(orderAppleyDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}

	public static void verifyMessage(VerifyMessageDto verifyMessageDto) {
		if(StrUtil.isEmpty(verifyMessageDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(verifyMessageDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(verifyMessageDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
		if(StrUtil.isEmpty(verifyMessageDto.getMchId())) {
			throw new RestException(401, "mchId不能为空!");
		}
		if(StrUtil.isEmpty(verifyMessageDto.getOutTradeNo())) {
			throw new RestException(401, "outTradeNo不能为空!");
		}
		if(StrUtil.isEmpty(verifyMessageDto.getVerifyCode())) {
			throw new RestException(401, "verifyCode不能为空!");
		}
		if(StrUtil.isEmpty(verifyMessageDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}

	public static void changeAgentCard(ChangeAgentCardDto changeAgentCardDto) {
		if(StrUtil.isEmpty(changeAgentCardDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(changeAgentCardDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(changeAgentCardDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
		if(StrUtil.isEmpty(changeAgentCardDto.getMchId())) {
			throw new RestException(401, "mchId不能为空!");
		}
		if(StrUtil.isEmpty(changeAgentCardDto.getOutTradeNo())) {
			throw new RestException(401, "outTradeNo不能为空!");
		}
		if(StrUtil.isEmpty(changeAgentCardDto.getBankCode())) {
			throw new RestException(401, "bankCode不能为空!");
		}
		if(StrUtil.isEmpty(changeAgentCardDto.getCardNo())) {
			throw new RestException(401, "cardNo不能为空!");
		}
		if(StrUtil.isEmpty(changeAgentCardDto.getCardType())) {
			throw new RestException(401, "cardType不能为空!");
		}
		if(!ArrayUtil.contains(cardType, changeAgentCardDto.getCardType())) {
			throw new RestException(401, "cardType类型错误!");
		}
		if(StrUtil.isEmpty(changeAgentCardDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}

	/**
	 * 请求参数校验
	 * @param
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
