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
package cn.hawy.quick.agent.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 *  工易付配置
 *
 * @author stylefeng
 * @Date 2017/5/23 22:31
 */
@Component
@ConfigurationProperties(prefix = GyfProperties.PREFIX)
public class GyfProperties {

    public static final String PREFIX = "gyf";

    private String merchantNo = "";

    private String privateKeyPath = "";

    private String publicKeyPath = "";

	private String password = "";

	private String mchUrl = "";

   	private String queryUrl = "";

   	private String transUrl = "";

	private String orderNotify = "";

	private String dfNotify = "";

	private String minOrderAmount = "";

	private String costFee = "50";


	public String getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMchUrl() {
		return mchUrl;
	}

	public void setMchUrl(String mchUrl) {
		this.mchUrl = mchUrl;
	}

	public String getQueryUrl() {
		return queryUrl;
	}

	public void setQueryUrl(String queryUrl) {
		this.queryUrl = queryUrl;
	}

	public String getTransUrl() {
		return transUrl;
	}

	public void setTransUrl(String transUrl) {
		this.transUrl = transUrl;
	}

	public String getMinOrderAmount() {
		return minOrderAmount;
	}

	public void setMinOrderAmount(String minOrderAmount) {
		this.minOrderAmount = minOrderAmount;
	}

	public String getOrderNotify() {
		return orderNotify;
	}

	public void setOrderNotify(String orderNotify) {
		this.orderNotify = orderNotify;
	}

	public String getDfNotify() {
		return dfNotify;
	}

	public void setDfNotify(String dfNotify) {
		this.dfNotify = dfNotify;
	}

	public String getCostFee() {
		return costFee;
	}

	public void setCostFee(String costFee) {
		this.costFee = costFee;
	}
}
