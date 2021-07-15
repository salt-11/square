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
@ConfigurationProperties(prefix = SumProperties.PREFIX)
public class SumProperties {

    public static final String PREFIX = "sum";

    private String appId = "";

    private String agentId = "";

    private String password = "";

    private String privateKeyPath = "";

    private String publicKeyPath = "";

    private String url = "";

    private String domain = "";

    private String shareMerNo = "";

    private String costRate = "";

    private String costFee = "40";

    private String minOrderAmount = "";

    private String orderNotify = "";

    private String signNotify = "";

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getShareMerNo() {
		return shareMerNo;
	}

	public void setShareMerNo(String shareMerNo) {
		this.shareMerNo = shareMerNo;
	}

	public String getCostRate() {
		return costRate;
	}

	public void setCostRate(String costRate) {
		this.costRate = costRate;
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

	public String getSignNotify() {
		return signNotify;
	}

	public void setSignNotify(String signNotify) {
		this.signNotify = signNotify;
	}

	public String getCostFee() {
		return costFee;
	}

	public void setCostFee(String costFee) {
		this.costFee = costFee;
	}
}
