package cn.hawy.quick.modular.api.dto.paf;

import lombok.Data;

@Data
public class BindCardNotifyDto {

	private String bizOrderNumber;

	private String completedTime;

	private String mid;

	private String srcAmt;

	private String sign;

}
