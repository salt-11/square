package cn.hawy.quick.modular.api.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class AgentDto {

	private String id;

	private String agentName ;

	private String account ;

	private String password  ;

	private String salt ;

	private String name ;

	private String cardNo;

	private String bankName;

	private Date createTime;





}
