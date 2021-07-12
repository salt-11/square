package cn.hawy.quick.modular.api.controller;

import cn.hawy.quick.config.properties.GyfProperties;
import cn.hawy.quick.core.common.RestResponse;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.util.IdGenerator;
import cn.hawy.quick.core.util.MapUtils;
import cn.hawy.quick.core.util.PayUtil;
import cn.hawy.quick.core.util.RSA;
import cn.hawy.quick.modular.api.channel.GyfChannel;
import cn.hawy.quick.modular.api.channel.gyf.GyfResponseDto;
import cn.hawy.quick.modular.api.dto.gyf.*;
import cn.hawy.quick.modular.api.dto.sumbt.CardAuthDto;
import cn.hawy.quick.modular.api.entity.*;
import cn.hawy.quick.modular.api.mq.MqPayNotify;
import cn.hawy.quick.modular.api.service.*;
import cn.hawy.quick.modular.api.validate.MchValidate;
import cn.hawy.quick.modular.api.validate.gyf.GyfMchValidate;
import cn.hawy.quick.modular.api.validate.sum.SumMchValidate;
import cn.hawy.quick.modular.api.validate.sumbt.SumBtMchValidate;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gyf/mch")
public class GyfMchController {

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
    TMchCashFlowService mchCashFlowService;
    @Autowired
    MqPayNotify mqPayNotify;
    @Autowired
    TMerPoolService merPoolServic;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    @ResponseBody
    public Object register(RegisterDto registerDto) {
        log.info("下游请求报文-register:request={}",JSON.toJSONString(registerDto));
        GyfMchValidate.register(registerDto);
        //渠道商信息
        Dept dept = deptService.getById(registerDto.getPartnerId());
        if(dept == null) {
            throw new RestException(401, "渠道商信息错误!");
        }
        //签名信息校验
        Map<String,Object> checkSignMap = BeanUtil.beanToMap(registerDto);
        checkSignMap.remove("signature");
        checkSignMap = MapUtils.removeStrNull(checkSignMap);
        String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
        Boolean flag = RSA.checkSign(checkSignContent, registerDto.getSignature(), dept.getPartnerPublickey());
        if(!flag) {
            throw new RestException(401, "签名验证错误!");
        }
        TDeptRateChannel deptRateChannel = deptRateChannelService.findByDeptIdAndChannel(registerDto.getPartnerId(), "gyf");
        if(deptRateChannel == null) {
            throw new RestException(401, "未找到渠道信息");
        }
        if(StrUtil.isEmpty(registerDto.getDeviceInfo())){
            registerDto.setDeviceInfo(PayUtil.genIMEI());
        }
        //通道调用
        String outMchId = gyfChannel.sub_merchant_register(registerDto,deptRateChannel);
        //插入数据
        String mchId = IdGenerator.getId();
        TMchInfo mchInfo = new TMchInfo();
        mchInfo.setMchId(mchId);
        mchInfo.setMchName(registerDto.getRealName());
        mchInfo.setMchShortName(registerDto.getRealName());
        mchInfo.setAreaCode("000000");
        mchInfo.setMchAddress(registerDto.getAddress());
        mchInfo.setDeptId(registerDto.getPartnerId());
        mchInfo.setMchStatus(2);
        mchInfo.setMobile(registerDto.getMobile());
        mchInfo.setEmail("xxx@xx.com");
        mchInfo.setCustomerName(registerDto.getRealName());
        mchInfo.setCustomerIdentType(0);
        mchInfo.setCustomerIdentNo(registerDto.getIdNo());
        mchInfo.setSettMode("D0");
        mchInfo.setSettCircle("0");
        mchInfo.setCardKind(1);
        mchInfo.setDeviceInfo(registerDto.getDeviceInfo());
        mchInfo.setCreateTime(LocalDateTime.now());
        //通道
        TMchInfoChannel mchInfoChannel = new TMchInfoChannel();
        mchInfoChannel.setMchId(mchId);
        mchInfoChannel.setChannel("gyf");
        mchInfoChannel.setOutMchId(outMchId);
        mchInfoChannel.setCreateTime(LocalDateTime.now());
        mchInfoService.addMerchant(mchInfo, mchInfoChannel);
        //组装返回报文
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("partnerId", registerDto.getPartnerId());
        map.put("mchId", mchId);
        String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
        String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
        map.put("signature", signature);
        log.info("下游返回报文-register:response={}", JSON.toJSONString(map));
        return RestResponse.success(map);
    }


