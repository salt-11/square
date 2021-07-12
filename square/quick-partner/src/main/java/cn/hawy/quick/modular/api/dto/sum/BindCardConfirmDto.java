package cn.hawy.quick.modular.api.dto.sum;

import lombok.Data;

@Data
public class BindCardConfirmDto {
	
	private String reqTime;
	
	private String partnerId;
	
	private String mchId;
	
	private String cardNo;
	
	private String verifyCode;
	
    private String signature;
	
	

}
