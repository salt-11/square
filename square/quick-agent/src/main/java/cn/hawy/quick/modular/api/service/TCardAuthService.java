package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.modular.api.entity.TCardAuth;
import cn.hawy.quick.modular.api.entity.TDeptAccountFlow;
import cn.hawy.quick.modular.api.mapper.TCardAuthMapper;

import cn.hawy.quick.modular.api.mapper.TDeptAccountFlowMapper;
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
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hawy
 * @since 2019-07-15
 */
@Service
public class TCardAuthService extends ServiceImpl<TCardAuthMapper, TCardAuth> {

	@Autowired
	TDeptAccountFlowMapper deptAccountFlowMapper;
	@Autowired
	DeptMapper deptMapper;

	public List<Map<String, Object>> findAll(Page page, String join, String beginTime, String endTime, String deptId){
		return this.baseMapper.findAll(page, join, beginTime, endTime,deptId);
	}

	public boolean updateStatus(Integer id,Integer newStatus,Integer oldStatus){
		TCardAuth cardAuth = new TCardAuth();
		cardAuth.setStatus(newStatus);

		UpdateWrapper<TCardAuth> updateWrapper = new UpdateWrapper<>();
		updateWrapper.eq("id",id);
		updateWrapper.eq("status",oldStatus);
		return this.update(cardAuth,updateWrapper);
	}


	@Transactional
	public boolean updateStatusSuccess(Integer id,Integer status,Long deptId,Long amount){
		boolean flag = updateStatus(id,status,0);
		if(flag){
			Dept dept = deptMapper.selectById(deptId);
			//增加渠道商账户流水
			TDeptAccountFlow deptAccountFlow = new TDeptAccountFlow();
			deptAccountFlow.setDeptId(dept.getDeptId().toString());
			deptAccountFlow.setDeptName(dept.getSimpleName());
			deptAccountFlow.setBalance(dept.getBalance());
			deptAccountFlow.setAmount(amount);
			deptAccountFlow.setBizType(5);
			deptAccountFlow.setDirection(2);
			deptAccountFlow.setTradeNo(id.longValue());
			deptAccountFlow.setCreateTime(LocalDateTime.now());
			deptAccountFlowMapper.insert(deptAccountFlow);
			int count = deptMapper.minusBalance(deptId, amount);
			if(count == 0){
				throw new RestException(401, "渠道商余额不足!");
			}
			return true;
		}else {
			return false;
		}
	}

	@Transactional
	public boolean updateStatusFail(Integer id,Integer status){
		return updateStatus(id,status,0);
	}
	
	public Map<String, Object> tongji(String beginTime, String endTime,String deptId) {
		return this.baseMapper.tongji(beginTime, endTime, deptId);
	}
}
