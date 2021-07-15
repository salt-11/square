package cn.hawy.quick.agent.modular.business.mapper;

import cn.hawy.quick.agent.modular.business.entity.TAgentRateChannel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hawy
 * @since 2019-07-15
 */
public interface TAgentRateChannelMapper extends BaseMapper<TAgentRateChannel> {

    List<Map<String, Object>> findAll(@Param("page") Page page,@Param("agentId") String agentId,@Param("channel") String channel);

}
