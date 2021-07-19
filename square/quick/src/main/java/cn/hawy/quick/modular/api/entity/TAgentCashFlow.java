package cn.hawy.quick.modular.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author hawy
 * @since 2021-07-13
 */
public class TAgentCashFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 渠道号
     */
    private String agentId;

    /**
     * 代理商名称
     */
    private String agentName;

    /**
     * 提现金额
     */
    private Long cashAmount;

    /**
     * 提现状态 1-提现中 2-提现成功 3-提现失败
     */
    private Integer cashStatus;

    /**
     * 提现费率
     */
    private String cashRate;

    /**
     * 提现手续费
     */
    private Long cashFee;

    /**
     * 出款金额
     */
    private Long outAmount;

    /**
     * 出款账户名称
     */
    private String name;

    /**
     * 出款账户
     */
    private String cardNo;

    private String bankName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public Long getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(Long cashAmount) {
        this.cashAmount = cashAmount;
    }

    public Integer getCashStatus() {
        return cashStatus;
    }

    public void setCashStatus(Integer cashStatus) {
        this.cashStatus = cashStatus;
    }

    public String getCashRate() {
        return cashRate;
    }

    public void setCashRate(String cashRate) {
        this.cashRate = cashRate;
    }

    public Long getCashFee() {
        return cashFee;
    }

    public void setCashFee(Long cashFee) {
        this.cashFee = cashFee;
    }

    public Long getOutAmount() {
        return outAmount;
    }

    public void setOutAmount(Long outAmount) {
        this.outAmount = outAmount;
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public String toString() {
        return "TAgentCashFlow{" +
        "id=" + id +
        ", agentId=" + agentId +
        ", agentName=" + agentName +
        ", cashAmount=" + cashAmount +
        ", cashStatus=" + cashStatus +
        ", cashRate=" + cashRate +
        ", cashFee=" + cashFee +
        ", outAmount=" + outAmount +
        ", name=" + name +
        ", cardNo=" + cardNo +
        ", createTime=" + createTime +
        "}";
    }
}
