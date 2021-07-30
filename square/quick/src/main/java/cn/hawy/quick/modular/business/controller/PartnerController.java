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
import cn.hawy.quick.core.shiro.ShiroUser;
import cn.hawy.quick.core.util.CollectionKit;
import cn.hawy.quick.core.util.IdGenerator;
import cn.hawy.quick.core.util.PayUtil;
import cn.hawy.quick.modular.api.dao.DeptAccountFlowExcel;
import cn.hawy.quick.modular.api.dao.DeptCashFlowExcel;
import cn.hawy.quick.modular.api.dao.DeptOrderReportExcel;
import cn.hawy.quick.modular.api.dto.PartnerDto;
import cn.hawy.quick.modular.api.entity.*;
import cn.hawy.quick.modular.api.mapper.TDeptCashFlowMapper;
import cn.hawy.quick.modular.api.param.AgentRateChannelParam;
import cn.hawy.quick.modular.api.param.DeptInfoParam;
import cn.hawy.quick.modular.api.param.DeptRateChannelParam;
import cn.hawy.quick.modular.api.service.*;
import cn.hawy.quick.modular.api.utils.DateUtils;
import cn.hawy.quick.modular.api.utils.ExportExcelUtil;
import cn.hawy.quick.modular.business.warpper.DeptInfoWrapper;
import cn.hawy.quick.modular.business.warpper.DeptAccountFlowWrapper;
import cn.hawy.quick.modular.business.warpper.DeptCashFlowWrapper;
import cn.hawy.quick.modular.business.warpper.DeptOrderReportWrapper;
import cn.hawy.quick.modular.business.warpper.DeptRateChannelWrapper;
import cn.hawy.quick.modular.system.entity.Dept;
import cn.hawy.quick.modular.system.model.DeptDto;
import cn.hawy.quick.modular.system.service.DeptService;
import cn.hutool.core.bean.BeanUtil;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 日志管理的控制器
 *
 */
@Controller
@RequestMapping("/partner")
public class PartnerController extends BaseController {

    private static String PREFIX = "/modular/business/partner/";

    @Autowired
    TDeptInfoService deptInfoService;
    @Autowired
	DeptService deptService;
    @Autowired
    TDeptCashFlowService deptCashFlowService;
    @Autowired
    TDeptAccountFlowService deptAccountFlowService;
    @Autowired
    TDeptBankCardService deptBankCardService;
    @Autowired
    TDeptCashFlowMapper deptCashFlowMapper;
    @Autowired
    TDeptOrderReportService deptOrderReportService;
    @Autowired
    TDeptRateChannelService deptRateChannelService;
    @Autowired
    TAgentInfoService agentInfoService;




    //--------------------------------------------渠道信息---------------------------------------------------------------------
    @RequestMapping("/deptInfo")
    public String deptInfo() {
        return PREFIX + "dept_info.html";
    }

    @RequestMapping("/deptInfoList")
    @ResponseBody
    public Object deptInfoList  (@RequestParam(required = false) String id,
                                 @RequestParam(required = false) String account,
                                 @RequestParam(required = false) String balance,
                                 @RequestParam(required = false) String agentId,
                                 @RequestParam(required = false) String beginTime,
                                 @RequestParam(required = false) String endTime,
                                 @RequestParam(required = false) String deptName ) {
        //获取分页参数
        Page page = LayuiPageFactory.defaultPage();
        if (ShiroKit.isAdmin()) {
            List<Map<String, Object>> result = deptInfoService.findAll( page,  id, account, balance, agentId, beginTime, endTime, deptName );
            page.setRecords(new DeptInfoWrapper(result).wrap());
        }else {
            String join = CollectionKit.join(ShiroKit.getDeptDataScope(), ",");
            List<Map<String, Object>> result = deptInfoService.findAll( page,  id, account, balance, agentId, beginTime, endTime, deptName );
            page.setRecords(new DeptInfoWrapper(result).wrap());
        }
        return LayuiPageFactory.createPageInfo(page);
    }

