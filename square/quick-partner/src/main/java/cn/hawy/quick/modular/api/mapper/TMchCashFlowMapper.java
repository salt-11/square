package cn.hawy.quick.modular.api.mapper;

import cn.hawy.quick.modular.api.entity.TMchCashFlow;

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

	int updateCashStatus(@Param("cashId") Long cashId, @Param("cashStatus") int cashStatus);
	
	int updateCashStatusAndReturnMsg(@Param("cashId") Long cashId, @Param("cashStatus") int cashStatus,@Param("returnMsg") String returnMsg);

	int updateNotifyCount(@Param("cashId") Long cashId, @Param("notifyCount") int notifyCount,@Param("notifyResult") String notifyResult);
	
	List<Map<String, Object>> findAll(@Param("page") Page page, @Param("id") String id, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("cashId") String cashId, @Param("outTradeNo") String outTradeNo, @Param("cashStatus") String cashStatus, @Param("mchName") String mchName, @Param("bankCardNo") String bankCardNo);

	List<TMchCashFlow> find(@Param("join") String join, @Param("deptId") String deptId, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("cashId") String cashId, @Param("outTradeNo") String outTradeNo, @Param("cashStatus") String cashStatus, @Param("mchName") String mchName, @Param("bankCardNo") String bankCardNo);

	Map<String, Object> tongji(@Param("beginTime") String beginTime, @Param("endTime") String endTime,@Param("deptId") String deptId,@Param("cashStatus")  Integer cashStatus);
}
