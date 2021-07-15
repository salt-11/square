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

import cn.hawy.quick.core.common.RestResponse;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.common.page.LayuiPageFactory;
import cn.hawy.quick.core.shiro.ShiroKit;
import cn.hawy.quick.core.util.CollectionKit;
import cn.hawy.quick.core.util.PayUtil;
import cn.hawy.quick.modular.api.channel.EfpsChannel;
import cn.hawy.quick.modular.api.channel.SumBtChannel;
import cn.hawy.quick.modular.api.channel.SumChannel;
import cn.hawy.quick.modular.api.entity.*;
import cn.hawy.quick.modular.api.mq.MqPayNotify;
import cn.hawy.quick.modular.api.service.*;
import cn.hawy.quick.modular.api.utils.DateUtils;
import cn.hawy.quick.modular.api.utils.ExportExcelUtil;
import cn.hawy.quick.modular.business.warpper.OrderWrapper;
import cn.hawy.quick.modular.system.entity.Dept;
import cn.hawy.quick.modular.system.service.DeptService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
@Controller
@RequestMapping("/order")
public class OrderController extends BaseController {

    private static String PREFIX = "/modular/business/order/";

    @Autowired
    TPayOrderService payOrderService;
    @Autowired
	MqPayNotify mqPayNotify;
    @Autowired
	SumChannel sumChannel;
    @Autowired
	DeptService deptService;
    @Autowired
	TDeptRateChannelService deptRateChannelService;
    @Autowired
	TMchCardService mchCardService;
    @Autowired
	SumBtChannel sumbtChannel;
    @Autowired
	TMchCashFlowService mchCashFlowService;
    @Autowired
	EfpsChannel efpsChannel;
    @Autowired
    TPlatformRateChannelService platformRateChannelService;


    /**
     * 跳转到支付订单管理的首页
     *
     */
    @RequestMapping("payOrder")
    public String index(Model model) {
        return PREFIX + "pay_order.html";
    }

    @RequestMapping("payOrderHistory")
    public String payOrderHistory(Model model) {
        return PREFIX + "pay_order_history.html";
    }

