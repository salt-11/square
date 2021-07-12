package cn.hawy.quick.modular.api.dto.paf;

import lombok.Data;

@Data
public class WithdrawQueryDto {
	
	private String reqTime;
	
	private String partnerId;
	
	private String mchId;
	
	private String outTradeNo;
	
	
    private String signature;
    
	
	

}
