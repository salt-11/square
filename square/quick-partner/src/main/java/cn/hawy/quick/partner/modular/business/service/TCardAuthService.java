package cn.hawy.quick.partner.modular.business.service;

import cn.hawy.quick.partner.modular.business.entity.TCardAuth;
import cn.hawy.quick.partner.modular.business.mapper.TCardAuthMapper;
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
public class TCardAuthService extends ServiceImpl<TCardAuthMapper, TCardAuth> {

	public List<Map<String, Object>> findAll(Page page, String join, String beginTime, String endTime, String deptId){
		return this.baseMapper.findAll(page, join, beginTime, endTime,deptId);
	}

	public Map<String, Object> tongji(String beginTime, String endTime,String deptId) {
		return this.baseMapper.tongji(beginTime, endTime, deptId);
	}
}
