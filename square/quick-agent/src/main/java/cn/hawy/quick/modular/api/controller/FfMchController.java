package cn.hawy.quick.modular.api.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import cn.hawy.quick.config.properties.FfProperties;
import cn.hawy.quick.config.properties.RestProperties;
import cn.hawy.quick.core.common.RestResponse;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.util.IdGenerator;
import cn.hawy.quick.core.util.MapUtils;
import cn.hawy.quick.core.util.RSA;
import cn.hawy.quick.modular.api.channel.FfChannel;
import cn.hawy.quick.modular.api.channel.PafChannel;
import cn.hawy.quick.modular.api.dto.ff.BalanceQueryDto;
import cn.hawy.quick.modular.api.dto.ff.BindCardConfirmDto;
import cn.hawy.quick.modular.api.dto.ff.BindCardDto;
import cn.hawy.quick.modular.api.dto.ff.RegisterDto;
import cn.hawy.quick.modular.api.dto.ff.WithdrawDto;
import cn.hawy.quick.modular.api.dto.ff.WithdrawNotifyDto;
import cn.hawy.quick.modular.api.dto.ff.WithdrawQueryDto;
import cn.hawy.quick.modular.api.entity.TBankCardBin;
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
import cn.hawy.quick.modular.api.validate.ff.FfMchValidate;
import cn.hawy.quick.modular.system.entity.Dept;
import cn.hawy.quick.modular.system.service.DeptService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;

@RestController
@RequestMapping("/api/ff/mch")
public class FfMchController {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	DeptService deptService;
	@Autowired
	PafChannel pafChannel;
	@Autowired
	FfChannel ffChannel;
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
	FfProperties ffProperties;
	@Autowired
    MqPayNotify mqPayNotify;

