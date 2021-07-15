package cn.hawy.quick.partner.modular.business.mapper;

import cn.hawy.quick.partner.modular.business.entity.TDeptCashFlow;

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
 * @since 2019-08-12
 */
public interface TDeptCashFlowMapper extends BaseMapper<TDeptCashFlow> {

	List<Map<String, Object>> findAll(@Param("page") Page page, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("cashStatusName") String cashStatusName, @Param("deptId") String deptId, @Param("name") String name);

	List<TDeptCashFlow> find(@Param("join") String join, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("deptType") String deptType, @Param("cashStatusName") String cashStatusName, @Param("deptId") String deptId, @Param("name") String name);
}
