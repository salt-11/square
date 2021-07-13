package cn.hawy.quick.modular.api.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import cn.hawy.quick.modular.api.entity.*;
import cn.hawy.quick.modular.api.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;

import cn.hawy.quick.config.properties.RestProperties;
import cn.hawy.quick.config.properties.SumProperties;
import cn.hawy.quick.core.common.RestResponse;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.util.IdGenerator;
import cn.hawy.quick.core.util.MapUtils;
import cn.hawy.quick.core.util.RSA;
import cn.hawy.quick.modular.api.channel.SumChannel;
import cn.hawy.quick.modular.api.dto.sum.BalanceQueryDto;
import cn.hawy.quick.modular.api.dto.sum.BindCardConfirmDto;
import cn.hawy.quick.modular.api.dto.sum.BindCardDto;
import cn.hawy.quick.modular.api.dto.sum.MchInfoDto;
import cn.hawy.quick.modular.api.dto.sum.ModifyMchInfoDto;
import cn.hawy.quick.modular.api.dto.sum.ModifyMchRateDto;
import cn.hawy.quick.modular.api.dto.sum.QueryMchStatusDto;
import cn.hawy.quick.modular.api.dto.sum.RegisterDto;
import cn.hawy.quick.modular.api.dto.sum.SignConfirmDto;
import cn.hawy.quick.modular.api.dto.sum.SignNotifyDto;
import cn.hawy.quick.modular.api.dto.sum.UploadImgDto;
import cn.hawy.quick.modular.api.dto.sum.WithdrawDto;
import cn.hawy.quick.modular.api.dto.sum.WithdrawQueryDto;
import cn.hawy.quick.modular.api.validate.sum.SumMchValidate;
import cn.hawy.quick.modular.system.entity.Dept;
import cn.hawy.quick.modular.system.service.DeptService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

@RestController
@RequestMapping("/api/vs/mch")
public class SumMchController {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private static final String Channel = "sum";

	@Autowired
	TDeptInfoService deptInfoService;
	@Autowired
	SumChannel sumChannel;
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
	SumProperties sumProperties;
	@Autowired
	TPlatformRateChannelService platformRateChannelService;


