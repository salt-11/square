package cn.hawy.quick.agent.modular.business.entity;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PayOrderContact implements Serializable {


	@TableId
	private Long orderId;

	/**
	 * 用户号
	 */
	private String mchId;

	private String mchName;

	private String bankCardNo;

	private String deptType;

	private String deptId;

	/**
	 * 订单金额
	 */
	private String orderAmount;

	/**
	 * 商户费率
	 */
	private String mchRate;

	/**
	 * 商户手续费
	 */
	private String mchFee;

	/**
	 * 渠道商费率
	 */
	private String deptRate;

	/**
	 * 渠道商利润
	 */
	private String deptAmount;

	/**
	 * 订单状态 1-支付中 2-支付成功 3-支付失败
	 */
	private String orderStatus;

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

	public String getBankCardNo() {
		return bankCardNo;
	}

	public void setBankCardNo(String bankCardNo) {
		this.bankCardNo = bankCardNo;
	}

	public String getDeptType() {
		return deptType;
	}

	public void setDeptType(String deptType) {
		this.deptType = deptType;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getMchRate() {
		return mchRate;
	}

	public void setMchRate(String mchRate) {
		this.mchRate = mchRate;
	}

	public String getMchFee() {
		return mchFee;
	}

	public void setMchFee(String mchFee) {
		this.mchFee = mchFee;
	}

	public String getDeptRate() {
		return deptRate;
	}

	public void setDeptRate(String deptRate) {
		this.deptRate = deptRate;
	}

	public String getDeptAmount() {
		return deptAmount;
	}

	public void setDeptAmount(String deptAmount) {
		this.deptAmount = deptAmount;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public LocalDateTime getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(LocalDateTime orderTime) {
		this.orderTime = orderTime;
	}
}
