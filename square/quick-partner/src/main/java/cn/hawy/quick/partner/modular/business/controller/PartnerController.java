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
package cn.hawy.quick.partner.modular.business.controller;

import cn.hawy.quick.partner.core.common.exception.BizExceptionEnum;
import cn.hawy.quick.partner.core.common.page.LayuiPageFactory;
import cn.hawy.quick.partner.core.shiro.ShiroKit;
import cn.hawy.quick.partner.core.shiro.ShiroUser;
import cn.hawy.quick.partner.core.util.CollectionKit;
import cn.hawy.quick.partner.core.util.PayUtil;
import cn.hawy.quick.partner.modular.business.dao.DeptAccountFlowExcel;
import cn.hawy.quick.partner.modular.business.dao.DeptCashFlowExcel;
import cn.hawy.quick.partner.modular.business.dao.DeptOrderReportExcel;
import cn.hawy.quick.partner.modular.business.entity.*;
import cn.hawy.quick.partner.modular.business.service.*;
import cn.hawy.quick.partner.modular.business.utils.DateUtils;
import cn.hawy.quick.partner.modular.business.utils.ExportExcelUtil;
import cn.hawy.quick.partner.modular.business.warpper.DeptAccountFlowWrapper;
import cn.hawy.quick.partner.modular.business.warpper.DeptCashFlowWrapper;
import cn.hawy.quick.partner.modular.business.warpper.DeptOrderReportWrapper;
import cn.hawy.quick.partner.modular.system.model.DeptDto;
import cn.hawy.quick.partner.modular.system.service.UserService;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.reqres.response.SuccessResponseData;
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
@RequestMapping("/partner")
public class PartnerController extends BaseController {

    private static String PREFIX = "/modular/business/partner/";

    @Autowired
    TDeptInfoService deptInfoService;
    @Autowired
    TDeptCashFlowService deptCashFlowService;
    @Autowired
    TDeptAccountFlowService deptAccountFlowService;
    @Autowired
    TDeptBankCardService deptBankCardService;
    @Autowired
    TDeptOrderReportService deptOrderReportService;
    @Autowired
    UserService userService;


    /**
     * 跳转到渠道提现的首页
     */
    @RequestMapping("/deptCashFlow")
    public String deptCashFlow() {
        return PREFIX + "dept_cash_flow.html";
    }

    /**
     * 跳转到渠道提现的列表
     */
    @RequestMapping("/deptCashFlowList")
    @ResponseBody
    public Object deptCashFlowList(@RequestParam(required = false) String beginTime,
                                   @RequestParam(required = false) String endTime,
                                   @RequestParam(required = false) String cashStatusName,
                                   @RequestParam(required = false) String name) {

        Page page = LayuiPageFactory.defaultPage();
        ShiroUser shiroUser = ShiroKit.getUserNotNull();
        List<Map<String, Object>> result = deptCashFlowService.findAll(page, beginTime, endTime, cashStatusName, shiroUser.getId(), name);
        page.setRecords(new DeptCashFlowWrapper(result).wrap());
        return LayuiPageFactory.createPageInfo(page);
    }

    private List<DeptCashFlowExcel> transForCashExport(List<TDeptCashFlow> dataList) {
        List<DeptCashFlowExcel> dataVals = new ArrayList<DeptCashFlowExcel>();
        for (TDeptCashFlow data : dataList) {
            DeptCashFlowExcel deptCashFlowExcel = new DeptCashFlowExcel();
            deptCashFlowExcel.setId(data.getId());
            deptCashFlowExcel.setDeptId(data.getDeptId());
            deptCashFlowExcel.setDeptName(data.getDeptName());

            if (data.getCashAmount() == null) {
                deptCashFlowExcel.setCashAmount("0");
            } else {
                deptCashFlowExcel.setCashAmount(PayUtil.transFenToYuan(String.valueOf(data.getCashAmount())));
            }
            if (data.getCashStatus() == 1) {
                deptCashFlowExcel.setCashStatus("提现中");
            } else if (data.getCashStatus() == 2) {
                deptCashFlowExcel.setCashStatus("提现成功");
            } else if (data.getCashStatus() == 3) {
                deptCashFlowExcel.setCashStatus("提现失败");
            }
            if (data.getCashFee() == null) {
                deptCashFlowExcel.setCashFee("0");
            } else {
                deptCashFlowExcel.setCashFee(PayUtil.transFenToYuan(String.valueOf(data.getCashFee())));
            }
            if (data.getOutAmount() == null) {
                deptCashFlowExcel.setOutAmount("0");
            } else {
                deptCashFlowExcel.setOutAmount(PayUtil.transFenToYuan(String.valueOf(data.getOutAmount())));
            }

            deptCashFlowExcel.setName(data.getName());
            deptCashFlowExcel.setCardNo(data.getCardNo());
            deptCashFlowExcel.setBankName(data.getBankName());
            deptCashFlowExcel.setCreateTime(data.getCreateTime());
            dataVals.add(deptCashFlowExcel);
        }
        return dataVals;
    }


