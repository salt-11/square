/**
 * Copyright 2018-2020 stylefeng & fengshuonan (sn93@qq.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.hawy.quick.partner.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;



/**
 * 易票联配置
 *
 * @author stylefeng
 * @Date 2017/5/23 22:31
 */
@Component
@ConfigurationProperties(prefix = EfpsProperties.PREFIX)
public class EfpsProperties {

    public static final String PREFIX = "efps";
    
    private String url = "";

    private String privateKeyPath = "";
    
    private String publicKeyPath = "";
    
    private String customerCode = "";
    
    private String signNo = "";
    
    private String password = "";
    
    private String orderNotifyUrl = "";
    
    private String splitNotifyUrl = "";
    
    private String mchCashNotifyUrl = "";
    
    private Boolean isServiceFee = true;
    
    private Boolean isQuerySubCustomer = true;
    
    private Long cashRate = 0L;
    

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPrivateKeyPath() {
		return privateKeyPath;
	}

	public void setPrivateKeyPath(String privateKeyPath) {
		this.privateKeyPath = privateKeyPath;
	}

	public String getPublicKeyPath() {
		return publicKeyPath;
	}

	public void setPublicKeyPath(String publicKeyPath) {
		this.publicKeyPath = publicKeyPath;
	}
	
	public Boolean getIsServiceFee() {
		return isServiceFee;
	}

	public void setIsServiceFee(Boolean isServiceFee) {
		this.isServiceFee = isServiceFee;
	}

	public String getSignNo() {
		return signNo;
	}

	public void setSignNo(String signNo) {
		this.signNo = signNo;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOrderNotifyUrl() {
		return orderNotifyUrl;
	}

	public void setOrderNotifyUrl(String orderNotifyUrl) {
		this.orderNotifyUrl = orderNotifyUrl;
	}

	public String getSplitNotifyUrl() {
		return splitNotifyUrl;
	}

	public void setSplitNotifyUrl(String splitNotifyUrl) {
		this.splitNotifyUrl = splitNotifyUrl;
	}

	public String getMchCashNotifyUrl() {
		return mchCashNotifyUrl;
	}

	public void setMchCashNotifyUrl(String mchCashNotifyUrl) {
		this.mchCashNotifyUrl = mchCashNotifyUrl;
	}

	public Long getCashRate() {
		return cashRate;
	}

	public void setCashRate(Long cashRate) {
		this.cashRate = cashRate;
	}

	public Boolean getIsQuerySubCustomer() {
		return isQuerySubCustomer;
	}

	public void setIsQuerySubCustomer(Boolean isQuerySubCustomer) {
		this.isQuerySubCustomer = isQuerySubCustomer;
	}
	
}
