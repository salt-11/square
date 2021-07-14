package cn.hawy.quick.modular.api.controller;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import cn.hawy.quick.modular.api.entity.*;
import cn.hawy.quick.modular.api.service.*;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
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
import cn.hawy.quick.modular.api.channel.SumBtChannel;
import cn.hawy.quick.modular.api.dto.sumbt.BindCardDto;
import cn.hawy.quick.modular.api.dto.sumbt.CardAuthDto;
import cn.hawy.quick.modular.api.dto.sumbt.RegisterDto;
import cn.hawy.quick.modular.api.validate.sumbt.SumBtMchValidate;
import cn.hawy.quick.modular.system.entity.Dept;
import cn.hawy.quick.modular.system.service.DeptService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;

@RestController
@RequestMapping("/api/vs/bt/mch")
public class SumBtMchController {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private static final String Channel = "sumbt";
	private static final String PlatformCode = "tts";

	@Autowired
	TDeptInfoService deptInfoService;
	@Autowired
	TDeptRateChannelService deptRateChannelService;
	@Autowired
	TMchInfoService mchInfoService;
	@Autowired
	TMchCardService mchCardService;
	@Autowired
	TCardAuthService cardAuthService;
	@Autowired
	SumBtChannel sumBtChannel;
	@Autowired
	TAgentInfoService agentInfoService;
	@Autowired
	TPlatformRateChannelService platformRateChannelService;
	@Autowired
	TAgentRateChannelService agentRateChannelService;

	@RequestMapping(value = "/register",method = RequestMethod.POST)
	@ResponseBody
	public Object register(RegisterDto registerDto) {
		log.info("下游请求报文-register:request={}",JSON.toJSONString(registerDto));
		SumBtMchValidate.register(registerDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(registerDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(registerDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, registerDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}

		String mchId = IdGenerator.getId();
		//插入数据
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
		mchInfoChannel.setChannel(Channel);
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
	public Object binkCard(BindCardDto bindCardDto) {
		log.info("下游请求报文-binkCard:request={}",JSON.toJSONString(bindCardDto));
		SumBtMchValidate.binkCard(bindCardDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(bindCardDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(bindCardDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, bindCardDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(bindCardDto.getPartnerId(), bindCardDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TMchCard mchCard = mchCardService.findBybankCardNo(bindCardDto.getMchId(), bindCardDto.getCardNo());
		if(mchCard != null) {
			//throw new RestException(401, "已绑卡成功，不能重复绑卡!");
			mchCard.setBankCardType(bindCardDto.getCardType());
			mchCard.setExpired(bindCardDto.getValidYear() + bindCardDto.getValidMonth());
			mchCard.setCvn(bindCardDto.getCvv());
			mchCard.setBankCode(bindCardDto.getBankCode());
			mchCard.setMobile(bindCardDto.getMobile());
		}else {
			mchCard = new TMchCard();
			mchCard.setMchId(bindCardDto.getMchId());
			mchCard.setBankCardNo(bindCardDto.getCardNo());
			mchCard.setBankCardType(bindCardDto.getCardType());
			mchCard.setExpired(bindCardDto.getValidYear() + bindCardDto.getValidMonth());
			mchCard.setCvn(bindCardDto.getCvv());
			mchCard.setBankCode(bindCardDto.getBankCode());
			mchCard.setMobile(bindCardDto.getMobile());
			mchCard.setCreateTime(LocalDateTime.now());
		}
		mchCardService.saveOrUpdate(mchCard);
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", bindCardDto.getPartnerId());
		map.put("mchId", bindCardDto.getMchId());
		map.put("cardNo", bindCardDto.getCardNo());
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-bindCard:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}

	/**
	 * 鉴权  无成本
	 * @param cardAuthDto
	 * @return
	 */
	@RequestMapping(value = "/cardAuth",method = RequestMethod.POST)
	@ResponseBody
	public Object cardAuth(CardAuthDto cardAuthDto) {
		log.info("下游请求报文-cardAuth:request={}",JSON.toJSONString(cardAuthDto));
		SumBtMchValidate.cardAuth(cardAuthDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(cardAuthDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(cardAuthDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, cardAuthDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TDeptRateChannel deptRateChannel = deptRateChannelService.findByDeptIdAndBankCodeAndChannel(cardAuthDto.getPartnerId(), PlatformCode, Channel);
		if(deptRateChannel == null) {
			throw new RestException(401, "未找到渠道费率信息");
		}
		if(dept.getBalance()<NumberUtil.parseLong(deptRateChannel.getCardAuthRate())){
			throw new RestException(401, "渠道商余额不足!");
		}
		TCardAuth cardAuth = new TCardAuth();
		BeanUtil.copyProperties(cardAuthDto, cardAuth);
		cardAuth.setDeptId(cardAuthDto.getPartnerId());
		cardAuth.setStatus(0);
		cardAuth.setAmount(NumberUtil.parseLong(deptRateChannel.getCardAuthRate()));
		cardAuth.setCreateTime(LocalDateTime.now());
		cardAuthService.save(cardAuth);
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,String> resMap = sumBtChannel.cardAuth(cardAuthDto);
		String status = resMap.get("status");
		map.put("returnMsg", "");
		if("1".equals(status)) {
			boolean updateStatusFlag = cardAuthService.updateStatusSuccess(cardAuth.getId(),1,NumberUtil.parseLong(cardAuth.getDeptId()),cardAuth.getAmount());
			map.put("status",updateStatusFlag?"1":"0");
		}else if("2".equals(status)) {
			boolean updateStatusFlag = cardAuthService.updateStatusSuccess(cardAuth.getId(),2,NumberUtil.parseLong(cardAuth.getDeptId()),cardAuth.getAmount());
			map.put("status",updateStatusFlag?"2":"0");
		}else if("3".equals(status)) {
			boolean updateStatusFlag = cardAuthService.updateStatusSuccess(cardAuth.getId(),3,NumberUtil.parseLong(cardAuth.getDeptId()),cardAuth.getAmount());
			map.put("status",updateStatusFlag?"3":"0");
		}else if("4".equals(status)) {
			boolean updateStatusFlag = cardAuthService.updateStatusFail(cardAuth.getId(), 4);
			map.put("status", updateStatusFlag ? "4" : "0");
			map.put("returnMsg", updateStatusFlag ? resMap.get("returnMsg") : "0");
		}
		map.put("partnerId", cardAuthDto.getPartnerId());
		//map.put("mchId", cardAuthDto.getMchId());
		map.put("cardNo", cardAuthDto.getCardNo());
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-cardAuth:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}

}