    /**
     * 新增页面
     *
     */
    @RequestMapping("/deptAdd")
    public String add()  {
        return PREFIX + "/dept_info_add.html";
    }

    /**
     * 新增接口
     *
     */
    @RequestMapping("/addItem")
    @ResponseBody
    public ResponseData addItem(PartnerDto partnerDto) {
        agentInfoService.getAgentInfo(partnerDto.getAgentId());
        this.deptInfoService.add(partnerDto);
        return ResponseData.success();
    }

    /**
     * 跳转到渠道提现的首页
     *
     */
    @RequestMapping("/deptCashFlow")
    @Permission
    public String deptCashFlow() {
        return PREFIX + "dept_cash_flow.html";
    }
    @RequestMapping("/deptRateChannelAdd")
    public String deptRateChannelAdd() {
        return PREFIX + "dept_rate_channel_add.html";
    }
    /**
     * 跳转到编辑页面
     *
     * @author fengshuonan
     * @Date 2018/12/24 22:43
     */
    @RequestMapping("/deptRateChannelEdit")
    public String deptRateChannelEdit() {
        return PREFIX + "dept_rate_channel_edit.html";
    }
    /**
     * 跳转到编辑页面
     *
     * @author fengshuonan
     * @Date 2018/12/24 22:43
     */
    @RequestMapping("/deptInfoEdit")
    public String deptInfoEdit() {
        return PREFIX + "dept_info_edit.html";
    }

    /**
     * 跳转到渠道提现的列表
     *
     */
    @RequestMapping("/deptCashFlowList")
    @Permission
    @ResponseBody
    public Object deptCashFlowList(@RequestParam(required = false) String beginTime,
                                   @RequestParam(required = false) String endTime,
                                   @RequestParam(required = false) String deptType,
                                   @RequestParam(required = false) String cashStatusName,
                                   @RequestParam(required = false) String deptId,
                                   @RequestParam(required = false) String name) {
    	 //获取分页参数
        Page page = LayuiPageFactory.defaultPage();
        //根据条件查询渠道提现信息
        if (ShiroKit.isAdmin()) {
            List<Map<String, Object>> result = deptCashFlowService.findAll(page, null, beginTime, endTime, deptType, cashStatusName, deptId, name);
            page.setRecords(new DeptCashFlowWrapper(result).wrap());
        } else {
            String join = CollectionKit.join(ShiroKit.getDeptDataScope(), ",");
            List<Map<String, Object>> result = deptCashFlowService.findAll(page, join, beginTime, endTime, deptType, cashStatusName, deptId, name);
            page.setRecords(new DeptCashFlowWrapper(result).wrap());
        }
        return LayuiPageFactory.createPageInfo(page);
    }

