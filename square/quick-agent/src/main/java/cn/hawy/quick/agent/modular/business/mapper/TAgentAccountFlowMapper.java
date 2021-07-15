package cn.hawy.quick.agent.modular.business.mapper;

import cn.hawy.quick.agent.modular.business.entity.TAgentAccountFlow;
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
 * @since 2021-07-13
 */
public interface TAgentAccountFlowMapper extends BaseMapper<TAgentAccountFlow> {

    List<Map<String, Object>> findAll(@Param("page") Page page, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("agentId") String agentId, @Param("bizTypeName") String bizTypeName, @Param("directionName") String directionName);

    List<TAgentAccountFlow> find(@Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("agentId") String agentId, @Param("bizTypeName") String bizTypeName, @Param("directionName") String directionName);

}
