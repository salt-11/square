package cn.hawy.quick.modular.api.dto;

import lombok.Data;

import java.util.Date;



	  /**
	   * 渠道表
	   *
	   */
@Data
public class PartnerDto {

	private String id;

	private String deptName ;

	private String account ;

	private String password  ;

	private String salt ;

	private String agentId;

	private String name ;

	private String cardNo;

	private String bankName;

	private Date createTime;





}