	/**
	 * 注册
	 * @param registerDto
	 * @return
	 */
	@RequestMapping(value = "/register",method = RequestMethod.POST)
	@ResponseBody
	public Object register(RegisterDto registerDto) {
		log.info("下游请求报文-register:request={}",JSON.toJSONString(registerDto));
		FfMchValidate.register(registerDto);
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
		mchInfoChannel.setChannel("ff");
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
		FfMchValidate.binkCard(bindCardDto);
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
		TMchCard mchCard = mchCardService.findBybankCardNo(bindCardDto.getMchId(), bindCardDto.getCardNo());
		if(mchCard != null) {
			TMchCardChannel mchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),"ff");
			if(mchCardChannel.getStatus() == 1) {
				String orderNo = IdGenerator.getId();
				Map<String,String> respMap =ffChannel.sendSmsOpenToken(bindCardDto.getCardNo(), bindCardDto.getMobile(), mchInfo.getCustomerName(), mchInfo.getCustomerIdentNo(), bindCardDto.getCvv(), bindCardDto.getValidYear()+bindCardDto.getValidMonth(), orderNo);
				mchCard.setCvn(bindCardDto.getCvv());
				mchCard.setExpired(bindCardDto.getValidYear()+bindCardDto.getValidMonth());
				mchCard.setMobile(bindCardDto.getMobile());
				mchCardService.updateById(mchCard);
				if(respMap.get("signCode").equals("2")) {
					mchCardChannel.setStatus(2);
				}
				mchCardChannel.setSmsNo(orderNo);
				mchCardChannelService.updateById(mchCardChannel);
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("partnerId", bindCardDto.getPartnerId());
				map.put("mchId", bindCardDto.getMchId());
				map.put("cardNo", bindCardDto.getCardNo());
				map.put("status", String.valueOf(mchCardChannel.getStatus()));
				map.put("signCode", respMap.get("signCode"));
				log.info("下游返回报文-bindCard:response={}",JSON.toJSONString(map));
				return RestResponse.success(map);
			}else if (mchCardChannel.getStatus() == 2) {
				//组装返回报文
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("partnerId", bindCardDto.getPartnerId());
				map.put("mchId", bindCardDto.getMchId());
				map.put("cardNo", bindCardDto.getCardNo());
				map.put("status", "2");
				map.put("signCode", "2");
				String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
				String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
				map.put("signature", signature);
				log.info("下游返回报文-bindCard:response={}",JSON.toJSONString(map));
				return RestResponse.success(map);
			}else {
				throw new RestException(401, "状态异常，请联系管理人员!");
			}
		}else {
			TBankCardBin bankCardBin = bankCardBinService.findBankNameByBankCardNo(bindCardDto.getCardNo());
			if(bankCardBin == null) {
				throw new RestException(401, "未找到卡bin信息");
			}
			TDeptRateChannel deptRateChannel = deptRateChannelService.findByDeptIdAndBankCodeAndChannel(bindCardDto.getPartnerId(), bankCardBin.getBankCode(), "ff");
			if(deptRateChannel == null) {
				throw new RestException(401, "未找到渠道费率信息");
			}
			String orderNo = IdGenerator.getId();
			Map<String,String> respMap =ffChannel.sendSmsOpenToken(bindCardDto.getCardNo(), bindCardDto.getMobile(), mchInfo.getCustomerName(), mchInfo.getCustomerIdentNo(), bindCardDto.getCvv(), bindCardDto.getValidYear()+bindCardDto.getValidMonth(), orderNo);
			mchCard = new TMchCard();
			mchCard.setMchId(bindCardDto.getMchId());
			mchCard.setBankCardNo(bindCardDto.getCardNo());
			mchCard.setBankCode(bankCardBin.getBankCode());
			mchCard.setBankName(bankCardBin.getBankName());
			mchCard.setBankCardType("credit");
			mchCard.setExpired(bindCardDto.getValidYear()+bindCardDto.getValidMonth());
			mchCard.setCvn(bindCardDto.getCvv());
			mchCard.setMobile(bindCardDto.getMobile());
			mchCard.setCreateTime(LocalDateTime.now());

			TMchCardChannel	mchCardChannel = new TMchCardChannel();
			mchCardChannel.setOutMchId("00000000");
			if(respMap.get("signCode").equals("2")) {
				mchCardChannel.setStatus(2);
			}else {
				mchCardChannel.setStatus(1);
			}
			mchCardChannel.setSmsNo(orderNo);
			mchCardChannel.setChannel("ff");
			mchCardChannel.setCreateTime(LocalDateTime.now());
			mchCardService.bindCard(mchCard, mchCardChannel);
			//组装返回报文
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("partnerId", bindCardDto.getPartnerId());
			map.put("mchId", bindCardDto.getMchId());
			map.put("cardNo", bindCardDto.getCardNo());
			map.put("status", String.valueOf(mchCardChannel.getStatus()));
			map.put("signCode", respMap.get("signCode"));
			String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
			String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
			map.put("signature", signature);
			log.info("下游返回报文-bindCard:response={}",JSON.toJSONString(map));
			return RestResponse.success(map);
		}
		
	}
	
	/**
	 * 短信绑卡确认
	 * @param bindCardConfirmDto
	 */
	@RequestMapping(value = "/bindCardConfirm",method = RequestMethod.POST)
	@ResponseBody
	public Object bindCardConfirm(BindCardConfirmDto bindCardConfirmDto) {
		log.info("下游请求报文-bindCardConfirm:request={}",JSON.toJSONString(bindCardConfirmDto));
		FfMchValidate.binkCardConfirm(bindCardConfirmDto);
		//渠道商信息
		Dept dept = deptService.getById(bindCardConfirmDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(bindCardConfirmDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, bindCardConfirmDto.getSignature(), dept.getPartnerPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		//查询商户信息
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(bindCardConfirmDto.getPartnerId(), bindCardConfirmDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TMchCard mchCard = mchCardService.findBybankCardNo(bindCardConfirmDto.getMchId(), bindCardConfirmDto.getCardNo());
		if(mchCard == null) {
			throw new RestException(401, "商户卡信息错误");
		}
		TMchCardChannel mchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),"ff");
		if(mchCardChannel == null) {
			throw new RestException(401, "商户卡信息渠道错误");
		}
		if(mchCardChannel.getStatus() == 1) {
			ffChannel.checkSmsOpenToken(bindCardConfirmDto.getCardNo(), mchCard.getMobile(), mchInfo.getCustomerName(), mchInfo.getCustomerIdentNo(), mchCard.getCvn(), mchCard.getExpired(), mchCardChannel.getSmsNo(), bindCardConfirmDto.getVerifyCode());
			mchCardChannel.setStatus(2);
			mchCardChannelService.updateById(mchCardChannel);
			//组装返回报文
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("partnerId", bindCardConfirmDto.getPartnerId());
			map.put("mchId", bindCardConfirmDto.getMchId());
			map.put("cardNo", bindCardConfirmDto.getCardNo());
			map.put("status", "2");
			String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
			String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
			map.put("signature", signature);
			log.info("下游返回报文-bindCardConfirm:response={}",JSON.toJSONString(map));
			return RestResponse.success(map);
		}else if(mchCardChannel.getStatus() == 2) {
			//组装返回报文
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("partnerId", bindCardConfirmDto.getPartnerId());
			map.put("mchId", bindCardConfirmDto.getMchId());
			map.put("cardNo", bindCardConfirmDto.getCardNo());
			map.put("status", "2");
			String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
			String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
			map.put("signature", signature);
			log.info("下游返回报文-bindCardConfirm:response={}",JSON.toJSONString(map));
			return RestResponse.success(map);
		}else {
			throw new RestException(401, "状态异常，请联系管理人员!");
		}
	}

	

	@RequestMapping(value = "/balanceQuery",method = RequestMethod.POST)
	@ResponseBody
	public Object balanceQuery(BalanceQueryDto balanceQueryDto) {
		log.info("下游请求报文-balanceQuery:request={}",JSON.toJSONString(balanceQueryDto));
		FfMchValidate.balanceQuery(balanceQueryDto);
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
		String availableBalance = ffChannel.queryUserBanlance(balanceQueryDto.getCardNo());
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
		FfMchValidate.withdraw(withdrawDto);
		//渠道商信息
		Dept dept = deptService.getById(withdrawDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		TDeptRateChannel deptRateChannel = null;
		if(dept.getChannelType() == 1){
			deptRateChannel = deptRateChannelService.findByDeptIdAndChannel(withdrawDto.getPartnerId(), "ff");
		}else {
			deptRateChannel = deptRateChannelService.findByDeptIdAndBankCodeAndChannel(withdrawDto.getPartnerId(), "TTS", "ff");
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
		//查询商户信息
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(withdrawDto.getPartnerId(), withdrawDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TMchCard mchCard = mchCardService.findBybankCardNo(withdrawDto.getMchId(), withdrawDto.getCardNo());
		if(mchCard == null) {
			throw new RestException(401, "商户卡信息错误");
		}
		TMchCardChannel mchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),2,"ff");
		if(mchCardChannel == null) {
			throw new RestException(401, "商户卡信息渠道错误");
		}
		TMchCashFlow mchCashFlow = mchCashFlowService.findByMchIdAndOutTradeNo(withdrawDto.getMchId(), withdrawDto.getOutTradeNo());
		if(mchCashFlow != null) {
			throw new RestException(401, "提现订单号已存在!");
		}
		Long cashAmount = NumberUtil.parseLong(withdrawDto.getCashAmount());
		Long deptAmount = cashFee - NumberUtil.parseLong(deptRateChannel.getCashRate());
		Long costAmount = NumberUtil.parseLong(deptRateChannel.getCashRate()) - NumberUtil.parseLong(ffProperties.getCostFee());
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
		mchCashFlow.setCostFee(ffProperties.getCostFee());
		mchCashFlow.setCostAmount(costAmount);
		mchCashFlow.setNotifyUrl(withdrawDto.getNotifyUrl());
		mchCashFlow.setNotifyCount(0);
		mchCashFlow.setCreateTime(LocalDateTime.now());
		mchCashFlowService.save(mchCashFlow);
		Map<String,String> resMap = ffChannel.checkOutOrder(String.valueOf(cashId), withdrawDto.getCashAmount(), withdrawDto.getCardNo(), mchCard.getMobile(), withdrawDto.getCashFee(), mchInfo.getCustomerName(), mchInfo.getCustomerIdentNo());

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
		log.info("下游返回报文-withdraw:map={}",JSON.toJSONString(map));
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
		FfMchValidate.withdrawQuery(withdrawQueryDto);
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
			Map<String,String> resMap = ffChannel.queryCheckOutOrderStatus(String.valueOf(mchCashFlow.getCashId()));
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
		log.info("FF提现通知请求-withdrawNotify={}",JSON.toJSONString(withdrawNotifyDto));
		String md5Str = "data="+withdrawNotifyDto.getData()
						+"&merchantId="+withdrawNotifyDto.getMerchantId()
						+"&key="+ffProperties.getKey();
		String checkSign = SecureUtil.md5(md5Str);
		if(!checkSign.equals(withdrawNotifyDto.getSign())) {
			log.info("FF提现通知返回-withdrawNotify={}","验签失败返回fail");
			return "fail";
		}
		String dataDecrypt = ffChannel.rsaDecrypt(withdrawNotifyDto.getData());
		HashMap<String, String> dataMap = FfChannel.form2Map(dataDecrypt);
		//获取订单信息
		TMchCashFlow mchCashFlow = mchCashFlowService.getById(dataMap.get("merOrderNumber"));
		if(mchCashFlow == null) {
			//什么都不做
			log.info("FF提现通知返回-withdrawNotify={}","订单不存在返回fail");
			return "fail";
		}
		//渠道商信息
        Dept dept = deptService.getById(mchCashFlow.getDeptId());
        if(dept == null) {
            log.info("FF提现通知返回-withdrawNotify={}","渠道信息不存在返回fail");
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
                log.info("FF提现通知返回-withdrawNotify={}","状态修改失败返回fail");
                return "fail";
            }
        }
        String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
        String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
        map.put("signature", signature);
        log.info("下游返回报文-withdrawNotify={}",JSON.toJSONString(map));
        msgObj.put("notifyMsg", RestResponse.success(map));
        mqPayNotify.send(JSON.toJSONString(msgObj));
	    log.info("FF提现通知返回-withdrawNotify={}","success");
	    return "success";
	}
	
	
}
