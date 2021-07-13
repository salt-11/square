package cn.hawy.quick.modular.api.mapper;

import cn.hawy.quick.modular.api.entity.TDeptInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

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

}
