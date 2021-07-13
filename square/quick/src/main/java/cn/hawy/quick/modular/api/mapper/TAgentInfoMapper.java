package cn.hawy.quick.modular.api.mapper;

import cn.hawy.quick.modular.api.entity.TAgentInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

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
}
