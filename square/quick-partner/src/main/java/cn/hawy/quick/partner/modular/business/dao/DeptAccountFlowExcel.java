package cn.hawy.quick.partner.modular.business.dao;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author hawy
 * @since 2019-07-11
 */
public class DeptAccountFlowExcel implements Serializable {

    /**
     * 渠道号
     */
    private String deptId;

    private String deptName;

    /**
     * 余额
     */
    private String balance;

    /**
     * 金额
     */
    private String amount;

    /**
     * 业务类型,1-支付,2-提现 3-提现失败返还 4- 商户提现手续费分润 5-退款 6-退款失败返还
     */
    private String bizType;

    /**
     * 变动方向,1-加款,2-减款
     */
    private String direction;

    /**
     * 订单号
     */
    private Long tradeNo;

    private LocalDateTime createTime;

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
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
}
