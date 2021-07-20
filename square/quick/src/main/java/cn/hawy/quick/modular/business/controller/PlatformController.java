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

import cn.hawy.quick.core.common.annotion.BussinessLog;
import cn.hawy.quick.core.common.annotion.Permission;
import cn.hawy.quick.core.common.constant.dictmap.UserDict;
import cn.hawy.quick.core.common.exception.BizExceptionEnum;
import cn.hawy.quick.core.common.page.LayuiPageFactory;
import cn.hawy.quick.core.log.LogObjectHolder;
import cn.hawy.quick.modular.api.entity.TPlatformRateChannel;
import cn.hawy.quick.modular.api.param.PlatformRateChannelParam;
import cn.hawy.quick.modular.api.service.*;
import cn.hawy.quick.modular.system.entity.User;
import cn.hawy.quick.modular.system.model.UserDto;
import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.reqres.response.SuccessResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.validation.Valid;
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

    @RequestMapping("/platformRateChannelAdd")
    public String platformRateChannelAdd() {
        return PREFIX + "platform_rate_channel_add.html";
    }
    /**
     * 跳转到编辑管理员页面
     *
     * @author fengshuonan
     * @Date 2018/12/24 22:43
     */
    @RequestMapping("/platformRateChannelEdit")
    public String userEdit() {
        return PREFIX + "platform_rate_channel_edit.html";
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

    /**
     * 添加平台通道费率
     * @param channelParam
     * @param result
     * @return
     */
    @RequestMapping("/add")
    @ResponseBody
    public ResponseData add(@Valid PlatformRateChannelParam channelParam, BindingResult result) {
        if (result.hasErrors()) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        TPlatformRateChannel byBankCodeAndChannel =platformRateChannelService.findByBankCodeAndChannel(channelParam.getBankCode(),channelParam.getChannel());
        if(!BeanUtil.isEmpty(byBankCodeAndChannel)){
            throw  new ServiceException(400,"该通道的银行编码已存在，请重新输入");
        }
        this.platformRateChannelService.addRateChannel(channelParam);
        return new SuccessResponseData();
    }
    /**
     * 编辑接口
     *
     * @author xxx
     * @Date 2019-12-27
     */
    @RequestMapping("/edit")
    @ResponseBody
    public ResponseData editItem(PlatformRateChannelParam channelParam) {
        this.platformRateChannelService.update(channelParam);
        return ResponseData.success();
    }

    /**
     * 删除店铺
     *
     * @author xxx
     * @Date 2019-12-27
     */
    @RequestMapping("/delete")
    @ResponseBody
    public ResponseData delete(PlatformRateChannelParam channelParam) {
        platformRateChannelService.removeById(channelParam.getId());
        return ResponseData.success();
    }
    /**
     * 查看详情接口
     *
     * @author xxx
     * @Date 2019-12-27
     */
    @RequestMapping("/detail")
    @ResponseBody
    public ResponseData detail(PlatformRateChannelParam channelParam){
        TPlatformRateChannel tPlatformRateChannel = this.platformRateChannelService.getById(channelParam.getId());
        return ResponseData.success(tPlatformRateChannel);
    }

}
