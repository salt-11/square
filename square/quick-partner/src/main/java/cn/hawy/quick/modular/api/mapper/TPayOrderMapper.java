package cn.hawy.quick.modular.api.mapper;

import cn.hawy.quick.modular.api.entity.PayOrderContact;
import cn.hawy.quick.modular.api.entity.TPayOrder;

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

	int updateOrderStatus(@Param("orderId") Long orderId, @Param("orderStatus") int orderStatus);

	int updateOrderStatusOfSumBt(@Param("orderId") Long orderId, @Param("orderStatus") int orderStatus);

	int updateOrderStatusAndReturnMsg(@Param("orderId") Long orderId, @Param("orderStatus") int orderStatus, @Param("returnMsg") String returnMsg);

	int updateOrderStatusAndReturnMsgOfSumBt(@Param("orderId") Long orderId, @Param("orderStatus") int orderStatus, @Param("returnMsg") String returnMsg);

	int updateOrderCashStatus(@Param("orderId") Long orderId, @Param("orderStatus") int orderStatus);

	int updateOrderStatusBy4(@Param("orderId") Long orderId, @Param("orderStatus") int orderStatus);

	int updateSplitStatus(@Param("orderId") Long orderId, @Param("splitStatus") int splistStatus);

	int updateNotifyCount(@Param("orderId") Long orderId, @Param("notifyCount") int notifyCount,@Param("notifyResult") String notifyResult);

	List<Map<String, Object>> findAll(@Param("page") Page page,@Param("join") String join,@Param("deptId") String deptId, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("orderId")  String orderId, @Param("outTradeNo")  String outTradeNo, @Param("mchId")  String mchId, @Param("orderStatus")  Integer orderStatus, @Param("channelNo") String channelNo);

	List<Map<String, Object>> findHistoryAll(@Param("page") Page page,@Param("join") String join,@Param("deptId") String deptId, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("orderId")  String orderId, @Param("outTradeNo")  String outTradeNo, @Param("mchId")  String mchId, @Param("orderStatus")  Integer orderStatus, @Param("channelNo") String channelNo);

	List<Map<String, Object>> findExcel(@Param("join") String join,@Param("deptId") String deptId, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("orderId")  String orderId, @Param("outTradeNo")  String outTradeNo, @Param("mchId")  String mchId, @Param("orderStatus")  Integer orderStatus, @Param("channelNo") String channelNo);

	List<TPayOrder> find(@Param("join") String join, @Param("deptId") String deptId, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("orderId")  String orderId, @Param("outTradeNo")  String outTradeNo, @Param("mchId")  String mchId, @Param("orderStatus")  Integer orderStatus, @Param("channelNo") String channelNo);

	Map<String, Object> tongji(@Param("beginTime") String beginTime, @Param("endTime") String endTime,@Param("deptId") String deptId,@Param("channelNo") String channelNo,@Param("orderStatus")  Integer orderStatus);

	Long getCurrentAmount(@Param("deptId") String deptId);
}
