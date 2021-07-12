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

import cn.hawy.quick.core.common.page.LayuiPageFactory;
import cn.hawy.quick.core.shiro.ShiroKit;
import cn.hawy.quick.core.util.CollectionKit;
import cn.hawy.quick.core.util.PayUtil;
import cn.hawy.quick.modular.api.dao.MchCashExcel;
import cn.hawy.quick.modular.api.dao.MchCashReportExcel;
import cn.hawy.quick.modular.api.entity.OrderReport;
import cn.hawy.quick.modular.api.entity.TMchCashFlow;
import cn.hawy.quick.modular.api.entity.TPayOrder;
import cn.hawy.quick.modular.api.service.TMchCashFlowService;
import cn.hawy.quick.modular.api.service.TPayOrderService;
import cn.hawy.quick.modular.api.utils.DateUtils;
import cn.hawy.quick.modular.api.utils.ExportExcelUtil;
import cn.hawy.quick.modular.business.warpper.MchCashFlowWrapper;
import cn.hawy.quick.modular.business.warpper.OrderWrapper;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

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
 *
 */
@Controller
@RequestMapping("/business/report")
public class ReportController extends BaseController {

    private static String PREFIX = "/modular/business/report/";

    @Autowired
    TPayOrderService payOrderService;
    @Autowired
    TMchCashFlowService mchCashFlowService;


    /**
     * 跳转到支付订单管理报表的首页
     *
     */
    @RequestMapping("orderFlow")
    public String index() {
        return PREFIX + "order_report.html";
    }

    /**
     * 跳转到支付订单管理报表的首页
     *
     */
    @RequestMapping("mchCashReport")
    public String mchCashReport() {
        return PREFIX + "mch_cash_report.html";
    }

    /**
     * 查询支付流水列表
     *
     */
    @RequestMapping("/payOrderList")
    @ResponseBody
    public Object payOrderlist(@RequestParam(required = false) String beginTime,
                               @RequestParam(required = false) String endTime,
                               @RequestParam(required = false) String deptId,
                               @RequestParam(required = false) String orderId,
                               @RequestParam(required = false) String outTradeNo,
                               @RequestParam(required = false) String mchId,
                               @RequestParam(required = false) Integer orderStatus,
                               @RequestParam(required = false) String channelNo) {
        Page page = LayuiPageFactory.defaultPage();
        if (ShiroKit.isAdmin()) {
            List<Map<String, Object>> result = payOrderService.findAll(page, null,deptId, beginTime, endTime,orderId, outTradeNo,mchId,orderStatus,channelNo);
            page.setRecords(new OrderWrapper(result).wrap());
        }else {
            String join = CollectionKit.join(ShiroKit.getDeptDataScope(), ",");
            List<Map<String, Object>> result = payOrderService.findAll(page, join,deptId, beginTime, endTime,orderId, outTradeNo,mchId,orderStatus,channelNo);
            page.setRecords(new OrderWrapper(result).wrap());
        }
        return LayuiPageFactory.createPageInfo(page);
    }



    @RequestMapping("/mchCashReportList")
    @ResponseBody
    public Object mchCashFlowList(@RequestParam(required = false) String beginTime,
            					  @RequestParam(required = false) String endTime,
            					  @RequestParam(required = false) String deptId,
            					  @RequestParam(required = false) String cashId,
            					  @RequestParam(required = false) String outTradeNo,
                                  @RequestParam(required = false) String cashStatus,
                                  @RequestParam(required = false) String mchName,
                                  @RequestParam(required = false) String bankCardNo) {
    	Page page = LayuiPageFactory.defaultPage();
        if (ShiroKit.isAdmin()) {
        	List<Map<String, Object>> result = mchCashFlowService.findAll(page, null,deptId, beginTime, endTime,cashId, outTradeNo, "2", mchName, bankCardNo);
            page.setRecords(new MchCashFlowWrapper(result).wrap());
        }else {
        	String join = CollectionKit.join(ShiroKit.getDeptDataScope(), ",");
        	List<Map<String, Object>> result = mchCashFlowService.findAll(page, join,deptId, beginTime, endTime,cashId, outTradeNo, "2", mchName, bankCardNo);
            page.setRecords(new MchCashFlowWrapper(result).wrap());
        }
        return LayuiPageFactory.createPageInfo(page);
    }

