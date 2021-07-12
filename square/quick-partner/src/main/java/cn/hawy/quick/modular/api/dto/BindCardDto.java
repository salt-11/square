package cn.hawy.quick.modular.api.dto;

import lombok.Data;

@Data
public class BindCardDto {
	
	private String reqTime;
	
	private String partnerId;
	
	private String mchId;
	
	private String mobile;
	
	private String bankCardNo;
	
	private String mchRate;
	
	private String bankCardType;
	
	private String expired;
	
	private String cvn;
	
	private String isSendIssuer;
	
    private String signature;
	
	

}
