package cn.hawy.quick.partner.modular.business.service;

import cn.hawy.quick.partner.modular.business.entity.TMchInfo;
import cn.hawy.quick.partner.modular.business.mapper.TMchInfoMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商户表 服务实现类
 * </p>
 *
 * @author hawy
 * @since 2019-07-09
 */
@Service
public class TMchInfoService extends ServiceImpl<TMchInfoMapper, TMchInfo> {
	

	
	public List<Map<String, Object>> findAll(Page page, String mchId, String beginTime, String endTime, String mchName, String id, String mobile){
		return this.baseMapper.findAll(page, mchId, beginTime, endTime, mchName, id, mobile);
	}

}
