package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.TDeptAccountFlow;
import cn.hawy.quick.modular.api.mapper.TDeptAccountFlowMapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hawy
 * @since 2019-07-11
 */
@Service
public class TDeptAccountFlowService extends ServiceImpl<TDeptAccountFlowMapper, TDeptAccountFlow> {

	public List<Map<String, Object>> findAll(Page page,  String beginTime, String endTime, String id, String bizTypeName, String directionName){
		return this.baseMapper.findAll(page, beginTime, endTime, id, bizTypeName, directionName);
	}

	public List<TDeptAccountFlow> find(String join, String beginTime, String endTime, String deptId, String deptType, String bizTypeName, String directionName){
		return this.baseMapper.find(join, beginTime, endTime, deptId, deptType, bizTypeName, directionName);
	}
}
