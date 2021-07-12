package cn.hawy.quick.modular.system.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 部门表
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
     * 父部门id
     */
    @TableField("PID")
    private Long pid;
    /**
     * 父级ids
     */
    @TableField("PIDS")
    private String pids;
    /**
     * 简称
     */
    @TableField("SIMPLE_NAME")
    private String simpleName;
    /**
     * 全称
     */
    @TableField("FULL_NAME")
    private String fullName;

    @TableField("balance")
    private Long balance;

    /**
     * 渠道商公钥
     */
    @TableField("partner_publickey")
    private String partnerPublickey;

    /**
     * 平台私钥
     */
    @TableField("platform_privatekey")
    private String platformPrivatekey;

    /**
     * 提现成本费率
     */
    @TableField("cash_rate")
    private String cashRate;

    @TableField("card_auth_rate")
    private String cardAuthRate;

    @TableField("agent_id")
    private String agentId;

    @TableField("channel_type")
    private Integer channelType;

    @TableField("day_max_amount")
    private Long dayMaxAmount;

    /**
     * 描述
     */
    @TableField("DESCRIPTION")
    private String description;
    /**
     * 版本（乐观锁保留字段）
     */
    @TableField("VERSION")
    private Integer version;
    /**
     * 排序
     */
    @TableField("SORT")
    private Integer sort;
    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 修改时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.UPDATE)
    private Date updateTime;
    /**
     * 创建人
     */
    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT)
    private Long createUser;
    /**
     * 修改人
     */
    @TableField(value = "UPDATE_USER", fill = FieldFill.UPDATE)
    private Long updateUser;


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

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }


    public String getPartnerPublickey() {
		return partnerPublickey;
	}

	public void setPartnerPublickey(String partnerPublickey) {
		this.partnerPublickey = partnerPublickey;
	}

	public String getPlatformPrivatekey() {
		return platformPrivatekey;
	}

	public void setPlatformPrivatekey(String platformPrivatekey) {
		this.platformPrivatekey = platformPrivatekey;
	}


	public String getCashRate() {
		return cashRate;
	}

	public void setCashRate(String cashRate) {
		this.cashRate = cashRate;
	}

	public Long getBalance() {
		return balance;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public String getCardAuthRate() {
		return cardAuthRate;
	}

	public void setCardAuthRate(String cardAuthRate) {
		this.cardAuthRate = cardAuthRate;
	}

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public Integer getChannelType() {
        return channelType;
    }

    public void setChannelType(Integer channelType) {
        this.channelType = channelType;
    }

    public Long getDayMaxAmount() {
        return dayMaxAmount;
    }

    public void setDayMaxAmount(Long dayMaxAmount) {
        this.dayMaxAmount = dayMaxAmount;
    }

    @Override
    public String toString() {
        return "Dept{" +
        ", deptId=" + deptId +
        ", pid=" + pid +
        ", pids=" + pids +
        ", simpleName=" + simpleName +
        ", fullName=" + fullName +
        ", description=" + description +
        ", version=" + version +
        ", sort=" + sort +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", createUser=" + createUser +
        ", updateUser=" + updateUser +
        "}";
    }
}
