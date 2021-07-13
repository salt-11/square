package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.TAgentRateChannel;
import cn.hawy.quick.modular.api.mapper.TAgentRateChannelMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hawy
 * @since 2019-07-15
 */
@Service
public class TAgentRateChannelService extends ServiceImpl<TAgentRateChannelMapper, TAgentRateChannel> {



	public TAgentRateChannel findByAgentIdAndBankCodeAndChannel(String agentId,String bankCode,String channel) {
		TAgentRateChannel agentRateChannel = new TAgentRateChannel();
		agentRateChannel.setAgentId(agentId);
		agentRateChannel.setBankCode(bankCode);
		agentRateChannel.setChannel(channel);
		return this.baseMapper.selectOne(new QueryWrapper<>(agentRateChannel));
	}

	public TAgentRateChannel findByAgentIdAndChannel(String agentId,String channel) {
		TAgentRateChannel agentRateChannel = new TAgentRateChannel();
		agentRateChannel.setAgentId(agentId);
		agentRateChannel.setChannel(channel);
		return this.baseMapper.selectOne(new QueryWrapper<>(agentRateChannel));
	}
	
}
