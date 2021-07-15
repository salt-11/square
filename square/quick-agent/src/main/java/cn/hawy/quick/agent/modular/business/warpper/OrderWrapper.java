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
package cn.hawy.quick.agent.modular.business.warpper;

import cn.hawy.quick.agent.core.util.PayUtil;
import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.core.util.ToolUtil;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 日志列表的包装类
 *
 * @author fengshuonan
 * @date 2017年4月5日22:56:24
 */
public class OrderWrapper extends BaseControllerWrapper {

    public OrderWrapper(Map<String, Object> single) {
        super(single);
    }

    public OrderWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    @Override
    protected void wrapTheMap(Map<String, Object> map) {
    	map.put("orderAmount", PayUtil.transFenToYuan(String.valueOf(map.get("orderAmount"))));
    	map.put("mchFee", PayUtil.transFenToYuan(String.valueOf(map.get("mchFee"))));
    	map.put("deptAmount", PayUtil.transFenToYuan(String.valueOf(map.get("deptAmount"))));
        if (map.get("agentAmount") == null) {
            map.put("agentAmount", "");
        }else {
            map.put("agentAmount", PayUtil.transFenToYuan(String.valueOf(map.get("agentAmount"))));
        }
    	if (map.get("costAmount") == null) {
            map.put("costAmount", "");
        } else {
            map.put("costAmount", PayUtil.transFenToYuan(String.valueOf(map.get("costAmount"))));
        }
    }
}
