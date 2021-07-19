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
package cn.hawy.quick.modular.business.controller;


import cn.hawy.quick.core.common.annotion.Permission;
import cn.hawy.quick.core.common.page.LayuiPageFactory;
import cn.hawy.quick.core.util.PayUtil;
import cn.hawy.quick.modular.api.dao.AgentAccountFlowExcel;
import cn.hawy.quick.modular.api.dao.AgentCashFlowExcel;
import cn.hawy.quick.modular.api.entity.TAgentAccountFlow;
import cn.hawy.quick.modular.api.entity.TAgentCashFlow;
import cn.hawy.quick.modular.api.service.*;
import cn.hawy.quick.modular.api.utils.DateUtils;
import cn.hawy.quick.modular.api.utils.ExportExcelUtil;
import cn.hawy.quick.modular.business.warpper.AgentAccountFlowWrapper;
import cn.hawy.quick.modular.business.warpper.AgentCashFlowWrapper;
import cn.hawy.quick.modular.system.service.UserService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.SuccessResponseData;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 日志管理的控制器
 */
@Controller
@RequestMapping("/platform")
public class PlatformController extends BaseController {

    private static String PREFIX = "/modular/business/platform/";

    @Autowired
    TPlatformRateChannelService platformRateChannelService;




    //--------------------------------------------通道费率---------------------------------------------------------------------

    @RequestMapping("/platformRateChannel")
    public String platformRateChannel() {
        return PREFIX + "platform_rate_channel.html";
    }


    @RequestMapping("/platformRateChannelList")
    @ResponseBody
    public Object platformRateChannelList(@RequestParam(required = false) String channel) {
        //获取分页参数
        Page page = LayuiPageFactory.defaultPage();
        List<Map<String, Object>> result = platformRateChannelService.findAll(page, channel);
        page.setRecords(result);
        return LayuiPageFactory.createPageInfo(page);
    }


}
