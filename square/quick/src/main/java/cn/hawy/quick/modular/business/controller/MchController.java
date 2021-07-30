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
import cn.hawy.quick.modular.api.entity.TMchCashFlow;
import cn.hawy.quick.modular.api.entity.TMchInfo;
import cn.hawy.quick.modular.api.service.TCardAuthService;
import cn.hawy.quick.modular.api.service.TMchCardService;
import cn.hawy.quick.modular.api.service.TMchCashFlowService;
import cn.hawy.quick.modular.api.service.TMchInfoService;
import cn.hawy.quick.modular.api.utils.DateUtils;
import cn.hawy.quick.modular.api.utils.ExportExcelUtil;
import cn.hawy.quick.modular.business.warpper.CardAuthWrapper;
import cn.hawy.quick.modular.business.warpper.MchCardWrapper;
import cn.hawy.quick.modular.business.warpper.MchCashFlowWrapper;
import cn.hawy.quick.modular.business.warpper.MchInfoWrapper;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.RequestEmptyException;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志管理的控制器
 *
 */
@Controller("business_mch")
@RequestMapping("/business/mch")
public class MchController extends BaseController {

    private static String PREFIX = "/modular/business/mch/";

    @Autowired
    TMchInfoService mchInfoService;
    @Autowired
    TMchCashFlowService mchCashFlowService;
    @Autowired
    TCardAuthService cardAuthService;
    @Autowired
    TMchCardService mchCardService;


    /**
     * 跳转到商户信息的首页
     *
     */
    @RequestMapping("/mchInfo")
    public String mchInfo() {
        return PREFIX + "mch_info.html";
    }

    @RequestMapping("/mchInfoDetails")
    public String mchInfoDetails(@RequestParam String mchId) {
        return PREFIX + "mch_info_detail.html";
    }

    @RequestMapping("/getMchInfo")
    @ResponseBody
    public Object getMchInfo(@RequestParam String mchId) {
        if (ToolUtil.isEmpty(mchId)) {
            throw new RequestEmptyException();
        }
        TMchInfo mchInfo = mchInfoService.getById(mchId);
        HashMap<Object, Object> hashMap = CollectionUtil.newHashMap();
        Map<String, Object> map = BeanUtil.beanToMap(mchInfo);
        hashMap.putAll(map);
        return ResponseData.success(hashMap);
    }

    /**
     * 查询商户信息列表
     *
     */
    @RequestMapping("/mchInfoList")
    @ResponseBody
    public Object mchInfoList(@RequestParam(required = false) String mchId,
                              @RequestParam(required = false) String beginTime,
                              @RequestParam(required = false) String endTime,
                              @RequestParam(required = false) String mchName,
                              @RequestParam(required = false) String deptId,
                              @RequestParam(required = false) String mobile) {
        //获取分页参数
        Page page = LayuiPageFactory.defaultPage();
        if (ShiroKit.isAdmin()) {
            List<Map<String, Object>> result = mchInfoService.findAll(page, null, mchId, beginTime, endTime, mchName, deptId, mobile);
            page.setRecords(new MchInfoWrapper(result).wrap());
        }else {
        	String join = CollectionKit.join(ShiroKit.getDeptDataScope(), ",");
        	List<Map<String, Object>> result = mchInfoService.findAll(page, join, mchId, beginTime, endTime, mchName, deptId, mobile);
            page.setRecords(new MchInfoWrapper(result).wrap());
        }
        return LayuiPageFactory.createPageInfo(page);
    }


    /**
     * 跳转到商户提现的首页
     *
     */
    @RequestMapping("/mchCashFlow")
    public String mchCashFlow() {
        return PREFIX + "mch_cash_flow.html";
    }

