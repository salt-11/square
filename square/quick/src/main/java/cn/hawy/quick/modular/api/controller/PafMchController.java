package cn.hawy.quick.modular.api.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import cn.hawy.quick.modular.api.dto.paf.*;
import cn.hawy.quick.modular.api.mq.MqBindCardNotify;
import cn.hawy.quick.modular.api.mq.MqMchCashNotify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import cn.hawy.quick.config.properties.PafProperties;
import cn.hawy.quick.config.properties.RestProperties;
import cn.hawy.quick.core.common.RestResponse;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.util.IdGenerator;
import cn.hawy.quick.core.util.MapUtils;
import cn.hawy.quick.core.util.RSA;
import cn.hawy.quick.modular.api.channel.PafChannel;
import cn.hawy.quick.modular.api.entity.TDeptRateChannel;
import cn.hawy.quick.modular.api.entity.TMchCard;
import cn.hawy.quick.modular.api.entity.TMchCardChannel;
import cn.hawy.quick.modular.api.entity.TMchCashFlow;
import cn.hawy.quick.modular.api.entity.TMchInfo;
import cn.hawy.quick.modular.api.entity.TMchInfoChannel;
import cn.hawy.quick.modular.api.mq.MqPayNotify;
import cn.hawy.quick.modular.api.service.TBankCardBinService;
import cn.hawy.quick.modular.api.service.TDeptRateChannelService;
import cn.hawy.quick.modular.api.service.TFestivalHolidayService;
import cn.hawy.quick.modular.api.service.TMchCardChannelService;
import cn.hawy.quick.modular.api.service.TMchCardService;
import cn.hawy.quick.modular.api.service.TMchCashFlowService;
import cn.hawy.quick.modular.api.service.TMchImgService;
import cn.hawy.quick.modular.api.service.TMchInfoChannelService;
import cn.hawy.quick.modular.api.service.TMchInfoService;
import cn.hawy.quick.modular.api.validate.paf.PafMchValidate;
import cn.hawy.quick.modular.system.entity.Dept;
import cn.hawy.quick.modular.system.service.DeptService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;

@RestController
@RequestMapping("/api/paf/mch")
public class PafMchController {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	DeptService deptService;
	@Autowired
	PafChannel pafChannel;
	@Autowired
	TMchInfoService mchInfoService;
	@Autowired
	RestProperties restProperties;
	@Autowired
	TMchImgService mchImgService;
	@Autowired
	TMchInfoChannelService mchInfoChannelService;
	@Autowired
	TMchCardService mchCardService;
	@Autowired
	TMchCashFlowService mchCashFlowService;
	@Autowired
	TMchCardChannelService mchCardChannelService;
	@Autowired
	TBankCardBinService bankCardBinService;
	@Autowired
	TDeptRateChannelService deptRateChannelService;
	@Autowired
	PafProperties pafProperties;
	@Autowired
    MqPayNotify mqPayNotify;
	@Autowired
	MqMchCashNotify mqMchCashNotify;
	@Autowired
	MqBindCardNotify mqBindCardNotify;
	@Autowired
	TFestivalHolidayService festivalHolidayService;

	/**
	 * 注册
	 * @param registerDto
	 * @return
	 */
	@RequestMapping(value = "/register",method = RequestMethod.POST)
	@ResponseBody
	public Object register(RegisterDto registerDto) {
		log.info("下游请求报文-register:request={}",JSON.toJSONString(registerDto));
		PafMchValidate.register(registerDto);
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
		mchInfo.setCreateTime(LocalDateTime.now());
		//通道
		TMchInfoChannel mchInfoChannel = new TMchInfoChannel();
		mchInfoChannel.setMchId(mchId);
		mchInfoChannel.setChannel("paf");
		mchInfoChannel.setOutMchId("00000000");
		mchInfoChannel.setCreateTime(LocalDateTime.now());
		mchInfoService.addMerchant(mchInfo, mchInfoChannel);
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", registerDto.getPartnerId());
		map.put("mchId", mchId);
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-register:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}


