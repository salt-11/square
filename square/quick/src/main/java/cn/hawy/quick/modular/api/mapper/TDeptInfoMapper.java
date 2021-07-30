package cn.hawy.quick.modular.api.mapper;

import cn.hawy.quick.modular.api.entity.TDeptInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 渠道信息表 Mapper 接口
 * </p>
 *
 * @author hawy
 * @since 2021-07-13
 */
public interface TDeptInfoMapper extends BaseMapper<TDeptInfo> {

    int addBalance(@Param("deptId") String deptId, @Param("balance") Long balance);

    List<Map<String, Object>> findAll(@Param("page") Page page, @Param("id") String id, @Param("account") String account, @Param("balance") String balance, @Param("agentId") String agentId, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("deptName") String deptName );

}
