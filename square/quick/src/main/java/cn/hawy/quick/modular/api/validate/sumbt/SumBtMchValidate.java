package cn.hawy.quick.modular.api.validate.sumbt;


import java.util.Date;

import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.modular.api.dto.sumbt.BindCardDto;
import cn.hawy.quick.modular.api.dto.sumbt.CardAuthDto;
import cn.hawy.quick.modular.api.dto.sumbt.RegisterDto;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

public class SumBtMchValidate {

	private static String[] cardType = {"credit"};

	private static String[] authType = {"3","4"};

	private static String[] ACardType = {"0","1"};

	public static void main(String[] args) {
		System.out.println(NumberUtil.compare(Double.parseDouble("0.3"),Double.parseDouble("0.3")));
		/*String dateStr = "20190527220345";
		Date date = DateUtil.parse(dateStr);
		long betweenMinute = DateUtil.between(date,DateUtil.date(),DateUnit.MINUTE);
		System.out.println(betweenMinute);*/
	}


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
		if(StrUtil.isEmpty(registerDto.getIdStartDate())) {
			throw new RestException(401, "idStartDate不能为空!");
		}
		if(StrUtil.isEmpty(registerDto.getIdEndDate())) {
			throw new RestException(401, "idEndDate不能为空!");
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
		if(StrUtil.isEmpty(bindCardDto.getBankCode())) {
			throw new RestException(401, "bankCode不能为空!");
		}
		if(StrUtil.isEmpty(bindCardDto.getCardType())) {
			throw new RestException(401, "cardType不能为空!");
		}
		if(!ArrayUtil.contains(cardType, bindCardDto.getCardType())) {
			throw new RestException(401, "cardType类型错误!");
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

	public static void cardAuth(CardAuthDto cardAuthDto) {
		if(StrUtil.isEmpty(cardAuthDto.getReqTime())) {
			throw new RestException(401, "reqTime不能为空!");
		}
		Date reqTime = DateUtil.parse(cardAuthDto.getReqTime());
		long betweenMinute = DateUtil.between(reqTime,DateUtil.date(),DateUnit.MINUTE);
		if(betweenMinute > 5) {
			throw new RestException(401, "reqTime时间失效!");
		}
		if(StrUtil.isEmpty(cardAuthDto.getPartnerId())) {
			throw new RestException(401, "partnerId不能为空!");
		}
//		if(StrUtil.isEmpty(cardAuthDto.getMchId())) {
//			throw new RestException(401, "mchId不能为空!");
//		}
		if(StrUtil.isEmpty(cardAuthDto.getAuthType())) {
			throw new RestException(401, "authType不能为空!");
		}
		if(!ArrayUtil.contains(authType, cardAuthDto.getAuthType())) {
			throw new RestException(401, "authType类型错误!");
		}
		if("3".equals(cardAuthDto.getAuthType()) || "4".equals(cardAuthDto.getAuthType())) {
			if(StrUtil.isEmpty(cardAuthDto.getIdType())) {
				throw new RestException(401, "idType不能为空!");
			}
			if(StrUtil.isEmpty(cardAuthDto.getIdNo())) {
				throw new RestException(401, "idNo不能为空!");
			}
		}
		if(StrUtil.isEmpty(cardAuthDto.getRealname())) {
			throw new RestException(401, "realname不能为空!");
		}
		if("4".equals(cardAuthDto.getAuthType())) {
			if(StrUtil.isEmpty(cardAuthDto.getMobile())) {
				throw new RestException(401, "mobile不能为空!");
			}
		}
		if(StrUtil.isEmpty(cardAuthDto.getCardNo())) {
			throw new RestException(401, "cardNo不能为空!");
		}
		if(StrUtil.isEmpty(cardAuthDto.getCardType())) {
			throw new RestException(401, "cardType不能为空!");
		}
		if(!ArrayUtil.contains(ACardType, cardAuthDto.getCardType())) {
			throw new RestException(401, "cardType类型错误!");
		}
		if("1".equals(cardAuthDto.getCardType())) {
			if(StrUtil.isEmpty(cardAuthDto.getCvv())) {
				throw new RestException(401, "cvv不能为空!");
			}
			if(StrUtil.isEmpty(cardAuthDto.getValidDate())) {
				throw new RestException(401, "validDate不能为空!");
			}
		}
		if(StrUtil.isEmpty(cardAuthDto.getSignature())) {
			throw new RestException(401, "signature不能为空!");
		}
	}

}
