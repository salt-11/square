package cn.hawy.quick.modular.api.channel.sumDto;

import lombok.Data;

@Data
public class BindedCardForm {
	
	private String bank_name;
	
	private String bank_code;
	
	private String card_no;
	
	private String card_type;
	
	private String bind_card_id;
	
	private String single_amount_limit;
	
	private String day_amount_limit;
	
	private String month_amount_limit;
	
	private String business_code;

}
