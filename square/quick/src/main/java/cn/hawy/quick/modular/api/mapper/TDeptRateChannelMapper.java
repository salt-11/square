package cn.hawy.quick.modular.api.mapper;

import cn.hawy.quick.modular.api.entity.TDeptRateChannel;
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
public interface TDeptRateChannelMapper extends BaseMapper<TDeptRateChannel> {

    List<Map<String, Object>> findAll(@Param("page") Page page, @Param("deptId") String deptId, @Param("channel") String channel);

}