    @RequestMapping(value = "/bindCard",method = RequestMethod.POST)
    @ResponseBody
    public Object binkCard(BindCardDto bindCardDto) {
        log.info("下游请求报文-binkCard:request={}",JSON.toJSONString(bindCardDto));
        GyfMchValidate.binkCard(bindCardDto);
        //渠道商信息
        Dept dept = deptService.getById(bindCardDto.getPartnerId());
        if(dept == null) {
            throw new RestException(401, "渠道商信息错误!");
        }
        TDeptRateChannel deptRateChannel = deptRateChannelService.findByDeptIdAndChannel(bindCardDto.getPartnerId(), "gyf");
        if(deptRateChannel == null) {
            throw new RestException(401, "未找到渠道信息");
        }
        //签名信息校验
        Map<String,Object> checkSignMap = BeanUtil.beanToMap(bindCardDto);
        checkSignMap.remove("signature");
        checkSignMap = MapUtils.removeStrNull(checkSignMap);
        String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
        Boolean flag = RSA.checkSign(checkSignContent, bindCardDto.getSignature(), dept.getPartnerPublickey());
        if(!flag) {
            throw new RestException(401, "签名验证错误!");
        }
        TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(bindCardDto.getPartnerId(), bindCardDto.getMchId());
        if(mchInfo == null) {
            throw new RestException(401, "商户信息错误!");
        }
        TMchInfoChannel mchInfoChannel = mchInfoChannelService.findByMchIdAndChannel(bindCardDto.getMchId(), "gyf");
        if(mchInfoChannel == null) {
            throw new RestException(401, "商户信息渠道错误!");
        }
        if(StrUtil.isEmpty(bindCardDto.getTerminalIp())){
            List<TMerPool> merPoolList = merPoolServic.findByPC(mchInfo.getCustomerIdentNo().substring(0,4),0);
            if(merPoolList.size() == 0) {
                throw new RestException(401, "绑卡上送的地区码不支持!");
            }else {
                TMerPool merPool = RandomUtil.randomEle(merPoolList); //正常随机，不计算权重
                bindCardDto.setTerminalIp(merPool.getIp());
            }
        }
        TMchCard mchCard = mchCardService.findBybankCardNo(bindCardDto.getMchId(), bindCardDto.getCardNo());
        if(mchCard != null) {
            TMchCardChannel mchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),2,"gyf");
            if(mchCardChannel == null) {
                throw new RestException(401, "商户卡信息渠道错误");
            }
            gyfChannel.sub_sign_pass_pay(bindCardDto,mchInfo,mchInfoChannel);
            //mchCard = new TMchCard();
            //mchCard.setMchId(bindCardDto.getMchId());
            mchCard.setBankCardNo(bindCardDto.getCardNo());
            mchCard.setBankCardType(bindCardDto.getCardType());
            mchCard.setExpired(bindCardDto.getValidYear()+bindCardDto.getValidMonth());
            mchCard.setCvn(bindCardDto.getCvv());
            mchCard.setMobile(bindCardDto.getMobile());
            mchCard.setCreateTime(LocalDateTime.now());

