package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.TDeptRateChannel;
import cn.hawy.quick.modular.api.entity.TPlatformRateChannel;
import cn.hawy.quick.modular.api.mapper.TDeptRateChannelMapper;

import cn.hawy.quick.modular.api.param.DeptRateChannelParam;
import cn.hawy.quick.modular.api.param.PlatformRateChannelParam;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hawy
 * @since 2019-07-15
 */
@Service
public class TDeptRateChannelService extends ServiceImpl<TDeptRateChannelMapper, TDeptRateChannel> {

	public TDeptRateChannel findByDeptIdAndBankNameAndChannel(String deptId,String bankName,String channel) {
		TDeptRateChannel deptRateChannel = new TDeptRateChannel();
		deptRateChannel.setDeptId(deptId);
		deptRateChannel.setBankName(bankName);
		deptRateChannel.setChannel(channel);
		return this.baseMapper.selectOne(new QueryWrapper<>(deptRateChannel));
	}

	public TDeptRateChannel findByDeptIdAndBankCodeAndChannel(String deptId,String bankCode,String channel) {
		TDeptRateChannel deptRateChannel = new TDeptRateChannel();
		deptRateChannel.setDeptId(deptId);
		deptRateChannel.setBankCode(bankCode);
		deptRateChannel.setChannel(channel);
		return this.baseMapper.selectOne(new QueryWrapper<>(deptRateChannel));
	}

	public TDeptRateChannel findByDeptIdAndChannel(String deptId,String channel) {
		TDeptRateChannel deptRateChannel = new TDeptRateChannel();
		deptRateChannel.setDeptId(deptId);
		deptRateChannel.setChannel(channel);
		return this.baseMapper.selectOne(new QueryWrapper<>(deptRateChannel));
	}

	public List<Map<String, Object>> findAll(Page page, String deptId, String channel){
		return this.baseMapper.findAll(page,deptId,channel);
	}
	/**
	 * 添加渠道通道费率
	 *
	 * @author fengshuonan
	 * @Date 2018/12/24 22:51
	 */
	public void addRateChannel(DeptRateChannelParam channelParam) {
		TDeptRateChannel TDeptRateChannel =new TDeptRateChannel();
		BeanUtil.copyProperties(channelParam,TDeptRateChannel);
		this.baseMapper.insert(TDeptRateChannel);
	}

	/**
	 * 修改渠道通道费率
	 * @param channelParam
	 */
	public void update(DeptRateChannelParam channelParam){
		TDeptRateChannel TDeptRateChannel =new TDeptRateChannel();
		BeanUtil.copyProperties(channelParam, TDeptRateChannel);
		this.updateById(TDeptRateChannel);
	}

}
