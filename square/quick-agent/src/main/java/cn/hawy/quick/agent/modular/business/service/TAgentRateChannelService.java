package cn.hawy.quick.agent.modular.business.service;



import cn.hawy.quick.agent.modular.business.entity.TAgentRateChannel;
import cn.hawy.quick.agent.modular.business.mapper.TAgentRateChannelMapper;
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

	public List<Map<String, Object>> findAll(Page page,String agentId,String channel){
		return this.baseMapper.findAll(page,agentId,channel);
	}
	
}
