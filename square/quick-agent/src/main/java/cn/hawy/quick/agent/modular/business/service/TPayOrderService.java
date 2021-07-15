package cn.hawy.quick.agent.modular.business.service;

import cn.hawy.quick.agent.modular.business.entity.TPayOrder;
import cn.hawy.quick.agent.modular.business.mapper.TPayOrderMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

	public List<Map<String, Object>> findAll(Page page, String agentId,String deptId,String beginTime, String endTime,String orderId, String outTradeNo,String mchId,Integer orderStatus,String channelNo){
		return this.baseMapper.findAll(page, agentId,deptId, beginTime, endTime, orderId, outTradeNo,mchId,orderStatus,channelNo);
	}

	public List<Map<String, Object>> findHistoryAll(Page page, String agentId,String deptId,String beginTime, String endTime,String orderId, String outTradeNo,String mchId,Integer orderStatus,String channelNo){
		return this.baseMapper.findHistoryAll(page, agentId,deptId, beginTime, endTime, orderId, outTradeNo,mchId,orderStatus,channelNo);
	}

	@Transactional
	public List<Map<String, Object>> findExcel(String agentId,String deptId,String beginTime, String endTime,String orderId, String outTradeNo,String mchId,Integer orderStatus,String channelNo){
		return this.baseMapper.findExcel(agentId,deptId, beginTime, endTime,orderId, outTradeNo,mchId,orderStatus,channelNo);
	}

	@Transactional
	public List<TPayOrder> find(String agentId, String deptId,String beginTime, String endTime,String orderId, String outTradeNo,String mchId,Integer orderStatus,String channelNo){
		return this.baseMapper.find(agentId, deptId, beginTime, endTime,orderId, outTradeNo,mchId,orderStatus,channelNo);
	}


	public Map<String, Object> tongji(String beginTime, String endTime,String deptId,String channelNo,Integer orderStatus) {
		return this.baseMapper.tongji(beginTime, endTime, deptId, channelNo, orderStatus);
	}


}