    /**
     * 跳转到商户提现的首页
     *
     */
    @RequestMapping("/mchCashFlowList")
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
        	List<Map<String, Object>> result = mchCashFlowService.findAll(page, null,deptId, beginTime, endTime,cashId, outTradeNo, cashStatus, mchName, bankCardNo);
            page.setRecords(new MchCashFlowWrapper(result).wrap());
        }else {
        	String join = CollectionKit.join(ShiroKit.getDeptDataScope(), ",");
        	List<Map<String, Object>> result = mchCashFlowService.findAll(page, join,deptId, beginTime, endTime,cashId, outTradeNo, cashStatus, mchName, bankCardNo);
            page.setRecords(new MchCashFlowWrapper(result).wrap());
        }
        return LayuiPageFactory.createPageInfo(page);
    }

    private List<MchCashExcel> transForMchExport(List<TMchCashFlow> dataList){
        List<MchCashExcel> dataVals = new ArrayList<MchCashExcel>();
        for (TMchCashFlow data : dataList) {
            MchCashExcel mchCashExcel = new MchCashExcel();
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
    @RequestMapping("/mchCashExcelList")
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
            String[] colNames = { "提现号", "商户号", "商户名称", "银行卡号", "渠道号", "提现金额", "提现状态", "错误原因", "提现手续费","渠道提现成本","渠道利润", "出款金额", "外部提现号", "创建时间" };
            List<MchCashExcel> dataVals = new ArrayList<MchCashExcel>();
            if (ShiroKit.isAdmin()) {
                List<TMchCashFlow> pay = mchCashFlowService.find(null, deptId, beginTime, endTime,cashId, outTradeNo, cashStatus, mchName, bankCardNo);
                dataVals.addAll(this.transForMchExport(pay));
            } else {
                String join = CollectionKit.join(ShiroKit.getDeptDataScope(), ",");
                List<TMchCashFlow> pay = mchCashFlowService.find(join, null, beginTime, endTime,cashId, outTradeNo, cashStatus, mchName, bankCardNo);
                dataVals.addAll(this.transForMchExport(pay));
            }

            // List<UserContact> dataVals = user.getContactsList();
            // OutputStream outps = new FileOutputStream("D://stud.xls"); //
            // 输出到服务器
            // OutputStream outps = response.getOutputStream(); // 输出到客户端
            response.setContentType("octets/stream");
            excelName = new String(excelName.getBytes("GB2312"), "ISO8859-1") + ".xls";
            response.addHeader("Content-Disposition", "attachment;filename=" + excelName);
            ExportExcelUtil<MchCashExcel> epec = new ExportExcelUtil<MchCashExcel>();
            epec.expExcel(sheetName, titleName, colWidths, colNames, dataVals, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/cardAuth")
    public String cardAuth() {
        return PREFIX + "card_auth.html";
    }

    /**
     * 查询鉴权列表
     *
     */
    @RequestMapping("/cardAuthList")
    @ResponseBody
    public Object cardAuthList(@RequestParam(required = false) String beginTime,
                              @RequestParam(required = false) String endTime,
                              @RequestParam(required = false) String deptId) {
        //获取分页参数
        Page page = LayuiPageFactory.defaultPage();
        if (ShiroKit.isAdmin()) {
            List<Map<String, Object>> result = cardAuthService.findAll(page, null, beginTime, endTime, deptId);
            page.setRecords(new CardAuthWrapper(result).wrap());
        }else {
        	String join = CollectionKit.join(ShiroKit.getDeptDataScope(), ",");
        	List<Map<String, Object>> result = cardAuthService.findAll(page, join, beginTime, endTime, deptId);
            page.setRecords(new CardAuthWrapper(result).wrap());
        }
        return LayuiPageFactory.createPageInfo(page);
    }
    
    @RequestMapping("/cardAuthTj")
    @ResponseBody
    public Object mchCashTj(@RequestParam(required = false) String beginTime,
    						 @RequestParam(required = false) String endTime,
            				 @RequestParam(required = false) String deptId)
    {
    	Map<String,Object> sMap = cardAuthService.tongji(beginTime, endTime, deptId);
    	StringBuffer tjHtmlStr = new StringBuffer();
    	tjHtmlStr.append("<table class=\"layui-table\">");
    	tjHtmlStr.append("<colgroup><col width=\"25%\"><col width=\"25%\"><col width=\"25%\"><col width=\"25%\"></colgroup>");
    	tjHtmlStr.append("<tbody>");
    	tjHtmlStr.append("<td class=\"text-right\">成功笔数：</td>");
    	tjHtmlStr.append("<td><span>"+sMap.get("authNum").toString()+"</span></td>");
    	tjHtmlStr.append("<td class=\"text-right\">成功鉴权金额：</td>");
    	tjHtmlStr.append("<td><span>"+PayUtil.transFenToYuan(sMap.get("authAmount").toString())+"</span></td>");
    	tjHtmlStr.append("</tr>");
    	tjHtmlStr.append("</tbody>");
    	tjHtmlStr.append("</table>");
        return  ResponseData.success(tjHtmlStr);
    }

    @RequestMapping("/mchCard")
    public String mchCard() {
        return PREFIX + "mch_card.html";
    }

    @RequestMapping("/mchCardList")
    @ResponseBody
    public Object mchCardList(@RequestParam(required = false) String mchId, @RequestParam(required = false) String bankCardNo) {
        Page page = LayuiPageFactory.defaultPage();
        if (ShiroKit.isAdmin()) {
            List<Map<String, Object>> result = mchCardService.findAll(page, null, mchId, bankCardNo);
            page.setRecords(new MchCardWrapper(result).wrap());
        }else {
            String join = CollectionKit.join(ShiroKit.getDeptDataScope(), ",");
            List<Map<String, Object>> result = mchCardService.findAll(page, join, mchId, bankCardNo);
            page.setRecords(new MchCardWrapper(result).wrap());
        }
        return LayuiPageFactory.createPageInfo(page);
    }

}
