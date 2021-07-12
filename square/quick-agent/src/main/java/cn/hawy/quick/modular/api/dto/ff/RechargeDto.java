package cn.hawy.quick.modular.api.dto.ff;

import lombok.Data;

@Data
public class RechargeDto {
	
	private String reqTime;
	
	private String partnerId;
	
	private String mchId;
	
	private String cardNo;
	
	private String outTradeNo;
	
	private String orderAmount;
	
	private String notifyUrl;
	
	private String city;
	
	private String mcc;
	
    private String signature;
    
    private String mchRate;
    
}
