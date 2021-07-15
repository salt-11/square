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
@ConfigurationProperties(prefix = FfProperties.PREFIX)
public class FfProperties {

    public static final String PREFIX = "ff";
    
    private String url = "";

    private String merchantId = "";
    
    private String key = "";
    
    private String privateKey = "";
    
    private String costPublicKey = "";
  
    private String costFee = "";

    private String minOrderAmount = "";

    private String orderNotify = "";
    
    private String withdrawNotify = "";
    
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getCostPublicKey() {
		return costPublicKey;
	}

	public void setCostPublicKey(String costPublicKey) {
		this.costPublicKey = costPublicKey;
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

	
}
