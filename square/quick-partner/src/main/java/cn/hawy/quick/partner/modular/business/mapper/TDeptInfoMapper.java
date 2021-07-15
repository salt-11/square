package cn.hawy.quick.partner.modular.business.mapper;

import cn.hawy.quick.partner.modular.business.entity.TDeptInfo;
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

    int minusBalance(@Param("deptId") Long deptId,@Param("balance") Long balance);

}
