package cn.hawy.quick.modular.api.controller;

import cn.hawy.quick.config.properties.GyfProperties;
import cn.hawy.quick.core.common.RestResponse;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.util.IdGenerator;
import cn.hawy.quick.core.util.MapUtils;
import cn.hawy.quick.core.util.RSA;
import cn.hawy.quick.modular.api.channel.GyfChannel;
import cn.hawy.quick.modular.api.channel.gyf.GyfResponseDto;
import cn.hawy.quick.modular.api.dto.gyf.BindCardDto;
import cn.hawy.quick.modular.api.dto.gyf.QueryDto;
import cn.hawy.quick.modular.api.dto.gyf.RechargeDto;
import cn.hawy.quick.modular.api.dto.gyf.RegisterDto;
import cn.hawy.quick.modular.api.entity.*;
import cn.hawy.quick.modular.api.mq.MqPayNotify;
import cn.hawy.quick.modular.api.service.*;
import cn.hawy.quick.modular.api.utils.gyf.CipherUtil;
import cn.hawy.quick.modular.api.utils.gyf.SignUtil;
import cn.hawy.quick.modular.api.validate.PayValidate;
import cn.hawy.quick.modular.api.validate.gyf.GyfPayValidate;
import cn.hawy.quick.modular.system.entity.Dept;
import cn.hawy.quick.modular.system.service.DeptService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gyf/pay")
public class GyfPayController {