    /**
     * 跳转到支付订单管理报表的首页
     *
     */
    @RequestMapping("orderStatistics")
    public String statistics() {
        return PREFIX + "order_statistics.html";
    }





    private List<OrderReport> transForReportExport(List<TPayOrder> dataList){
        List<OrderReport> dataVals = new ArrayList<OrderReport>();
        for (TPayOrder data : dataList) {
            OrderReport orderReport = new OrderReport();
            orderReport.setOrderId(data.getOrderId());
            orderReport.setMchId(data.getMchId());
            orderReport.setMchName(data.getMchName());
            orderReport.setBankCardNo(data.getBankCardNo());

            if ("000000000".equals(data.getChannelNo())) {
                orderReport.setDeptType("银联新无卡");
            } else if ("101243663".equals(data.getChannelNo())) {
                orderReport.setDeptType("商盟新快捷");
            } else if ("101243664".equals(data.getChannelNo())) {
                orderReport.setDeptType("银联新快捷(大O)");
            } else if ("101733657".equals(data.getChannelNo())) {
                orderReport.setDeptType("商盟小额");
            } else if ("101553668".equals(data.getChannelNo())) {
                orderReport.setDeptType("银联新快捷O");
            } else if ("101713675".equals(data.getChannelNo())) {
                orderReport.setDeptType("银联新快捷N");
            } else if (data.getChannelNo().equals("000000001")){
                orderReport.setDeptType("银联小额");
            } else if (data.getChannelNo().equals("000000002")){
                orderReport.setDeptType("PAF小额");
            } else if (data.getChannelNo().equals("000000003")){
                orderReport.setDeptType("FF大额");
            } else if (data.getChannelNo().equals("102423765")){
                orderReport.setDeptType("银联新快捷X");
            }

            orderReport.setDeptId(data.getDeptId());

            if (data.getOrderAmount() == null) {
                orderReport.setOrderAmount("0");
            } else {
                orderReport.setOrderAmount(PayUtil.transFenToYuan(String.valueOf(data.getOrderAmount())));
            }
            orderReport.setMchRate(data.getMchRate());

            if (data.getMchFee() == null) {
                orderReport.setMchFee("0");
            } else {
                orderReport.setMchFee(PayUtil.transFenToYuan(String.valueOf(data.getMchFee())));
            }
            orderReport.setDeptRate(data.getDeptRate());

            if (data.getDeptAmount() == null){
                orderReport.setDeptAmount("0");
            } else {
                orderReport.setDeptAmount(PayUtil.transFenToYuan(String.valueOf(data.getDeptAmount())));
            }
            orderReport.setCostRate(data.getCostRate());

            if (data.getCostAmount() == null){
                orderReport.setCostAmount("0");
            } else {
                orderReport.setCostAmount(PayUtil.transFenToYuan(String.valueOf(data.getCostAmount())));
            }
            orderReport.setAgentId(data.getAgentId());
            orderReport.setAgentRate(data.getAgentRate());
            if (data.getAgentAmount() == null){
                orderReport.setAgentAmount("0");
            } else {
                orderReport.setAgentAmount(PayUtil.transFenToYuan(String.valueOf(data.getAgentAmount())));
            }

            if (data.getOrderStatus() == 1){
                orderReport.setOrderStatus("支付中");
            } else if (data.getOrderStatus() == 2){
                orderReport.setOrderStatus("支付成功");
            } else if (data.getOrderStatus() == 3){
                orderReport.setOrderStatus("支付失败");
            }

            orderReport.setReturnMsg(data.getReturnMsg());
            orderReport.setOutTradeNo(data.getOutTradeNo());
            orderReport.setOrderTime(data.getOrderTime());
            dataVals.add(orderReport);
        }
        return dataVals;
    }

