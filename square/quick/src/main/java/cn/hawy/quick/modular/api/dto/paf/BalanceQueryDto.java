package cn.hawy.quick.modular.api.dto.paf;

import lombok.Data;

@Data
public class BalanceQueryDto {
	
	private String reqTime;
	
	private String partnerId;
	
	private String mchId;
	
	private String cardNo;
	
    private String signature;
    
	
	

}
