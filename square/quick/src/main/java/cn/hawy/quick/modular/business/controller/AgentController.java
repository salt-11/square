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
import cn.hawy.quick.core.common.exception.BizExceptionEnum;
import cn.hawy.quick.core.common.page.LayuiPageFactory;
import cn.hawy.quick.core.shiro.ShiroKit;
import cn.hawy.quick.core.util.PayUtil;
import cn.hawy.quick.modular.api.dao.AgentAccountFlowExcel;
import cn.hawy.quick.modular.api.dao.AgentCashFlowExcel;
import cn.hawy.quick.modular.api.entity.*;
import cn.hawy.quick.modular.api.param.AgentRateChannelParam;
import cn.hawy.quick.modular.api.param.PlatformRateChannelParam;
import cn.hawy.quick.modular.api.service.*;
import cn.hawy.quick.modular.api.utils.DateUtils;
import cn.hawy.quick.modular.api.utils.ExportExcelUtil;
import cn.hawy.quick.modular.business.warpper.AgentAccountFlowWrapper;
import cn.hawy.quick.modular.business.warpper.AgentCashFlowWrapper;
import cn.hawy.quick.modular.system.service.UserService;
import cn.hutool.core.util.StrUtil;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.reqres.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 日志管理的控制器
 */
@Controller
@RequestMapping("/agent")
public class AgentController extends BaseController {

    private static String PREFIX = "/modular/business/agent/";

    @Autowired
    TAgentInfoService agentInfoService;
    @Autowired
    TAgentCashFlowService agentCashFlowService;
    @Autowired
    TAgentAccountFlowService agentAccountFlowService;
    @Autowired
    UserService userService;
    @Autowired
    TAgentRateChannelService agentRateChannelService;
    @Autowired
    TDeptInfoService deptInfoService;


    /**
     * 跳转到代理提现的首页
     */
    @RequestMapping("/agentCashFlow")
    public String agentCashFlow() {
        return PREFIX + "agent_cash_flow.html";
    }
    @RequestMapping("/agentRateChannelAdd")
    public String platformRateChannelAdd() {
        return PREFIX + "agent_rate_channel_add.html";
    }
    /**
     * 跳转到编辑管理员页面
     *
     * @author fengshuonan
     * @Date 2018/12/24 22:43
     */
    @RequestMapping("/agentRateChannelEdit")
    public String userEdit() {
        return PREFIX + "agent_rate_channel_edit.html";
    }

    /**
     * 跳转到代理提现的列表
     */
    @RequestMapping("/agentCashFlowList")
    @Permission
    @ResponseBody
    public Object agentCashFlowList(@RequestParam(required = false) String beginTime,
                                   @RequestParam(required = false) String endTime,
                                    @RequestParam(required = false) String agentId,
                                   @RequestParam(required = false) String cashStatusName,
                                   @RequestParam(required = false) String name) {

        Page page = LayuiPageFactory.defaultPage();
        List<Map<String, Object>> result = agentCashFlowService.findAll(page, beginTime, endTime, cashStatusName, agentId, name);
        page.setRecords(new AgentCashFlowWrapper(result).wrap());
        return LayuiPageFactory.createPageInfo(page);
    }

    private List<AgentCashFlowExcel> transForCashExport(List<TAgentCashFlow> dataList) {
        List<AgentCashFlowExcel> dataVals = new ArrayList<AgentCashFlowExcel>();
        for (TAgentCashFlow data : dataList) {
            AgentCashFlowExcel agentCashFlowExcel = new AgentCashFlowExcel();
            agentCashFlowExcel.setId(data.getId());
            agentCashFlowExcel.setAgentId(data.getAgentId());
            agentCashFlowExcel.setAgentName(data.getAgentName());

            if (data.getCashAmount() == null) {
                agentCashFlowExcel.setCashAmount("0");
            } else {
                agentCashFlowExcel.setCashAmount(PayUtil.transFenToYuan(String.valueOf(data.getCashAmount())));
            }
            if (data.getCashStatus() == 1) {
                agentCashFlowExcel.setCashStatus("提现中");
            } else if (data.getCashStatus() == 2) {
                agentCashFlowExcel.setCashStatus("提现成功");
            } else if (data.getCashStatus() == 3) {
                agentCashFlowExcel.setCashStatus("提现失败");
            }
            if (data.getCashFee() == null) {
                agentCashFlowExcel.setCashFee("0");
            } else {
                agentCashFlowExcel.setCashFee(PayUtil.transFenToYuan(String.valueOf(data.getCashFee())));
            }
            if (data.getOutAmount() == null) {
                agentCashFlowExcel.setOutAmount("0");
            } else {
                agentCashFlowExcel.setOutAmount(PayUtil.transFenToYuan(String.valueOf(data.getOutAmount())));
            }

            agentCashFlowExcel.setName(data.getName());
            agentCashFlowExcel.setCardNo(data.getCardNo());
            agentCashFlowExcel.setBankName(data.getBankName());
            agentCashFlowExcel.setCreateTime(data.getCreateTime());
            dataVals.add(agentCashFlowExcel);
        }
        return dataVals;
    }