    /**
     * 查询操作日志列表
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
        List<Map<String, Object>> result = payOrderService.findAll(page, null,deptId, beginTime, endTime, orderId, outTradeNo,mchId,orderStatus,channelNo);
        page.setRecords(new OrderWrapper(result).wrap());
    	return LayuiPageFactory.createPageInfo(page);
    }

    @RequestMapping("/payOrderHistoryList")
    @ResponseBody
    public Object payOrderHistoryList(@RequestParam(required = false) String beginTime,
                               @RequestParam(required = false) String endTime,
                               @RequestParam(required = false) String deptId,
                               @RequestParam(required = false) String orderId,
                               @RequestParam(required = false) String outTradeNo,
                               @RequestParam(required = false) String mchId,
                               @RequestParam(required = false) Integer orderStatus,
                               @RequestParam(required = false) String channelNo) {
        Page page = LayuiPageFactory.defaultPage();
        if (ShiroKit.isAdmin()) {
            List<Map<String, Object>> result = payOrderService.findHistoryAll(page, null,deptId, beginTime, endTime, orderId, outTradeNo,mchId,orderStatus,channelNo);
            page.setRecords(new OrderWrapper(result).wrap());
        }else {
            String join = CollectionKit.join(ShiroKit.getDeptDataScope(), ",");
            List<Map<String, Object>> result = payOrderService.findHistoryAll(page, join,deptId, beginTime, endTime, orderId, outTradeNo,mchId,orderStatus,channelNo);
            page.setRecords(new OrderWrapper(result).wrap());
        }
        return LayuiPageFactory.createPageInfo(page);
    }



    private List<PayOrderContact> transForOrderExport(List<TPayOrder> dataList){
        List<PayOrderContact> dataVals = new ArrayList<PayOrderContact>();
        for (TPayOrder data : dataList) {
            PayOrderContact payOrder = new PayOrderContact();
            payOrder.setOrderId(data.getOrderId());
            payOrder.setMchId(data.getMchId());
            payOrder.setMchName(data.getMchName());
            payOrder.setBankCardNo(data.getBankCardNo());

            if (data.getChannelNo().equals("000000000")){
                payOrder.setDeptType("银联新无卡");
            } else if (data.getChannelNo().equals("101243663")){
                payOrder.setDeptType("商盟新快捷");
            } else if (data.getChannelNo().equals("101243664")){
                payOrder.setDeptType("银联新快捷(大O)");
            } else if (data.getChannelNo().equals("101733657")){
                payOrder.setDeptType("商盟小额");
            } else if (data.getChannelNo().equals("101553668")){
                payOrder.setDeptType("银联新快捷O");
            } else if (data.getChannelNo().equals("101713675")){
                payOrder.setDeptType("银联新快捷N");
            } else if (data.getChannelNo().equals("000000001")){
                payOrder.setDeptType("银联小额");
            } else if (data.getChannelNo().equals("000000002")){
                payOrder.setDeptType("PAF小额");
            } else if (data.getChannelNo().equals("000000003")){
                payOrder.setDeptType("FF大额");
            } else if (data.getChannelNo().equals("102423765")){
                payOrder.setDeptType("银联新快捷X");
            }

            payOrder.setDeptId(data.getDeptId());
            if (data.getOrderAmount() == null) {
                payOrder.setOrderAmount("0");
            } else {
                payOrder.setOrderAmount(PayUtil.transFenToYuan(String.valueOf(data.getOrderAmount())));
            }
            payOrder.setMchRate(data.getMchRate());

            if (data.getMchFee() == null) {
                payOrder.setMchFee("0");
            } else {
                payOrder.setMchFee(PayUtil.transFenToYuan(String.valueOf(data.getMchFee())));
            }
            payOrder.setDeptRate(data.getDeptRate());

            if (data.getDeptAmount() == null){
                payOrder.setDeptAmount("0");
            } else {
                payOrder.setDeptAmount(PayUtil.transFenToYuan(String.valueOf(data.getDeptAmount())));
            }

            if (data.getOrderStatus() == 1){
                payOrder.setOrderStatus("支付中");
            } else if (data.getOrderStatus() == 2){
                payOrder.setOrderStatus("支付成功");
            } else if (data.getOrderStatus() == 3){
                payOrder.setOrderStatus("支付失败");
            } else if (data.getOrderStatus() == 4){
                payOrder.setOrderStatus("代付失败");
            } else if (data.getOrderStatus() == 5){
                payOrder.setOrderStatus("代付中");
            }

            payOrder.setReturnMsg(data.getReturnMsg());
            payOrder.setOutTradeNo(data.getOutTradeNo());
            payOrder.setOrderTime(data.getOrderTime());
            dataVals.add(payOrder);
        }
        return dataVals;
    }


    /**
     * 报表导出
     *
     *
     */
    @RequestMapping("/payOrderExcelList")
    @ResponseBody
    public void payOrderExcelList(@RequestParam(required = false) String beginTime,
                                  @RequestParam(required = false) String endTime,
                                  @RequestParam(required = false) String deptId,
                                  @RequestParam(required = false) String orderId,
                                  @RequestParam(required = false) String outTradeNo,
                                  @RequestParam(required = false) String mchId,
                                  @RequestParam(required = false) Integer orderStatus,
                                  @RequestParam(required = false) String channelNo,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception
    {
//        Map<String, Object> result = new HashMap<String, Object>();
        try {
            // System.out.println(userAuthen.next().getIdcardName());
            String excelName = "订单列表" + DateUtils.getCurrentTimeStr();
            String sheetName = "订单列表";
            String titleName = "订单列表";
            int[] colWidths = { 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
            String[] colNames = { "订单号", "商户号", "商户名称", "银行卡号", "支付类型", "渠道号", "订单金额", "商户费率", "商户手续费", "渠道商费率", "渠道商利润", "订单状态", "错误原因", "外部订单号", "订单时间" };
            List<PayOrderContact> dataVals = new ArrayList<PayOrderContact>();
            if (ShiroKit.isAdmin()) {
                List<TPayOrder> pay = payOrderService.find(null, deptId, beginTime, endTime,orderId, outTradeNo, mchId, orderStatus, channelNo);
                dataVals.addAll(this.transForOrderExport(pay));
            } else {
                String join = CollectionKit.join(ShiroKit.getDeptDataScope(), ",");
                List<TPayOrder> pay = payOrderService.find(join, null, beginTime, endTime,orderId, outTradeNo, mchId, orderStatus, channelNo);
                dataVals.addAll(this.transForOrderExport(pay));
            }

            // List<UserContact> dataVals = user.getContactsList();
            // OutputStream outps = new FileOutputStream("D://stud.xls"); //
            // 输出到服务器
            // OutputStream outps = response.getOutputStream(); // 输出到客户端
            response.setContentType("octets/stream");
            excelName = new String(excelName.getBytes("GB2312"), "ISO8859-1") + ".xls";
            response.addHeader("Content-Disposition", "attachment;filename=" + excelName);
            ExportExcelUtil<PayOrderContact> epec = new ExportExcelUtil<PayOrderContact>();
            epec.expExcel(sheetName, titleName, colWidths, colNames, dataVals, response.getOutputStream());
        } catch (Exception e) {

        }
    }

    @RequestMapping("/notify")
    @ResponseBody
    public Object notify(@RequestParam(required = false) String orderId) {
    	TPayOrder payOrder = payOrderService.getById(orderId);
    	if(payOrder == null) {
    		throw new ServiceException(400,"订单号不存在");
    	}
    	if(!payOrder.getOrderStatus().equals(2)) {
    		throw new ServiceException(400,"订单成功才能异步通知");
    	}
    	JSONObject msgObj = new JSONObject();
		msgObj.put("notifyUrl", payOrder.getNotifyUrl());
		msgObj.put("notifyCount", payOrder.getNotifyCount());
		msgObj.put("orderId", payOrder.getOrderId());
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", payOrder.getDeptId());
		map.put("mchId", payOrder.getMchId());
		map.put("outTradeNo", payOrder.getOutTradeNo());
		map.put("orderAmount", String.valueOf(payOrder.getOrderAmount()));
		map.put("orderStatus", String.valueOf(payOrder.getOrderStatus()));
		map.put("returnMsg", payOrder.getReturnMsg());
    	msgObj.put("notifyMsg", RestResponse.success(map));
    	mqPayNotify.send(JSON.toJSONString(msgObj));
    	return SUCCESS_TIP;
    }

    @RequestMapping("/query")
    @ResponseBody
    public Object query(@RequestParam(required = false) String orderId) {
    	TPayOrder payOrder = payOrderService.getById(orderId);
    	if(payOrder == null) {
    		throw new ServiceException(400,"订单号不存在");
    	}
    	Dept dept = deptService.getById(payOrder.getDeptId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
        TMchCard mchCard = mchCardService.findBybankCardNo(payOrder.getMchId(), payOrder.getBankCardNo());
        if(mchCard == null) {
            throw new RestException(401, "商户卡信息错误");
        }
        TPlatformRateChannel platformRateChannel = platformRateChannelService.findByChannel("sum");
        if(platformRateChannel == null) {
            throw new RestException(401, "未找到平台信息");
        }
    	if("101243663".equals(payOrder.getChannelNo()) || "101243664".equals(payOrder.getChannelNo()) || "101733657".equals(payOrder.getChannelNo())) {
    		Map<String,String> resMap = sumChannel.queryOrderStatus(String.valueOf(payOrder.getOrderId()),platformRateChannel.getChannelNo(),platformRateChannel.getChannelMerAppId());
    		if("1".equals(resMap.get("orderStatus"))) {
				//不做处理
			}else if("2".equals(resMap.get("orderStatus"))) {
				payOrderService.updateOrderStatusSuccess(payOrder,resMap.get("returnMsg"));
			}else if("3".equals(resMap.get("orderStatus"))) {
				payOrderService.updateOrderStatusFail(payOrder.getOrderId(),resMap.get("returnMsg"));
			}else {
				throw new RestException(401, "订单查询失败!");
			}
    	}else if("101553668".equals(payOrder.getChannelNo()) || "101713675".equals(payOrder.getChannelNo()) || "102423765".equals(payOrder.getChannelNo())) {

    		TMchCashFlow mchCashFlow = mchCashFlowService.getById(payOrder.getOrderId());
    		if(mchCashFlow == null) {
    			throw new RestException(401, "提现单号不存在!");
    		}
    		Map<String,String> resMap = sumbtChannel.query(String.valueOf(payOrder.getOrderId()),platformRateChannel.getChannelNo());
			if("1".equals(resMap.get("orderStatus"))) {
			}else if("2".equals(resMap.get("orderStatus"))) {
				payOrderService.updateOrderStatusSuccessOfSumBt(payOrder,mchCashFlow);
			}else if("3".equals(resMap.get("orderStatus"))) {
				payOrderService.updateOrderStatusFailOfSumBt(payOrder.getOrderId(),resMap.get("returnMsg"));
			}else if("4".equals(resMap.get("orderStatus"))) {
				payOrderService.updateOrderStatusAndReturnMsgOfSumBt(payOrder.getOrderId(), 4, resMap.get("returnMsg"));
			}else if("5".equals(resMap.get("orderStatus"))) {
				if(payOrder.getOrderStatus() == 1) {
					payOrderService.updateOrderStatusOther(payOrder.getOrderId(), 5);
				}
			}else {
				throw new RestException(401, "订单查询失败!");
			}
    	}
    	return SUCCESS_TIP;
    }




}
