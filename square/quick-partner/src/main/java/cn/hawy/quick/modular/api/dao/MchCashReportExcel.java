package cn.hawy.quick.modular.api.dao;

import java.time.LocalDateTime;

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
public class MchCashReportExcel implements Serializable {

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

    private String bankCardNo;

    /**
     * 渠道号
     */
    private String deptId;

    /**
     * 提现金额
     */
    private String cashAmount;

    private String cashStatus;

    private String returnMsg;

    /**
     * 提现手续费
     */
    private String cashFee;
    
    private String cashRate;
    
    private String deptAmount;
    
    private String costAmount;

    /**
     * 出款金额
     */
    private String outAmount;

    /**
     * 下游提现单号
     */
    private String outTradeNo;

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

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
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

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
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

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

	public String getDeptAmount() {
		return deptAmount;
	}

	public void setDeptAmount(String deptAmount) {
		this.deptAmount = deptAmount;
	}

	public String getCostAmount() {
		return costAmount;
	}

	public void setCostAmount(String costAmount) {
		this.costAmount = costAmount;
	}

	public String getCashRate() {
		return cashRate;
	}

	public void setCashRate(String cashRate) {
		this.cashRate = cashRate;
	}
    
}
