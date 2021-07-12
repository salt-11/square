package cn.hawy.quick.modular.api.dto.gyf;

import lombok.Data;

@Data
public class WithdrawDto {

	private String reqTime;

	private String partnerId;

	private String mchId;

	private String cardNo;

	private String outTradeNo;

	private String mobile;

	private String cashAmount;

	private String cashFee;

	private String notifyUrl;

    private String signature;




}