    /**
     * 报表导出
     *
     *
     */
    @RequestMapping("/reportOrderExcelList")
    @ResponseBody
    public void reportOrderExcelList(@RequestParam(required = false) String beginTime,
                                     @RequestParam(required = false) String endTime,
                                     @RequestParam(required = false) String deptId,
                                     @RequestParam(required = false) String orderId,
                                     @RequestParam(required = false) String outTradeNo,
                                     @RequestParam(required = false) String mchId,
                                     @RequestParam(required = false) Integer orderStatus,
                                     @RequestParam(required = false) String channelNo,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
//        Map<String, Object> result = new HashMap<String, Object>();
        try {
            // System.out.println(userAuthen.next().getIdcardName());
            String excelName = "订单列表" + DateUtils.getCurrentTimeStr();
            String sheetName = "订单列表";
            String titleName = "订单列表";
            int[] colWidths = { 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
            String[] colNames = { "订单号", "商户号", "商户名称", "银行卡号", "支付类型", "渠道号", "订单金额", "商户费率", "商户手续费", "渠道商费率", "渠道商利润","代理商号","代理商费率","代理商利润", "平台费率", "平台利润", "订单状态", "错误原因", "外部订单号", "订单时间" };
            List<OrderReport> dataVals = new ArrayList<OrderReport>();
            List<TPayOrder> pay = payOrderService.find(null, deptId, beginTime, endTime,orderId, outTradeNo,mchId,orderStatus,channelNo);
            dataVals.addAll(this.transForReportExport(pay));
            // List<UserContact> dataVals = user.getContactsList();
            // OutputStream outps = new FileOutputStream("D://stud.xls"); //
            // 输出到服务器
            // OutputStream outps = response.getOutputStream(); // 输出到客户端
            response.setContentType("octets/stream");
            excelName = new String(excelName.getBytes("GB2312"), "ISO8859-1") + ".xls";
            response.addHeader("Content-Disposition", "attachment;filename=" + excelName);
            ExportExcelUtil<OrderReport> epec = new ExportExcelUtil<OrderReport>();
            epec.expExcel(sheetName, titleName, colWidths, colNames, dataVals, response.getOutputStream());
        } catch (Exception e) {

        }
    }

    private List<MchCashReportExcel> transForMchExport(List<TMchCashFlow> dataList){
        List<MchCashReportExcel> dataVals = new ArrayList<MchCashReportExcel>();
        for (TMchCashFlow data : dataList) {
        	MchCashReportExcel mchCashExcel = new MchCashReportExcel();
            mchCashExcel.setCashId(data.getCashId());
            mchCashExcel.setMchId(data.getMchId());
            mchCashExcel.setMchName(data.getMchName());
            mchCashExcel.setBankCardNo(data.getBankCardNo());
            mchCashExcel.setDeptId(data.getDeptId());

            if (data.getCashAmount() == null) {
                mchCashExcel.setCashAmount("0");
            } else {
                mchCashExcel.setCashAmount(PayUtil.transFenToYuan(String.valueOf(data.getCashAmount())));
            }

            if (data.getCashStatus() == 1){
                mchCashExcel.setCashStatus("提现中");
            } else if (data.getCashStatus() == 2){
                mchCashExcel.setCashStatus("提现成功");
            } else if (data.getCashStatus() == 3){
                mchCashExcel.setCashStatus("提现失败");
            }

            mchCashExcel.setReturnMsg(data.getReturnMsg());

            if (data.getCashFee() == null) {
                mchCashExcel.setCashFee("0");
            } else {
                mchCashExcel.setCashFee(PayUtil.transFenToYuan(String.valueOf(data.getCashFee())));
            }

            mchCashExcel.setCashRate(PayUtil.transFenToYuan(String.valueOf(data.getCashRate())));
            mchCashExcel.setDeptAmount(PayUtil.transFenToYuan(String.valueOf(data.getDeptAmount())));
            mchCashExcel.setCostAmount(PayUtil.transFenToYuan(String.valueOf(data.getCostAmount())));

            if (data.getOutAmount() == null) {
                mchCashExcel.setOutAmount("0");
            } else {
                mchCashExcel.setOutAmount(PayUtil.transFenToYuan(String.valueOf(data.getOutAmount())));
            }

            mchCashExcel.setOutTradeNo(data.getOutTradeNo());
            mchCashExcel.setCreateTime(data.getCreateTime());
            dataVals.add(mchCashExcel);
        }
        return dataVals;
    }

    /**
     * 报表导出
     *
     *
     */
    @RequestMapping("/mchCashReportExcelList")
    @ResponseBody
    public void payOrderExcelList(@RequestParam(required = false) String beginTime,
                                  @RequestParam(required = false) String endTime,
                                  @RequestParam(required = false) String deptId,
                                  @RequestParam(required = false) String cashId,
                                  @RequestParam(required = false) String outTradeNo,
                                  @RequestParam(required = false) String cashStatus,
                                  @RequestParam(required = false) String mchName,
                                  @RequestParam(required = false) String bankCardNo,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception
    {
//        Map<String, Object> result = new HashMap<String, Object>();
        try {
            // System.out.println(userAuthen.next().getIdcardName());
            String excelName = "商户提现列表" + DateUtils.getCurrentTimeStr();
            String sheetName = "商户提现列表";
            String titleName = "商户提现列表";
            int[] colWidths = { 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
            String[] colNames = { "提现号", "商户号", "商户名称", "银行卡号", "渠道号", "提现金额", "提现状态", "错误原因", "提现手续费","渠道商提现成本","渠道商利润","平台利润", "出款金额", "外部提现号", "创建时间" };
            List<MchCashReportExcel> dataVals = new ArrayList<MchCashReportExcel>();
            if (ShiroKit.isAdmin()) {
                List<TMchCashFlow> pay = mchCashFlowService.find(null, deptId, beginTime, endTime,cashId, outTradeNo, "2", mchName, bankCardNo);
                dataVals.addAll(this.transForMchExport(pay));
            } else {
                String join = CollectionKit.join(ShiroKit.getDeptDataScope(), ",");
                List<TMchCashFlow> pay = mchCashFlowService.find(join, null, beginTime, endTime,cashId, outTradeNo, "2", mchName, bankCardNo);
                dataVals.addAll(this.transForMchExport(pay));
            }

            response.setContentType("octets/stream");
            excelName = new String(excelName.getBytes("GB2312"), "ISO8859-1") + ".xls";
            response.addHeader("Content-Disposition", "attachment;filename=" + excelName);
            ExportExcelUtil<MchCashReportExcel> epec = new ExportExcelUtil<MchCashReportExcel>();
            epec.expExcel(sheetName, titleName, colWidths, colNames, dataVals, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/payOrderTj")
    @ResponseBody
    public Object payOrderTj(@RequestParam(required = false) String beginTime,
    						 @RequestParam(required = false) String endTime,
            				 @RequestParam(required = false) String deptId,
                             @RequestParam(required = false) String channelNo)
    {
    	Map<String,Object> tMap = payOrderService.tongji(beginTime, endTime, deptId, channelNo, null);
    	Map<String,Object> sMap = payOrderService.tongji(beginTime, endTime, deptId, channelNo, 2);
    	StringBuffer tjHtmlStr = new StringBuffer();
    	tjHtmlStr.append("<table class=\"layui-table\">");
    	tjHtmlStr.append("<colgroup><col width=\"25%\"><col width=\"25%\"><col width=\"25%\"><col width=\"25%\"></colgroup>");
    	tjHtmlStr.append("<tbody>");
    	tjHtmlStr.append("<tr>");
    	tjHtmlStr.append("<td class=\"text-right\">总交易笔数：</td>");
    	tjHtmlStr.append("<td><span>"+tMap.get("orderNum").toString()+"</span></td>");
    	tjHtmlStr.append("<td class=\"text-right\">总交易金额：</td>");
    	tjHtmlStr.append("<td><span>"+PayUtil.transFenToYuan(tMap.get("orderAmount").toString())+"</span></td>");
    	tjHtmlStr.append("</tr>");
    	tjHtmlStr.append("<tr>");
    	tjHtmlStr.append("<td class=\"text-right\">成功交易笔数：</td>");
    	tjHtmlStr.append("<td><span>"+sMap.get("orderNum").toString()+"</span></td>");
    	tjHtmlStr.append("<td class=\"text-right\">成功交易金额：</td>");
    	tjHtmlStr.append("<td><span>"+PayUtil.transFenToYuan(sMap.get("orderAmount").toString())+"</span></td>");
    	tjHtmlStr.append("</tr>");
        tjHtmlStr.append("<tr>");
        tjHtmlStr.append("<td class=\"text-right\">渠道商利润：</td>");
        tjHtmlStr.append("<td><span>"+PayUtil.transFenToYuan(sMap.get("deptAmount").toString())+"</span></td>");
        tjHtmlStr.append("<td class=\"text-right\">平台利润：</td>");
        tjHtmlStr.append("<td><span>"+PayUtil.transFenToYuan(sMap.get("costAmount").toString())+"</span></td>");
        tjHtmlStr.append("</tr>");
    	tjHtmlStr.append("</tbody>");
    	tjHtmlStr.append("</table>");
        return  ResponseData.success(tjHtmlStr);
    }

    @RequestMapping("/mchCashTj")
    @ResponseBody
    public Object mchCashTj(@RequestParam(required = false) String beginTime,
    						 @RequestParam(required = false) String endTime,
            				 @RequestParam(required = false) String deptId)
    {
    	Map<String,Object> sMap = mchCashFlowService.tongji(beginTime, endTime, deptId, 2);
    	StringBuffer tjHtmlStr = new StringBuffer();
    	tjHtmlStr.append("<table class=\"layui-table\">");
    	tjHtmlStr.append("<colgroup><col width=\"25%\"><col width=\"25%\"><col width=\"25%\"><col width=\"25%\"></colgroup>");
    	tjHtmlStr.append("<tbody>");
    	tjHtmlStr.append("<td class=\"text-right\">成功提现笔数：</td>");
    	tjHtmlStr.append("<td><span>"+sMap.get("cashNum").toString()+"</span></td>");
    	tjHtmlStr.append("<td class=\"text-right\">成功提现金额：</td>");
    	tjHtmlStr.append("<td><span>"+PayUtil.transFenToYuan(sMap.get("cashAmount").toString())+"</span></td>");
    	tjHtmlStr.append("</tr>");
        tjHtmlStr.append("<tr>");
        tjHtmlStr.append("<td class=\"text-right\">渠道商利润：</td>");
        tjHtmlStr.append("<td><span>"+PayUtil.transFenToYuan(sMap.get("deptAmount").toString())+"</span></td>");
        tjHtmlStr.append("<td class=\"text-right\">平台利润：</td>");
        tjHtmlStr.append("<td><span>"+PayUtil.transFenToYuan(sMap.get("costAmount").toString())+"</span></td>");
        tjHtmlStr.append("</tr>");
    	tjHtmlStr.append("</tbody>");
    	tjHtmlStr.append("</table>");
        return  ResponseData.success(tjHtmlStr);
    }
}
