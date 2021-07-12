package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.TDeptAccountFlow;
import cn.hawy.quick.modular.api.entity.TDeptCashFlow;
import cn.hawy.quick.modular.api.mapper.TDeptAccountFlowMapper;
import cn.hawy.quick.modular.api.mapper.TDeptCashFlowMapper;
import cn.hawy.quick.modular.system.entity.Dept;
import cn.hawy.quick.modular.system.mapper.DeptMapper;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hawy
 * @since 2019-08-12
 */
@Service
public class TDeptCashFlowService extends ServiceImpl<TDeptCashFlowMapper, TDeptCashFlow> {
	
	@Autowired
	TDeptAccountFlowMapper deptAccountFlowMapper;
	
	@Autowired
	DeptMapper deptMapper;
	
	public List<Map<String, Object>> findAll(Page page, String join, String beginTime, String endTime, String deptType, String cashStatusName, String deptId, String name){
		return this.baseMapper.findAll(page, join, beginTime, endTime, deptType, cashStatusName, deptId, name);
	}

	public List<TDeptCashFlow> find(String join, String beginTime, String endTime, String deptType, String cashStatusName, String deptId, String name){
		return this.baseMapper.find(join, beginTime, endTime, deptType, cashStatusName, deptId, name);
	}

	@Transactional
	public void updateCashStatus(TDeptCashFlow deptCashFlow) {
		this.baseMapper.updateById(deptCashFlow);
	}
	
	
	@Transactional
	public void refuse(String id) {
		
		TDeptCashFlow deptCashFlow = baseMapper.selectById(id);
        deptCashFlow.setCashStatus(3);
        UpdateWrapper<TDeptCashFlow> updateWrapper = new UpdateWrapper<TDeptCashFlow>();
        updateWrapper.eq("id", deptCashFlow.getId());
        updateWrapper.eq("cash_status", 1);
        Boolean flag = this.update(deptCashFlow, updateWrapper);
        if(flag) {
        	Dept dept = deptMapper.selectById(deptCashFlow.getDeptId());
            //增加渠道商账户流水
    		TDeptAccountFlow deptAccountFlow = new TDeptAccountFlow();
    		deptAccountFlow.setDeptId(deptCashFlow.getDeptId());
    		deptAccountFlow.setDeptName(deptCashFlow.getDeptName());
    		deptAccountFlow.setBalance(dept.getBalance());
    		deptAccountFlow.setAmount(deptCashFlow.getCashAmount());
    		deptAccountFlow.setBizType(4);
    		deptAccountFlow.setDirection(1);
    		deptAccountFlow.setTradeNo(deptCashFlow.getId().longValue());
    		deptAccountFlow.setCreateTime(LocalDateTime.now());
    		deptAccountFlowMapper.insert(deptAccountFlow);
    		//增加渠道商账户余额
    		deptAccountFlowMapper.addBalance(deptCashFlow.getDeptId(), deptCashFlow.getCashAmount());
        }
        
	}
}
