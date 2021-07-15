package cn.hawy.quick.agent.modular.business.mapper;

import cn.hawy.quick.agent.modular.business.entity.TDeptInfo;
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


    List<Map<String, Object>> findAll(@Param("page") Page page,@Param("agentId") String agentId);

}
