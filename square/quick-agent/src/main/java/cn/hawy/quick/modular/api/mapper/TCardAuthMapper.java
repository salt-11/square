package cn.hawy.quick.modular.api.mapper;

import cn.hawy.quick.modular.api.entity.TCardAuth;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TCardAuthMapper extends BaseMapper<TCardAuth> {
	
	List<Map<String, Object>> findAll(@Param("page") Page page,@Param("join") String join, @Param("beginTime") String beginTime, @Param("endTime") String endTime,@Param("deptId") String deptId);

	Map<String, Object> tongji(@Param("beginTime") String beginTime, @Param("endTime") String endTime,@Param("deptId") String deptId);
}
