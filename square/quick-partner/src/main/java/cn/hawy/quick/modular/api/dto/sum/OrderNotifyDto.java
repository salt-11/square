package cn.hawy.quick.modular.api.dto.sum;

import lombok.Data;

@Data
public class OrderNotifyDto {
	
	private String resp_code;
	
	private String resp_msg;
	
	private String sign_type;
	
	private String sign;
	
	private String order_no;
	
	private String order_type;
	
	private String user_id;
	
	private String order_time;
	
	private String finish_time;
	
	private String order_amount;
	
	private String trade_no;
	
	private String status;
	
	private String error_code;
	
	private String error_msg;
}
