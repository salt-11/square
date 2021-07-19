package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.TAgentRateChannel;
import cn.hawy.quick.modular.api.entity.TPlatformRateChannel;
import cn.hawy.quick.modular.api.mapper.TPlatformRateChannelMapper;
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
public class TPlatformRateChannelService extends ServiceImpl<TPlatformRateChannelMapper, TPlatformRateChannel> {

	public TPlatformRateChannel findByBankCodeAndChannel(String bankCode,String channel) {
		TPlatformRateChannel platformRateChannel = new TPlatformRateChannel();
		platformRateChannel.setBankCode(bankCode);
		platformRateChannel.setChannel(channel);
		return this.baseMapper.selectOne(new QueryWrapper<>(platformRateChannel));
	}

	public TPlatformRateChannel findByChannel(String channel) {
		TPlatformRateChannel platformRateChannel = new TPlatformRateChannel();
		platformRateChannel.setChannel(channel);
		return this.baseMapper.selectOne(new QueryWrapper<>(platformRateChannel));
	}

	public List<Map<String, Object>> findAll(Page page, String channel){
		return this.baseMapper.findAll(page,channel);
	}
	
}
