package cn.hawy.quick.partner.modular.business.service;

import cn.hawy.quick.partner.modular.business.entity.TPayOrder;
import cn.hawy.quick.partner.modular.business.mapper.TDeptAccountFlowMapper;
import cn.hawy.quick.partner.modular.business.mapper.TMchCashFlowMapper;
import cn.hawy.quick.partner.modular.business.mapper.TPayOrderMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hawy
 * @since 2019-07-11
 */
@Service
public class TPayOrderService extends ServiceImpl<TPayOrderMapper, TPayOrder> {

	public List<Map<String, Object>> findAll(Page page, String join,String deptId,String beginTime, String endTime,String orderId, String outTradeNo,String mchId,Integer orderStatus,String channelNo){
		return this.baseMapper.findAll(page, join,deptId, beginTime, endTime, orderId, outTradeNo,mchId,orderStatus,channelNo);
	}

	public List<Map<String, Object>> findHistoryAll(Page page, String join,String deptId,String beginTime, String endTime,String orderId, String outTradeNo,String mchId,Integer orderStatus,String channelNo){
		return this.baseMapper.findHistoryAll(page, join,deptId, beginTime, endTime, orderId, outTradeNo,mchId,orderStatus,channelNo);
	}

	@Transactional
	public List<Map<String, Object>> findExcel(String join,String deptId,String beginTime, String endTime,String orderId, String outTradeNo,String mchId,Integer orderStatus,String channelNo){
		return this.baseMapper.findExcel(join,deptId, beginTime, endTime,orderId, outTradeNo,mchId,orderStatus,channelNo);
	}

	@Transactional
	public List<TPayOrder> find(String join, String deptId,String beginTime, String endTime,String orderId, String outTradeNo,String mchId,Integer orderStatus,String channelNo){
		return this.baseMapper.find(join, deptId, beginTime, endTime,orderId, outTradeNo,mchId,orderStatus,channelNo);
	}


	public Map<String, Object> tongji(String beginTime, String endTime,String deptId,String channelNo,Integer orderStatus) {
		return this.baseMapper.tongji(beginTime, endTime, deptId, channelNo, orderStatus);
	}


}
