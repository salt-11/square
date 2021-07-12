package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.TMchInfo;
import cn.hawy.quick.modular.api.entity.TMchInfoChannel;
import cn.hawy.quick.modular.api.entity.TMchRateChannel;
import cn.hawy.quick.modular.api.mapper.TMchInfoChannelMapper;
import cn.hawy.quick.modular.api.mapper.TMchInfoMapper;
import cn.hawy.quick.modular.api.mapper.TMchRateChannelMapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 商户表 服务实现类
 * </p>
 *
 * @author hawy
 * @since 2019-07-09
 */
@Service
public class TMchInfoService extends ServiceImpl<TMchInfoMapper, TMchInfo> {
	
	@Autowired
	TMchInfoChannelMapper mchInfoChannelMapper;
	
	/**
	 * 通过身份证号查询商户信息
	 * @param identNo
	 * @return
	 */
	public TMchInfo findByIdentNo(String identNo) {
		TMchInfo mchInfo = new TMchInfo();
		mchInfo.setCustomerIdentNo(identNo);
		return this.baseMapper.selectOne(new QueryWrapper<>(mchInfo));
	}
	
	/**
	 * 通过身份证号查询商户信息
	 * @param identNo
	 * @return
	 */
	public TMchInfo findByIdentNoAndDeptId(String identNo,String deptId) {
		TMchInfo mchInfo = new TMchInfo();
		mchInfo.setDeptId(deptId);
		mchInfo.setCustomerIdentNo(identNo);
		return this.baseMapper.selectOne(new QueryWrapper<>(mchInfo));
	}
	
	public TMchInfo findByDeptIdAndMchId(String deptId,String mchId) {
		TMchInfo mchInfo = new TMchInfo();
		mchInfo.setDeptId(deptId);
		mchInfo.setMchId(mchId);
		return this.baseMapper.selectOne(new QueryWrapper<>(mchInfo));
	}
	
	
	/**
	 * 进件
	 */
	@Transactional
	public void addMerchant(TMchInfo mchInfo,TMchInfoChannel mchInfoChannel) {
		baseMapper.insert(mchInfo);
		mchInfoChannelMapper.insert(mchInfoChannel);
	}
	
	public List<Map<String, Object>> findAll(Page page, String join, String mchId, String beginTime, String endTime, String mchName, String deptId, String mobile){
		return this.baseMapper.findAll(page, join, mchId, beginTime, endTime, mchName, deptId, mobile);
	}

}
