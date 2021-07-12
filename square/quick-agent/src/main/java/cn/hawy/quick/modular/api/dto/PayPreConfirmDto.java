package cn.hawy.quick.modular.api.dto;

import lombok.Data;

@Data
public class PayPreConfirmDto {
	
	private String reqTime;
	
	private String partnerId;
	
	private String mchId;
	
	
	private String outTradeNo;
	
	private String smsCode;
	
	
    private String signature;
    
	
	

}