    @Autowired
    DeptService deptService;
    @Autowired
    TDeptRateChannelService deptRateChannelService;
    @Autowired
    TMchInfoService mchInfoService;
    @Autowired
    TMchInfoChannelService mchInfoChannelService;
    @Autowired
    GyfChannel gyfChannel;
    @Autowired
    GyfProperties gyfProperties;
    @Autowired
    TMchCardService mchCardService;
    @Autowired
    TMchCardChannelService mchCardChannelService;
    @Autowired
    TPayOrderService payOrderService;
    @Autowired
    MqPayNotify mqPayNotify;
    @Autowired
    TMerPoolService merPoolServic;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/recharge",method = RequestMethod.POST)
    @ResponseBody
    public Object recharge(RechargeDto rechargeDto) {
        log.info("下游请求报文-recharge:request={}",JSON.toJSONString(rechargeDto));
        //参数校验
        GyfPayValidate.recharge(rechargeDto);

        if(NumberUtil.compare(Double.parseDouble(rechargeDto.getOrderAmount()),Double.parseDouble(gyfProperties.getMinOrderAmount())) < 0) {
            throw new RestException(401, "支付金额不能低于最低交易限额!");
        }
        //渠道商信息
        Dept dept = deptService.getById(rechargeDto.getPartnerId());
        if(dept == null) {
            throw new RestException(401, "渠道商信息错误!");
        }
        //签名信息校验
        Map<String,Object> checkSignMap = BeanUtil.beanToMap(rechargeDto);
        checkSignMap.remove("signature");
        checkSignMap = MapUtils.removeStrNull(checkSignMap);
        String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
        Boolean flag = RSA.checkSign(checkSignContent, rechargeDto.getSignature(), dept.getPartnerPublickey());
        if(!flag) {
            throw new RestException(401, "签名验证错误!");
        }
        TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(rechargeDto.getPartnerId(),rechargeDto.getMchId());
        if(mchInfo == null) {
            throw new RestException(401, "商户信息错误!");
        }
        TMchInfoChannel mchInfoChannel = mchInfoChannelService.findByMchIdAndChannel(rechargeDto.getMchId(), "gyf");
        if(mchInfoChannel == null) {
            throw new RestException(401, "商户信息渠道错误!");
        }
        TMchCard mchCard = mchCardService.findBybankCardNo(rechargeDto.getMchId(), rechargeDto.getCardNo());
        if(mchCard == null) {
            throw new RestException(401, "商户卡信息错误");
        }
        TMchCardChannel mchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),2,"gyf");
        if(mchCardChannel == null) {
            throw new RestException(401, "商户卡信息渠道错误");
        }
        TDeptRateChannel deptRateChannel = deptRateChannelService.findByDeptIdAndChannel(rechargeDto.getPartnerId(), "gyf");
        if(deptRateChannel == null) {
            throw new RestException(401, "未找到渠道费率信息");
        }
        TPayOrder payOrder = payOrderService.findByMchIdAndOutTradeNo(rechargeDto.getMchId(), rechargeDto.getOutTradeNo());
        if(payOrder != null) {
            throw new RestException(401, "订单号已存在!");
        }
        Integer orderStatus = 1;
        String returnMsg = "";
        String channelMerNo = "";
        if(StrUtil.isEmpty(rechargeDto.getTerminalIp())){
            List<TMerPool> merPoolList = merPoolServic.findByPC(mchInfo.getCustomerIdentNo().substring(0,4),0);;
            if(merPoolList.size() == 0) {
                orderStatus = 3;
                returnMsg = "订单上送的地区码交易不支持";
            }else {
                TMerPool merPool = RandomUtil.randomEle(merPoolList); //正常随机，不计算权重
                rechargeDto.setTerminalIp(merPool.getIp());
            }
        }

        String mchRate = "";
        Long mchFee = 0L;
        if(StrUtil.isEmpty(rechargeDto.getMchFee())) {
            mchRate = rechargeDto.getMchRate();
            mchFee = NumberUtil.round(NumberUtil.mul(rechargeDto.getOrderAmount(), mchRate),0, RoundingMode.UP).longValue();
        }else {
            mchRate = NumberUtil.div(rechargeDto.getMchFee(), rechargeDto.getOrderAmount(),4,RoundingMode.DOWN).toString();
            mchFee = NumberUtil.parseLong(rechargeDto.getMchFee());
        }
        if(NumberUtil.compare(Double.parseDouble(mchRate),Double.parseDouble(deptRateChannel.getCostRate())) < 0) {
            throw new RestException(401, "商户费率不能低于渠道商费率!");
        }
        Long orderId = IdGenerator.getIdLong();
        Long orderAmount = NumberUtil.parseLong(rechargeDto.getOrderAmount());
        //计算方式和后端通道保持一致
        //商户
        Long mchAmount = orderAmount - mchFee;
        //渠道
        Long deptFee = NumberUtil.round(NumberUtil.mul(rechargeDto.getOrderAmount(), deptRateChannel.getCostRate()),0,RoundingMode.UP).longValue();
        Long deptAmount = mchFee - deptFee;
        //代理
        Long agentFee = 0L;
        Long agentAmount = 0L;
        Long costFee = 0L;
        Long costAmount = 0L;
        if(StrUtil.isEmpty(dept.getAgentId())) { //没有代理,则平台利润是渠道差
            //平台利润
            costFee = NumberUtil.round(NumberUtil.mul(rechargeDto.getOrderAmount(), deptRateChannel.getChannelCostRate()),0).longValue();
            costAmount = deptFee -  costFee;
        }else { //有代理，则平台利润是代理差
            if(StrUtil.isEmpty(deptRateChannel.getAgentRate())){
                throw new RestException(401, "代理商费率为空!");
            }
            //代理利润
            agentFee = NumberUtil.round(NumberUtil.mul(rechargeDto.getOrderAmount(), deptRateChannel.getAgentRate()),0).longValue();
            agentAmount = deptFee - agentFee;
            //平台利润
            costFee = NumberUtil.round(NumberUtil.mul(rechargeDto.getOrderAmount(), deptRateChannel.getChannelCostRate()),0).longValue();
            costAmount = agentFee -  costFee;
        }
        //通道相关
        Long shareAmount = mchFee - costFee;
        payOrder = new TPayOrder();
        payOrder.setOrderId(orderId);
        payOrder.setMchId(rechargeDto.getMchId());
        payOrder.setMchName(mchInfo.getMchName());
        payOrder.setDeptId(rechargeDto.getPartnerId());
        payOrder.setChannel("gyf");
        payOrder.setChannelNo(deptRateChannel.getChannelNo());
        payOrder.setBankCardNo(rechargeDto.getCardNo());
        payOrder.setOutTradeNo(rechargeDto.getOutTradeNo());
        payOrder.setOrderAmount(orderAmount);
        payOrder.setOutMchId(mchInfoChannel.getOutMchId());
        payOrder.setMerPoolNo(channelMerNo);
        payOrder.setMchRate(mchRate);
        payOrder.setMchFee(mchFee);
        payOrder.setMchAmount(mchAmount);
        payOrder.setDeptRate(deptRateChannel.getCostRate());
        payOrder.setDeptAmount(deptAmount);
        payOrder.setAgentId(dept.getAgentId());
        payOrder.setAgentRate(deptRateChannel.getAgentRate());
        payOrder.setAgentAmount(agentAmount);
        payOrder.setCostRate(deptRateChannel.getChannelCostRate());
        payOrder.setCostAmount(costAmount);
        payOrder.setOrderStatus(orderStatus);
        payOrder.setReturnMsg(returnMsg);
        payOrder.setSplitStatus(1);
        payOrder.setNotifyUrl(rechargeDto.getNotifyUrl());
        payOrder.setNotifyCount(0);
        payOrder.setOrderTime(LocalDateTime.now());
        payOrderService.save(payOrder);

        //组装返回报文
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("partnerId", rechargeDto.getPartnerId());
        map.put("mchId", rechargeDto.getMchId());
        map.put("cardNo", rechargeDto.getCardNo());
        map.put("outTradeNo", rechargeDto.getOutTradeNo());
        map.put("orderStatus", String.valueOf(payOrder.getOrderStatus()));
        map.put("returnMsg", payOrder.getReturnMsg());
        if(payOrder.getOrderStatus() == 1) {
            Map<String, String> resMap = gyfChannel.sub_trans_pass_pay(payOrder, rechargeDto, mchInfo, mchInfoChannel);
            if ("1".equals(resMap.get("orderStatus"))) {
                //处理中不做处理
            } else if ("2".equals(resMap.get("orderStatus"))) {
                boolean orderStatusFlag = payOrderService.updateOrderStatusSuccess(payOrder, dept, resMap.get("returnMsg"));
                if (orderStatusFlag) {
                    map.put("orderStatus", "2");
                    map.put("returnMsg", resMap.get("returnMsg"));
                }
            } else if ("3".equals(resMap.get("orderStatus"))) {
                boolean orderStatusFlag = payOrderService.updateOrderStatusFail(payOrder.getOrderId(), resMap.get("returnMsg"));
                if (orderStatusFlag) {
                    map.put("orderStatus", "3");
                    map.put("returnMsg", resMap.get("returnMsg"));
                }
            } else {
                throw new RestException(401, "交易状态异常，请联系运维人员!");
            }
        }
        String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
        String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
        map.put("signature", signature);
        log.info("下游返回报文-recharge:response={}",JSON.toJSONString(map));
        return RestResponse.success(map);
    }


    @RequestMapping(value = "/query",method = RequestMethod.POST)
    @ResponseBody
    public Object query(QueryDto queryDto) {
        log.info("下游请求报文-query:request={}",JSON.toJSONString(queryDto));
        //参数校验
        GyfPayValidate.query(queryDto);
        //渠道商信息
        Dept dept = deptService.getById(queryDto.getPartnerId());
        if(dept == null) {
            throw new RestException(401, "渠道商信息错误!");
        }
        TDeptRateChannel deptRateChannel = deptRateChannelService.findByDeptIdAndChannel(queryDto.getPartnerId(), "gyf");
        if(deptRateChannel == null) {
            throw new RestException(401, "未找到渠道信息");
        }
        //签名信息校验
        Map<String,Object> checkSignMap = BeanUtil.beanToMap(queryDto);
        checkSignMap.remove("signature");
        checkSignMap = MapUtils.removeStrNull(checkSignMap);
        String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
        Boolean flag = RSA.checkSign(checkSignContent, queryDto.getSignature(), dept.getPartnerPublickey());
        if(!flag) {
            throw new RestException(401, "签名验证错误!");
        }
        TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(queryDto.getPartnerId(),queryDto.getMchId());
        if(mchInfo == null) {
            throw new RestException(401, "商户信息错误!");
        }
        TPayOrder payOrder = payOrderService.findByMchIdAndOutTradeNo(queryDto.getMchId(), queryDto.getOutTradeNo());
        if(payOrder == null) {
            throw new RestException(401, "订单号不存在!");
        }
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("partnerId", queryDto.getPartnerId());
        map.put("mchId", queryDto.getMchId());
        map.put("outTradeNo", queryDto.getOutTradeNo());
        map.put("orderAmount", String.valueOf(payOrder.getOrderAmount()));
        map.put("orderStatus", String.valueOf(payOrder.getOrderStatus()));
        map.put("returnMsg", payOrder.getReturnMsg());
        if(payOrder.getOrderStatus() == 1) {
            Map<String,String> resMap = gyfChannel.query_quick_pay(payOrder);
            if("1".equals(resMap.get("orderStatus"))) {
                //不做处理
            }else if("2".equals(resMap.get("orderStatus"))) {
                boolean orderStatusFlag = payOrderService.updateOrderStatusSuccess(payOrder, dept,resMap.get("returnMsg"));
                if(orderStatusFlag) {
                    map.put("orderStatus", "2");
                    map.put("returnMsg", resMap.get("returnMsg"));
                }
            }else if("3".equals(resMap.get("orderStatus"))) {
                boolean orderStatusFlag = payOrderService.updateOrderStatusFail(payOrder.getOrderId(),resMap.get("returnMsg"));
                if(orderStatusFlag) {
                    map.put("orderStatus", "3");
                    map.put("returnMsg", resMap.get("returnMsg"));
                }
            }else {
                throw new RestException(401, "订单查询失败!");
            }
        }
        String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
        String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
        map.put("signature", signature);
        log.info("下游返回报文-query:response={}",JSON.toJSONString(map));
        return RestResponse.success(map);
    }

    @RequestMapping(value = "/orderNotify",method = RequestMethod.POST)
    @ResponseBody
    public Object orderNotify(@RequestBody String result) {
        log.info("GYF订单通知-orderNotify:request={}",result);
        try{
            result = gyfChannel.callback(result);
        }catch (Exception e){
            throw new RestException(501, "通道解密异常");
        }
        log.info("GYF订单通知返回解密-orderNotify:response={}",result);
        GyfResponseDto gyfRes = JSONUtil.toBean(result, GyfResponseDto.class);
        if(gyfRes.getCode().equals("0000")){
            JSONObject dataRes = (JSONObject)gyfRes.getData();
            //获取订单信息
            TPayOrder payOrder = payOrderService.getById(dataRes.getStr("orderId"));
            if(payOrder == null) {
                log.info("GYF订单通知返回-orderNotify:response={}","订单不存在返回0001");
                return "0001";
            }
            //渠道商信息
            Dept dept = deptService.getById(payOrder.getDeptId());
            if(dept == null) {
                log.info("GYF订单通知返回-orderNotify:response={}","渠道不存在返回0002");
                return "0002";
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
            if(payOrder.getOrderStatus() == 1) {
                if("0".equals(dataRes.getStr("transState"))) {
                    boolean flag = payOrderService.updateOrderStatusSuccess(payOrder, dept, "交易成功");
                    if(flag) {
                        map.put("orderStatus", "2");
                        map.put("returnMsg", "交易成功");
                    }else {
                        log.info("GYF订单通知返回-orderNotify:response={}","状态修改失败返回0003");
                        return "0003";
                    }
                }else if("1".equals(dataRes.getStr("transState"))) {
                    log.info("GYF订单通知返回-orderNotify:response={}","回调通知处理中返回0004");
                    return "0004";
                }else if("3".equals(dataRes.getStr("transState"))) {
                    boolean flag = payOrderService.updateOrderStatusFail(payOrder.getOrderId(),dataRes.getStr("resultNote"));
                    if(flag) {
                        map.put("orderStatus", "3");
                        map.put("returnMsg", dataRes.getStr("resultNote"));
                    }else {
                        log.info("GYF订单通知返回-orderNotify:response={}","状态修改失败返回0005");
                        return "0005";
                    }
                }else {
                    log.info("GYF订单通知返回-orderNotify:response={}","回调通知其他状态返回0006");
                    return "0006";
                }
            }
            String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
            String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
            map.put("signature", signature);
            log.info("下游返回报文-payNotify:response={}",JSON.toJSONString(map));
            msgObj.put("notifyMsg", RestResponse.success(map));
            mqPayNotify.send(JSON.toJSONString(msgObj));
        }
        log.info("GYF订单通知-orderNotify:response={}","0000");
        return "0000";
    }




}
