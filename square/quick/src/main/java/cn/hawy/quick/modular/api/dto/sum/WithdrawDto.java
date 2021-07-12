package cn.hawy.quick.modular.api.dto.sum;

import lombok.Data;

@Data
public class WithdrawDto {
	
	private String reqTime;
	
	private String partnerId;
	
	private String mchId;
	
	private String cardNo;
	
	private String outTradeNo;
	
	private String cashAmount;
	
	private String cashFee;
	
	private String payPassword;
	
	private String notifyUrl;
	
    private String signature;
    
	
	

}
