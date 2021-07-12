package cn.hawy.quick.modular.api.dto.sumbt;

import lombok.Data;

@Data
public class VerifyMessageDto {
	
	private String reqTime;
	
	private String partnerId;
	
	private String mchId;
	
	
	private String outTradeNo;
	
	private String verifyCode;
	
	
    private String signature;
    
	
	

}
