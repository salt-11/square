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

import cn.hawy.quick.core.common.RestResponse;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.util.IdGenerator;
import cn.hawy.quick.core.util.MapUtils;
import cn.hawy.quick.core.util.RSA;
import cn.hawy.quick.modular.api.channel.EfpsChannel;
import cn.hawy.quick.modular.api.dto.AddMerchantDto;
import cn.hawy.quick.modular.api.dto.BindCardConfirmDto;
import cn.hawy.quick.modular.api.dto.BindCardDto;
import cn.hawy.quick.modular.api.dto.MchBalanceQueryDto;
import cn.hawy.quick.modular.api.dto.MchCashDto;
import cn.hawy.quick.modular.api.dto.MchCashQueryDto;
import cn.hawy.quick.modular.api.entity.TBankCardBin;
import cn.hawy.quick.modular.api.entity.TDeptRateChannel;
import cn.hawy.quick.modular.api.entity.TMchCard;
import cn.hawy.quick.modular.api.entity.TMchCardChannel;
import cn.hawy.quick.modular.api.entity.TMchCashFlow;
import cn.hawy.quick.modular.api.entity.TMchInfo;
import cn.hawy.quick.modular.api.entity.TMchInfoChannel;
import cn.hawy.quick.modular.api.mq.MqMchCashNotify;
import cn.hawy.quick.modular.api.service.TBankCardBinService;
import cn.hawy.quick.modular.api.service.TDeptRateChannelService;
import cn.hawy.quick.modular.api.service.TMchCardChannelService;
import cn.hawy.quick.modular.api.service.TMchCardService;
import cn.hawy.quick.modular.api.service.TMchCashFlowService;
import cn.hawy.quick.modular.api.service.TMchInfoChannelService;
import cn.hawy.quick.modular.api.service.TMchInfoService;
import cn.hawy.quick.modular.api.validate.MchValidate;
import cn.hawy.quick.modular.system.entity.Dept;
import cn.hawy.quick.modular.system.service.DeptService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;


@RestController
@RequestMapping("/api/mch")
public class MchController {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	DeptService deptService;
	@Autowired
	EfpsChannel efpsChannel;
	@Autowired
	TMchInfoService mchInfoService;
	@Autowired
	TMchInfoChannelService mchInfoChannelService;
	@Autowired
	TMchCardService mchCardService;
	@Autowired
	TMchCardChannelService mchCardChannelService;
	@Autowired
	TMchCashFlowService mchCashFlowService;
	@Autowired
	MqMchCashNotify mqMchCashNotify;
	@Autowired
	TBankCardBinService bankCardBinService;
	@Autowired
	TDeptRateChannelService deptRateChannelService;

