package cn.hawy.quick.modular.api.dto.paf;

import lombok.Data;

@Data
public class WithdrawNotifyDto {
	
	private String bizOrderNumber;
	
	private String completedTime;
	
	private String mid;
	
	private String srcAmt;
	
	private String sign;

}
