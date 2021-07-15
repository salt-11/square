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
package cn.hawy.quick.partner.modular.business.warpper;
import cn.hawy.quick.partner.core.common.constant.factory.ConstantFactory;
import cn.hawy.quick.partner.core.util.PayUtil;
import cn.hutool.core.util.StrUtil;
import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.core.util.ToolUtil;

import java.util.List;
import java.util.Map;

/**
 * 日志列表的包装类
 *
 * @author fengshuonan
 * @date 2017年4月5日22:56:24
 */
public class CardAuthWrapper extends BaseControllerWrapper {

    public CardAuthWrapper(Map<String, Object> single) {
        super(single);
    }

    public CardAuthWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    @Override
    protected void wrapTheMap(Map<String, Object> map) {
        map.put("idNo", StrUtil.subPre(String.valueOf(map.get("idNo")),4)+"*****"+StrUtil.subSuf(String.valueOf(map.get("idNo")),-4));
        map.put("cardNo", StrUtil.subPre(String.valueOf(map.get("cardNo")),4)+"*****"+StrUtil.subSuf(String.valueOf(map.get("cardNo")),-4));
    	map.put("amount", PayUtil.transFenToYuan(String.valueOf(map.get("amount"))));
    }
}