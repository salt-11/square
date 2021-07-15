package cn.hawy.quick.partner.modular.business.service;

import cn.hawy.quick.partner.modular.business.entity.TMchCashFlow;
import cn.hawy.quick.partner.modular.business.mapper.TMchCashFlowMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
	
	public List<Map<String, Object>> findAll(Page page, String deptId, String beginTime, String endTime,String cashId, String outTradeNo, String cashStatus, String mchName, String bankCardNo){
		return this.baseMapper.findAll(page, deptId, beginTime, endTime,cashId, outTradeNo, cashStatus, mchName, bankCardNo);
	}

	public List<TMchCashFlow> find(String deptId, String beginTime, String endTime,String cashId, String outTradeNo, String cashStatus, String mchName, String bankCardNo){
		return this.baseMapper.find(deptId, beginTime, endTime,cashId, outTradeNo, cashStatus, mchName, bankCardNo);
	}
	
	public Map<String, Object> tongji(String beginTime, String endTime,String deptId,Integer cashStatus) {
		return this.baseMapper.tongji(beginTime, endTime, deptId, cashStatus);
	}
	
}
