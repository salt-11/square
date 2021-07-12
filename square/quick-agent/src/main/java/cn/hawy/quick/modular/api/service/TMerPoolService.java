package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.TMerPool;
import cn.hawy.quick.modular.api.mapper.TMerPoolMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hawy
 * @since 2019-08-06
 */
@Service
public class TMerPoolService extends ServiceImpl<TMerPoolMapper, TMerPool> {

	public List<TMerPool> findByPC(String provinceCode,String cityCode,Integer type){
		List<TMerPool> list =  this.baseMapper.findByPC(provinceCode, cityCode,type);
		if(list.size() == 0) {
			list = this.baseMapper.findByPC(provinceCode, null,type);
		}
		return list;
	}

	public List<TMerPool> findByPC(String cityCode,Integer type){
		List<TMerPool> list =  this.baseMapper.findByPC(cityCode, cityCode,type);
		if(list.size() == 0) {
			list = this.baseMapper.findByPC(cityCode.substring(0,2), cityCode.substring(0,2),type);
		}
		return list;
	}

}