            //mchCardChannel.setOutMchId(mchInfoChannel.getOutMchId());
            //mchCardChannel.setStatus(2);
            //mchCardChannel.setChannel("gyf");
            mchCardChannel.setCreateTime(LocalDateTime.now());
            mchCardService.bindCard(mchCard, mchCardChannel);

        }else{

            gyfChannel.sub_sign_pass_pay(bindCardDto,mchInfo,mchInfoChannel);
            mchCard = new TMchCard();
            mchCard.setMchId(bindCardDto.getMchId());
            mchCard.setBankCardNo(bindCardDto.getCardNo());
            mchCard.setBankCardType(bindCardDto.getCardType());
            mchCard.setExpired(bindCardDto.getValidYear()+bindCardDto.getValidMonth());
            mchCard.setCvn(bindCardDto.getCvv());
            mchCard.setMobile(bindCardDto.getMobile());
            mchCard.setCreateTime(LocalDateTime.now());

            TMchCardChannel	mchCardChannel = new TMchCardChannel();
            mchCardChannel.setOutMchId(mchInfoChannel.getOutMchId());
            mchCardChannel.setStatus(2);
            mchCardChannel.setChannel("gyf");
            mchCardChannel.setCreateTime(LocalDateTime.now());
            mchCardService.bindCard(mchCard, mchCardChannel);
        }
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("partnerId", bindCardDto.getPartnerId());
        map.put("mchId", bindCardDto.getMchId());
        map.put("cardNo", bindCardDto.getCardNo());
        map.put("status", "2");//绑卡成功
        String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
        String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
        map.put("signature", signature);
        log.info("下游返回报文-bindCard:response={}",JSON.toJSONString(map));
        return RestResponse.success(map);
    }

    @RequestMapping(value = "/balanceQuery",method = RequestMethod.POST)
    @ResponseBody
    public Object balanceQuery(BalanceQueryDto balanceQueryDto) {
        log.info("下游请求报文-balanceQuery:request={}",JSON.toJSONString(balanceQueryDto));
        GyfMchValidate.balanceQuery(balanceQueryDto);
        //渠道商信息
        Dept dept = deptService.getById(balanceQueryDto.getPartnerId());
        if(dept == null) {
            throw new RestException(401, "渠道商信息错误!");
        }
        TDeptRateChannel deptRateChannel = deptRateChannelService.findByDeptIdAndChannel(balanceQueryDto.getPartnerId(), "gyf");
        if(deptRateChannel == null) {
            throw new RestException(401, "未找到渠道信息");
        }
        //签名信息校验
        Map<String,Object> checkSignMap = BeanUtil.beanToMap(balanceQueryDto);
        checkSignMap.remove("signature");
        checkSignMap = MapUtils.removeStrNull(checkSignMap);
        String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
        Boolean flag = RSA.checkSign(checkSignContent, balanceQueryDto.getSignature(), dept.getPartnerPublickey());
        if(!flag) {
            throw new RestException(401, "签名验证错误!");
        }
        //查询商户信息
        TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(balanceQueryDto.getPartnerId(), balanceQueryDto.getMchId());
        if(mchInfo == null) {
            throw new RestException(401, "商户信息错误!");
        }
        TMchInfoChannel mchInfoChannel = mchInfoChannelService.findByMchIdAndChannel(balanceQueryDto.getMchId(), "gyf");
        if(mchInfoChannel == null) {
            throw new RestException(401, "商户信息渠道错误!");
        }
        String availableBalance = gyfChannel.sub_merchant_query_balance(mchInfoChannel);
        //组装返回报文
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("partnerId", balanceQueryDto.getPartnerId());
        map.put("mchId", balanceQueryDto.getMchId());
        map.put("balance", availableBalance);
        String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
        String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
        map.put("signature", signature);
        log.info("下游返回报文-balanceQuery:response={}",JSON.toJSONString(map));
        return RestResponse.success(map);
    }


    /**
     * 还款
     * @param
     * @return
     */
    @RequestMapping(value = "/withdraw",method = RequestMethod.POST)
    @ResponseBody
    public Object withdraw(WithdrawDto withdrawDto) {
        log.info("下游请求报文-withdraw={}",JSON.toJSONString(withdrawDto));
        //参数校验
        GyfMchValidate.withdraw(withdrawDto);
        //渠道商信息
        Dept dept = deptService.getById(withdrawDto.getPartnerId());
        if(dept == null) {
            throw new RestException(401, "渠道商信息错误!");
        }
        TDeptRateChannel deptRateChannel = deptRateChannelService.findByDeptIdAndChannel(withdrawDto.getPartnerId(), "gyf");
        if(deptRateChannel == null) {
            throw new RestException(401, "未找到渠道信息");
        }
        Long cashFee = NumberUtil.parseLong(withdrawDto.getCashFee());
        if(cashFee < NumberUtil.parseLong(deptRateChannel.getCashRate())) {
            throw new RestException(401, "提现手续费不能小于提现成本手续费!");
        }
        //签名信息校验
        Map<String,Object> checkSignMap = BeanUtil.beanToMap(withdrawDto);
        checkSignMap.remove("signature");
        checkSignMap = MapUtils.removeStrNull(checkSignMap);
        String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
        Boolean flag = RSA.checkSign(checkSignContent, withdrawDto.getSignature(), dept.getPartnerPublickey());
        if(!flag) {
            throw new RestException(401, "签名验证错误!");
        }
        //查询商户信息
        TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(withdrawDto.getPartnerId(), withdrawDto.getMchId());
        if(mchInfo == null) {
            throw new RestException(401, "商户信息错误!");
        }
        TMchCard mchCard = mchCardService.findBybankCardNo(withdrawDto.getMchId(), withdrawDto.getCardNo());
        if(mchCard == null) {
            throw new RestException(401, "商户卡信息错误");
        }
        TMchCardChannel mchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),2,"gyf");
        if(mchCardChannel == null) {
            throw new RestException(401, "商户卡信息渠道错误");
        }
        TMchCashFlow mchCashFlow = mchCashFlowService.findByMchIdAndOutTradeNo(withdrawDto.getMchId(), withdrawDto.getOutTradeNo());
        if(mchCashFlow != null) {
            throw new RestException(401, "提现订单号已存在!");
        }
        Long cashAmount = NumberUtil.parseLong(withdrawDto.getCashAmount());
        Long deptAmount = cashFee - NumberUtil.parseLong(deptRateChannel.getCashRate());
        Long costAmount = NumberUtil.parseLong(deptRateChannel.getCashRate()) - NumberUtil.parseLong(gyfProperties.getCostFee());
        Long outAmount = cashAmount - cashFee;
        Long cashId = IdGenerator.getIdLong();
        mchCashFlow = new TMchCashFlow();
        mchCashFlow.setCashId(cashId);
        mchCashFlow.setMchId(withdrawDto.getMchId());
        mchCashFlow.setMchName(mchInfo.getMchShortName());
        mchCashFlow.setDeptId(withdrawDto.getPartnerId());
        mchCashFlow.setBankCardNo(withdrawDto.getCardNo());
        mchCashFlow.setOutTradeNo(withdrawDto.getOutTradeNo());
        mchCashFlow.setOutMchId(mchCardChannel.getOutMchId());
        mchCashFlow.setCashAmount(cashAmount);
        mchCashFlow.setCashFee(cashFee);
        mchCashFlow.setOutAmount(outAmount);
        mchCashFlow.setCashStatus(1);
        mchCashFlow.setReturnMsg("");
        mchCashFlow.setCashRate(deptRateChannel.getCashRate());
        mchCashFlow.setDeptAmount(deptAmount);
        mchCashFlow.setCostFee(gyfProperties.getCostFee());
        mchCashFlow.setCostAmount(costAmount);
        mchCashFlow.setNotifyUrl(withdrawDto.getNotifyUrl());
        mchCashFlow.setNotifyCount(0);
        mchCashFlow.setCreateTime(LocalDateTime.now());
        mchCashFlowService.save(mchCashFlow);
        Map<String,String> resMap = gyfChannel.sub_trans_credit_df(withdrawDto,mchCashFlow,mchInfo);

        //组装返回报文
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("partnerId", withdrawDto.getPartnerId());
        map.put("mchId", withdrawDto.getMchId());
        map.put("cardNo", withdrawDto.getCardNo());
        map.put("outTradeNo", withdrawDto.getOutTradeNo());
        map.put("cashAmount", withdrawDto.getCashAmount());
        map.put("cashStatus", String.valueOf(mchCashFlow.getCashStatus()));
        map.put("returnMsg", mchCashFlow.getReturnMsg());
        if("1".equals(resMap.get("orderStatus"))) {
            //不做处理
        }else if("2".equals(resMap.get("orderStatus"))) {
            boolean orderStatusFlag = mchCashFlowService.updateCashStatusSuccess(mchCashFlow, dept, resMap.get("returnMsg"));
            if(orderStatusFlag) {
                map.put("cashStatus", "2");
                map.put("returnMsg", resMap.get("returnMsg"));
            }
        }else if("3".equals(resMap.get("orderStatus"))) {
            boolean orderStatusFlag = mchCashFlowService.updateCashStatusFail(mchCashFlow.getCashId(), resMap.get("returnMsg"));
            if(orderStatusFlag) {
                map.put("cashStatus", "3");
                map.put("returnMsg", resMap.get("returnMsg"));
            }
        }
        String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
        String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
        map.put("signature", signature);
        log.info("下游返回报文-mchCash:map={}",JSON.toJSONString(map));
        return RestResponse.success(map);
    }

    /**
     * 还款查询
     * @param
     * @return
     */
    @RequestMapping(value = "/withdrawQuery",method = RequestMethod.POST)
    @ResponseBody
    public Object withdrawQuery(WithdrawQueryDto withdrawQueryDto) {
        log.info("下游请求报文-withdrawQuery={}",JSON.toJSONString(withdrawQueryDto));
        //参数校验
        GyfMchValidate.withdrawQuery(withdrawQueryDto);
        //渠道商信息
        Dept dept = deptService.getById(withdrawQueryDto.getPartnerId());
        if(dept == null) {
            throw new RestException(401, "渠道商信息错误!");
        }
        TDeptRateChannel deptRateChannel = deptRateChannelService.findByDeptIdAndChannel(withdrawQueryDto.getPartnerId(),"gyf");
        if(deptRateChannel == null) {
            throw new RestException(401, "未找到渠道信息");
        }
        //签名信息校验
        Map<String,Object> checkSignMap = BeanUtil.beanToMap(withdrawQueryDto);
        checkSignMap.remove("signature");
        checkSignMap = MapUtils.removeStrNull(checkSignMap);
        String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
        Boolean flag = RSA.checkSign(checkSignContent, withdrawQueryDto.getSignature(), dept.getPartnerPublickey());
        if(!flag) {
            throw new RestException(401, "签名验证错误!");
        }
        //查询商户信息
        TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(withdrawQueryDto.getPartnerId(), withdrawQueryDto.getMchId());
        if(mchInfo == null) {
            throw new RestException(401, "商户信息错误!");
        }
        TMchCashFlow mchCashFlow = mchCashFlowService.findByMchIdAndOutTradeNo(withdrawQueryDto.getMchId(), withdrawQueryDto.getOutTradeNo());
        if(mchCashFlow == null) {
            throw new RestException(401, "提现订单号不存在!");
        }
        //组装返回报文
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("partnerId", withdrawQueryDto.getPartnerId());
        map.put("mchId", withdrawQueryDto.getMchId());
        map.put("outTradeNo", withdrawQueryDto.getOutTradeNo());
        map.put("cashAmount", String.valueOf(mchCashFlow.getCashAmount()));
        map.put("cashStatus", String.valueOf(mchCashFlow.getCashStatus()));
        map.put("returnMsg", mchCashFlow.getReturnMsg());
        if(mchCashFlow.getCashStatus() == 1) {
            Map<String,String> resMap = gyfChannel.query_df(mchCashFlow);
            if("1".equals(resMap.get("orderStatus"))) {
                //不做处理
            }else if("2".equals(resMap.get("orderStatus"))) {
                boolean orderStatusFlag = mchCashFlowService.updateCashStatusSuccess(mchCashFlow, dept, "交易成功");
                if(orderStatusFlag) {
                    map.put("cashStatus", "2");
                    map.put("returnMsg", "交易成功");
                }
            }else if("3".equals(resMap.get("orderStatus"))) {
                boolean orderStatusFlag = mchCashFlowService.updateCashStatusFail(mchCashFlow.getCashId(), resMap.get("returnMsg"));
                if(orderStatusFlag) {
                    map.put("cashStatus", "3");
                    map.put("returnMsg", resMap.get("returnMsg"));
                }
            }else {
                throw new RestException(401, "提现订单查询失败!");
            }
        }
        String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
        String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
        map.put("signature", signature);
        log.info("下游返回报文-withdrawQuery={}",JSON.toJSONString(map));
        return RestResponse.success(map);
    }

    @RequestMapping(value = "/dfNotify",method = RequestMethod.POST)
    @ResponseBody
    public Object dfNotify(@RequestBody String result) {
        log.info("GYF代付通知-dfNotify:request={}",result);
        try{
            result = gyfChannel.callback(result);
        }catch (Exception e){
            log.info("GYF订单通知返回-dfNotify:response={}","解密异常回0007");
            return "0007";
        }
        GyfResponseDto gyfRes = JSONUtil.toBean(result, GyfResponseDto.class);
        if(gyfRes.getCode().equals("0000")){
            JSONObject dataRes = (JSONObject)gyfRes.getData();
            //获取订单信息
            TMchCashFlow mchCashFlow = mchCashFlowService.getById(dataRes.getStr("orderId"));
            if(mchCashFlow == null) {
                log.info("GYF订单通知返回-dfNotify:response={}","订单不存在返回0001");
                return "0001";
            }
            //渠道商信息
            Dept dept = deptService.getById(mchCashFlow.getDeptId());
            if(dept == null) {
                log.info("GYF订单通知返回-dfNotify:response={}","渠道不存在返回0002");
                return "0002";
            }
            JSONObject msgObj = new JSONObject();
            msgObj.put("notifyUrl", mchCashFlow.getNotifyUrl());
            msgObj.put("notifyCount", mchCashFlow.getNotifyCount());
            msgObj.put("cashId", mchCashFlow.getCashId());
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("partnerId", mchCashFlow.getDeptId());
            map.put("mchId", mchCashFlow.getMchId());
            map.put("outTradeNo", mchCashFlow.getOutTradeNo());
            map.put("cashAmount", String.valueOf(mchCashFlow.getCashAmount()));
            map.put("outAmount", String.valueOf(mchCashFlow.getOutAmount()));
            map.put("cashStatus", String.valueOf(mchCashFlow.getCashStatus()));
            map.put("returnMsg", mchCashFlow.getReturnMsg());
            if(mchCashFlow.getCashStatus() == 1) {
                if("0".equals(dataRes.getStr("transState"))) {
                    boolean flag = mchCashFlowService.updateCashStatusSuccess(mchCashFlow, dept, "交易成功");
                    if(flag) {
                        map.put("cashStatus", "2");
                        map.put("returnMsg", "交易成功");
                    }else {
                        log.info("GYF订单通知返回-dfNotify:response={}","状态修改失败返回0003");
                        return "0003";
                    }
                }else if("1".equals(dataRes.getStr("transState"))) {
                    log.info("GYF订单通知返回-dfNotify:response={}","回调通知处理中返回0004");
                    return "0004";
                }else if("3".equals(dataRes.getStr("transState"))) {
                    boolean flag = mchCashFlowService.updateCashStatusFail(mchCashFlow.getCashId(), dataRes.getStr("resultNote"));
                    if(flag) {
                        map.put("cashStatus", "3");
                        map.put("returnMsg", dataRes.getStr("resultNote"));
                    }else {
                        log.info("GYF订单通知返回-dfNotify:response={}","状态修改失败返回0005");
                        return "0005";
                    }
                }else {
                    log.info("GYF订单通知返回-dfNotify:response={}","回调通知其他状态返回0006");
                    return "0006";
                }
            }
            String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
            String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
            map.put("signature", signature);
            log.info("下游返回报文-dfNotify:response={}",JSON.toJSONString(map));
            msgObj.put("notifyMsg", RestResponse.success(map));
            mqPayNotify.send(JSON.toJSONString(msgObj));
        }
        log.info("GYF代付通知-dfNotify:response={}","0000");
        return "0000";
    }

    @RequestMapping(value = "/test",method = RequestMethod.POST)
    @ResponseBody
    public Object test(@RequestBody String message) {
        System.out.println(message);
        return "0000 ";
    }

    /**
     * 鉴权
     * @param cardAuthDto
     * @return
     */
