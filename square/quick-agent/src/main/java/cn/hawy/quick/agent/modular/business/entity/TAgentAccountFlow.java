package cn.hawy.quick.agent.modular.business.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author hawy
 * @since 2021-07-13
 */
public class TAgentAccountFlow implements Serializable {

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
     * 余额
     */
    private Long balance;

    /**
     * 金额
     */
    private Long amount;

    /**
     * 业务类型,1-支付,2- 商户提现手续费分润
     */
    private Integer bizType;

    /**
     * 变动方向,1-加款,2-减款
     */
    private Integer direction;

    /**
     * 订单号
     */
    private Long tradeNo;

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

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public Long getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(Long tradeNo) {
        this.tradeNo = tradeNo;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TAgentAccountFlow{" +
        "id=" + id +
        ", agentId=" + agentId +
        ", agentName=" + agentName +
        ", balance=" + balance +
        ", amount=" + amount +
        ", bizType=" + bizType +
        ", direction=" + direction +
        ", tradeNo=" + tradeNo +
        ", createTime=" + createTime +
        "}";
    }
}
