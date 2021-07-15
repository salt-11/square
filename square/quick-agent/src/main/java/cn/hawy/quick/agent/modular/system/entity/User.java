package cn.hawy.quick.agent.modular.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 管理员表
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-07
 */
@TableName("t_agent_info")
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId
    private String id;
    /**
     * 账号
     */
    @TableField("account")
    private String account;
    /**
     * 密码
     */
    @TableField("password")
    private String password;
    /**
     * md5密码盐
     */
    @TableField("salt")
    private String salt;
    /**
     * 名字
     */
    @TableField("agent_name")
    private String name;

    private List<String> deptIdList;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private Date createTime;


}
