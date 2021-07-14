package cn.hawy.quick.modular.api.entity;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author hawy
 * @since 2019-07-12
 */
public class TMchCashFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long cashId;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户名称
     */
    private String mchName;

    /**
     * 渠道号
     */
    private String deptId;

    private String bankCardNo;

    /**
     * 下游提现单号
     */
    private String outTradeNo;

    private String outMchId;

    /**
     * 提现金额
     */
    private Long cashAmount;


    /**
     * 提现手续费
     */
    private Long cashFee;

    /**
     * 出款金额
     */
    private Long outAmount;

    /**
     * 提现状态 1-提现中 2-提现成功 3-提现失败
     */
    private Integer cashStatus;

    private String returnMsg;

    /**
     * 渠道提现费率
     */
    private String cashRate;

    private Long deptAmount;

    //代理商id
    private String agentId;

    private String agentRate;

    private Long agentAmount;

    private String costFee;

    private Long costAmount;

    /**
     * 通知地址
     */
    private String notifyUrl;

    private Integer notifyCount;

    private String notifyResult;

    private LocalDateTime notifyTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    public Long getCashId() {
        return cashId;
    }

    public void setCashId(Long cashId) {
        this.cashId = cashId;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getMchName() {
        return mchName;
    }

    public void setMchName(String mchName) {
        this.mchName = mchName;
    }


    public String getOutMchId() {
		return outMchId;
	}

	public void setOutMchId(String outMchId) {
		this.outMchId = outMchId;
	}

	public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public Long getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(Long cashAmount) {
        this.cashAmount = cashAmount;
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

    public Long getDeptAmount() {
        return deptAmount;
    }

    public void setDeptAmount(Long deptAmount) {
        this.deptAmount = deptAmount;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public Integer getNotifyCount() {
        return notifyCount;
    }

    public void setNotifyCount(Integer notifyCount) {
        this.notifyCount = notifyCount;
    }

    public String getNotifyResult() {
        return notifyResult;
    }

    public void setNotifyResult(String notifyResult) {
        this.notifyResult = notifyResult;
    }

    public LocalDateTime getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(LocalDateTime notifyTime) {
        this.notifyTime = notifyTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

    public String getCostFee() {
        return costFee;
    }

    public void setCostFee(String costFee) {
        this.costFee = costFee;
    }

    public Long getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(Long costAmount) {
        this.costAmount = costAmount;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentRate() {
        return agentRate;
    }

    public void setAgentRate(String agentRate) {
        this.agentRate = agentRate;
    }

    public Long getAgentAmount() {
        return agentAmount;
    }

    public void setAgentAmount(Long agentAmount) {
        this.agentAmount = agentAmount;
    }

    @Override
    public String toString() {
        return "TMchCashFlow{" +
        "cashId=" + cashId +
        ", mchId=" + mchId +
        ", mchName=" + mchName +
        ", deptId=" + deptId +
        ", bankCardNo=" + bankCardNo +
        ", outTradeNo=" + outTradeNo +
        ", cashAmount=" + cashAmount +
        ", cashFee=" + cashFee +
        ", outAmount=" + outAmount +
        ", cashStatus=" + cashStatus +
        ", cashRate=" + cashRate +
        ", deptAmount=" + deptAmount +
        ", notifyUrl=" + notifyUrl +
        ", notifyCount=" + notifyCount +
        ", notifyResult=" + notifyResult +
        ", notifyTime=" + notifyTime +
        ", createTime=" + createTime +
        "}";
    }
}