	/**
	 * 绑卡
	 * @param bindCardDto
	 * @return
	 */
	@RequestMapping(value = "/bindCard",method = RequestMethod.POST)
	@ResponseBody
	public Object bindCard(BindCardDto bindCardDto) {
		log.info("下游请求报文-binkCard:request={}",JSON.toJSONString(bindCardDto));
		PafMchValidate.binkCard(bindCardDto);
		//渠道商信息
		Dept dept = deptService.getById(bindCardDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
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
		String orderNo = IdGenerator.getId();
		TMchCard mchCard = mchCardService.findBybankCardNo(bindCardDto.getMchId(), bindCardDto.getCardNo());
		if(mchCard != null) {
			TMchCardChannel mchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),"paf");
			if(mchCardChannel == null){
				throw new RestException(401, "商户卡渠道信息错误!");
			}else {
				if(mchCardChannel.getStatus() == 1) {
					Map<String,String> respMap = pafChannel.fastpayOpenToken(mchInfo.getCustomerIdentNo(),bindCardDto.getCardNo(),mchInfo.getCustomerName(),bindCardDto.getMobile(),orderNo,bindCardDto.getFrontUrl());
					mchCard.setMobile(bindCardDto.getMobile());
					mchCard.setCvn(bindCardDto.getCvv());
					mchCard.setExpired(bindCardDto.getValidYear()+bindCardDto.getValidMonth());
					mchCard.setMobile(bindCardDto.getMobile());
					if(respMap.get("sign_code").equals("2")) {
						mchCardChannel.setStatus(2);
					}else {
						mchCardChannel.setStatus(1);
					}
					mchCardChannel.setSmsNo(orderNo);
					mchCardChannel.setNotifyUrl(bindCardDto.getNotifyUrl());
					mchCardChannel.setNotifyCount(0);
					mchCardService.bindCard(mchCard, mchCardChannel);

					Map<String,Object> map = new HashMap<String,Object>();
					if(respMap.get("sign_code").equals("2")) {
						map.put("status", "2");
						map.put("form",  "");
					}else {
						map.put("status", "1");
						map.put("form", respMap.get("form"));
					}
					map.put("signCode", respMap.get("sign_code"));

					map.put("partnerId", bindCardDto.getPartnerId());
					map.put("mchId", bindCardDto.getMchId());
					map.put("cardNo", bindCardDto.getCardNo());
					String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
					String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
					map.put("signature", signature);
					log.info("下游返回报文-bindCard:response={}",JSON.toJSONString(map));
					return RestResponse.success(map);
				}else if(mchCardChannel.getStatus() == 2){
					//组装返回报文
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("partnerId", bindCardDto.getPartnerId());
					map.put("mchId", bindCardDto.getMchId());
					map.put("cardNo", bindCardDto.getCardNo());
					map.put("status", "2");//绑卡成功
					map.put("signCode", "2");
					map.put("form", "");
					String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
					String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
					map.put("signature", signature);
					log.info("下游返回报文-bindCard:response={}",JSON.toJSONString(map));
					return RestResponse.success(map);
				}else {
					throw new RestException(401, "状态异常，请联系管理人员!");
				}
			}
		}else {
			Map<String,String> respMap = pafChannel.fastpayOpenToken(mchInfo.getCustomerIdentNo(),bindCardDto.getCardNo(),mchInfo.getCustomerName(),bindCardDto.getMobile(),orderNo,bindCardDto.getFrontUrl());
			mchCard = new TMchCard();
			mchCard.setMchId(bindCardDto.getMchId());
			mchCard.setBankCardNo(bindCardDto.getCardNo());
			mchCard.setBankCardType("credit");
			mchCard.setExpired(bindCardDto.getValidYear()+bindCardDto.getValidMonth());
			mchCard.setCvn(bindCardDto.getCvv());
			mchCard.setMobile(bindCardDto.getMobile());
			mchCard.setCreateTime(LocalDateTime.now());

			TMchCardChannel	mchCardChannel = new TMchCardChannel();
			mchCardChannel.setOutMchId("00000000");
			if(respMap.get("sign_code").equals("2")) {
				mchCardChannel.setStatus(2);
			}else {
				mchCardChannel.setStatus(1);
			}
			mchCardChannel.setSmsNo(orderNo);
			mchCardChannel.setNotifyUrl(bindCardDto.getNotifyUrl());
			mchCardChannel.setNotifyCount(0);
			mchCardChannel.setChannel("paf");
			mchCardChannel.setCreateTime(LocalDateTime.now());

			mchCardService.bindCard(mchCard, mchCardChannel);
			//组装返回报文
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("partnerId", bindCardDto.getPartnerId());
			map.put("mchId", bindCardDto.getMchId());
			if(respMap.get("sign_code").equals("2")) {
				map.put("status", "2");
				map.put("form",  "");
			}else {
				map.put("status", "1");
				map.put("form", respMap.get("form"));
			}
			map.put("signCode", respMap.get("sign_code"));
			map.put("cardNo", bindCardDto.getCardNo());
			String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
			String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
			map.put("signature", signature);
			log.info("下游返回报文-bindCard:response={}",JSON.toJSONString(map));
			return RestResponse.success(map);
		}
	}


	@RequestMapping(value = "/bindCardNotify",method = RequestMethod.POST)
	@ResponseBody
	public Object bindCardNotify(BindCardNotifyDto bindCardNotifyDto) {
		log.info("PAF绑卡通知请求-bindCardNotify={}",JSON.toJSONString(bindCardNotifyDto));
		String md5Str = "bizOrderNumber="+bindCardNotifyDto.getBizOrderNumber()
				+"&completedTime="+bindCardNotifyDto.getCompletedTime()
				+"&mid="+bindCardNotifyDto.getMid()
				+"&srcAmt="+bindCardNotifyDto.getSrcAmt()
				+"&key="+pafProperties.getPublicKey();
		String checkSign = SecureUtil.md5(md5Str);
//		if(!checkSign.equals(bindCardNotifyDto.getSign())) {
//			log.info("PAF绑卡通知返回-bindCardNotify={}","验签失败返回fail");
//			return "fail";
//		}
		TMchCardChannel mchCardChannel = mchCardChannelService.findBySmsNoAndChannel(bindCardNotifyDto.getBizOrderNumber(),"paf");
		if(mchCardChannel == null) {
			//什么都不做
			log.info("PAF绑卡通知返回-bindCardNotify={}","绑卡单号不存在返回fail");
			return "fail";
		}
		TMchCard mchCard = mchCardService.getById(mchCardChannel.getCardId());
		if(mchCard == null) {
			//什么都不做
			log.info("PAF绑卡通知返回-bindCardNotify={}","商户绑卡信息不存在返回fail");
			return "fail";
		}
		TMchInfo mchInfo = mchInfoService.getById(mchCard.getMchId());
		if(mchInfo == null) {
			//什么都不做
			log.info("PAF绑卡通知返回-bindCardNotify={}","商户信息不存在返回fail");
			return "fail";
		}
		//渠道商信息
		Dept dept = deptService.getById(mchInfo.getDeptId());
		if(dept == null) {
			log.info("PAF绑卡通知返回-bindCardNotify={}","渠道信息不存在返回fail");
			return "fail";
		}
		JSONObject msgObj = new JSONObject();
		msgObj.put("notifyUrl", mchCardChannel.getNotifyUrl());
		msgObj.put("notifyCount", mchCardChannel.getNotifyCount());
		msgObj.put("id", mchCardChannel.getId());
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", mchInfo.getDeptId());
		map.put("mchId", mchInfo.getMchId());
		map.put("cardNo", mchCard.getBankCardNo());
		if(mchCardChannel.getStatus() == 1) {
			mchCardChannel.setStatus(2);
			boolean flag = mchCardChannelService.updateById(mchCardChannel);
		}
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-bindCardNotify={}",JSON.toJSONString(map));
		msgObj.put("notifyMsg", RestResponse.success(map));
		mqBindCardNotify.send(JSON.toJSONString(msgObj));
		log.info("PAF绑卡通知返回-bindCardNotify={}","success");
		return "success";
	}


	@RequestMapping(value = "/balanceQuery",method = RequestMethod.POST)
	@ResponseBody
	public Object balanceQuery(BalanceQueryDto balanceQueryDto) {
		log.info("下游请求报文-balanceQuery:request={}",JSON.toJSONString(balanceQueryDto));
		PafMchValidate.balanceQuery(balanceQueryDto);
		//渠道商信息
		Dept dept = deptService.getById(balanceQueryDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
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
		TMchCard mchCard = mchCardService.findBybankCardNo(balanceQueryDto.getMchId(), balanceQueryDto.getCardNo());
		if(mchCard == null) {
			throw new RestException(401, "商户卡信息错误");
		}
		String availableBalance = pafChannel.fastpayTransferBalanceQuery(balanceQueryDto.getCardNo());
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", balanceQueryDto.getPartnerId());
		map.put("mchId", balanceQueryDto.getMchId());
		map.put("cardNo", balanceQueryDto.getCardNo());
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
		PafMchValidate.withdraw(withdrawDto);
		//渠道商信息
		Dept dept = deptService.getById(withdrawDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		TDeptRateChannel deptRateChannel = null;
		if(dept.getChannelType() == 1){
			deptRateChannel = deptRateChannelService.findByDeptIdAndChannel(withdrawDto.getPartnerId(), "paf");
		}else {
			deptRateChannel = deptRateChannelService.findByDeptIdAndBankNameAndChannel(withdrawDto.getPartnerId(), "TTS", "paf");
		}
		if(deptRateChannel == null) {
			throw new RestException(401, "未找到通道信息");
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
		boolean openFlag = festivalHolidayService.isOpenFlag();
		if(!openFlag) {
			throw new RestException(401, "通道关闭中!");
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
		TMchCashFlow mchCashFlow = mchCashFlowService.findByMchIdAndOutTradeNo(withdrawDto.getMchId(), withdrawDto.getOutTradeNo());
		if(mchCashFlow != null) {
			throw new RestException(401, "提现订单号已存在!");
		}
		Long cashAmount = NumberUtil.parseLong(withdrawDto.getCashAmount());
		Long deptAmount = cashFee - NumberUtil.parseLong(deptRateChannel.getCashRate());
		Long costAmount = NumberUtil.parseLong(deptRateChannel.getCashRate()) - NumberUtil.parseLong(pafProperties.getCostFee());
		Long outAmount = cashAmount - cashFee;
		Long cashId = IdGenerator.getIdLong();
		mchCashFlow = new TMchCashFlow();
		mchCashFlow.setCashId(cashId);
		mchCashFlow.setMchId(withdrawDto.getMchId());
		mchCashFlow.setMchName(mchInfo.getMchShortName());
		mchCashFlow.setDeptId(withdrawDto.getPartnerId());
		mchCashFlow.setBankCardNo(withdrawDto.getCardNo());
		mchCashFlow.setOutTradeNo(withdrawDto.getOutTradeNo());
		mchCashFlow.setOutMchId("00000000");
		mchCashFlow.setCashAmount(cashAmount);
		mchCashFlow.setCashFee(cashFee);
		mchCashFlow.setOutAmount(outAmount);
		mchCashFlow.setCashStatus(1);
		mchCashFlow.setReturnMsg("");
		mchCashFlow.setCashRate(deptRateChannel.getCashRate());
		mchCashFlow.setDeptAmount(deptAmount);
		mchCashFlow.setCostFee(pafProperties.getCostFee());
		mchCashFlow.setCostAmount(costAmount);
		mchCashFlow.setNotifyUrl(withdrawDto.getNotifyUrl());
		mchCashFlow.setNotifyCount(0);
		mchCashFlow.setCreateTime(LocalDateTime.now());
		mchCashFlowService.save(mchCashFlow);
		Map<String,String> resMap = pafChannel.fastpayTransferCreate(String.valueOf(cashId),withdrawDto.getCardNo(),withdrawDto.getCashFee(),withdrawDto.getCashAmount(),mchInfo.getCustomerIdentNo(),mchInfo.getCustomerName(),mchCard.getMobile());

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
		}else {
			throw new RestException(401, "提现订单查询失败!");
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
		PafMchValidate.withdrawQuery(withdrawQueryDto);
		//渠道商信息
		Dept dept = deptService.getById(withdrawQueryDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
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
			Map<String,String> resMap = pafChannel.fastpayTransferQuery(String.valueOf(mchCashFlow.getCashId()));
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


	@RequestMapping(value = "/withdrawNotify",method = RequestMethod.POST)
	@ResponseBody
	public Object withdrawNotify(WithdrawNotifyDto withdrawNotifyDto) {
		log.info("PAF提现通知请求-withdrawNotify={}",JSON.toJSONString(withdrawNotifyDto));
		String md5Str = "bizOrderNumber="+withdrawNotifyDto.getBizOrderNumber()
						+"&completedTime="+withdrawNotifyDto.getCompletedTime()
						+"&mid="+withdrawNotifyDto.getMid()
						+"&srcAmt="+withdrawNotifyDto.getSrcAmt()
						+"&key="+pafProperties.getPublicKey();
		String checkSign = SecureUtil.md5(md5Str);
		if(!checkSign.equals(withdrawNotifyDto.getSign())) {
			log.info("PAF提现通知返回-withdrawNotify={}","验签失败返回fail");
			return "fail";
		}
		//获取订单信息
		TMchCashFlow mchCashFlow = mchCashFlowService.getById(withdrawNotifyDto.getBizOrderNumber());
		if(mchCashFlow == null) {
			//什么都不做
			log.info("PAF提现通知返回-withdrawNotify={}","订单不存在返回fail");
			return "fail";
		}
		//渠道商信息
        Dept dept = deptService.getById(mchCashFlow.getDeptId());
        if(dept == null) {
            log.info("PAF提现通知返回-withdrawNotify={}","渠道信息不存在返回fail");
            return "fail";
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
            boolean flag = mchCashFlowService.updateCashStatusSuccess(mchCashFlow, dept, "交易成功");
            if(flag) {
                map.put("cashStatus", "2");
                map.put("returnMsg", "交易成功");
            }else {
                log.info("PAF提现通知返回-withdrawNotify={}","状态修改失败返回fail");
                return "fail";
            }
        }
        String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
        String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
        map.put("signature", signature);
        log.info("下游返回报文-withdrawNotify={}",JSON.toJSONString(map));
        msgObj.put("notifyMsg", RestResponse.success(map));
		mqMchCashNotify.send(JSON.toJSONString(msgObj));
	    log.info("PAF提现通知返回-withdrawNotify={}","success");
	    return "success";
	}
}