    /**
     * 报表导出
     */
    @RequestMapping("/agentCashFlowExcelList")
    @ResponseBody
    public void agentCashFlowExcelList(@RequestParam(required = false) String beginTime,
                                      @RequestParam(required = false) String endTime,
                                      @RequestParam(required = false) String cashStatusName,
                                      @RequestParam(required = false) String agentId,
                                      @RequestParam(required = false) String name,
                                      HttpServletRequest request, HttpServletResponse response) throws Exception {
//        Map<String, Object> result = new HashMap<String, Object>();
        try {
            // System.out.println(userAuthen.next().getIdcardName());
            String excelName = "代理提现列表" + DateUtils.getCurrentTimeStr();
            String sheetName = "代理提现列表";
            String titleName = "代理提现列表";
            int[] colWidths = {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
            String[] colNames = {"提现号", "代理号", "代理名称", "提现金额", "提现状态", "提现手续费", "出款金额", "出款账户名", "出款账户号", "出款银行", "创建时间"};
            List<AgentCashFlowExcel> dataVals = new ArrayList<AgentCashFlowExcel>();

            List<TAgentCashFlow> pay = agentCashFlowService.find(beginTime, endTime, cashStatusName, agentId, name);
            dataVals.addAll(this.transForCashExport(pay));

            // 输出到服务器
            // OutputStream outps = response.getOutputStream(); // 输出到客户端
            response.setContentType("octets/stream");
            excelName = new String(excelName.getBytes("GB2312"), "ISO8859-1") + ".xls";
            response.addHeader("Content-Disposition", "attachment;filename=" + excelName);
            ExportExcelUtil<AgentCashFlowExcel> epec = new ExportExcelUtil<AgentCashFlowExcel>();
            epec.expExcel(sheetName, titleName, colWidths, colNames, dataVals, response.getOutputStream());
        } catch (Exception e) {

        }
    }


    /**------------------------------------------代理账户流水--------------------------------------------------------------**/

    /**
     * 跳转到代理账户流水的首页
     */
    @RequestMapping("/agentAccountFlow")
    public String agentAccountFlow() {
        return PREFIX + "agent_account_flow.html";
    }

    /**
     * 跳转到代理账户流水list
     */
    @RequestMapping("/agentAccountFlowList")
    @ResponseBody
    public Object agentAccountFlowList(@RequestParam(required = false) String beginTime,
                                      @RequestParam(required = false) String endTime,
                                       @RequestParam(required = false) String agentId,
                                      @RequestParam(required = false) String bizTypeName,
                                      @RequestParam(required = false) String directionName) {
        //获取分页参数
        Page page = LayuiPageFactory.defaultPage();

        List<Map<String, Object>> result = agentAccountFlowService.findAll(page, beginTime, endTime, agentId, bizTypeName, directionName);
        page.setRecords(new AgentAccountFlowWrapper(result).wrap());
        return LayuiPageFactory.createPageInfo(page);
    }

    private List<AgentAccountFlowExcel> transForAccountExport(List<TAgentAccountFlow> dataList) {
        List<AgentAccountFlowExcel> dataVals = new ArrayList<AgentAccountFlowExcel>();
        for (TAgentAccountFlow data : dataList) {
            AgentAccountFlowExcel agentAccountFlowExcel = new AgentAccountFlowExcel();
            agentAccountFlowExcel.setAgentId(data.getAgentId());
            agentAccountFlowExcel.setAgentName(data.getAgentName());

            if (data.getBalance() == null) {
                agentAccountFlowExcel.setBalance("0");
            } else {
                agentAccountFlowExcel.setBalance(PayUtil.transFenToYuan(String.valueOf(data.getBalance())));
            }
            if (data.getAmount() == null) {
                agentAccountFlowExcel.setAmount("0");
            } else {
                agentAccountFlowExcel.setAmount(PayUtil.transFenToYuan(String.valueOf(data.getAmount())));
            }
            if (data.getBizType() == 1) {
                agentAccountFlowExcel.setBizType("商户交易分润");
            } else if (data.getBizType() == 2) {
                agentAccountFlowExcel.setBizType("商户提现分润");
            } else if (data.getBizType() == 3) {
                agentAccountFlowExcel.setBizType("提现");
            } else if (data.getBizType() == 4) {
                agentAccountFlowExcel.setBizType("提现拒绝");
            }

            if (data.getDirection() == 1) {
                agentAccountFlowExcel.setDirection("加款");
            } else if (data.getDirection() == 2) {
                agentAccountFlowExcel.setDirection("减款");
            }

            agentAccountFlowExcel.setTradeNo(data.getTradeNo());
            agentAccountFlowExcel.setCreateTime(data.getCreateTime());
            dataVals.add(agentAccountFlowExcel);
        }
        return dataVals;
    }


    /**
     * 报表导出
     */
    @RequestMapping("/agentAccountFlowExcelList")
    @ResponseBody
    public void agentAccountFlowExcelList(@RequestParam(required = false) String beginTime,
                                         @RequestParam(required = false) String endTime,
                                         @RequestParam(required = false) String agentId,
                                         @RequestParam(required = false) String bizTypeName,
                                         @RequestParam(required = false) String directionName,
                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
//        Map<String, Object> result = new HashMap<String, Object>();
        try {
            // System.out.println(userAuthen.next().getIdcardName());
            String excelName = "代理账户列表" + DateUtils.getCurrentTimeStr();
            String sheetName = "代理账户列表";
            String titleName = "代理账户列表";
            int[] colWidths = {20, 20, 20, 20, 20, 20, 20, 20};
            String[] colNames = {"代理号", "代理名称", "余额", "变动金额", "业务类型", "变动方向", "内部单号", "创建时间"};
            List<AgentAccountFlowExcel> dataVals = new ArrayList<AgentAccountFlowExcel>();
            List<TAgentAccountFlow> pay = agentAccountFlowService.find(beginTime, endTime, agentId, bizTypeName, directionName);
            dataVals.addAll(this.transForAccountExport(pay));

            // List<UserContact> dataVals = user.getContactsList();
            // OutputStream outps = new FileOutputStream("D://stud.xls"); //
            // 输出到服务器
            // OutputStream outps = response.getOutputStream(); // 输出到客户端
            response.setContentType("octets/stream");
            excelName = new String(excelName.getBytes("GB2312"), "ISO8859-1") + ".xls";
            response.addHeader("Content-Disposition", "attachment;filename=" + excelName);
            ExportExcelUtil<AgentAccountFlowExcel> epec = new ExportExcelUtil<AgentAccountFlowExcel>();
            epec.expExcel(sheetName, titleName, colWidths, colNames, dataVals, response.getOutputStream());
        } catch (Exception e) {

        }
    }


    //---------------------------------------------------------------------------------------------------------------------
    @RequestMapping("/agentCashFlowVerb")
    @Permission
    public String agentCashFlowVerb() {
        return PREFIX + "agent_cash_flow_verb.html";
    }

    @RequestMapping("/agentCashFlowVerbAccept")
    @ResponseBody
    @Permission
    public SuccessResponseData accept(@RequestParam(required = false) String id) {
        TAgentCashFlow agentCashFlow = agentCashFlowService.getById(id);
        agentCashFlow.setCashStatus(2);
        agentCashFlowService.updateCashStatus(agentCashFlow);
        return SUCCESS_TIP;
    }

    @RequestMapping("/agentCashFlowVerbRefuse")
    @ResponseBody
    @Permission
    public SuccessResponseData refuse(@RequestParam(required = false) String id) {
        agentCashFlowService.refuse(id);
        return SUCCESS_TIP;
    }



    //--------------------------------------------通道费率---------------------------------------------------------------------

    @RequestMapping("/agentRateChannel")
    public String agentRateChannel() {
        return PREFIX + "agent_rate_channel.html";
    }


    @RequestMapping("/agentRateChannelList")
    @ResponseBody
    public Object agentRateChannelList(@RequestParam(required = false) String agentId,
                                       @RequestParam(required = false) String channel) {
        //获取分页参数
        Page page = LayuiPageFactory.defaultPage();
        List<Map<String, Object>> result = agentRateChannelService.findAll(page, agentId, channel);
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
    public ResponseData add(@Valid AgentRateChannelParam channelParam, BindingResult result) {
        if (result.hasErrors()) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        agentInfoService.getAgentInfo(channelParam.getAgentId());
        this.agentRateChannelService.addRateChannel(channelParam);
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
    public ResponseData editItem(AgentRateChannelParam channelParam) {
        agentInfoService.getAgentInfo(channelParam.getAgentId());
        this.agentRateChannelService.update(channelParam);
        return new SuccessResponseData();
    }

    /**
     * 删除店铺
     *
     * @author xxx
     * @Date 2019-12-27
     */
    @RequestMapping("/delete")
    @ResponseBody
    public ResponseData delete(AgentRateChannelParam channelParam) {
        agentRateChannelService.removeById(channelParam.getId());
        return new SuccessResponseData();
    }
    /**
     * 查看详情接口
     *
     * @author xxx
     * @Date 2019-12-27
     */
    @RequestMapping("/detail")
    @ResponseBody
    public ResponseData detail(AgentRateChannelParam channelParam){
         TAgentRateChannel tAgentRateChannel = this.agentRateChannelService.getById(channelParam.getId());
        return new SuccessResponseData(tAgentRateChannel);
    }


}
