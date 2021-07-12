package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.TDeptAccountFlow;
import cn.hawy.quick.modular.api.entity.TMchCashFlow;
import cn.hawy.quick.modular.api.entity.TPayOrder;
import cn.hawy.quick.modular.api.mapper.TDeptAccountFlowMapper;
import cn.hawy.quick.modular.api.mapper.TMchCashFlowMapper;
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
 * @since 2019-07-12
 */
@Service
public class TMchCashFlowService extends ServiceImpl<TMchCashFlowMapper, TMchCashFlow> {

	@Autowired
	TDeptAccountFlowMapper deptAccountFlowMapper;
	
	public TMchCashFlow findByMchIdAndOutTradeNo(String mchId,String outTradeNo) {
		TMchCashFlow mchCashFlow = new TMchCashFlow();
		mchCashFlow.setMchId(mchId);
		mchCashFlow.setOutTradeNo(outTradeNo);
		return this.baseMapper.selectOne(new QueryWrapper<>(mchCashFlow));
	}
	
	@Transactional
	public boolean updateCashStatusSuccess(TMchCashFlow mchCashFlow,Dept dept) {
		int count = this.baseMapper.updateCashStatus(mchCashFlow.getCashId(), 2);
		if(count == 1) {
			if(mchCashFlow.getDeptAmount()>0) {
				//增加渠道商账户流水
				TDeptAccountFlow deptAccountFlow = new TDeptAccountFlow();
				deptAccountFlow.setDeptId(mchCashFlow.getDeptId());
				deptAccountFlow.setDeptName(dept.getSimpleName());
				deptAccountFlow.setBalance(dept.getBalance());
				deptAccountFlow.setAmount(mchCashFlow.getDeptAmount());
				deptAccountFlow.setBizType(2);
				deptAccountFlow.setDirection(1);
				deptAccountFlow.setTradeNo(mchCashFlow.getCashId());
				deptAccountFlow.setCreateTime(LocalDateTime.now());
				deptAccountFlowMapper.insert(deptAccountFlow);
				//增加渠道商账户余额
				deptAccountFlowMapper.addBalance(mchCashFlow.getDeptId(), mchCashFlow.getDeptAmount());
			}
			return true;
		}else {
			return false;
		}
	}
	
	@Transactional
	public boolean updateCashStatusSuccess(TMchCashFlow mchCashFlow,Dept dept,String returnMsg) {
		int count = this.baseMapper.updateCashStatusAndReturnMsg(mchCashFlow.getCashId(), 2, returnMsg);
		if(count == 1) {
			if(mchCashFlow.getDeptAmount()>0) {
				//增加渠道商账户流水
				TDeptAccountFlow deptAccountFlow = new TDeptAccountFlow();
				deptAccountFlow.setDeptId(mchCashFlow.getDeptId());
				deptAccountFlow.setDeptName(dept.getSimpleName());
				deptAccountFlow.setBalance(dept.getBalance());
				deptAccountFlow.setAmount(mchCashFlow.getDeptAmount());
				deptAccountFlow.setBizType(2);
				deptAccountFlow.setDirection(1);
				deptAccountFlow.setTradeNo(mchCashFlow.getCashId());
				deptAccountFlow.setCreateTime(LocalDateTime.now());
				deptAccountFlowMapper.insert(deptAccountFlow);
				//增加渠道商账户余额
				deptAccountFlowMapper.addBalance(mchCashFlow.getDeptId(), mchCashFlow.getDeptAmount());
			}
			return true;
		}else {
			return false;
		}
	}
	
	
	public boolean updateCashStatusFail(Long cashId) {
		//修改订单为失败状态
		int count = this.baseMapper.updateCashStatus(cashId, 3);
		if(count == 1) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean updateCashStatusFail(Long cashId,String returnMsg) {
		//修改订单为失败状态
		int count = this.baseMapper.updateCashStatusAndReturnMsg(cashId, 3, returnMsg);
		if(count == 1) {
			return true;
		}else {
			return false;
		}
	}
	
	public int updateNotifyCount(Long cashId,int notifyCount,String notifyResult) {
		int count = this.baseMapper.updateNotifyCount(cashId, notifyCount, notifyResult);
		return count;
	}
	
	
	public List<Map<String, Object>> findAll(Page page, String join, String deptId, String beginTime, String endTime,String cashId, String outTradeNo, String cashStatus, String mchName, String bankCardNo){
		return this.baseMapper.findAll(page, join, deptId, beginTime, endTime,cashId, outTradeNo, cashStatus, mchName, bankCardNo);
	}

	public List<TMchCashFlow> find(String join, String deptId, String beginTime, String endTime,String cashId, String outTradeNo, String cashStatus, String mchName, String bankCardNo){
		return this.baseMapper.find(join, deptId, beginTime, endTime,cashId, outTradeNo, cashStatus, mchName, bankCardNo);
	}
	
	public Map<String, Object> tongji(String beginTime, String endTime,String deptId,Integer cashStatus) {
		return this.baseMapper.tongji(beginTime, endTime, deptId, cashStatus);
	}
	
}