	/**
	 * 注册
	 * @param registerDto
	 * @return
	 */
	@RequestMapping(value = "/register",method = RequestMethod.POST)
	@ResponseBody
	public Object register(RegisterDto registerDto) {
		log.info("下游请求报文-register:request={}",JSON.toJSONString(registerDto));
		SumMchValidate.register(registerDto);
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

		TPlatformRateChannel platformRateChannel = platformRateChannelService.findByChannel(Channel);
		if(platformRateChannel == null) {
			throw new RestException(401, "未找到平台信息");
		}

		TMchImg idCardFrontMchImg = mchImgService.getById(registerDto.getIdCardFront());
		if(idCardFrontMchImg == null) {
			throw new RestException(401, "身份证正面地址错误!");
		}
		registerDto.setIdCardFront(idCardFrontMchImg.getImgPath());
		TMchImg idCardBackMchImg = mchImgService.getById(registerDto.getIdCardBack());
		if(idCardBackMchImg == null) {
			throw new RestException(401, "身份证反面地址错误!");
		}
		registerDto.setIdCardBack(idCardBackMchImg.getImgPath());
		//通道调用
		String outMchId = sumChannel.register(registerDto,platformRateChannel.getChannelMerAppId(),platformRateChannel.getChannelNo());
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
		mchInfoChannel.setChannel(Channel);
		mchInfoChannel.setOutMchId(outMchId);
		//mchInfoChannel.setMchRate(registerDto.getMchRate());
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
	 * 获取商户信息
	 * @return
	 */
	@RequestMapping(value = "/mchInfo",method = RequestMethod.POST)
	@ResponseBody
	public Object mchInfo(MchInfoDto mchInfoDto) {
		log.info("下游请求报文-mchInfo:request={}",JSON.toJSONString(mchInfoDto));
		SumMchValidate.mchInfo(mchInfoDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(mchInfoDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(mchInfoDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, mchInfoDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TMchInfo mchInfo = mchInfoService.findByIdentNoAndDeptId(mchInfoDto.getIdNo(), mchInfoDto.getPartnerId());
		if(mchInfo == null) {
			throw new RestException(401, "未查询到商户信息!");
		}
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", mchInfoDto.getPartnerId());
		map.put("mchId", mchInfo.getMchId());
		map.put("realName", mchInfo.getCustomerName());
		map.put("idNo", mchInfo.getCustomerIdentNo());
		map.put("mobile", mchInfo.getMobile());
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-register:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}

	@RequestMapping(value = "/queryMchStatus",method = RequestMethod.POST)
	@ResponseBody
	public Object queryMchStatus(QueryMchStatusDto queryMchStatusDto) {
		log.info("下游请求报文-queryMchStatus:request={}",JSON.toJSONString(queryMchStatusDto));
		SumMchValidate.queryMchStatus(queryMchStatusDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(queryMchStatusDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(queryMchStatusDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, queryMchStatusDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TPlatformRateChannel platformRateChannel = platformRateChannelService.findByChannel(Channel);
		if(platformRateChannel == null) {
			throw new RestException(401, "未找到平台信息");
		}
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(queryMchStatusDto.getPartnerId(), queryMchStatusDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TMchInfoChannel mchInfoChannel = mchInfoChannelService.findByMchIdAndChannel(queryMchStatusDto.getMchId(), Channel);
		if(mchInfoChannel == null) {
			throw new RestException(401, "商户信息渠道错误!");
		}
		Map<String,String> respMap = sumChannel.queryUserStatus(platformRateChannel.getChannelMerAppId(), platformRateChannel.getChannelNo(),mchInfoChannel.getOutMchId());
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", queryMchStatusDto.getPartnerId());
		map.put("mchId", mchInfo.getMchId());
		map.put("userStatus", respMap.get("user_status"));
		map.put("checkRemarks", respMap.get("check_remarks"));
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-queryMchStatus:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}

	@RequestMapping(value = "/modifyMchInfo",method = RequestMethod.POST)
	@ResponseBody
	public Object modifyMchInfo(ModifyMchInfoDto modifyMchInfoDto) {
		log.info("下游请求报文-modifyMchInfo:request={}",JSON.toJSONString(modifyMchInfoDto));
		SumMchValidate.modifyMchInfo(modifyMchInfoDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(modifyMchInfoDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(modifyMchInfoDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, modifyMchInfoDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TPlatformRateChannel platformRateChannel = platformRateChannelService.findByChannel(Channel);
		if(platformRateChannel == null) {
			throw new RestException(401, "未找到平台信息");
		}
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(modifyMchInfoDto.getPartnerId(), modifyMchInfoDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TMchInfoChannel mchInfoChannel = mchInfoChannelService.findByMchIdAndChannel(modifyMchInfoDto.getMchId(), Channel);
		if(mchInfoChannel == null) {
			throw new RestException(401, "商户信息渠道错误!");
		}
		if(StrUtil.isNotEmpty(modifyMchInfoDto.getIdCardFront())) {
			TMchImg idCardFrontMchImg = mchImgService.getById(modifyMchInfoDto.getIdCardFront());
			if(idCardFrontMchImg == null) {
				throw new RestException(401, "身份证正面地址错误!");
			}
			modifyMchInfoDto.setIdCardFront(idCardFrontMchImg.getImgPath());
		}
		if(StrUtil.isNotEmpty(modifyMchInfoDto.getIdCardBack())) {
			TMchImg idCardBackMchImg = mchImgService.getById(modifyMchInfoDto.getIdCardBack());
			if(idCardBackMchImg == null) {
				throw new RestException(401, "身份证反面地址错误!");
			}
			modifyMchInfoDto.setIdCardBack(idCardBackMchImg.getImgPath());
		}
		sumChannel.modifyUserInfo(modifyMchInfoDto, platformRateChannel.getChannelMerAppId(), platformRateChannel.getChannelNo(),mchInfoChannel.getOutMchId());
		mchInfo.setMobile(StrUtil.isEmpty(modifyMchInfoDto.getMobile())? mchInfo.getMobile():modifyMchInfoDto.getMobile());
		mchInfo.setMchAddress(StrUtil.isEmpty(modifyMchInfoDto.getAddress())? mchInfo.getMchAddress():modifyMchInfoDto.getAddress());
		mchInfoService.updateById(mchInfo);
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", modifyMchInfoDto.getPartnerId());
		map.put("mchId", mchInfo.getMchId());
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-modifyMchInfo:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}


	@RequestMapping(value = "/bindCard",method = RequestMethod.POST)
	@ResponseBody
	public Object binkCard(BindCardDto bindCardDto) {
		log.info("下游请求报文-binkCard:request={}",JSON.toJSONString(bindCardDto));
		SumMchValidate.binkCard(bindCardDto);
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
		TPlatformRateChannel platformRateChannel = platformRateChannelService.findByChannel(Channel);
		if(platformRateChannel == null) {
			throw new RestException(401, "未找到平台信息");
		}
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(bindCardDto.getPartnerId(), bindCardDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TMchInfoChannel mchInfoChannel = mchInfoChannelService.findByMchIdAndChannel(bindCardDto.getMchId(), Channel);
		if(mchInfoChannel == null) {
			throw new RestException(401, "商户信息渠道错误!");
		}
		String orderNo = IdGenerator.getId();
		TMchCard mchCard = mchCardService.findBybankCardNo(bindCardDto.getMchId(), bindCardDto.getCardNo());
		if(mchCard != null) {
			TMchCardChannel mchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),Channel);
			if(mchCardChannel != null) {
				Map<String,String> respMap = sumChannel.sendMessage(bindCardDto,mchInfoChannel.getOutMchId(), orderNo, mchInfo.getCustomerName(), mchInfo.getCustomerIdentNo(),platformRateChannel.getChannelNo(),platformRateChannel.getChannelMerAppId());
				mchCard.setBankCardType(bindCardDto.getCardType());
				mchCard.setExpired(bindCardDto.getValidYear()+bindCardDto.getValidMonth());
				mchCard.setCvn(bindCardDto.getCvv());
				mchCard.setBankCode(bindCardDto.getBankCode());
				mchCard.setMobile(bindCardDto.getMobile());
				if(respMap.get("sign_code").equals("2")) {
					mchCardChannel.setStatus(2);
					mchCardChannel.setProtocol(respMap.get("bind_card_id"));
				}else {
					mchCardChannel.setStatus(1);
				}
				mchCardChannel.setSmsNo(orderNo);
				mchCardService.bindCard(mchCard, mchCardChannel);

				Map<String,Object> map = new HashMap<String,Object>();
				if(respMap.get("sign_code").equals("2")) {
					map.put("status", "2");
				}else {
					map.put("status", "1");
				}
				map.put("signCode", respMap.get("sign_code"));
				map.put("form", respMap.get("form"));
				map.put("partnerId", bindCardDto.getPartnerId());
				map.put("mchId", bindCardDto.getMchId());
				map.put("cardNo", bindCardDto.getCardNo());
				String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
				String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
				map.put("signature", signature);
				log.info("下游返回报文-bindCard:response={}",JSON.toJSONString(map));
				return RestResponse.success(map);
			}else {
				Map<String,String> respMap = sumChannel.sendMessage(bindCardDto,mchInfoChannel.getOutMchId(), orderNo, mchInfo.getCustomerName(), mchInfo.getCustomerIdentNo(),platformRateChannel.getChannelNo(),platformRateChannel.getChannelMerAppId());

				mchCard.setBankCardType(bindCardDto.getCardType());
				mchCard.setExpired(bindCardDto.getValidYear()+bindCardDto.getValidMonth());
				mchCard.setCvn(bindCardDto.getCvv());
				mchCard.setBankCode(bindCardDto.getBankCode());
				mchCard.setMobile(bindCardDto.getMobile());


				mchCardChannel = new TMchCardChannel();
				mchCardChannel.setOutMchId(mchInfoChannel.getOutMchId());
				if(respMap.get("sign_code").equals("2")) {
					mchCardChannel.setStatus(2);
					mchCardChannel.setProtocol(respMap.get("bind_card_id"));
				}else {
					mchCardChannel.setStatus(1);
				}
				mchCardChannel.setChannel(Channel);
				mchCardChannel.setSmsNo(orderNo);
				mchCardChannel.setCreateTime(LocalDateTime.now());
				mchCardService.bindCard(mchCard, mchCardChannel);
				//组装返回报文
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("partnerId", bindCardDto.getPartnerId());
				map.put("mchId", bindCardDto.getMchId());
				if(respMap.get("sign_code").equals("2")) {
					map.put("status", "2");
				}else {
					map.put("status", "1");
				}
				map.put("signCode", respMap.get("sign_code"));
				map.put("form", respMap.get("form"));
				map.put("cardNo", bindCardDto.getCardNo());
				String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
				String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
				map.put("signature", signature);
				log.info("下游返回报文-bindCard:response={}",JSON.toJSONString(map));
				return RestResponse.success(map);
			}
		}else {
			Map<String,String> respMap = sumChannel.sendMessage(bindCardDto,mchInfoChannel.getOutMchId(), orderNo, mchInfo.getCustomerName(), mchInfo.getCustomerIdentNo(),platformRateChannel.getChannelNo(),platformRateChannel.getChannelMerAppId());

			mchCard = new TMchCard();
			mchCard.setMchId(bindCardDto.getMchId());
			mchCard.setBankCardNo(bindCardDto.getCardNo());
			mchCard.setBankCardType(bindCardDto.getCardType());
			mchCard.setExpired(bindCardDto.getValidYear()+bindCardDto.getValidMonth());
			mchCard.setCvn(bindCardDto.getCvv());
			mchCard.setBankCode(bindCardDto.getBankCode());
			mchCard.setMobile(bindCardDto.getMobile());
			mchCard.setCreateTime(LocalDateTime.now());

			TMchCardChannel	mchCardChannel = new TMchCardChannel();
			mchCardChannel.setOutMchId(mchInfoChannel.getOutMchId());
			if(respMap.get("sign_code").equals("2")) {
				mchCardChannel.setStatus(2);
				mchCardChannel.setProtocol(respMap.get("bind_card_id"));
			}else {
				mchCardChannel.setStatus(1);
			}

			mchCardChannel.setChannel(Channel);
			mchCardChannel.setSmsNo(orderNo);
			mchCardChannel.setCreateTime(LocalDateTime.now());
			mchCardService.bindCard(mchCard, mchCardChannel);
			//组装返回报文
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("partnerId", bindCardDto.getPartnerId());
			map.put("mchId", bindCardDto.getMchId());
			if(respMap.get("sign_code").equals("2")) {
				map.put("status", "2");
			}else {
				map.put("status", "1");
			}
			map.put("signCode", respMap.get("sign_code"));
			map.put("cardNo", bindCardDto.getCardNo());
			map.put("form", respMap.get("form"));
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
		SumMchValidate.binkCardConfirm(bindCardConfirmDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(bindCardConfirmDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(bindCardConfirmDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, bindCardConfirmDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TPlatformRateChannel platformRateChannel = platformRateChannelService.findByChannel(Channel);
		if(platformRateChannel == null) {
			throw new RestException(401, "未找到平台信息");
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
		TMchCardChannel mchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),1,Channel);
		if(mchCardChannel == null) {
			throw new RestException(401, "商户卡信息渠道错误");
		}
		String bindCardId = sumChannel.validMessage(bindCardConfirmDto.getVerifyCode(), mchCardChannel.getOutMchId(), mchCardChannel.getSmsNo(),platformRateChannel.getChannelNo(),platformRateChannel.getChannelMerAppId());
		mchCardChannel.setStatus(2);
		mchCardChannel.setProtocol(bindCardId);
		mchCardChannelService.bindCardConfirm(mchCardChannel,null);
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", bindCardConfirmDto.getPartnerId());
		map.put("mchId", bindCardConfirmDto.getMchId());
		map.put("cardNo", bindCardConfirmDto.getCardNo());
		map.put("status", "2");//绑卡成功
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-bindCardConfirm:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}

	/**
	 * 外签绑卡确认
	 * @param signConfirmDto
	 * @return
	 */
	@RequestMapping(value = "/signConfirm",method = RequestMethod.POST)
	@ResponseBody
	public Object signConfirm(SignConfirmDto signConfirmDto) {
		log.info("下游请求报文-signConfirm:request={}",JSON.toJSONString(signConfirmDto));
		SumMchValidate.signConfirm(signConfirmDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(signConfirmDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(signConfirmDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, signConfirmDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TPlatformRateChannel platformRateChannel = platformRateChannelService.findByChannel(Channel);
		if(platformRateChannel == null) {
			throw new RestException(401, "未找到平台信息");
		}
		//查询商户信息
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(signConfirmDto.getPartnerId(), signConfirmDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TMchCard mchCard = mchCardService.findBybankCardNo(signConfirmDto.getMchId(), signConfirmDto.getCardNo());
		if(mchCard == null) {
			throw new RestException(401, "商户卡信息错误");
		}
		TMchCardChannel mchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),1,Channel);
		if(mchCardChannel == null) {
			throw new RestException(401, "未找到需要绑卡的信息");
		}
		String bindCardId = sumChannel.avaliableBank(mchCardChannel.getOutMchId(), mchCard.getBankCode(), signConfirmDto.getCardNo(),platformRateChannel.getChannelNo(),platformRateChannel.getChannelMerAppId());
		mchCardChannel.setStatus(2);
		mchCardChannel.setProtocol(bindCardId);
		mchCardChannelService.bindCardConfirm(mchCardChannel);
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", signConfirmDto.getPartnerId());
		map.put("mchId", signConfirmDto.getMchId());
		map.put("cardNo", signConfirmDto.getCardNo());
		map.put("status", "2");//绑卡成功
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-signConfirm:response={}",JSON.toJSONString(map));
		return RestResponse.success(map);
	}

	@RequestMapping(value = "/balanceQuery",method = RequestMethod.POST)
	@ResponseBody
	public Object balanceQuery(BalanceQueryDto balanceQueryDto) {
		log.info("下游请求报文-balanceQuery:request={}",JSON.toJSONString(balanceQueryDto));
		SumMchValidate.balanceQuery(balanceQueryDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(balanceQueryDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(balanceQueryDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, balanceQueryDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TPlatformRateChannel platformRateChannel = platformRateChannelService.findByChannel(Channel);
		if(platformRateChannel == null) {
			throw new RestException(401, "未找到平台信息");
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
		TMchCardChannel mchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),2,Channel);
		if(mchCardChannel == null) {
			throw new RestException(401, "商户卡信息渠道错误");
		}
		String availableBalance = sumChannel.queryAvaliableAmount(balanceQueryDto, mchCardChannel.getOutMchId(), mchCardChannel.getProtocol(),platformRateChannel.getChannelNo(),platformRateChannel.getChannelMerAppId());
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


	@RequestMapping(value = "/modifyMchRate",method = RequestMethod.POST)
	@ResponseBody
	public Object modifyMchRate(ModifyMchRateDto modifyMchRateDto) {
		log.info("下游请求报文-modifyMchRate:request={}",JSON.toJSONString(modifyMchRateDto));
		SumMchValidate.modifyMchRate(modifyMchRateDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(modifyMchRateDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}

		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(modifyMchRateDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, modifyMchRateDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TDeptRateChannel deptRateChannel = deptRateChannelService.findByDeptIdAndChannel(modifyMchRateDto.getPartnerId(),Channel);
		if(deptRateChannel == null) {
			throw new RestException(401, "未找到渠道信息");
		}
		//商户费率不能低于渠道商费率
		if(NumberUtil.compare(Double.parseDouble(modifyMchRateDto.getMchRate()),Double.parseDouble(deptRateChannel.getCostRate())) < 0) {
			throw new RestException(401, "商户费率不能低于渠道商费率!");
		}
		//查询商户信息
		TMchInfo mchInfo = mchInfoService.findByDeptIdAndMchId(modifyMchRateDto.getPartnerId(), modifyMchRateDto.getMchId());
		if(mchInfo == null) {
			throw new RestException(401, "商户信息错误!");
		}
		TMchInfoChannel mchInfoChannel = mchInfoChannelService.findByMchIdAndChannel(modifyMchRateDto.getMchId(), Channel);
		if(mchInfoChannel == null) {
			throw new RestException(401, "商户信息渠道错误!");
		}
		mchInfoChannel.setMchRate(modifyMchRateDto.getMchRate());
		mchInfoChannelService.updateById(mchInfoChannel);
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", modifyMchRateDto.getPartnerId());
		map.put("mchId", modifyMchRateDto.getMchId());
		map.put("mchRate", modifyMchRateDto.getMchRate());
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		return RestResponse.success(map);
	}


	/**
	 * 图片上传
	 * @param picture
	 * @param uploadImgDto
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "uploadImg",method = RequestMethod.POST)
	@ResponseBody
	public RestResponse uploadImg(@RequestParam("picture") MultipartFile picture,UploadImgDto uploadImgDto) throws Exception {
		log.info("图片上传-uploadImg:request={}",JSON.toJSONString(uploadImgDto));
		SumMchValidate.uploadImg(uploadImgDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(uploadImgDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名校验
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(uploadImgDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, uploadImgDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		//图片信息
		String prefix = picture.getOriginalFilename().substring(picture.getOriginalFilename().lastIndexOf("."));
		byte[] data = picture.getBytes();
		String fileName = IdGenerator.getId()+prefix;
		String pathFile = restProperties.getImgPath()+"/"+uploadImgDto.getPartnerId();
		File filePath = new File(pathFile);
		if (!filePath.exists()){
			filePath.mkdir();
		}
		String pathName = pathFile+"/"+fileName;
		String imgUrl = restProperties.getImgUrl()+"/"+uploadImgDto.getPartnerId()+"/"+fileName;
		File imageFile = new File(pathName);
		FileOutputStream outStream = new FileOutputStream(imageFile);
		outStream.write(data);
		outStream.close();
		//插入图片
		TMchImg mchImg = new TMchImg();
		mchImg.setImgId(IdGenerator.getId());
		mchImg.setImgUrl(imgUrl);
		mchImg.setImgPath(pathName);
		mchImgService.save(mchImg);
		//组装返回报文
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partnerId", uploadImgDto.getPartnerId());
		map.put("imgId", mchImg.getImgId());
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(map), "&", "=");
		String signature = RSA.sign(signContent, dept.getPlatformPrivatekey());
		map.put("signature", signature);
		log.info("下游返回报文-uploadImg:map={}",JSON.toJSONString(map));
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
		SumMchValidate.withdraw(withdrawDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(withdrawDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(withdrawDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, withdrawDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TPlatformRateChannel platformRateChannel = platformRateChannelService.findByChannel(Channel);
		if(platformRateChannel == null) {
			throw new RestException(401, "未找到平台信息");
		}
		TDeptRateChannel deptRateChannel = deptRateChannelService.findByDeptIdAndChannel(withdrawDto.getPartnerId(), Channel);
		if(deptRateChannel == null) {
			throw new RestException(401, "未找到渠道信息");
		}
		Long cashFee = NumberUtil.parseLong(withdrawDto.getCashFee());
		if(cashFee < NumberUtil.parseLong(deptRateChannel.getCashRate())) {
			throw new RestException(401, "提现手续费不能小于渠道提现手续费!");
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
		TMchCardChannel mchCardChannel = mchCardChannelService.findByCardIdAndChannel(mchCard.getId(),2,Channel);
		if(mchCardChannel == null) {
			throw new RestException(401, "商户卡信息渠道错误");
		}
		TMchCashFlow mchCashFlow = mchCashFlowService.findByMchIdAndOutTradeNo(withdrawDto.getMchId(), withdrawDto.getOutTradeNo());
		if(mchCashFlow != null) {
			throw new RestException(401, "提现订单号已存在!");
		}
		Long cashAmount = NumberUtil.parseLong(withdrawDto.getCashAmount());
		Long deptAmount = cashFee - NumberUtil.parseLong(deptRateChannel.getCashRate());
		Long costAmount = NumberUtil.parseLong(deptRateChannel.getCashRate()) - NumberUtil.parseLong(platformRateChannel.getCashRate());
		Long outAmount = cashAmount - cashFee;
		Long shareAmount = cashFee - NumberUtil.parseLong(platformRateChannel.getCashRate());
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
		mchCashFlow.setCostFee(platformRateChannel.getCashRate());
		mchCashFlow.setCostAmount(costAmount);
		mchCashFlow.setNotifyUrl(withdrawDto.getNotifyUrl());
		mchCashFlow.setNotifyCount(0);
		mchCashFlow.setCreateTime(LocalDateTime.now());
		mchCashFlowService.save(mchCashFlow);
		Map<String,String> resMap = sumChannel.withdraw(String.valueOf(cashId), String.valueOf(outAmount), mchCardChannel.getOutMchId(), mchCardChannel.getProtocol(), withdrawDto.getPayPassword(),String.valueOf(shareAmount),platformRateChannel.getChannelNo(),platformRateChannel.getChannelMerAppId());

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
			boolean orderStatusFlag = mchCashFlowService.updateCashStatusSuccess(mchCashFlow, resMap.get("returnMsg"));
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
		SumMchValidate.withdrawQuery(withdrawQueryDto);
		//渠道商信息
		TDeptInfo dept = deptInfoService.getById(withdrawQueryDto.getPartnerId());
		if(dept == null) {
			throw new RestException(401, "渠道商信息错误!");
		}
		//签名信息校验
		Map<String,Object> checkSignMap = BeanUtil.beanToMap(withdrawQueryDto);
		checkSignMap.remove("signature");
		checkSignMap = MapUtils.removeStrNull(checkSignMap);
		String checkSignContent = MapUtil.joinIgnoreNull(MapUtil.sort(checkSignMap), "&", "=");
		Boolean flag = RSA.checkSign(checkSignContent, withdrawQueryDto.getSignature(), dept.getDeptPublickey());
		if(!flag) {
			throw new RestException(401, "签名验证错误!");
		}
		TPlatformRateChannel platformRateChannel = platformRateChannelService.findByChannel(Channel);
		if(platformRateChannel == null) {
			throw new RestException(401, "未找到平台信息");
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
			Map<String,String> resMap = sumChannel.queryOrderStatus(String.valueOf(mchCashFlow.getCashId()),platformRateChannel.getChannelNo(),platformRateChannel.getChannelMerAppId());
			if("1".equals(resMap.get("orderStatus"))) {
				//不做处理
			}else if("2".equals(resMap.get("orderStatus"))) {
				boolean orderStatusFlag = mchCashFlowService.updateCashStatusSuccess(mchCashFlow, "交易成功");
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


	@RequestMapping(value = "/signNotify",method = RequestMethod.POST)
	@ResponseBody
	public Object signNotify(@RequestBody SignNotifyDto signNotifyDto) {
		log.info("SUM外签通知-signNotify:request={}",JSON.toJSONString(signNotifyDto));
		Map<String,String> restMap = new HashMap<String,String>();
		restMap.put("resp_code", "000000");
		restMap.put("resp_msg", "成功");
		return restMap;
	}

}
