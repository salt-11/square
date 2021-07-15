package cn.hawy.quick.partner.modular.business.dao;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author hawy
 * @since 2020-05-06
 */
public class DeptOrderReportExcel implements Serializable {


    private String reportDate;

    private String deptId;

    private String deptName;

    private String channelNo;

    /**
     * 交易笔数
     */
    private String orderNum;

    /**
     * 交易金额
     */
    private String orderAmount;

    /**
     * 交易渠道利润
     */
    private String orderDeptAmount;

    /**
     * 交易平台雷润
     */
    private String orderCostAmount;

    /**
     * 提现笔数
     */
    private String cashNum;

    /**
     * 提现金额
     */
    private String cashAmount;

    /**
     * 提现渠道利润
     */
    private String cashDeptAmount;

    /**
     * 提现平台利润
     */
    private String cashCostAmount;

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

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderDeptAmount() {
        return orderDeptAmount;
    }

    public void setOrderDeptAmount(String orderDeptAmount) {
        this.orderDeptAmount = orderDeptAmount;
    }

    public String getOrderCostAmount() {
        return orderCostAmount;
    }

    public void setOrderCostAmount(String orderCostAmount) {
        this.orderCostAmount = orderCostAmount;
    }

    public String getCashNum() {
        return cashNum;
    }

    public void setCashNum(String cashNum) {
        this.cashNum = cashNum;
    }

    public String getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(String cashAmount) {
        this.cashAmount = cashAmount;
    }

    public String getCashDeptAmount() {
        return cashDeptAmount;
    }

    public void setCashDeptAmount(String cashDeptAmount) {
        this.cashDeptAmount = cashDeptAmount;
    }

    public String getCashCostAmount() {
        return cashCostAmount;
    }

    public void setCashCostAmount(String cashCostAmount) {
        this.cashCostAmount = cashCostAmount;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
