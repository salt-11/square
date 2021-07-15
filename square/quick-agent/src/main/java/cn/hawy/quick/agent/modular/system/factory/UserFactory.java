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
package cn.hawy.quick.agent.modular.system.factory;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hawy.quick.agent.core.common.constant.state.ManagerStatus;
import cn.hawy.quick.agent.modular.system.entity.User;
import cn.hawy.quick.agent.modular.system.model.UserDto;
import cn.stylefeng.roses.core.util.ToolUtil;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户创建工厂
 *
 * @author fengshuonan
 * @date 2017-05-05 22:43
 */
public class UserFactory {

    /**
     * 根据请求创建实体
     */
    public static User createUser(UserDto userDto, String md5Password, String salt) {
        if (userDto == null) {
            return null;
        } else {
            User user = new User();
            BeanUtils.copyProperties(userDto, user);
            user.setCreateTime(new Date());
            user.setPassword(md5Password);
            user.setSalt(salt);
            return user;
        }
    }



    /**
     * 过滤不安全字段并转化为map
     */
    public static Map<String, Object> removeUnSafeFields(User user) {
        if (user == null) {
            return new HashMap<>();
        } else {
            Map<String, Object> map = BeanUtil.beanToMap(user);
            map.remove("password");
            map.remove("salt");
            return map;
        }
    }
}
