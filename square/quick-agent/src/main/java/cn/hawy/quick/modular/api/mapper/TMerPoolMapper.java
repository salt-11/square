package cn.hawy.quick.modular.api.mapper;

import cn.hawy.quick.modular.api.entity.TMerPool;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hawy
 * @since 2019-08-06
 */
public interface TMerPoolMapper extends BaseMapper<TMerPool> {

	public List<TMerPool> findByPC(@Param("provinceCode") String provinceCode,@Param("cityCode") String cityCode,@Param("type") Integer type);


}
