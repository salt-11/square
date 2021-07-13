package cn.hawy.quick.modular.api.entity;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * <p>
 * 渠道信息表
 * </p>
 *
 * @author hawy
 * @since 2021-07-13
 */
public class TDeptInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 渠道号
     */
    @TableId
    private String id;

    /**
     * 渠道名称
     */
    private String deptName;

    /**
     * 账户
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * md5密码盐
     */
    private String salt;

    /**
     * 余额
     */
    private Long balance;

    /**
     * 代理商id
     */
    private String agentId;

    /**
     * 渠道公钥
     */
    private String deptPublickey;

    /**
     * 平台私钥
     */
    private String platformPrivatekey;

    /**
     * 平台公钥
     */
    private String platformPublickey;

    private Long dayMaxAmount;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getDeptPublickey() {
        return deptPublickey;
    }

    public void setDeptPublickey(String deptPublickey) {
        this.deptPublickey = deptPublickey;
    }

    public String getPlatformPrivatekey() {
        return platformPrivatekey;
    }

    public void setPlatformPrivatekey(String platformPrivatekey) {
        this.platformPrivatekey = platformPrivatekey;
    }

    public String getPlatformPublickey() {
        return platformPublickey;
    }

    public void setPlatformPublickey(String platformPublickey) {
        this.platformPublickey = platformPublickey;
    }

    public Long getDayMaxAmount() {
        return dayMaxAmount;
    }

    public void setDayMaxAmount(Long dayMaxAmount) {
        this.dayMaxAmount = dayMaxAmount;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TDeptInfo{" +
        "id=" + id +
        ", deptName=" + deptName +
        ", account=" + account +
        ", password=" + password +
        ", salt=" + salt +
        ", balance=" + balance +
        ", agentId=" + agentId +
        ", deptPublickey=" + deptPublickey +
        ", platformPrivatekey=" + platformPrivatekey +
        ", platformPublickey=" + platformPublickey +
        ", createTime=" + createTime +
        "}";
    }
}
