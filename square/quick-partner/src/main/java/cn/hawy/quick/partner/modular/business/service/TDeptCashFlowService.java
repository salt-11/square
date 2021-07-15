package cn.hawy.quick.partner.modular.business.service;

import cn.hawy.quick.partner.modular.business.entity.TDeptCashFlow;
import cn.hawy.quick.partner.modular.business.mapper.TDeptCashFlowMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

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

	
	public List<Map<String, Object>> findAll(Page page, String beginTime, String endTime, String cashStatusName, String id, String name){
		return this.baseMapper.findAll(page, beginTime, endTime, cashStatusName, id, name);
	}

	public List<TDeptCashFlow> find(String join, String beginTime, String endTime, String deptType, String cashStatusName, String deptId, String name){
		return this.baseMapper.find(join, beginTime, endTime, deptType, cashStatusName, deptId, name);
	}

	@Transactional
	public void updateCashStatus(TDeptCashFlow deptCashFlow) {
		this.baseMapper.updateById(deptCashFlow);
	}
	

}
