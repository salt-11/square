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
package cn.hawy.quick.agent.modular.business.controller;

import cn.hawy.quick.agent.core.common.exception.BizExceptionEnum;
import cn.hawy.quick.agent.core.common.page.LayuiPageFactory;
import cn.hawy.quick.agent.core.shiro.ShiroKit;
import cn.hawy.quick.agent.core.shiro.ShiroUser;
import cn.hawy.quick.agent.core.util.PayUtil;
import cn.hawy.quick.agent.modular.business.dao.AgentAccountFlowExcel;
import cn.hawy.quick.agent.modular.business.dao.AgentCashFlowExcel;
import cn.hawy.quick.agent.modular.business.entity.TAgentAccountFlow;
import cn.hawy.quick.agent.modular.business.entity.TAgentCashFlow;
import cn.hawy.quick.agent.modular.business.entity.TAgentInfo;
import cn.hawy.quick.agent.modular.business.service.*;
import cn.hawy.quick.agent.modular.business.utils.DateUtils;
import cn.hawy.quick.agent.modular.business.utils.ExportExcelUtil;
import cn.hawy.quick.agent.modular.business.warpper.AgentAccountFlowWrapper;
import cn.hawy.quick.agent.modular.business.warpper.AgentCashFlowWrapper;
import cn.hawy.quick.agent.modular.system.service.UserService;
import cn.hutool.core.util.StrUtil;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    /**
     * 跳转到代理提现的列表
     */
    @RequestMapping("/agentCashFlowList")
    @ResponseBody
    public Object agentCashFlowList(@RequestParam(required = false) String beginTime,
                                   @RequestParam(required = false) String endTime,
                                   @RequestParam(required = false) String cashStatusName,
                                   @RequestParam(required = false) String name) {

        Page page = LayuiPageFactory.defaultPage();
        ShiroUser shiroUser = ShiroKit.getUserNotNull();
        List<Map<String, Object>> result = agentCashFlowService.findAll(page, beginTime, endTime, cashStatusName, shiroUser.getId(), name);
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
            ShiroUser shiroUser = ShiroKit.getUserNotNull();
            List<TAgentCashFlow> pay = agentCashFlowService.find(beginTime, endTime, cashStatusName, shiroUser.getId(), name);
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
                                      @RequestParam(required = false) String bizTypeName,
                                      @RequestParam(required = false) String directionName) {
        //获取分页参数
        Page page = LayuiPageFactory.defaultPage();
        ShiroUser shiroUser = ShiroKit.getUserNotNull();
        List<Map<String, Object>> result = agentAccountFlowService.findAll(page, beginTime, endTime, shiroUser.getId(), bizTypeName, directionName);
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
            ShiroUser shiroUser = ShiroKit.getUserNotNull();
            List<TAgentAccountFlow> pay = agentAccountFlowService.find(beginTime, endTime, shiroUser.getId(), bizTypeName, directionName);
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

    /**------------------------------------------代理账户提现--------------------------------------------------------------**/

    /**
     * 跳转到代理提现的首页
     */
    @RequestMapping("/agentCashFlowVerb")
    public String agentCashFlowVerb() {
        return PREFIX + "agent_cash_flow_verb.html";
    }

    /**
     * 跳转到余额的首页
     */
    @RequestMapping("/agentCashApply")
    public String agentCashApply(Model model) {
        ShiroUser shiroUser = ShiroKit.getUserNotNull();
        TAgentInfo agent = agentInfoService.getById(shiroUser.getId());
        if (agent == null) {
            throw new ServiceException(BizExceptionEnum.DB_RESOURCE_NULL);
        }
        model.addAttribute("balance", PayUtil.transFenToYuan(String.valueOf(agent.getBalance())));
        return PREFIX + "agent_cash.html";
    }

    @RequestMapping("/agentCash")
    @ResponseBody
    public ResponseData agentCash(String cashAmount) {
        if (StrUtil.isEmpty(cashAmount)) {
            throw new ServiceException(BizExceptionEnum.NO_PERMITION);
        }
        agentInfoService.agentCash(cashAmount);
        return SUCCESS_TIP;
    }

    //--------------------------------------------通道费率---------------------------------------------------------------------

    @RequestMapping("/agentRateChannel")
    public String agentRateChannel() {
        return PREFIX + "agent_rate_channel.html";
    }


    @RequestMapping("/agentRateChannelList")
    @ResponseBody
    public Object agentRateChannelList(@RequestParam(required = false) String channel) {
        //获取分页参数
        Page page = LayuiPageFactory.defaultPage();
        ShiroUser shiroUser = ShiroKit.getUserNotNull();
        List<Map<String, Object>> result = agentRateChannelService.findAll(page, shiroUser.getId(), channel);
        page.setRecords(result);
        return LayuiPageFactory.createPageInfo(page);
    }

    //--------------------------------------------我的商户---------------------------------------------------------------------
    @RequestMapping("/agentDeptInfo")
    public String agentDeptInfo() {
        return PREFIX + "agent_dept_info.html";
    }

    @RequestMapping("/agentDeptInfoList")
    @ResponseBody
    public Object agentDeptInfolList() {
        //获取分页参数
        Page page = LayuiPageFactory.defaultPage();
        ShiroUser shiroUser = ShiroKit.getUserNotNull();
        List<Map<String, Object>> result = deptInfoService.findAll(page, shiroUser.getId());
        page.setRecords(result);
        return LayuiPageFactory.createPageInfo(page);
    }
}
