package cn.hawy.quick.modular.api.dto;

import lombok.Data;

@Data
public class PayPreDto {
	
	private String reqTime;
	
	private String partnerId;
	
	private String mchId;
	
	private String bankCardNo;
	
	private String outTradeNo;
	
	private String goodsId;
	
	private String goodsType;
	
	private String goodsName;
	
	private String orderAmount;
	
	private String areaCode;
	
	private String mccCode;
	
	private String notifyUrl;
	
	private String isSendSmsCode;
	
    private String signature;
    
    /*-----------------------*/
    
    private String orderId;
	
	

}
