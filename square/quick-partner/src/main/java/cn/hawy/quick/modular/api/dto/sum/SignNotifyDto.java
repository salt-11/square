package cn.hawy.quick.modular.api.dto.sum;

import lombok.Data;

@Data
public class SignNotifyDto {
	
	private String resp_code;
	
	private String resp_msg;
	
	private String sign_type;
	
	private String sign;
	
	private String mer_no;
	
	private String order_no;
	
	private String status;
	
	private String token;

}
