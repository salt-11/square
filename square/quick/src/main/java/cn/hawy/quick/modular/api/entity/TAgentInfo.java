package cn.hawy.quick.modular.api.entity;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 代理商信息表
 * </p>
 *
 * @author hawy
 * @since 2021-07-13
 */
public class TAgentInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 渠道号
     */
    @TableId
    private String id;

    /**
     * 代理名称
     */
    private String agentName;

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
     * 银行卡姓名
     */
    private String name;

    /**
     * 银行卡号
     */
    private String cardNo;
    /**
     * 银行卡号
     */
    private String bankName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TAgentInfo{" +
        "id=" + id +
        ", agentName=" + agentName +
        ", account=" + account +
        ", password=" + password +
        ", salt=" + salt +
        ", balance=" + balance +
        ", name=" + name +
        ", cardNo=" + cardNo +
        ", createTime=" + createTime +
        "}";
    }
}
