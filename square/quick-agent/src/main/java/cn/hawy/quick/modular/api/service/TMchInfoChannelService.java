package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.TMchInfoChannel;
import cn.hawy.quick.modular.api.mapper.TMchInfoChannelMapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商户表 服务实现类
 * </p>
 *
 * @author hawy
 * @since 2019-07-09
 */
@Service
public class TMchInfoChannelService extends ServiceImpl<TMchInfoChannelMapper, TMchInfoChannel> {

	public TMchInfoChannel findByMchIdAndChannel(String mchId,String channel) {
		TMchInfoChannel mchInfoChannel = new TMchInfoChannel();
		mchInfoChannel.setMchId(mchId);
		mchInfoChannel.setChannel(channel);
		return this.baseMapper.selectOne(new QueryWrapper<>(mchInfoChannel));
	}
	
	
}
