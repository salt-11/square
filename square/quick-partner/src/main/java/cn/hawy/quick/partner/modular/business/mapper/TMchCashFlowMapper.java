package cn.hawy.quick.partner.modular.business.mapper;

import cn.hawy.quick.partner.modular.business.entity.TMchCashFlow;

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
 * @since 2019-07-12
 */
public interface TMchCashFlowMapper extends BaseMapper<TMchCashFlow> {

	List<Map<String, Object>> findAll(@Param("page") Page page, @Param("id") String id, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("cashId") String cashId, @Param("outTradeNo") String outTradeNo, @Param("cashStatus") String cashStatus, @Param("mchName") String mchName, @Param("bankCardNo") String bankCardNo);

	List<TMchCashFlow> find(@Param("deptId") String id, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("cashId") String cashId, @Param("outTradeNo") String outTradeNo, @Param("cashStatus") String cashStatus, @Param("mchName") String mchName, @Param("bankCardNo") String bankCardNo);

	Map<String, Object> tongji(@Param("beginTime") String beginTime, @Param("endTime") String endTime,@Param("deptId") String deptId,@Param("cashStatus")  Integer cashStatus);
}
