package cn.hawy.quick.modular.system.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 专业表
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-07
 */
@TableName("sys_dept")
public class Dept implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "DEPT_ID", type = IdType.ID_WORKER)
    private Long deptId;
    /**
     * 父级id
     */
    @TableField("PID")
    private Long pid;
    /**
     * 父级ids
     */
    @TableField("PIDS")
    private String pids;

    /**
     * 全称
     */
    @TableField("FULL_NAME")
    private String fullName;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getPids() {
        return pids;
    }

    public void setPids(String pids) {
        this.pids = pids;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Dept{" +
        ", deptId=" + deptId +
        ", pid=" + pid +
        ", pids=" + pids +
        ", fullName=" + fullName +
        "}";
    }
}
