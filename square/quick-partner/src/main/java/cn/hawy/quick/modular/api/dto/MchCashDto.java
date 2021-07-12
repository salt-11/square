package cn.hawy.quick.modular.api.dto;

import lombok.Data;

@Data
public class MchCashDto {
	
	private String reqTime;
	
	private String partnerId;
	
	private String mchId;
	
	private String bankCardNo;
	
	private String outTradeNo;
	
	private String cashAmount;
	
	private String cashFee;
	
    private String signature;
    
	
	

}