	@RequestMapping(value = "/addMerchant",method = RequestMethod.POST)
	@ResponseBody
	public Object addMerchant(AddMerchantDto addMerchantDto) {
		log.info("下游请求报文-addMerchant:addMerchantDto={}",JSON.toJSONString(addMerchantDto));
		//参数校验
		MchValidate.addMerchantValidate(addMerchantDto);
		//渠道商信息
		Dept dept = deptService.getById(addMerchantDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(addMerchantDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, addMerchantDto.getSignature(), dept.getPartnerPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TMchInfo mchInfo = mchInfoService.findByIdentNoAndDeptId(addMerchantDto.getCustomerIdentNo(), addMerchantDto.getPartnerId());
		if(mchInfo != null) {
			throw new RestException(401, "身份证号重复存在!");
		}
		//通道调用
		String outMchId = efpsChannel.addMerchant(addMerchantDto);
		//插入数据
		mchInfo = new TMchInfo();
		String mchId = IdGenerator.getId();
		mchInfo.setMchId(mchId);
		mchInfo.setDeptId(addMerchantDto.getPartnerId());
		mchInfo.setMchStatus(2);
		mchInfo.setCustomerIdentType(0);
		mchInfo.setSettMode("D0");
		mchInfo.setSettCircle("0");
		mchInfo.setCardKind(1);
		mchInfo.setCreateTime(LocalDateTime.now());
		BeanUtil.copyProperties(addMerchantDto, mchInfo);
		//通道
		TMchInfoChannel mchInfoChannel = new TMchInfoChannel();
		mchInfoChannel.setMchId(mchId);
		mchInfoChannel.setChannel("efps");
		mchInfoChannel.setOutMchId(outMchId);
		mchInfoChannel.setCreateTime(LocalDateTime.now());
		mchInfoService.addMerchant(mchInfo, mchInfoChannel);
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", addMerchantDto.getPartnerId());
		map.put("mchId", mchId);
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-addMerchant:map={}",JSON.toJSONString(map));
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
		log.info("下游请求报文-bindCard:request={}",JSON.toJSONString(bindCardDto));
		//参数校验
		MchValidate.bindCardValidate(bindCardDto);
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
		TMchInfoChannel mchInfoChannel = mchInfoChannelService.findByMchIdAndChannel(bindCardDto.getMchId(), "efps");
		if(mchInfoChannel == null) {
			throw new RestException(401, "商户信息渠道错误!");
		}
		TMchCard mchCard = mchCardService.findBybankCardNo(bindCardDto.getMchId(), bindCardDto.getBankCardNo());
		TMchCardChannel successMchCardChannel = null;
		if(mchCard != null) {
			mchCardChannelService.updateStatusFail(mchCard.getId(), "efps");
			successMchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),2,"efps");
		}else {
			mchCard = new TMchCard();
		}
		TBankCardBin bankCardBin = bankCardBinService.findBankNameByBankCardNo(bindCardDto.getBankCardNo());
		if(bankCardBin == null) {
			throw new RestException(401, "未找到卡bin信息");
		}
		TDeptRateChannel deptRateChannel = deptRateChannelService.findByDeptIdAndBankNameAndChannel(bindCardDto.getPartnerId(), bankCardBin.getBankName(), "efps");
		if(deptRateChannel == null) {
			throw new RestException(401, "未找到渠道费率信息");
		}
		//商户费率不能低于渠道商费率
		if(NumberUtil.compare(Double.parseDouble(bindCardDto.getMchRate()),Double.parseDouble(deptRateChannel.getCostRate())) < 0) {
			throw new RestException(401, "商户费率不能低于渠道商费率!");
		}
		//通道绑卡
		Map<String,Object> restMap = efpsChannel.bindCard(bindCardDto,mchInfo,mchInfoChannel.getOutMchId());
		//数据插入
		BeanUtil.copyProperties(bindCardDto, mchCard);
		mchCard.setBankName(bankCardBin.getBankName());
		mchCard.setCreateTime(LocalDateTime.now());
		TMchCardChannel mchCardChannel = new TMchCardChannel();
		mchCardChannel.setOutMchId(mchInfoChannel.getOutMchId());
		mchCardChannel.setMchRate(bindCardDto.getMchRate());
		String isBindCardConfirm = "0";
		if("credit".equals(bindCardDto.getBankCardType())) {
			if(String.valueOf(restMap.get("smsNo")).startsWith("QY")) {
				if(String.valueOf(restMap.get("protocol")).startsWith("p")) {
					mchCardChannel.setStatus(2);
					mchCardChannel.setSmsNo(String.valueOf(restMap.get("smsNo")));
					mchCardChannel.setProtocol(String.valueOf(restMap.get("protocol")));
					if(successMchCardChannel != null) {
						successMchCardChannel.setStatus(3);
					}
				}else {
					isBindCardConfirm = "1";
					mchCardChannel.setStatus(1);
					mchCardChannel.setSmsNo(String.valueOf(restMap.get("smsNo")));
				}
			}else if(String.valueOf(restMap.get("smsNo")).startsWith("p")){
				mchCardChannel.setStatus(2);
				mchCardChannel.setSmsNo(String.valueOf(restMap.get("smsNo")));
				mchCardChannel.setProtocol(String.valueOf(restMap.get("smsNo")));
				if(successMchCardChannel != null) {
					successMchCardChannel.setStatus(3);
				}
			}else {
				throw new RestException(501, "通道绑卡错误返回");
			}
		}
		if("debit".equals(bindCardDto.getBankCardType())) {
			mchCardChannel.setStatus(2);
			mchCardChannel.setProtocol(String.valueOf(restMap.get("protocol")));
			if(successMchCardChannel != null) {
				successMchCardChannel.setStatus(3);
			}
		}
		
		mchCardChannel.setIsSendIssuer(bindCardDto.getIsSendIssuer());
		mchCardChannel.setChannel("efps");
		mchCardChannel.setCreateTime(LocalDateTime.now());
		mchCardService.bindCard(mchCard, mchCardChannel,successMchCardChannel);
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", bindCardDto.getPartnerId());
		map.put("mchId", bindCardDto.getMchId());
		map.put("bankCardNo", bindCardDto.getBankCardNo());
		map.put("isBindCardConfirm", isBindCardConfirm);
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-bindCard:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}
	
