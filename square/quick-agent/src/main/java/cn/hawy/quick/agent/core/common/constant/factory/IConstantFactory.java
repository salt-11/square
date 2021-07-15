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
package cn.hawy.quick.agent.core.common.constant.factory;


/**
 * 常量生产工厂的接口
 *
 * @author fengshuonan
 * @date 2017-06-14 21:12
 */
public interface IConstantFactory {


    
    String getDictsByCode(String pcode, String code);

    
    /**
     * 获取渠道业务类型名称
     */
    String getDeptBizTypeName(String bizTypeCode);
    
    /**
     * 获取渠道变动方向名称
     */
    String getDeptDirectionName(String direction);
    
    /**
     * 获取商户提现状态名称
     */
    String getMchCashStatusName(String cashStatus);
    
    /**
     * 获取渠道提现状态名称
     */
    String getDeptCashStatusName(String cashStatus);

}
