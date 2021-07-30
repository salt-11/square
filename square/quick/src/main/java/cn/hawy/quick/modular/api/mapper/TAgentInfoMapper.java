package cn.hawy.quick.modular.api.mapper;

import cn.hawy.quick.modular.api.entity.TAgentInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 代理商信息表 Mapper 接口
 * </p>
 *
 * @author hawy
 * @since 2021-07-13
 */
public interface TAgentInfoMapper extends BaseMapper<TAgentInfo> {

    int addBalance(@Param("agentId") String agentId, @Param("balance") Long balance);

    List<Map<String, Object>> findAll(@Param("page") Page page, @Param("id") String id, @Param("account") String account,@Param("balance") String balance, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("agentName") String agentName );
    List<Map<String, Object>> getAgentInfo(String deptId);
}
