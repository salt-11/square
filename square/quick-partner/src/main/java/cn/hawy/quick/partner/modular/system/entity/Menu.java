package cn.hawy.quick.partner.modular.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-07
 */

@TableName("t_dept_menu")
@Data
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "menu_id")
    private String menuId;
    /**
     * 菜单编号
     */
    @TableField("code")
    private String code;
    /**
     * 菜单父编号
     */
    @TableField("pcode")
    private String pcode;

    /**
     * 菜单名称
     */
    @TableField("name")
    private String name;
    /**
     * 菜单图标
     */
    @TableField("icon")
    private String icon;
    /**
     * url地址
     */
    @TableField("url")
    private String url;
    /**
     * 菜单排序号
     */
    @TableField("sort")
    private Integer sort;
    /**
     * 菜单层级
     */
    @TableField("levels")
    private Integer levels;


    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private Date createTime;




}