//    @RequestMapping(value = "/cardAuth",method = RequestMethod.POST)
//    @ResponseBody
//    public Object cardAuth(CardAuthDto cardAuthDto) {
//        log.info("下游请求报文-cardAuth:request={}",JSON.toJSONString(cardAuthDto));
//        SumBtMchValidate.cardAuth(cardAuthDto);
//        //渠道商信息
//        Dept dept = deptService.getById(cardAuthDto.getPartnerId());
//        if(dept == null) {
//            throw new RestException(401, "渠道商信息错误!");
//        }
//        //签名信息校验
//        Map<String,Object> checkSignMap = BeanUtil.beanToMap(cardAuthDto);
//        checkSignMap.remove("signature");
//        checkSignMap = MapUtils.removeStrNull(checkSignMap);
//        String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
//        Boolean flag = RSA.checkSign(checkSignContent, cardAuthDto.getSignature(), dept.getPartnerPublickey());
//        if(!flag) {
//            throw new RestException(401, "签名验证错误!");
//        }
//        if(dept.getBalance()<NumberUtil.parseLong(dept.getCardAuthRate())){
//            throw new RestException(401, "渠道商余额不足!");
//        }
//        TCardAuth cardAuth = new TCardAuth();
//        BeanUtil.copyProperties(cardAuthDto, cardAuth);
//        cardAuth.setDeptId(cardAuthDto.getPartnerId());
//        cardAuth.setStatus(0);
//        cardAuth.setAmount(NumberUtil.parseLong(dept.getCardAuthRate()));
//        cardAuth.setCreateTime(LocalDateTime.now());
//        cardAuthService.save(cardAuth);
//        //组装返回报文
//        Map<String,Object> map = new HashMap<String,Object>();
//        Map<String,String> resMap = sumBtChannel.cardAuth(cardAuthDto);
//        String status = resMap.get("status");
//        map.put("returnMsg", "");
//        if("1".equals(status)) {
//            boolean updateStatusFlag = cardAuthService.updateStatusSuccess(cardAuth.getId(),1,NumberUtil.parseLong(cardAuth.getDeptId()),cardAuth.getAmount());
//            map.put("status",updateStatusFlag?"1":"0");
//        }else if("2".equals(status)) {
//            boolean updateStatusFlag = cardAuthService.updateStatusSuccess(cardAuth.getId(),2,NumberUtil.parseLong(cardAuth.getDeptId()),cardAuth.getAmount());
//            map.put("status",updateStatusFlag?"2":"0");
//        }else if("3".equals(status)) {
//            boolean updateStatusFlag = cardAuthService.updateStatusSuccess(cardAuth.getId(),3,NumberUtil.parseLong(cardAuth.getDeptId()),cardAuth.getAmount());
//            map.put("status",updateStatusFlag?"3":"0");
//        }else if("4".equals(status)) {
//            boolean updateStatusFlag = cardAuthService.updateStatusFail(cardAuth.getId(), 4);
//            map.put("status", updateStatusFlag ? "4" : "0");
//            map.put("returnMsg", updateStatusFlag ? resMap.get("returnMsg") : "0");
//        }
//        map.put("partnerId", cardAuthDto.getPartnerId());
//        //map.put("mchId", cardAuthDto.getMchId());
//        map.put("cardNo", cardAuthDto.getCardNo());
//        String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
//        String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
//        map.put("signature", signature);
//        log.info("下游返回报文-cardAuth:response={}",JSON.toJSONString(map));
//        return RestResponse.success(map);
//    }
}
