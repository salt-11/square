package cn.hawy.quick.partner.modular.business.mapper;

import cn.hawy.quick.partner.modular.business.entity.PayOrderContact;
import cn.hawy.quick.partner.modular.business.entity.TPayOrder;

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
public interface TPayOrderMapper extends BaseMapper<TPayOrder> {



	List<Map<String, Object>> findAll(@Param("page") Page page,@Param("join") String join,@Param("deptId") String deptId, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("orderId")  String orderId, @Param("outTradeNo")  String outTradeNo, @Param("mchId")  String mchId, @Param("orderStatus")  Integer orderStatus, @Param("channelNo") String channelNo);

	List<Map<String, Object>> findHistoryAll(@Param("page") Page page,@Param("join") String join,@Param("deptId") String deptId, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("orderId")  String orderId, @Param("outTradeNo")  String outTradeNo, @Param("mchId")  String mchId, @Param("orderStatus")  Integer orderStatus, @Param("channelNo") String channelNo);

	List<Map<String, Object>> findExcel(@Param("join") String join,@Param("deptId") String deptId, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("orderId")  String orderId, @Param("outTradeNo")  String outTradeNo, @Param("mchId")  String mchId, @Param("orderStatus")  Integer orderStatus, @Param("channelNo") String channelNo);

	List<TPayOrder> find(@Param("join") String join, @Param("deptId") String deptId, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("orderId")  String orderId, @Param("outTradeNo")  String outTradeNo, @Param("mchId")  String mchId, @Param("orderStatus")  Integer orderStatus, @Param("channelNo") String channelNo);

	Map<String, Object> tongji(@Param("beginTime") String beginTime, @Param("endTime") String endTime,@Param("deptId") String deptId,@Param("channelNo") String channelNo,@Param("orderStatus")  Integer orderStatus);

}
