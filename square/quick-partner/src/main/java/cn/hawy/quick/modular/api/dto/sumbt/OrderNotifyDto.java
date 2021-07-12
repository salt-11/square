package cn.hawy.quick.modular.api.dto.sumbt;

import lombok.Data;

@Data
public class OrderNotifyDto {
	
	private String resp_code;
	
	private String resp_msg;
	
	private String sign_type;
	
	private String sign;
	
	private String mer_no;
	
	private String sub_mer_no;
	
	private String order_no;
	
	private String order_time;
	
	private String trade_no;
	
	private String status;
	
	private String order_amount;
	
	private String success_time;
	
	private String fee_amount;
}
