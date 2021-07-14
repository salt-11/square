package cn.hawy.quick.modular.api.mapper;

import cn.hawy.quick.modular.api.entity.TDeptAccountFlow;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hawy
 * @since 2019-07-11
 */
public interface TDeptAccountFlowMapper extends BaseMapper<TDeptAccountFlow> {

	
	List<Map<String, Object>> findAll(@Param("page") Page page,@Param("join") String join, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("deptId") String deptId, @Param("deptType") String deptType, @Param("bizTypeName") String bizTypeName, @Param("directionName") String directionName);

	List<TDeptAccountFlow> find(@Param("join") String join, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("deptId") String deptId, @Param("deptType") String deptType, @Param("bizTypeName") String bizTypeName, @Param("directionName") String directionName);
}
