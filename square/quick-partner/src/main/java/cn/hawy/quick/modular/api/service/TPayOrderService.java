package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.PayOrderContact;
import cn.hawy.quick.modular.api.entity.TDeptAccountFlow;
import cn.hawy.quick.modular.api.entity.TMchCashFlow;
import cn.hawy.quick.modular.api.entity.TPayOrder;
import cn.hawy.quick.modular.api.mapper.TDeptAccountFlowMapper;
import cn.hawy.quick.modular.api.mapper.TMchCashFlowMapper;
import cn.hawy.quick.modular.api.mapper.TPayOrderBcMapper;
import cn.hawy.quick.modular.api.mapper.TPayOrderMapper;
import cn.hawy.quick.modular.system.entity.Dept;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Autowired
	TDeptAccountFlowMapper deptAccountFlowMapper;

	@Autowired
	TMchCashFlowMapper mchCashFlowMapper;

	@Autowired
	TPayOrderBcMapper payOrderBcMapper;

	public TPayOrder findByMchIdAndOutTradeNo(String mchId,String outTradeNo) {
		TPayOrder payOrder = new TPayOrder();
		payOrder.setMchId(mchId);
		payOrder.setOutTradeNo(outTradeNo);
		return this.baseMapper.selectOne(new QueryWrapper<>(payOrder));
	}

	@Transactional
	public boolean updateOrderStatusSuccess(TPayOrder payOrder,Dept dept) {
		int count = this.baseMapper.updateOrderStatus(payOrder.getOrderId(), 2);
		if(count == 1) {
			if(payOrder.getDeptAmount()>0) {
				//增加渠道商账户流水
				TDeptAccountFlow deptAccountFlow = new TDeptAccountFlow();
				deptAccountFlow.setDeptId(payOrder.getDeptId());
				deptAccountFlow.setDeptName(dept.getSimpleName());
				deptAccountFlow.setBalance(dept.getBalance());
				deptAccountFlow.setAmount(payOrder.getDeptAmount());
				deptAccountFlow.setBizType(1);
				deptAccountFlow.setDirection(1);
				deptAccountFlow.setTradeNo(payOrder.getOrderId());
				deptAccountFlow.setCreateTime(LocalDateTime.now());
				deptAccountFlowMapper.insert(deptAccountFlow);
				//增加渠道商账户余额
				deptAccountFlowMapper.addBalance(payOrder.getDeptId(), payOrder.getDeptAmount());
			}
			return true;
		}else {
			return false;
		}
	}

	@Transactional
	public boolean updateOrderStatusSuccess(TPayOrder payOrder,Dept dept,String returnMsg) {
		int count = this.baseMapper.updateOrderStatusAndReturnMsg(payOrder.getOrderId(), 2, returnMsg);
		if(count == 1) {
			if(payOrder.getDeptAmount()>0) {
				//增加渠道商账户流水
				TDeptAccountFlow deptAccountFlow = new TDeptAccountFlow();
				deptAccountFlow.setDeptId(payOrder.getDeptId());
				deptAccountFlow.setDeptName(dept.getSimpleName());
				deptAccountFlow.setBalance(dept.getBalance());
				deptAccountFlow.setAmount(payOrder.getDeptAmount());
				deptAccountFlow.setBizType(1);
				deptAccountFlow.setDirection(1);
				deptAccountFlow.setTradeNo(payOrder.getOrderId());
				deptAccountFlow.setCreateTime(LocalDateTime.now());
				deptAccountFlowMapper.insert(deptAccountFlow);
				//增加渠道商账户余额
				deptAccountFlowMapper.addBalance(payOrder.getDeptId(), payOrder.getDeptAmount());
			}
			return true;
		}else {
			return false;
		}
	}

	@Transactional
	public boolean updateOrderStatusSuccessOfSumBt(TPayOrder payOrder,TMchCashFlow mchCashFlow,Dept dept) {
		int count = this.baseMapper.updateOrderStatusOfSumBt(payOrder.getOrderId(), 2);
		int cashCount = mchCashFlowMapper.updateCashStatus(mchCashFlow.getCashId(), 2);
		if(count == 1 && cashCount == 1) {
			if(payOrder.getDeptAmount()>0) {
				//增加渠道商账户流水
				TDeptAccountFlow deptAccountFlow = new TDeptAccountFlow();
				deptAccountFlow.setDeptId(payOrder.getDeptId());
				deptAccountFlow.setDeptName(dept.getSimpleName());
				deptAccountFlow.setBalance(dept.getBalance());
				deptAccountFlow.setAmount(payOrder.getDeptAmount());
				deptAccountFlow.setBizType(1);
				deptAccountFlow.setDirection(1);
				deptAccountFlow.setTradeNo(payOrder.getOrderId());
				deptAccountFlow.setCreateTime(LocalDateTime.now());
				deptAccountFlowMapper.insert(deptAccountFlow);
				//增加渠道商账户余额
				deptAccountFlowMapper.addBalance(payOrder.getDeptId(), payOrder.getDeptAmount());
			}
			if(mchCashFlow.getDeptAmount()>0) {
				//增加渠道商账户流水
				TDeptAccountFlow deptAccountFlow = new TDeptAccountFlow();
				deptAccountFlow.setDeptId(payOrder.getDeptId());
				deptAccountFlow.setDeptName(dept.getSimpleName());
				deptAccountFlow.setBalance(dept.getBalance());
				deptAccountFlow.setAmount(mchCashFlow.getDeptAmount());
				deptAccountFlow.setBizType(2);
				deptAccountFlow.setDirection(1);
				deptAccountFlow.setTradeNo(payOrder.getOrderId());
				deptAccountFlow.setCreateTime(LocalDateTime.now());
				deptAccountFlowMapper.insert(deptAccountFlow);
				//增加渠道商账户余额
				deptAccountFlowMapper.addBalance(payOrder.getDeptId(), mchCashFlow.getDeptAmount());
			}
			return true;
		}else {
			return false;
		}
	}

	public boolean updateOrderStatusFail(Long orderId) {
		//修改订单为失败状态
		int count = this.baseMapper.updateOrderStatus(orderId, 3);
		if(count == 1) {
			return true;
		}else {
			return false;
		}
	}

	public boolean updateOrderStatusFail(Long orderId,String returnMsg) {
		//修改订单为失败状态
		int count = this.baseMapper.updateOrderStatusAndReturnMsg(orderId, 3,returnMsg);
		if(count == 1) {
			return true;
		}else {
			return false;
		}
	}

	public boolean updateOrderStatusFailOfSumBt(Long orderId,String returnMsg) {
		//修改订单为失败状态
		int count = this.baseMapper.updateOrderStatusAndReturnMsgOfSumBt(orderId, 3,returnMsg);
		if(count == 1) {
			return true;
		}else {
			return false;
		}
	}

	public boolean updateOrderStatusAndReturnMsg(Long orderId,int orderStatus,String returnMsg) {
		//修改订单为失败状态
		int count = this.baseMapper.updateOrderStatusAndReturnMsg(orderId, orderStatus,returnMsg);
		if(count == 1) {
			return true;
		}else {
			return false;
		}
	}

	public boolean updateOrderStatusAndReturnMsgOfSumBt(Long orderId,int orderStatus,String returnMsg) {
		//修改订单为失败状态
		int count = this.baseMapper.updateOrderStatusAndReturnMsgOfSumBt(orderId, orderStatus,returnMsg);
		if(count == 1) {
			return true;
		}else {
			return false;
		}
	}

	public boolean updateOrderStatusOther(Long orderId,int orderStatus) {
		int count = this.baseMapper.updateOrderStatus(orderId, orderStatus);
		if(count == 1) {
			return true;
		}else {
			return false;
		}
	}

	/*public boolean updateOrderCashStatus(Long orderId,int orderStatus) {
		int count = this.baseMapper.updateOrderCashStatus(orderId, orderStatus);
		if(count == 1) {
			return true;
		}else {
			return false;
		}
	}*/

	public boolean updateOrderStatusBy4(Long orderId,int orderStatus) {
		int count = this.baseMapper.updateOrderStatusBy4(orderId, orderStatus);

		if(count == 1) {
			return true;
		}else {
			return false;
		}
	}

	public boolean updateSplitStatusSuccess(Long orderId) {
		//修改订单为失败状态
		int count = this.baseMapper.updateSplitStatus(orderId, 2);
		if(count == 1) {
			return true;
		}else {
			return false;
		}
	}

	public int updateNotifyCount(Long orderId,int notifyCount,String notifyResult) {
		int count = this.baseMapper.updateNotifyCount(orderId, notifyCount, notifyResult);
		return count;
	}

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


	@Transactional
	public void orderAppley(TPayOrder payOrder,TMchCashFlow mchCashFlow) {
		this.baseMapper.insert(payOrder);
		mchCashFlowMapper.insert(mchCashFlow);
	}

	@Transactional
	public void changeAgentCard(TPayOrder payOrder,TMchCashFlow mchCashFlow) {
		this.baseMapper.updateOrderStatusBy4(payOrder.getOrderId(), 1);
		mchCashFlowMapper.updateById(mchCashFlow);
	}

	public Map<String, Object> tongji(String beginTime, String endTime,String deptId,String channelNo,Integer orderStatus) {
		return this.baseMapper.tongji(beginTime, endTime, deptId, channelNo, orderStatus);
	}

	public Long getCurrentAmount(String deptId){
		return this.baseMapper.getCurrentAmount(deptId);
	}

}
