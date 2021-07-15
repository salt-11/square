package cn.hawy.quick.partner.modular.business.service;


import cn.hawy.quick.partner.modular.business.entity.TDeptRateChannel;
import cn.hawy.quick.partner.modular.business.mapper.TDeptRateChannelMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
 * @since 2019-07-15
 */
@Service
public class TDeptRateChannelService extends ServiceImpl<TDeptRateChannelMapper, TDeptRateChannel> {

	public List<Map<String, Object>> findAll(Page page,String deptId,String channel){
		return this.baseMapper.findAll(page,deptId,channel);
	}
	
}
