package cn.hawy.quick.partner.modular.system.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 字典信息
 *
 * @author fengshuonan
 * @Date 2018/12/8 18:16
 */
@Data
public class DeptDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String deptId;
    /**
     * 余额
     */
    private Long balance;

    private Long cashAmount;

    private Long cashId;

    private String cashRate;

    private Long cashFee;

    private Long outAmount;

    private String name;

    private String cardNo;

    private String bankName;

}
