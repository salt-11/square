package cn.hawy.quick.modular.api.mapper;

import cn.hawy.quick.modular.api.entity.TMchInfo;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 商户表 Mapper 接口
 * </p>
 *
 * @author hawy
 * @since 2019-07-09
 */
public interface TMchInfoMapper extends BaseMapper<TMchInfo> {
	List<Map<String, Object>> findAll(@Param("page") Page page, @Param("join") String join, @Param("mchId") String mchId, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("mchName") String mchName, @Param("deptId") String deptId, @Param("mobile") String mobile);
}
