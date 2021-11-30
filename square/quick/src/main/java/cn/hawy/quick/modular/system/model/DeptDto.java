package cn.hawy.quick.modular.system.model;

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
    private Long deptId;
    /**
     * 父级id
     */
    private Long pid;
    /**
     * 父级名称
     */
    private String pName;
    /**
     * 全称
     */
    private String fullName;

}
