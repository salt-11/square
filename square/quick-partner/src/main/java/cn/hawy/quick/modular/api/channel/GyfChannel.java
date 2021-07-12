package cn.hawy.quick.modular.api.channel;

import cn.hawy.quick.config.properties.GyfProperties;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.util.PayUtil;
import cn.hawy.quick.modular.api.channel.gyf.GyfResponseDto;
import cn.hawy.quick.modular.api.dto.gyf.BindCardDto;
import cn.hawy.quick.modular.api.dto.gyf.RechargeDto;
import cn.hawy.quick.modular.api.dto.gyf.RegisterDto;
import cn.hawy.quick.modular.api.dto.gyf.WithdrawDto;
import cn.hawy.quick.modular.api.entity.*;
import cn.hawy.quick.modular.api.utils.gyf.CertInfo;
import cn.hawy.quick.modular.api.utils.gyf.CipherUtil;
import cn.hawy.quick.modular.api.utils.gyf.SignUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class GyfChannel {

    public static String publicKeyPath = "E:\\temp\\gyf\\gyf-online.cer";
    public static String privateKeyPath = "E:\\temp\\gyf\\101121000008.pfx";
    public static String password = "qwerty";
    public static String SIGN_ALGORITHMS = "SHA1WithRSA";

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GyfProperties gyfProperties;

    public static void main(String[] args) {
        System.out.println(DateUtil.format(DateUtil.date(),"yyyyMMdd"));
        //System.out.println(1);
        //sub_merchant_register();
        //sub_sign_pass_pay();
        //sub_trans_pass_pay();
        //GyfChannel gyf = new GyfChannel();
        //gyf.sub_merchant_query_balance();
        //query_quick_pay();
        //sub_trans_credit_df();
        //query_df();
      //  System.out.println();
    }

    public String sub_merchant_register(RegisterDto registerDto, TDeptRateChannel deptRateChannel) {

        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("bankAccountNo", registerDto.getSettleCardNo());
        dataMap.put("certificateNo", registerDto.getIdNo());
        dataMap.put("mobile", registerDto.getMobile());
        dataMap.put("subMerchantFeeRate", PayUtil.movePointRight(deptRateChannel.getCostRate(),4));
        dataMap.put("subMerchantName", registerDto.getRealName());
        dataMap.put("subMerchantWithdrawFee", deptRateChannel.getCashRate());
        String data = JSONUtil.toJsonStr(dataMap);
        log.info("工易付请求报文-sub_merchant_register业务数据:[request={}]", data);
        String encryptDataJson = "";
        String sign = "";
        try{
            encryptDataJson = CipherUtil.encryptData(getGyfCertInfo(), data,
                    CipherUtil.PKCS1Padding, CipherUtil.CHARSET);
            sign = SignUtil.signMsg(getMerchantCertInfo(), data, SIGN_ALGORITHMS,
                    CipherUtil.CHARSET);
        }catch (Exception e){
            throw new RestException(501, "通道加密异常");
        }

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("msgId", IdWorker.getIdStr());
        paramMap.put("merchantNo",gyfProperties.getMerchantNo());
        paramMap.put("data",encryptDataJson);
        paramMap.put("sign",sign);
        log.info("工易付请求报文-sub_merchant_register加密数据:[request={}]", JSONUtil.toJsonStr(paramMap));
        String result= HttpUtil.post(gyfProperties.getMchUrl()+"/api/sub_merchant_register", paramMap);
        log.info("工易付返回报文-sub_merchant_register原始数据:[response={}]", result);
        try{
            result = callback(result);
        }catch (Exception e){
            throw new RestException(501, "通道解密异常");
        }
        log.info("工易付返回报文-sub_merchant_register解密数据:[response={}]", result);
        GyfResponseDto gyfRes = JSONUtil.toBean(result, GyfResponseDto.class);
        if(gyfRes.getCode().equals("0000")){
            JSONObject dataRes = (JSONObject)gyfRes.getData();
            return dataRes.getStr("subMerchantNo");
        }else{
            throw new RestException(501, gyfRes.getMsg());
        }


    }

    public String sub_merchant_query_balance(TMchInfoChannel mchInfoChannel) {
        HashMap<String, Object> dataMap = new HashMap<>();
        String data = JSONUtil.toJsonStr(dataMap);
        log.info("工易付请求报文-sub_merchant_query_balance业务数据:[request={}]", data);
        String encryptDataJson = "";
        String sign = "";
        try{
            encryptDataJson = CipherUtil.encryptData(getGyfCertInfo(), data,
                    CipherUtil.PKCS1Padding, CipherUtil.CHARSET);
            sign = SignUtil.signMsg(getMerchantCertInfo(), data, SIGN_ALGORITHMS,
                    CipherUtil.CHARSET);
        }catch (Exception e){
            throw new RestException(501, "通道加密异常");
        }
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("msgId", IdWorker.getIdStr());
        paramMap.put("merchantNo",gyfProperties.getMerchantNo());
        paramMap.put("subMerchantNo",mchInfoChannel.getOutMchId());
        paramMap.put("data",encryptDataJson);
        paramMap.put("sign",sign);
        log.info("工易付请求报文-sub_merchant_query_balance加密数据:[request={}]", JSONUtil.toJsonStr(paramMap));
        String result= HttpUtil.post(gyfProperties.getMchUrl()+"/api/sub_merchant_query_balance", paramMap);
        log.info("工易付返回报文-sub_merchant_query_balance原始数据:[response={}]", result);
        try{
            result = callback(result);
        }catch (Exception e){
            throw new RestException(501, "通道解密异常");
        }
        log.info("工易付返回报文-sub_merchant_query_balance解密数据:[response={}]", result);
        GyfResponseDto gyfRes = JSONUtil.toBean(result, GyfResponseDto.class);
        if(gyfRes.getCode().equals("0000")){
            JSONObject dataRes = (JSONObject)gyfRes.getData();
            return dataRes.getStr("availableBalance");
        }else{
            throw new RestException(501, gyfRes.getMsg());
        }
    }

    public void sub_sign_pass_pay(BindCardDto bindCardDto, TMchInfo mchInfo, TMchInfoChannel mchInfoChannel){
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("bankAccountExpiry", bindCardDto.getValidMonth()+bindCardDto.getValidYear());
        dataMap.put("bankAccountName", mchInfo.getCustomerName());
        dataMap.put("bankAccountNo", bindCardDto.getCardNo());
        dataMap.put("bgUrl", "http://qpay.shineroon.com:6001/quick/api/gyf/mch/test");
        dataMap.put("certificateNo", mchInfo.getCustomerIdentNo());
        dataMap.put("cvv", bindCardDto.getCvv());
        dataMap.put("mobile", bindCardDto.getMobile());
        dataMap.put("orderId", IdWorker.getIdStr());
        dataMap.put("pgUrl", "http://qpay.shineroon.com:6001/quick/api/gyf/mch/test");
        dataMap.put("platUserId", bindCardDto.getMchId());
        dataMap.put("terminalIp", bindCardDto.getTerminalIp());
        String data = JSONUtil.toJsonStr(dataMap);
        log.info("工易付请求报文-sub_sign_pass_pay业务数据:[request={}]", data);
        String encryptDataJson = "";
        String sign = "";
        try{
            encryptDataJson = CipherUtil.encryptData(getGyfCertInfo(), data,
                    CipherUtil.PKCS1Padding, CipherUtil.CHARSET);
            sign = SignUtil.signMsg(getMerchantCertInfo(), data, SIGN_ALGORITHMS,
                    CipherUtil.CHARSET);
        }catch (Exception e){
            throw new RestException(501, "通道加密异常");
        }
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("msgId", IdWorker.getIdStr());
        paramMap.put("merchantNo",gyfProperties.getMerchantNo());
        paramMap.put("subMerchantNo",mchInfoChannel.getOutMchId());
        paramMap.put("data",encryptDataJson);
        paramMap.put("sign",sign);
        log.info("工易付请求报文-sub_sign_pass_pay加密数据:[request={}]", JSONUtil.toJsonStr(paramMap));
        String result= HttpUtil.post(gyfProperties.getMchUrl()+"/api/sub_sign_pass_pay_h5", paramMap);
        log.info("工易付返回报文-sub_sign_pass_pay原始数据:[response={}]", result);
        try{
            result = callback(result);
        }catch (Exception e){
            throw new RestException(501, "通道解密异常");
        }
        log.info("工易付返回报文-sub_sign_pass_pay解密数据:[response={}]", result);
        GyfResponseDto gyfRes = JSONUtil.toBean(result, GyfResponseDto.class);

        if(gyfRes.getCode().equals("0000")){

        }else{
            throw new RestException(501, gyfRes.getMsg());
        }
    }

    public Map<String,String> sub_trans_pass_pay(TPayOrder payOrder, RechargeDto rechargeDto,TMchInfo mchInfo,TMchInfoChannel mchInfoChannel){
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("bankAccountName", mchInfo.getCustomerName());
        dataMap.put("bankAccountNo", rechargeDto.getCardNo());
        dataMap.put("cityCode", rechargeDto.getCityCode());
        dataMap.put("mcc", rechargeDto.getMcc());
        dataMap.put("notifyBgUrl", gyfProperties.getOrderNotify());
        dataMap.put("orderAmount", rechargeDto.getOrderAmount());
        dataMap.put("orderDate", DateUtil.format(DateUtil.date(),"yyyyMMdd"));
        dataMap.put("orderDeviceInfo", mchInfo.getDeviceInfo());
        dataMap.put("orderId", payOrder.getOrderId().toString());
        dataMap.put("orderNote", payOrder.getOrderId().toString());
        dataMap.put("subMerchantFee", payOrder.getMchFee().toString());
        dataMap.put("terminalIp", rechargeDto.getTerminalIp());
        String data = JSONUtil.toJsonStr(dataMap);
        log.info("工易付请求报文-sub_trans_pass_pay业务数据:[request={}]", data);
        String encryptDataJson = "";
        String sign = "";
        try{
            encryptDataJson = CipherUtil.encryptData(getGyfCertInfo(), data,
                    CipherUtil.PKCS1Padding, CipherUtil.CHARSET);
            sign = SignUtil.signMsg(getMerchantCertInfo(), data, SIGN_ALGORITHMS,
                    CipherUtil.CHARSET);
        }catch (Exception e){
            throw new RestException(501, "通道加密异常");
        }
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("msgId", IdWorker.getIdStr());
        paramMap.put("merchantNo",gyfProperties.getMerchantNo());
        paramMap.put("subMerchantNo",mchInfoChannel.getOutMchId());
        paramMap.put("data",encryptDataJson);
        paramMap.put("sign",sign);
        log.info("工易付请求报文-sub_trans_pass_pay加密数据:[request={}]", JSONUtil.toJsonStr(paramMap));
        String result= HttpUtil.post(gyfProperties.getTransUrl()+"/api/sub_trans_pass_pay", paramMap);
        log.info("工易付返回报文-sub_trans_pass_pay原始数据:[response={}]", result);
        try{
            result = callback(result);
        }catch (Exception e){
            throw new RestException(501, "通道解密异常");
        }
        log.info("工易付返回报文-sub_trans_pass_pay解密数据:[response={}]", result);
        GyfResponseDto gyfRes = JSONUtil.toBean(result, GyfResponseDto.class);
        Map<String,String> resMap = new HashMap<String,String>();
        if(gyfRes.getCode().equals("0000")){
            JSONObject dataRes = (JSONObject)gyfRes.getData();
            if("0".equals(dataRes.getStr("transState"))) {
                resMap.put("orderStatus", "2");
                resMap.put("returnMsg", "交易成功");
            }else if("1".equals(dataRes.getStr("transState"))) {
                resMap.put("orderStatus", "1");
            }else if("3".equals(dataRes.getStr("transState"))) {
                resMap.put("orderStatus", "3");
                resMap.put("returnMsg", dataRes.getStr("resultNote"));
            }else {
                resMap.put("orderStatus", "1");
            }
            return resMap;
        }else{
            throw new RestException(501, gyfRes.getMsg());
        }
    }

    public Map<String,String> query_quick_pay(TPayOrder payOrder){
        HashMap<String, Object> dataMap = new HashMap<>();
        String orderDate =  DateUtil.format(Date.from(payOrder.getOrderTime().atZone(ZoneId.systemDefault()).toInstant()),"yyyyMMdd");
        dataMap.put("orderDate", orderDate);
        dataMap.put("orderId", String.valueOf(payOrder.getOrderId()));
        String data = JSONUtil.toJsonStr(dataMap);
        log.info("工易付请求报文-query_quick_pay业务数据:[request={}]", data);
        String encryptDataJson = "";
        String sign = "";
        try{
            encryptDataJson = CipherUtil.encryptData(getGyfCertInfo(), data,
                    CipherUtil.PKCS1Padding, CipherUtil.CHARSET);
            sign = SignUtil.signMsg(getMerchantCertInfo(), data, SIGN_ALGORITHMS,
                    CipherUtil.CHARSET);
        }catch (Exception e){
            throw new RestException(501, "通道加密异常");
        }
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("msgId", IdWorker.getIdStr());
        paramMap.put("merchantNo",gyfProperties.getMerchantNo());
        paramMap.put("subMerchantNo",payOrder.getOutMchId());
        paramMap.put("data",encryptDataJson);
        paramMap.put("sign",sign);
        log.info("工易付请求报文-query_quick_pay加密数据:[request={}]", JSONUtil.toJsonStr(paramMap));
        String result= HttpUtil.post(gyfProperties.getQueryUrl()+"/api/query_quick_pay", paramMap);
        log.info("工易付返回报文-query_quick_pay原始数据:[response={}]", result);
        try{
            result = callback(result);
        }catch (Exception e){
            throw new RestException(501, "通道解密异常");
        }
        log.info("工易付返回报文-query_quick_pay解密数据:[response={}]", result);
        GyfResponseDto gyfRes = JSONUtil.toBean(result, GyfResponseDto.class);
        Map<String,String> resMap = new HashMap<String,String>();
        if(gyfRes.getCode().equals("0000")){
            JSONObject dataRes = (JSONObject)gyfRes.getData();
            if("0".equals(dataRes.getStr("transState"))) {
                resMap.put("orderStatus", "2");
                resMap.put("returnMsg", "交易成功");
            }else if("1".equals(dataRes.getStr("transState"))) {
                resMap.put("orderStatus", "1");
            }else if("3".equals(dataRes.getStr("transState"))) {
                resMap.put("orderStatus", "3");
                resMap.put("returnMsg", dataRes.getStr("resultNote"));
            }else {
                resMap.put("orderStatus", "1");
            }
            return resMap;
        }else{
            throw new RestException(501, gyfRes.getMsg());
        }
    }

    public Map<String,String> sub_trans_credit_df(WithdrawDto withdrawDto,TMchCashFlow mchCashFlow,TMchInfo mchInfo){
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("bankAccountName", mchInfo.getCustomerName());
        dataMap.put("bankAccountNo", withdrawDto.getCardNo());
        dataMap.put("certificateNo", mchInfo.getCustomerIdentNo());
        dataMap.put("mobile", withdrawDto.getMobile());
        dataMap.put("notifyBgUrl", gyfProperties.getDfNotify());
        dataMap.put("orderAmount", mchCashFlow.getOutAmount());
        dataMap.put("orderDate", DateUtil.format(DateUtil.date(),"yyyyMMdd"));
        dataMap.put("orderId", mchCashFlow.getCashId().toString());
        dataMap.put("orderNote", mchCashFlow.getCashId().toString());
        dataMap.put("subMerchantFee",mchCashFlow.getCashFee());
        String data = JSONUtil.toJsonStr(dataMap);
        log.info("工易付请求报文-sub_trans_credit_df业务数据:[request={}]", data);
        String encryptDataJson = "";
        String sign = "";
        try{
            encryptDataJson = CipherUtil.encryptData(getGyfCertInfo(), data,
                    CipherUtil.PKCS1Padding, CipherUtil.CHARSET);
            sign = SignUtil.signMsg(getMerchantCertInfo(), data, SIGN_ALGORITHMS,
                    CipherUtil.CHARSET);
        }catch (Exception e){
            throw new RestException(501, "通道加密异常");
        }
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("msgId", IdWorker.getIdStr());
        paramMap.put("merchantNo",gyfProperties.getMerchantNo());
        paramMap.put("subMerchantNo",mchCashFlow.getOutMchId());
        paramMap.put("data",encryptDataJson);
        paramMap.put("sign",sign);
        log.info("工易付请求报文-sub_trans_credit_df加密数据:[request={}]", JSONUtil.toJsonStr(paramMap));
        String result= HttpUtil.post(gyfProperties.getTransUrl()+"/api/sub_trans_credit_df", paramMap);
        log.info("工易付返回报文-sub_trans_credit_df原始数据:[response={}]", result);
        try{
            result = callback(result);
        }catch (Exception e){
            throw new RestException(501, "通道解密异常");
        }
        log.info("工易付返回报文-sub_trans_credit_df解密数据:[response={}]", result);
        GyfResponseDto gyfRes = JSONUtil.toBean(result, GyfResponseDto.class);
        Map<String,String> resMap = new HashMap<String,String>();
        if(gyfRes.getCode().equals("0000")){
            JSONObject dataRes = (JSONObject)gyfRes.getData();
            if("0".equals(dataRes.getStr("transState"))) {
                resMap.put("orderStatus", "2");
                resMap.put("returnMsg", "交易成功");
            }else if("1".equals(dataRes.getStr("transState"))) {
                resMap.put("orderStatus", "1");
            }else if("3".equals(dataRes.getStr("transState"))) {
                resMap.put("orderStatus", "3");
                resMap.put("returnMsg", dataRes.getStr("resultNote"));
            }else {
                resMap.put("orderStatus", "1");
            }
            return resMap;
        }else{
            throw new RestException(501, gyfRes.getMsg());
        }
    }

    public Map<String,String> query_df(TMchCashFlow mchCashFlow){
        HashMap<String, Object> dataMap = new HashMap<>();
        String orderDate =  DateUtil.format(Date.from(mchCashFlow.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()),"yyyyMMdd");
        dataMap.put("orderDate", orderDate);
        dataMap.put("orderId", mchCashFlow.getCashId().toString());
        String data = JSONUtil.toJsonStr(dataMap);
        log.info("工易付请求报文-query_df业务数据:[request={}]", data);
        String encryptDataJson = "";
        String sign = "";
        try{
            encryptDataJson = CipherUtil.encryptData(getGyfCertInfo(), data,
                    CipherUtil.PKCS1Padding, CipherUtil.CHARSET);
            sign = SignUtil.signMsg(getMerchantCertInfo(), data, SIGN_ALGORITHMS,
                    CipherUtil.CHARSET);
        }catch (Exception e){
            throw new RestException(501, "通道加密异常");
        }
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("msgId", IdWorker.getIdStr());
        paramMap.put("merchantNo",gyfProperties.getMerchantNo());
        paramMap.put("subMerchantNo",mchCashFlow.getOutMchId());
        paramMap.put("data",encryptDataJson);
        paramMap.put("sign",sign);
        System.out.println(JSONUtil.toJsonStr(paramMap));
        String result= HttpUtil.post(gyfProperties.getQueryUrl()+"/api/query_df", paramMap);
        log.info("工易付返回报文-query_df业务数据原始数据:[response={}]", result);
        try{
            result = callback(result);
        }catch (Exception e){
            throw new RestException(501, "通道解密异常");
        }
        log.info("工易付返回报文-query_df业务数据解密数据:[response={}]", result);
        GyfResponseDto gyfRes = JSONUtil.toBean(result, GyfResponseDto.class);
        Map<String,String> resMap = new HashMap<String,String>();
        if(gyfRes.getCode().equals("0000")){
            JSONObject dataRes = (JSONObject)gyfRes.getData();
            if("0".equals(dataRes.getStr("transState"))) {
                resMap.put("orderStatus", "2");
                resMap.put("returnMsg", "交易成功");
            }else if("1".equals(dataRes.getStr("transState"))) {
                resMap.put("orderStatus", "1");
            }else if("3".equals(dataRes.getStr("transState"))) {
                resMap.put("orderStatus", "3");
                resMap.put("returnMsg", dataRes.getStr("resultNote"));
            }else {
                resMap.put("orderStatus", "1");
            }
            return resMap;
        }else{
            throw new RestException(501, gyfRes.getMsg());
        }
    }

    /**
     * 获取GYF公钥钥
     *
     * @throws Exception
     */
    public CertInfo getGyfCertInfo(){
        CertInfo gyfCertInfo = new CertInfo();
        try {
            gyfCertInfo.readPublicKeyFromX509Certificate(gyfProperties.getPublicKeyPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gyfCertInfo;
    }

    /**
     * 获取商户私钥
     *
     * @throws Exception
     */
    public CertInfo getMerchantCertInfo(){
        CertInfo merchantCertInfo = new CertInfo();
        try {
            merchantCertInfo.readKeyFromPKCS12(gyfProperties.getPrivateKeyPath(), gyfProperties.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return merchantCertInfo;
    }

    /**
     * 处理GYF返回结果
     *
     * @param resultMsg
     */
    public String callback(String resultMsg) throws Exception {
        //解密
        JSONObject jsonObject = JSONUtil.parseObj(resultMsg);

        //正常返回有data域
        JSONObject dataJsonObject = jsonObject.getJSONObject("data");
        if (dataJsonObject != null) {
            /******************************** 商户私钥解密 */
            String encryptedKey = dataJsonObject.getStr("key");
            String encryptedData = dataJsonObject.getStr("content");
            String data = CipherUtil.decryptData(getMerchantCertInfo(), encryptedKey, encryptedData,
                    CipherUtil.PKCS1Padding, CipherUtil.CHARSET);
            /******************************** GYF公钥验证签名 */
            String sign = jsonObject.getStr("sign");
            boolean verifySign = SignUtil.verifyMsg(getGyfCertInfo(), data, sign, null, null);
            if(verifySign){
                return data;
            }else{
               return null;
            }
        } else {
            return null;
        }
    }


}
