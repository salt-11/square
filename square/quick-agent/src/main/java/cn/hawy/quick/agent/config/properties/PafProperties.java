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
 * 易票联配置
 *
 * @author stylefeng
 * @Date 2017/5/23 22:31
 */
@Component
@ConfigurationProperties(prefix = PafProperties.PREFIX)
public class PafProperties {

    public static final String PREFIX = "paf";

    private String mid = "";

    private String encryptId = "";

    private String publicKey = "";

    private String costFee = "40";

    private String minOrderAmount = "";

    private String orderNotify = "";

    private String withdrawNotify = "";

	private String bindCardNotify = "";

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getEncryptId() {
		return encryptId;
	}

	public void setEncryptId(String encryptId) {
		this.encryptId = encryptId;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getCostFee() {
		return costFee;
	}

	public void setCostFee(String costFee) {
		this.costFee = costFee;
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

	public String getWithdrawNotify() {
		return withdrawNotify;
	}

	public void setWithdrawNotify(String withdrawNotify) {
		this.withdrawNotify = withdrawNotify;
	}

	public String getBindCardNotify() {
		return bindCardNotify;
	}

	public void setBindCardNotify(String bindCardNotify) {
		this.bindCardNotify = bindCardNotify;
	}
}
