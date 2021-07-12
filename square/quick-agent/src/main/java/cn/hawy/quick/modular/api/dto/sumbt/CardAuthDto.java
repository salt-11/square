package cn.hawy.quick.modular.api.dto.sumbt;

import lombok.Data;

@Data
public class CardAuthDto {

	private String reqTime;

	private String partnerId;

	//private String mchId;

	private String authType;

	private String idType;

	private String idNo;

	private String realname;

	private String mobile;

	private String cardNo;

	private String cardType;

	private String cvv;

	private String validDate;

    private String signature;



}