	/**
	 * 绑卡确认
	 * @param bindCardConfirmDto
	 * @return
	 */
	@RequestMapping(value = "/bindCardConfirm",method = RequestMethod.POST)
	@ResponseBody
	public Object bindCardConfirm(BindCardConfirmDto bindCardConfirmDto) {
		log.info("下游请求报文-bindCardConfirm:request={}",JSON.toJSONString(bindCardConfirmDto));
		//参数校验
		MchValidate.bindCardConfirmValidate(bindCardConfirmDto);
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
		TMchCard mchCard = mchCardService.findBybankCardNo(bindCardConfirmDto.getMchId(), bindCardConfirmDto.getBankCardNo());
		if(mchCard == null) {
			throw new RestException(401, "商户卡信息错误");
		}
		TMchCardChannel mchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),1,"efps");
		if(mchCardChannel == null) {
			throw new RestException(401, "商户卡信息渠道错误");
		}
		//通道绑卡
		String protocol = efpsChannel.bindCardConfirm(bindCardConfirmDto.getSmsCode(),mchCardChannel.getSmsNo(),mchCardChannel.getOutMchId());
		TMchCardChannel successMchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),2,"efps");
		//数据插入
		mchCardChannel.setStatus(2);
		mchCardChannel.setProtocol(protocol);
		mchCardChannelService.bindCardConfirm(mchCardChannel,successMchCardChannel);
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", bindCardConfirmDto.getPartnerId());
		map.put("mchId", bindCardConfirmDto.getMchId());
		map.put("bankCardNo", bindCardConfirmDto.getBankCardNo());
		map.put("status", "2");//绑卡成功
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-bindCard:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}
	
	/**
	 * 余额查询
	 * @param mchBalanceQueryDto
	 * @return
	 */
	@RequestMapping(value = "/mchBalanceQuery",method = RequestMethod.POST)
	@ResponseBody
	public Object mchBalanceQuery(MchBalanceQueryDto mchBalanceQueryDto) {
		log.info("下游请求报文-mchBalanceQuery:request={}",JSON.toJSONString(mchBalanceQueryDto));
		//参数校验
		MchValidate.mchBalanceQueryValidate(mchBalanceQueryDto);
		//渠道商信息
		Dept dept = deptService.getById(mchBalanceQueryDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(mchBalanceQueryDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, mchBalanceQueryDto.getSignature(), dept.getPartnerPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		//查询商户信息
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(mchBalanceQueryDto.getPartnerId(), mchBalanceQueryDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TMchInfoChannel mchInfoChannel = mchInfoChannelService.findByMchIdAndChannel(mchBalanceQueryDto.getMchId(), "efps");
		if(mchInfoChannel == null) {
			throw new RestException(401, "商户信息渠道错误!");
		}
		String availableBalance = efpsChannel.subCustomerAccountQuery(mchInfoChannel.getOutMchId());
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", mchBalanceQueryDto.getPartnerId());
		map.put("mchId", mchBalanceQueryDto.getMchId());
		map.put("balance", availableBalance);
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-mchBalanceQuery:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}
	
	/**
	 * 提现
	 * @param mchCashDto
	 * @return
	 */
	@RequestMapping(value = "/mchCash",method = RequestMethod.POST)
	@ResponseBody
	public Object mchCash(MchCashDto mchCashDto) {
		log.info("下游请求报文-mchCash={}",JSON.toJSONString(mchCashDto));
		//参数校验
		MchValidate.mchCashValidate(mchCashDto);
		//渠道商信息
		Dept dept = deptService.getById(mchCashDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//查询商户信息
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(mchCashDto.getPartnerId(), mchCashDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TMchCard mchCard = mchCardService.findBybankCardNo(mchCashDto.getMchId(), mchCashDto.getBankCardNo());
		if(mchCard == null) {
			throw new RestException(401, "商户卡信息错误");
		}
		if(!"debit".equals(mchCard.getBankCardType())) {
			throw new RestException(401, "提现商户卡必须是储蓄卡");
		}
		TMchCardChannel mchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),2,"efps");
		if(mchCardChannel == null) {
			throw new RestException(401, "商户卡信息渠道错误");
		}
		TDeptRateChannel deptRateChannel = deptRateChannelService.findByDeptIdAndBankNameAndChannel(mchCashDto.getPartnerId(), mchCard.getBankName(), "efps");
		if(deptRateChannel == null) {
			throw new RestException(401, "未找到渠道费率信息");
		}
		Long cashFee = NumberUtil.parseLong(mchCashDto.getCashFee());
		if(cashFee < NumberUtil.parseLong(deptRateChannel.getCashRate())) {
			throw new RestException(401, "提现手续费不能小于提现成本手续费!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(mchCashDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, mchCashDto.getSignature(), dept.getPartnerPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TMchCashFlow mchCashFlow = mchCashFlowService.findByMchIdAndOutTradeNo(mchCashDto.getMchId(), mchCashDto.getOutTradeNo());
		if(mchCashFlow != null) {
			throw new RestException(401, "提现订单号已存在!");
		}
		Long cashAmount = NumberUtil.parseLong(mchCashDto.getCashAmount());
		Long deptAmount = cashFee - NumberUtil.parseLong(deptRateChannel.getCashRate());
		Long outAmount = cashAmount - cashFee;
		Long cashId = IdGenerator.getIdLong();
		mchCashFlow = new TMchCashFlow();
		mchCashFlow.setCashId(cashId);
		mchCashFlow.setMchId(mchCashDto.getMchId());
		mchCashFlow.setMchName(mchInfo.getMchShortName());
		mchCashFlow.setDeptId(mchCashDto.getPartnerId());
		mchCashFlow.setBankCardNo(mchCashDto.getBankCardNo());
		mchCashFlow.setOutTradeNo(mchCashDto.getOutTradeNo());
		mchCashFlow.setOutMchId(mchCardChannel.getOutMchId());
		mchCashFlow.setCashAmount(cashAmount);
		mchCashFlow.setCashFee(cashFee);
		mchCashFlow.setOutAmount(outAmount);
		mchCashFlow.setCashStatus(1);
		mchCashFlow.setCashRate(deptRateChannel.getCashRate());
		mchCashFlow.setDeptAmount(deptAmount);
		//mchCashFlow.setNotifyCount(0);
		mchCashFlow.setCreateTime(LocalDateTime.now());
		mchCashFlowService.save(mchCashFlow);
		efpsChannel.withdrawalForSubMerchant(String.valueOf(cashId), mchCardChannel.getOutMchId(), mchCardChannel.getProtocol(), outAmount, cashFee);
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", mchCashDto.getPartnerId());
		map.put("mchId", mchCashDto.getMchId());
		map.put("bankCardNo", mchCashDto.getBankCardNo());
		map.put("outTradeNo", mchCashDto.getOutTradeNo());
		map.put("cashAmount", mchCashDto.getCashAmount());
		map.put("cashFee", mchCashDto.getCashFee());
		map.put("cashStatus", "1");
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-mchCash:map={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}
	
	/**
	 * 提现查询
	 * @param mchCashQueryDto
	 * @return
	 */
	@RequestMapping(value = "/mchCashQuery",method = RequestMethod.POST)
	@ResponseBody
	public Object mchCashQuery(MchCashQueryDto mchCashQueryDto) {
		log.info("下游请求报文-mchCashQuery={}",JSON.toJSONString(mchCashQueryDto));
		//参数校验
		MchValidate.mchCashQueryValidate(mchCashQueryDto);
		//渠道商信息
		Dept dept = deptService.getById(mchCashQueryDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(mchCashQueryDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, mchCashQueryDto.getSignature(), dept.getPartnerPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		//查询商户信息
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(mchCashQueryDto.getPartnerId(), mchCashQueryDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TMchCashFlow mchCashFlow = mchCashFlowService.findByMchIdAndOutTradeNo(mchCashQueryDto.getMchId(), mchCashQueryDto.getOutTradeNo());
		if(mchCashFlow == null) {
			throw new RestException(401, "提现订单号不存在!");
		}
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		if(mchCashFlow.getCashStatus() == 1) {
			int cashStatus = efpsChannel.withdrawalQueryForSubMerchant(mchCashFlow.getOutMchId(),String.valueOf(mchCashFlow.getCashId()));
			if(cashStatus == 1) {
				map.put("cashStatus", "1");
			}else if(cashStatus == 2) {
				boolean orderStatusFlag = mchCashFlowService.updateCashStatusSuccess(mchCashFlow, dept);
				if(orderStatusFlag) {
					map.put("cashStatus", "2");
				}else {
					map.put("cashStatus", "1");
				}
			}else if(cashStatus == 3) {
				boolean orderStatusFlag = mchCashFlowService.updateCashStatusFail(mchCashFlow.getCashId());
				if(orderStatusFlag) {
					map.put("cashStatus", "3");
				}else {
					map.put("cashStatus", "1");
				}
			}else {
				throw new RestException(401, "提现订单查询失败!");
			}
		}else {
			map.put("cashStatus", String.valueOf(mchCashFlow.getCashStatus()));
		}
		map.put("partnerId", mchCashQueryDto.getPartnerId());
		map.put("mchId", mchCashQueryDto.getMchId());
		map.put("outTradeNo", mchCashQueryDto.getOutTradeNo());
		map.put("cashAmount", String.valueOf(mchCashFlow.getCashAmount()));
		map.put("cashFee", String.valueOf(mchCashFlow.getCashFee()));
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-mchCashQuery={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}
	
	/*@RequestMapping(value = "/epspMchCashNofify",method = RequestMethod.POST)
	@ResponseBody
	public Object epspMchCashNofify(@RequestBody OrderNotify orderNotify) {
		log.info("上游支付通知-epspCashNofify:request={}",JSON.toJSONString(orderNotify));
		Map<String,String> restMap = new HashMap<String,String>();
		//获取订单信息
		TMchCashFlow mchCashFlow = mchCashFlowService.getById(orderNotify.getOutTradeNo());
		if(mchCashFlow == null) {
			//什么都不做
			restMap.put("returnCode", "0001");
			restMap.put("returnMsg", "查无订单");
			log.info("上游支付通知返回-epspCashNofify:response={}",JSON.toJSONString(restMap));
			return restMap;
		}
		if(mchCashFlow.getCashStatus() != 1) {
			restMap.put("returnCode", "0001");
			restMap.put("returnMsg", "订单已处理");
			log.info("上游支付通知返回-epspCashNofify:response={}",JSON.toJSONString(restMap));
			return restMap;
		}
		if("00".equals(orderNotify.getPayState())) {
			//渠道商信息
			Dept dept = deptService.getById(mchCashFlow.getDeptId());
			if(dept == null) {
				restMap.put("returnCode", "0001");
				restMap.put("returnMsg", "提现订单渠道异常");
				log.info("上游支付通知返回-epspCashNofify:response={}",JSON.toJSONString(restMap));
				return restMap;
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
			map.put("cashFee", String.valueOf(mchCashFlow.getCashFee()));
			map.put("outAmount", String.valueOf(mchCashFlow.getOutAmount()));
			boolean flag = mchCashFlowService.updateCashStatusSuccess(mchCashFlow, dept);
			if(flag) {
				map.put("orderStatus", "2");
				String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
				String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
				map.put("signature", signature);
				log.info("下游返回报文-epspCashNofify:response={}",JSON.toJSONString(map));
				msgObj.put("notifyMsg", RestResponse.success(map));
				mqMchCashNotify.send(JSON.toJSONString(msgObj));
				restMap.put("returnCode", "0000");
				restMap.put("returnMsg", "");
				log.info("上游支付通知返回-epspCashNofify:response={}",JSON.toJSONString(restMap));
				return restMap;
			}else {
				restMap.put("returnCode", "0001");
				restMap.put("returnMsg", "提现订单修改失败");
				log.info("上游支付通知返回-epspCashNofify:response={}",JSON.toJSONString(restMap));
				return restMap;
			}
		}else {
			//什么都不做
			restMap.put("returnCode", "0001");
			restMap.put("returnMsg", "通知提现状态异常");
			log.info("上游支付通知返回-epspCashNofify:response={}",JSON.toJSONString(restMap));
			return restMap;
		}
	}*/
	
	
	
	
	
}
