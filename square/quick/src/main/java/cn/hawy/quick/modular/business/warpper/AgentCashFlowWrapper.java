/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
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
package cn.hawy.quick.modular.business.warpper;


import cn.hawy.quick.core.common.constant.factory.ConstantFactory;
import cn.hawy.quick.core.util.PayUtil;
import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;

import java.util.List;
import java.util.Map;

/**
 * 日志列表的包装类
 *
 * @author fengshuonan
 * @date 2017年4月5日22:56:24
 */
public class AgentCashFlowWrapper extends BaseControllerWrapper {

    public AgentCashFlowWrapper(Map<String, Object> single) {
        super(single);
    }

    public AgentCashFlowWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    @Override
    protected void wrapTheMap(Map<String, Object> map) {
    	map.put("cashStatusName", ConstantFactory.me().getDeptCashStatusName(String.valueOf(map.get("cashStatus"))));
    	map.put("cashAmount", PayUtil.transFenToYuan(String.valueOf(map.get("cashAmount"))));
    	map.put("cashFee", PayUtil.transFenToYuan(String.valueOf(map.get("cashFee"))));
    	map.put("outAmount", PayUtil.transFenToYuan(String.valueOf(map.get("outAmount"))));
    }
}
