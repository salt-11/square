package cn.hawy.quick.modular.api.dto.sum;

import lombok.Data;

@Data
public class BindCardDto {
	
	private String reqTime;
	
	private String partnerId;
	
	private String mchId;
	
	private String mobile;
	
	private String cardNo;
	
	private String bankCode;
	
	private String cardType;
	
	private String cvv;
	
	private String validYear;
	
	private String validMonth;
	
	private String returnUrl;
	
    private String signature;
	
	

}
