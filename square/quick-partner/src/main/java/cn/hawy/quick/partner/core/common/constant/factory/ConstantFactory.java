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
package cn.hawy.quick.partner.core.common.constant.factory;


import cn.hawy.quick.partner.core.common.constant.cache.Cache;

import cn.hawy.quick.partner.modular.system.entity.*;
import cn.hawy.quick.partner.modular.system.mapper.DictMapper;
import cn.stylefeng.roses.core.util.SpringContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 常量的生产工厂
 *
 * @author fengshuonan
 * @date 2017年2月13日 下午10:55:21
 */
@Component
@DependsOn("springContextHolder")
public class ConstantFactory implements IConstantFactory {


    private DictMapper dictMapper = SpringContextHolder.getBean(DictMapper.class);


    public static IConstantFactory me() {
        return SpringContextHolder.getBean("constantFactory");
    }



    @Override
    public String getDictsByCode(String pcode, String code) {
        Dict temp = new Dict();
        temp.setCode(pcode);
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>(temp);
        Dict dict = dictMapper.selectOne(queryWrapper);
        if (dict == null) {
            return "";
        } else {
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper = wrapper.eq("PID", dict.getDictId());
            List<Dict> dicts = dictMapper.selectList(wrapper);
            for (Dict item : dicts) {
                if (item.getCode() != null && item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
    }



    @Override
    @Cacheable(value = Cache.CONSTANT, key = "methodName +#bizTypeCode")
    public String getDeptBizTypeName(String bizTypeCode) {
        // TODO Auto-generated method stub
        return getDictsByCode("DeptBizType", bizTypeCode);
    }


    @Override
    @Cacheable(value = Cache.CONSTANT, key = "methodName +#direction")
    public String getDeptDirectionName(String direction) {
        // TODO Auto-generated method stub
        return getDictsByCode("DeptDirection", direction);
    }

    @Override
    @Cacheable(value = Cache.CONSTANT, key = "methodName +#cashStatus")
    public String getMchCashStatusName(String cashStatus) {
        return getDictsByCode("MchCashStatus", cashStatus);
    }


    @Override
    @Cacheable(value = Cache.CONSTANT, key = "methodName +#cashStatus")
    public String getDeptCashStatusName(String cashStatus) {
        // TODO Auto-generated method stub
        return getDictsByCode("DeptCashStatus", cashStatus);
    }


}
