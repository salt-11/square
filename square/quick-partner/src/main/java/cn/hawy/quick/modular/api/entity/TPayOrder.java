package cn.hawy.quick.modular.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author hawy
 * @since 2019-07-11
 */
public class TPayOrder implements Serializable {

    @TableId
    private Long orderId;

    /**
     * 用户号
     */
    private String mchId;

    private String mchName;

    private String bankCardNo;

    private String deptId;

    /**
     * 订单金额
     */
    private Long orderAmount;

    /**
     * 商户费率
     */
    private String mchRate;

    /**
     * 商户手续费
     */
    private Long mchFee;

    /**
     * 渠道商费率
     */
    private String deptRate;



    /**
     * 渠道商利润
     */
    private Long deptAmount;

    //代理商id
    private String agentId;

    private String agentRate;

    private Long agentAmount;

    private String costRate;

    private Long costAmount;

    /**
     * 订单状态 1-支付中 2-支付成功 3-支付失败
     */
    private Integer orderStatus;

    /**
     * 错误原因
     */
    private String returnMsg;

    /**
     * 订单号
     */
    private String outTradeNo;

    /**
     * 订单时间
     */
    private LocalDateTime orderTime;

    /**
     * 外部商户号
     */
    private String outMchId;

    /**
     * 外部商户号
     */
    private String merPoolNo;

    /**
     * 商户金额
     */
    private Long mchAmount;

    /**
     * 分账状态
     */
    private Integer splitStatus;

    /**
     * 通知地址
     */
    private String notifyUrl;

    /**
     * 通知次数
     */
    private Integer notifyCount;

    /**
     * 通知时间
     */
    private LocalDateTime notifyTime;

    /**
     * 通知结果
     */
    private String notifyResult;

    private String token;

    private Long cashAmount;

    private String channel;

    private String channelNo;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public Long getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Long orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOutMchId() {
        return outMchId;
    }

    public void setOutMchId(String outMchId) {
        this.outMchId = outMchId;
    }

    public String getMchRate() {
        return mchRate;
    }

    public void setMchRate(String mchRate) {
        this.mchRate = mchRate;
    }

    public Long getMchFee() {
        return mchFee;
    }

    public void setMchFee(Long mchFee) {
        this.mchFee = mchFee;
    }

    public Long getMchAmount() {
        return mchAmount;
    }

    public void setMchAmount(Long mchAmount) {
        this.mchAmount = mchAmount;
    }

    public String getDeptRate() {
		return deptRate;
	}

	public void setDeptRate(String deptRate) {
		this.deptRate = deptRate;
	}

	public Long getDeptAmount() {
		return deptAmount;
	}

	public void setDeptAmount(Long deptAmount) {
		this.deptAmount = deptAmount;
	}

	public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }



    public Integer getSplitStatus() {
        return splitStatus;
    }

    public void setSplitStatus(Integer splitStatus) {
        this.splitStatus = splitStatus;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
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

	public LocalDateTime getNotifyTime() {
		return notifyTime;
	}

	public void setNotifyTime(LocalDateTime notifyTime) {
		this.notifyTime = notifyTime;
	}

	public String getNotifyResult() {
		return notifyResult;
	}

	public void setNotifyResult(String notifyResult) {
		this.notifyResult = notifyResult;
	}

	public String getBankCardNo() {
		return bankCardNo;
	}

	public void setBankCardNo(String bankCardNo) {
		this.bankCardNo = bankCardNo;
	}


	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCostRate() {
		return costRate;
	}

	public void setCostRate(String costRate) {
		this.costRate = costRate;
	}

	public Long getCostAmount() {
		return costAmount;
	}

	public void setCostAmount(Long costAmount) {
		this.costAmount = costAmount;
	}

	public Long getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(Long cashAmount) {
		this.cashAmount = cashAmount;
	}


	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public String getMerPoolNo() {
		return merPoolNo;
	}

	public void setMerPoolNo(String merPoolNo) {
		this.merPoolNo = merPoolNo;
	}

	public String getChannelNo() {
		return channelNo;
	}

	public void setChannelNo(String channelNo) {
		this.channelNo = channelNo;
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
        return "TPayOrder{" +
        "orderId=" + orderId +
        ", mchId=" + mchId +
        ", mchName=" + mchName +
        ", deptId=" + deptId +
        ", channel=" + channel +
        ", outTradeNo=" + outTradeNo +
        ", orderAmount=" + orderAmount +
        ", outMchId=" + outMchId +
        ", mchRate=" + mchRate +
        ", mchFee=" + mchFee +
        ", mchAmount=" + mchAmount +
        ", orderStatus=" + orderStatus +
        ", returnMsg=" + returnMsg +
        ", splitStatus=" + splitStatus +
        ", orderTime=" + orderTime +
        "}";
    }
}
