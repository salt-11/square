package cn.hawy.quick.modular.api.entity;

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
 * @since 2020-05-06
 */
public class TDeptOrderReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private String reportDate;

    private String deptId;

    private String channelNo;

    /**
     * 交易笔数
     */
    private Integer orderNum;

    /**
     * 交易金额
     */
    private Long orderAmount;

    /**
     * 交易渠道利润
     */
    private Long orderDeptAmount;

    /**
     * 交易平台雷润
     */
    private Long orderCostAmount;

    /**
     * 提现笔数
     */
    private Integer cashNum;

    /**
     * 提现金额
     */
    private Long cashAmount;

    /**
     * 提现渠道利润
     */
    private Long cashDeptAmount;

    /**
     * 提现平台利润
     */
    private Long cashCostAmount;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Long getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Long orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Long getOrderDeptAmount() {
        return orderDeptAmount;
    }

    public void setOrderDeptAmount(Long orderDeptAmount) {
        this.orderDeptAmount = orderDeptAmount;
    }

    public Long getOrderCostAmount() {
        return orderCostAmount;
    }

    public void setOrderCostAmount(Long orderCostAmount) {
        this.orderCostAmount = orderCostAmount;
    }

    public Integer getCashNum() {
        return cashNum;
    }

    public void setCashNum(Integer cashNum) {
        this.cashNum = cashNum;
    }

    public Long getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(Long cashAmount) {
        this.cashAmount = cashAmount;
    }

    public Long getCashDeptAmount() {
        return cashDeptAmount;
    }

    public void setCashDeptAmount(Long cashDeptAmount) {
        this.cashDeptAmount = cashDeptAmount;
    }

    public Long getCashCostAmount() {
        return cashCostAmount;
    }

    public void setCashCostAmount(Long cashCostAmount) {
        this.cashCostAmount = cashCostAmount;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TDeptOrderReport{" +
        "reportDate=" + reportDate +
        ", deptId=" + deptId +
        ", channelNo=" + channelNo +
        ", orderNum=" + orderNum +
        ", orderAmount=" + orderAmount +
        ", orderDeptAmount=" + orderDeptAmount +
        ", orderCostAmount=" + orderCostAmount +
        ", cashNum=" + cashNum +
        ", cashAmount=" + cashAmount +
        ", cashDeptAmount=" + cashDeptAmount +
        ", cashCostAmount=" + cashCostAmount +
        ", createTime=" + createTime +
        "}";
    }
}