    /**
     * 报表导出
     */
    @RequestMapping("/deptCashFlowExcelList")
    @ResponseBody
    public void deptCashFlowExcelList(@RequestParam(required = false) String beginTime,
                                      @RequestParam(required = false) String endTime,
                                      @RequestParam(required = false) String deptType,
                                      @RequestParam(required = false) String cashStatusName,
                                      @RequestParam(required = false) String deptId,
                                      @RequestParam(required = false) String name,
                                      HttpServletRequest request, HttpServletResponse response) throws Exception {
//        Map<String, Object> result = new HashMap<String, Object>();
        try {
            // System.out.println(userAuthen.next().getIdcardName());
            String excelName = "渠道提现列表" + DateUtils.getCurrentTimeStr();
            String sheetName = "渠道提现列表";
            String titleName = "渠道提现列表";
            int[] colWidths = {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
            String[] colNames = {"提现号", "渠道号", "渠道名称", "提现金额", "提现状态", "提现手续费", "出款金额", "出款账户名", "出款账户号", "出款银行", "创建时间"};
            List<DeptCashFlowExcel> dataVals = new ArrayList<DeptCashFlowExcel>();
            ShiroUser shiroUser = ShiroKit.getUserNotNull();
            List<TDeptCashFlow> pay = deptCashFlowService.find(null, beginTime, endTime, deptType, cashStatusName, shiroUser.getId(), name);
            dataVals.addAll(this.transForCashExport(pay));

            // 输出到服务器
            // OutputStream outps = response.getOutputStream(); // 输出到客户端
            response.setContentType("octets/stream");
            excelName = new String(excelName.getBytes("GB2312"), "ISO8859-1") + ".xls";
            response.addHeader("Content-Disposition", "attachment;filename=" + excelName);
            ExportExcelUtil<DeptCashFlowExcel> epec = new ExportExcelUtil<DeptCashFlowExcel>();
            epec.expExcel(sheetName, titleName, colWidths, colNames, dataVals, response.getOutputStream());
        } catch (Exception e) {

        }
    }


    /**------------------------------------------渠道账户流水--------------------------------------------------------------**/

    /**
     * 跳转到渠道账户流水的首页
     */
    @RequestMapping("/deptAccountFlow")
    public String deptAccountFlow() {
        return PREFIX + "dept_account_flow.html";
    }

    /**
     * 跳转到渠道账户流水list
     */
    @RequestMapping("/deptAccountFlowList")
    @ResponseBody
    public Object deptAccountFlowList(@RequestParam(required = false) String beginTime,
                                      @RequestParam(required = false) String endTime,
                                      @RequestParam(required = false) String bizTypeName,
                                      @RequestParam(required = false) String directionName) {
        //获取分页参数
        Page page = LayuiPageFactory.defaultPage();
        ShiroUser shiroUser = ShiroKit.getUserNotNull();
        List<Map<String, Object>> result = deptAccountFlowService.findAll(page, beginTime, endTime, shiroUser.getId(), bizTypeName, directionName);
        page.setRecords(new DeptAccountFlowWrapper(result).wrap());
        return LayuiPageFactory.createPageInfo(page);
    }

    private List<DeptAccountFlowExcel> transForAccountExport(List<TDeptAccountFlow> dataList) {
        List<DeptAccountFlowExcel> dataVals = new ArrayList<DeptAccountFlowExcel>();
        for (TDeptAccountFlow data : dataList) {
            DeptAccountFlowExcel deptAccountFlowExcel = new DeptAccountFlowExcel();
            deptAccountFlowExcel.setDeptId(data.getDeptId());
            deptAccountFlowExcel.setDeptName(data.getDeptName());

            if (data.getBalance() == null) {
                deptAccountFlowExcel.setBalance("0");
            } else {
                deptAccountFlowExcel.setBalance(PayUtil.transFenToYuan(String.valueOf(data.getBalance())));
            }
            if (data.getAmount() == null) {
                deptAccountFlowExcel.setAmount("0");
            } else {
                deptAccountFlowExcel.setAmount(PayUtil.transFenToYuan(String.valueOf(data.getAmount())));
            }
            if (data.getBizType() == 1) {
                deptAccountFlowExcel.setBizType("商户交易分润");
            } else if (data.getBizType() == 2) {
                deptAccountFlowExcel.setBizType("商户提现分润");
            } else if (data.getBizType() == 3) {
                deptAccountFlowExcel.setBizType("渠道提现");
            }

            if (data.getDirection() == 1) {
                deptAccountFlowExcel.setDirection("加款");
            } else if (data.getDirection() == 2) {
                deptAccountFlowExcel.setDirection("减款");
            }

            deptAccountFlowExcel.setTradeNo(data.getTradeNo());
            deptAccountFlowExcel.setCreateTime(data.getCreateTime());
            dataVals.add(deptAccountFlowExcel);
        }
        return dataVals;
    }


    /**
     * 报表导出
     */
    @RequestMapping("/deptAccountFlowExcelList")
    @ResponseBody
    public void deptAccountFlowExcelList(@RequestParam(required = false) String beginTime,
                                         @RequestParam(required = false) String endTime,
                                         @RequestParam(required = false) String deptId,
                                         @RequestParam(required = false) String deptType,
                                         @RequestParam(required = false) String bizTypeName,
                                         @RequestParam(required = false) String directionName,
                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
//        Map<String, Object> result = new HashMap<String, Object>();
        try {
            // System.out.println(userAuthen.next().getIdcardName());
            String excelName = "渠道账户列表" + DateUtils.getCurrentTimeStr();
            String sheetName = "渠道账户列表";
            String titleName = "渠道账户列表";
            int[] colWidths = {20, 20, 20, 20, 20, 20, 20, 20};
            String[] colNames = {"渠道号", "渠道名称", "余额", "变动金额", "业务类型", "变动方向", "内部单号", "创建时间"};
            List<DeptAccountFlowExcel> dataVals = new ArrayList<DeptAccountFlowExcel>();
            ShiroUser shiroUser = ShiroKit.getUserNotNull();
            List<TDeptAccountFlow> pay = deptAccountFlowService.find(null, beginTime, endTime, shiroUser.getId(), deptType, bizTypeName, directionName);
            dataVals.addAll(this.transForAccountExport(pay));

            // List<UserContact> dataVals = user.getContactsList();
            // OutputStream outps = new FileOutputStream("D://stud.xls"); //
            // 输出到服务器
            // OutputStream outps = response.getOutputStream(); // 输出到客户端
            response.setContentType("octets/stream");
            excelName = new String(excelName.getBytes("GB2312"), "ISO8859-1") + ".xls";
            response.addHeader("Content-Disposition", "attachment;filename=" + excelName);
            ExportExcelUtil<DeptAccountFlowExcel> epec = new ExportExcelUtil<DeptAccountFlowExcel>();
            epec.expExcel(sheetName, titleName, colWidths, colNames, dataVals, response.getOutputStream());
        } catch (Exception e) {

        }
    }

    /**------------------------------------------渠道账户提现--------------------------------------------------------------**/

    /**
     * 跳转到渠道提现的首页
     */
    @RequestMapping("/deptCashFlowVerb")
    public String deptCashFlowVerb() {
        return PREFIX + "dept_cash_flow_verb.html";
    }

    /**
     * 跳转到余额的首页
     */
    @RequestMapping("/deptCashApply")
    public String deptCashApply(Model model) {
        ShiroUser shiroUser = ShiroKit.getUserNotNull();
        TDeptInfo dept = deptInfoService.getById(shiroUser.getId());
        if (dept == null) {
            throw new ServiceException(BizExceptionEnum.DB_RESOURCE_NULL);
        }
        model.addAttribute("balance", PayUtil.transFenToYuan(String.valueOf(dept.getBalance())));
        return PREFIX + "dept_cash.html";
    }

    @RequestMapping("/deptCash")
    @ResponseBody
    public ResponseData deptCash(String cashAmount) {
        if (StrUtil.isEmpty(cashAmount)) {
            throw new ServiceException(BizExceptionEnum.NO_PERMITION);
        }
        deptInfoService.deptCash(cashAmount);
        return SUCCESS_TIP;
    }




    /**
     * ------------------------------------------渠道流水报表--------------------------------------------------------------
     **/

    @RequestMapping("/deptOrderReport")
    public String deptOrderReport() {
        return PREFIX + "dept_order_report.html";
    }


    @RequestMapping("/deptOrderReportList")
    @ResponseBody
    public Object deptOrderReportList(@RequestParam(required = false) String beginTime,
                                      @RequestParam(required = false) String endTime,
                                      @RequestParam(required = false) String channelNo) {
        //获取分页参数
        Page page = LayuiPageFactory.defaultPage();
        ShiroUser shiroUser = ShiroKit.getUserNotNull();
        List<Map<String, Object>> result = deptOrderReportService.findAll(page, null, beginTime, endTime, shiroUser.getId(), channelNo);
        page.setRecords(new DeptOrderReportWrapper(result).wrap());
        return LayuiPageFactory.createPageInfo(page);
    }

    @RequestMapping("/deptOrderReportListExcel")
    @ResponseBody
    public void deptOrderReportListExcel(@RequestParam(required = false) String beginTime,
                                         @RequestParam(required = false) String endTime,
                                         @RequestParam(required = false) String channelNo,
                                         HttpServletRequest request, HttpServletResponse response) {
        try {
            String excelName = "渠道流水报表" + DateUtils.getCurrentTimeStr();
            String sheetName = "渠道流水报表";
            String titleName = "渠道流水报表";
            int[] colWidths = {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
            String[] colNames = {"统计日期", "渠道号", "渠道名称", "支付类型", "交易笔数", "交易金额", "交易渠道利润", "交易平台利润", "提现笔数", "提现金额", "提现渠道笔润", "提现平台利润", "创建时间"};
            List<DeptOrderReportExcel> dataVals = new ArrayList<DeptOrderReportExcel>();
            ShiroUser shiroUser = ShiroKit.getUserNotNull();
            List<DeptOrderReportExcel> result = deptOrderReportService.find(beginTime, endTime, shiroUser.getId(), channelNo);
            dataVals.addAll(this.transForDeptOrderReportListExcel(result));
            // List<UserContact> dataVals = user.getContactsList();
            // OutputStream outps = new FileOutputStream("D://stud.xls"); //
            // 输出到服务器
            // OutputStream outps = response.getOutputStream(); // 输出到客户端
            response.setContentType("octets/stream");
            excelName = new String(excelName.getBytes("GB2312"), "ISO8859-1") + ".xls";
            response.addHeader("Content-Disposition", "attachment;filename=" + excelName);
            ExportExcelUtil<DeptOrderReportExcel> epec = new ExportExcelUtil<DeptOrderReportExcel>();
            epec.expExcel(sheetName, titleName, colWidths, colNames, dataVals, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<DeptOrderReportExcel> transForDeptOrderReportListExcel(List<DeptOrderReportExcel> dataList) {
        //List<DeptOrderReportExcel> dataVals = new ArrayList<>();
        for (DeptOrderReportExcel data : dataList) {
            data.setOrderAmount(PayUtil.transFenToYuan(data.getOrderAmount()));
            data.setOrderDeptAmount(PayUtil.transFenToYuan(data.getOrderDeptAmount()));
            data.setOrderCostAmount(PayUtil.transFenToYuan(data.getOrderCostAmount()));
            data.setCashAmount(PayUtil.transFenToYuan(data.getCashAmount()));
            data.setCashDeptAmount(PayUtil.transFenToYuan(data.getCashDeptAmount()));
            data.setCashCostAmount(PayUtil.transFenToYuan(data.getCashCostAmount()));
            if ("101243663".equals(data.getChannelNo())) {
                data.setChannelNo("商盟新快捷");
            } else if ("101243664".equals(data.getChannelNo())) {
                data.setChannelNo("银联新快捷(大O)");
            } else if ("101733657".equals(data.getChannelNo())) {
                data.setChannelNo("商盟小额");
            } else if ("101713675".equals(data.getChannelNo())) {
                data.setChannelNo("银联新快捷");
            } else if ("102423765".equals(data.getChannelNo())) {
                data.setChannelNo("银联新快捷N");
            }
        }
        return dataList;
    }


}
