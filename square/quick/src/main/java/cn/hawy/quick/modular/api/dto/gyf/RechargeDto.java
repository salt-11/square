package cn.hawy.quick.modular.api.dto.gyf;

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

	private String cityCode;

	private String mcc;

	private String terminalIp;

	private String mchRate;

	private String mchFee;

    private String signature;







}
