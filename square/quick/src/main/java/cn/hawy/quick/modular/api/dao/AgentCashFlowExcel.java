package cn.hawy.quick.modular.api.dao;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author hawy
 * @since 2019-08-12
 */
public class AgentCashFlowExcel implements Serializable {

    private Integer id;

    /**
     * 渠道号
     */
    private String agentId;

    private String agentName;


    /**
     * 提现金额
     */
    private String cashAmount;

    /**
     * 提现状态 1-提现中 2-提现成功 3-提现失败
     */
    private String cashStatus;

    /**
     * 提现手续费
     */
    private String cashFee;

    /**
     * 出款金额
     */
    private String outAmount;

    /**
     * 出款账户名称
     */
    private String name;

    /**
     * 出款账户
     */
    private String cardNo;

    private String bankName;

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

    public String getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(String cashAmount) {
        this.cashAmount = cashAmount;
    }

    public String getCashStatus() {
        return cashStatus;
    }

    public void setCashStatus(String cashStatus) {
        this.cashStatus = cashStatus;
    }

    public String getCashFee() {
        return cashFee;
    }

    public void setCashFee(String cashFee) {
        this.cashFee = cashFee;
    }

    public String getOutAmount() {
        return outAmount;
    }

    public void setOutAmount(String outAmount) {
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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}