package cn.hawy.quick.modular.api.mapper;

import cn.hawy.quick.modular.api.entity.TAgentCashFlow;
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
public interface TAgentCashFlowMapper extends BaseMapper<TAgentCashFlow> {

    List<Map<String, Object>> findAll(@Param("page") Page page, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("cashStatusName") String cashStatusName, @Param("agentId") String agentId, @Param("name") String name);

    List<TAgentCashFlow> find(@Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("cashStatusName") String cashStatusName, @Param("agentId") String agentId, @Param("name") String name);

}
