package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.TAgentRateChannel;
import cn.hawy.quick.modular.api.entity.TPlatformRateChannel;
import cn.hawy.quick.modular.api.mapper.TAgentRateChannelMapper;
import cn.hawy.quick.modular.api.param.AgentRateChannelParam;
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

	public List<Map<String, Object>> findAll(Page page, String agentId, String channel){
		return this.baseMapper.findAll(page,agentId,channel);
	}
	/**
	 * 添加代理通道费率
	 *
	 * @author fengshuonan
	 * @Date 2018/12/24 22:51
	 */
	public void addRateChannel(AgentRateChannelParam channelParam) {
		TAgentRateChannel tAgentRateChannel =new TAgentRateChannel();
		BeanUtil.copyProperties(channelParam,tAgentRateChannel);
		this.baseMapper.insert(tAgentRateChannel);
	}

	/**
	 * 修改代理通道费率
	 * @param channelParam
	 */
	public void update(AgentRateChannelParam channelParam){
		TAgentRateChannel tAgentRateChannel =new TAgentRateChannel();
		BeanUtil.copyProperties(channelParam,tAgentRateChannel);
		this.updateById(tAgentRateChannel);
	}



}