    private List<DeptCashFlowExcel> transForCashExport(List<TDeptCashFlow> dataList){
        List<DeptCashFlowExcel> dataVals = new ArrayList<DeptCashFlowExcel>();
        for (TDeptCashFlow data : dataList) {
            DeptCashFlowExcel deptCashFlowExcel = new DeptCashFlowExcel();
            deptCashFlowExcel.setId(data.getId());
            deptCashFlowExcel.setDeptId(data.getDeptId());
            deptCashFlowExcel.setDeptName(data.getDeptName());

            if (data.getCashAmount() == null){
                deptCashFlowExcel.setCashAmount("0");
            } else {
                deptCashFlowExcel.setCashAmount(PayUtil.transFenToYuan(String.valueOf(data.getCashAmount())));
            }
            if (data.getCashStatus() == 1) {
                deptCashFlowExcel.setCashStatus("提现中");
            } else if (data.getCashStatus() == 2){
                deptCashFlowExcel.setCashStatus("提现成功");
            } else if (data.getCashStatus() == 3){
                deptCashFlowExcel.setCashStatus("提现失败");
            }
            if (data.getCashFee() == null){
                deptCashFlowExcel.setCashFee("0");
            } else {
                deptCashFlowExcel.setCashFee(PayUtil.transFenToYuan(String.valueOf(data.getCashFee())));
            }
            if (data.getOutAmount() == null){
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
     *
     *
     */
    @RequestMapping("/deptCashFlowExcelList")
    @ResponseBody
    public void deptCashFlowExcelList(@RequestParam(required = false) String beginTime,
                                  @RequestParam(required = false) String endTime,
                                  @RequestParam(required = false) String deptType,
                                  @RequestParam(required = false) String cashStatusName,
                                  @RequestParam(required = false) String deptId,
                                  @RequestParam(required = false) String name,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception
    {
//        Map<String, Object> result = new HashMap<String, Object>();
        try {
            // System.out.println(userAuthen.next().getIdcardName());
            String excelName = "渠道提现列表" + DateUtils.getCurrentTimeStr();
            String sheetName = "渠道提现列表";
            String titleName = "渠道提现列表";
            int[] colWidths = { 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
            String[] colNames = { "提现号", "渠道号", "渠道名称", "提现金额", "提现状态", "提现手续费", "出款金额", "出款账户名", "出款账户号", "出款银行", "创建时间" };
            List<DeptCashFlowExcel> dataVals = new ArrayList<DeptCashFlowExcel>();
            if (ShiroKit.isAdmin()) {
                List<TDeptCashFlow> pay = deptCashFlowService.find(null, beginTime, endTime, deptType, cashStatusName, deptId, name);
                dataVals.addAll(this.transForCashExport(pay));
            } else {
                String join = CollectionKit.join(ShiroKit.getDeptDataScope(), ",");
                List<TDeptCashFlow> pay = deptCashFlowService.find(join, beginTime, endTime, deptType, cashStatusName, null, name);
                dataVals.addAll(this.transForCashExport(pay));
            }

            // List<UserContact> dataVals = user.getContactsList();
            // OutputStream outps = new FileOutputStream("D://stud.xls"); //
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
     *
     */
    @RequestMapping("/deptAccountFlow")
    @Permission
    public String deptAccountFlow() {
        return PREFIX + "dept_account_flow.html";
    }

    /**
     * 跳转到渠道账户流水list
     *
     */
    @RequestMapping("/deptAccountFlowList")
    @Permission
    @ResponseBody
    public Object deptAccountFlowList(@RequestParam(required = false) String beginTime,
                                      @RequestParam(required = false) String endTime,
                                      @RequestParam(required = false) String deptId,
                                      @RequestParam(required = false) String deptType,
                                      @RequestParam(required = false) String bizTypeName,
                                      @RequestParam(required = false) String directionName) {
    	 //获取分页参数
        Page page = LayuiPageFactory.defaultPage();
        //根据条件查询商户提现信息
        if (ShiroKit.isAdmin()) {
        	List<Map<String, Object>> result = deptAccountFlowService.findAll(page, null, beginTime, endTime, deptId, deptType, bizTypeName, directionName);
            page.setRecords(new DeptAccountFlowWrapper(result).wrap());
        }else {
        	String join = CollectionKit.join(ShiroKit.getDeptDataScope(), ",");
        	List<Map<String, Object>> result = deptAccountFlowService.findAll(page, join, beginTime, endTime, deptId, deptType, bizTypeName, directionName);
            page.setRecords(new DeptAccountFlowWrapper(result).wrap());
        }

        return LayuiPageFactory.createPageInfo(page);
    }

    private List<DeptAccountFlowExcel> transForAccountExport(List<TDeptAccountFlow> dataList){
        List<DeptAccountFlowExcel> dataVals = new ArrayList<DeptAccountFlowExcel>();
        for (TDeptAccountFlow data : dataList) {
            DeptAccountFlowExcel deptAccountFlowExcel = new DeptAccountFlowExcel();
            deptAccountFlowExcel.setDeptId(data.getDeptId());
            deptAccountFlowExcel.setDeptName(data.getDeptName());

            if (data.getBalance() == null){
                deptAccountFlowExcel.setBalance("0");
            } else {
                deptAccountFlowExcel.setBalance(PayUtil.transFenToYuan(String.valueOf(data.getBalance())));
            }
            if (data.getAmount() == null){
                deptAccountFlowExcel.setAmount("0");
            } else {
                deptAccountFlowExcel.setAmount(PayUtil.transFenToYuan(String.valueOf(data.getAmount())));
            }
            if (data.getBizType() == 1) {
                deptAccountFlowExcel.setBizType("商户交易分润");
            } else if (data.getBizType() == 2){
                deptAccountFlowExcel.setBizType("商户提现分润");
            } else if (data.getBizType() == 3){
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
     *
     *
     */
    @RequestMapping("/deptAccountFlowExcelList")
    @ResponseBody
    public void deptAccountFlowExcelList(@RequestParam(required = false) String beginTime,
                                  @RequestParam(required = false) String endTime,
                                  @RequestParam(required = false) String deptId,
                                  @RequestParam(required = false) String deptType,
                                  @RequestParam(required = false) String bizTypeName,
                                  @RequestParam(required = false) String directionName,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception
    {
//        Map<String, Object> result = new HashMap<String, Object>();
        try {
            // System.out.println(userAuthen.next().getIdcardName());
            String excelName = "渠道账户列表" + DateUtils.getCurrentTimeStr();
            String sheetName = "渠道账户列表";
            String titleName = "渠道账户列表";
            int[] colWidths = { 20, 20, 20, 20, 20, 20, 20, 20};
            String[] colNames = { "渠道号", "渠道名称", "余额", "变动金额", "业务类型", "变动方向", "内部单号", "创建时间" };
            List<DeptAccountFlowExcel> dataVals = new ArrayList<DeptAccountFlowExcel>();
            if (ShiroKit.isAdmin()) {
                List<TDeptAccountFlow> pay = deptAccountFlowService.find(null, beginTime, endTime, deptId, deptType, bizTypeName, directionName);
                dataVals.addAll(this.transForAccountExport(pay));
            } else {
                String join = CollectionKit.join(ShiroKit.getDeptDataScope(), ",");
                List<TDeptAccountFlow> pay = deptAccountFlowService.find(join, beginTime, endTime, null, deptType, bizTypeName, directionName);
                dataVals.addAll(this.transForAccountExport(pay));
            }

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
     *
     */
    @RequestMapping("/deptCashFlowVerb")
    @Permission
    public String deptCashFlowVerb() {
        return PREFIX + "dept_cash_flow_verb.html";
    }

    /**
     * 跳转到余额的首页
     *
     */
    @Permission
    @RequestMapping("/deptCashApply")
    public String deptCashApply(Model model) {
    	if(!ShiroKit.isPartner()) {
    		throw new ServiceException(BizExceptionEnum.NO_PERMITION);
    	}
    	Dept dept = deptService.getById(ShiroKit.getDeptId());
    	if(dept == null) {
    		throw new ServiceException(BizExceptionEnum.DB_RESOURCE_NULL);
    	}
    	model.addAttribute("balance", PayUtil.transFenToYuan(String.valueOf(dept.getBalance())));
    	return PREFIX + "dept_cash.html";
    }

    @RequestMapping("/deptCash")
    @ResponseBody
    public ResponseData deptCash(String cashAmount) {
    	if(StrUtil.isEmpty(cashAmount)) {
    		throw new ServiceException(BizExceptionEnum.NO_PERMITION);
    	}
    	if(!ShiroKit.isPartner()) {
    		throw new ServiceException(BizExceptionEnum.NO_PERMITION);
    	}
    	Dept dept = deptService.getById(ShiroKit.getDeptId());
    	if(dept == null) {
    		throw new ServiceException(BizExceptionEnum.DB_RESOURCE_NULL);
    	}
    	cashAmount = PayUtil.transYuanToFen(cashAmount);
    	if(NumberUtil.parseLong(cashAmount)>dept.getBalance()) {
			throw new ServiceException(400, "渠道商账户余额不足!");
		}
    	DeptDto deptDto = new DeptDto();

    	String cashRate = "0";
    	Long cashFee = NumberUtil.parseLong("0");
    	deptDto.setDeptId(ShiroKit.getDeptId());
    	deptDto.setCashAmount(NumberUtil.parseLong(cashAmount));
        deptDto.setCashRate(cashRate);
        deptDto.setCashFee(cashFee);
        deptDto.setOutAmount(NumberUtil.parseLong(cashAmount));
        deptDto.setFullName(dept.getFullName());
        deptDto.setBalance(dept.getBalance());
        deptDto.setName(deptBankCardService.getById(ShiroKit.getDeptId()).getName());
        deptDto.setCardNo(deptBankCardService.getById(ShiroKit.getDeptId()).getCardNo());
        deptDto.setBankName(deptBankCardService.getById(ShiroKit.getDeptId()).getBankName());
		deptService.deptCash(deptDto);
    	return SUCCESS_TIP;
    }

    /**
     * 渠道提现审核
     *
     */
    @RequestMapping("/deptCashFlowVerbAccept")
    @ResponseBody
    @Permission
    public SuccessResponseData accept(@RequestParam(required = false) String id) {
        TDeptCashFlow deptCashFlow = deptCashFlowService.getById(id);
        deptCashFlow.setCashStatus(2);
        deptCashFlowService.updateCashStatus(deptCashFlow);
        return SUCCESS_TIP;
    }

    /**
     * 渠道提现审核拒绝
     *
     */
    @RequestMapping("/deptCashFlowVerbRefuse")
    @ResponseBody
    @Permission
    public SuccessResponseData refuse(@RequestParam(required = false) String id) {
    	deptCashFlowService.refuse(id);
        return SUCCESS_TIP;
    }

    /**------------------------------------------渠道流水报表--------------------------------------------------------------**/

    @RequestMapping("/deptOrderReport")
    @Permission
    public String deptOrderReport() {
        return PREFIX + "dept_order_report.html";
    }


    @RequestMapping("/deptOrderReportList")
    @Permission
    @ResponseBody
    public Object deptOrderReportList(@RequestParam(required = false) String beginTime,
                                   @RequestParam(required = false) String endTime,
                                   @RequestParam(required = false) String deptId,
                                   @RequestParam(required = false) String channelNo) {
        //获取分页参数
        Page page = LayuiPageFactory.defaultPage();
        //根据条件查询渠道提现信息
        if (ShiroKit.isAdmin()) {
            List<Map<String, Object>> result = deptOrderReportService.findAll(page, null, beginTime, endTime,deptId, channelNo);
            page.setRecords(new DeptOrderReportWrapper(result).wrap());
        } else {
            String join = CollectionKit.join(ShiroKit.getDeptDataScope(), ",");
            List<Map<String, Object>> result = deptOrderReportService.findAll(page, join, beginTime, endTime,deptId, channelNo);
            page.setRecords(new DeptOrderReportWrapper(result).wrap());
        }
        return LayuiPageFactory.createPageInfo(page);
    }

    @RequestMapping("/deptOrderReportListExcel")
    @Permission
    @ResponseBody
    public void deptOrderReportListExcel(@RequestParam(required = false) String beginTime,
                                      @RequestParam(required = false) String endTime,
                                      @RequestParam(required = false) String deptId,
                                      @RequestParam(required = false) String channelNo,
                                      HttpServletRequest request, HttpServletResponse response) {
        try {
            String excelName = "渠道流水报表" + DateUtils.getCurrentTimeStr();
            String sheetName = "渠道流水报表";
            String titleName = "渠道流水报表";
            int[] colWidths = { 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
            String[] colNames = { "统计日期", "渠道号", "渠道名称", "支付类型", "交易笔数", "交易金额", "交易渠道利润", "交易平台利润", "提现笔数", "提现金额", "提现渠道笔润","提现平台利润","创建时间"};
            List<DeptOrderReportExcel> dataVals = new ArrayList<DeptOrderReportExcel>();
            List<DeptOrderReportExcel> result = deptOrderReportService.find(beginTime, endTime,deptId,channelNo);
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

    private List<DeptOrderReportExcel> transForDeptOrderReportListExcel(List<DeptOrderReportExcel> dataList){
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
            } else if ("102423765".equals(data.getChannelNo())){
                data.setChannelNo("银联新快捷N");
            }
        }
        return dataList;
    }

    //--------------------------------------------通道费率---------------------------------------------------------------------
    /**
     * 跳转到渠道账户流水的首页
     */
    @RequestMapping("/deptRateChannel")
    public String deptRateChannel() {
        return PREFIX + "dept_rate_channel.html";
    }

    /**
     * 跳转到渠道账户流水list
     */
    @RequestMapping("/deptRateChannelList")
    @ResponseBody
    public Object deptRateChannelList(@RequestParam(required = false) String deptId,
                                      @RequestParam(required = false) String channel) {
        //获取分页参数
        Page page = LayuiPageFactory.defaultPage();
        List<Map<String, Object>> result = deptRateChannelService.findAll(page, deptId, channel);
        page.setRecords(new DeptRateChannelWrapper(result).wrap());
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
    public ResponseData add(@Valid DeptRateChannelParam channelParam, BindingResult result) {
        if (result.hasErrors()) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        deptInfoService.getDeptInfo(channelParam.getDeptId());
         TDeptRateChannel byDeptId = deptRateChannelService.findByDeptIdAndBankCodeAndChannel(channelParam.getDeptId().toString(), channelParam.getBankCode(), channelParam.getChannel());
            if(!BeanUtil.isEmpty(byDeptId)){
                throw new ServiceException(400,"该通道的渠道号已存在该银行编码");
            }
         this.deptRateChannelService.addRateChannel(channelParam);
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
    public ResponseData editItem(DeptRateChannelParam channelParam) {
        deptInfoService.getDeptInfo(channelParam.getDeptId());
        this.deptRateChannelService.update(channelParam);
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
    public ResponseData delete(DeptRateChannelParam channelParam) {
        deptRateChannelService.removeById(channelParam.getId());
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
    public ResponseData detail(DeptRateChannelParam channelParam){
         TDeptRateChannel tDeptRateChannel = this.deptRateChannelService.getById(channelParam.getId());
        return new SuccessResponseData(tDeptRateChannel);
    }
    /**
     * 查看渠道详情接口
     *
     * @author xxx
     * @Date 2019-12-27
     */
    @RequestMapping("/deptDetail")
    @ResponseBody
    public ResponseData deptDetail(DeptInfoParam channelParam){
         TDeptInfo dept = this.deptInfoService.getById(channelParam.getId());
        return new SuccessResponseData(dept);
    }
    /**
     * 渠道编辑接口
     *
     * @author xxx
     * @Date 2019-12-27
     */
    @RequestMapping("/deptEdit")
    @ResponseBody
    public ResponseData deptEdit(DeptInfoParam channelParam) {
        agentInfoService.getAgentInfo(channelParam.getAgentId());
       /* TDeptInfo byDeptIdAndAgentId = deptInfoService.findByDeptIdAndAgentId(channelParam.getId(), channelParam.getAgentId());
        if(!BeanUtil.isEmpty(byDeptIdAndAgentId)){
            throw new ServiceException(400,"该渠道已存在该代理商");
        }*/
        this.deptInfoService.update(channelParam);
        return new SuccessResponseData();
    }
}
