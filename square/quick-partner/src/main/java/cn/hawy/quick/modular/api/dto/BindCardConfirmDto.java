package cn.hawy.quick.modular.api.dto;

import lombok.Data;

@Data
public class BindCardConfirmDto {
	
	private String reqTime;
	
	private String partnerId;
	
	private String mchId;
	
	private String bankCardNo;
	
	private String smsCode;
	
    private String signature;
	
	

}
