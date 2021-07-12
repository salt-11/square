package cn.hawy.quick.modular.api.channel.efpsDto;


public class OrderNotify {

	private String customerCode;
	private String outTradeNo;
	private String transactionNo;
	private String amount;
	private String payState;
	private String payTime;
	private String settCycle;
	private String settCycleInterval;
	private String procedureFee;
	private String attachData;
	private String nonceStr;


	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getTransactionNo() {
		return transactionNo;
	}

	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getPayState() {
		return payState;
	}

	public void setPayState(String payState) {
		this.payState = payState;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public String getSettCycle() {
		return settCycle;
	}

	public void setSettCycle(String settCycle) {
		this.settCycle = settCycle;
	}

	public String getSettCycleInterval() {
		return settCycleInterval;
	}

	public void setSettCycleInterval(String settCycleInterval) {
		this.settCycleInterval = settCycleInterval;
	}

	public String getProcedureFee() {
		return procedureFee;
	}

	public void setProcedureFee(String procedureFee) {
		this.procedureFee = procedureFee;
	}

	public String getAttachData() {
		return attachData;
	}

	public void setAttachData(String attachData) {
		this.attachData = attachData;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
}
