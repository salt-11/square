package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.TFestivalHoliday;
import cn.hawy.quick.modular.api.mapper.TFestivalHolidayMapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hawy
 * @since 2019-07-31
 */
@Service
public class TFestivalHolidayService extends ServiceImpl<TFestivalHolidayMapper, TFestivalHoliday> {

	public boolean isOpenFlag() {
		TFestivalHoliday entity = new TFestivalHoliday();
		entity.setSysDate(LocalDate.now());
		TFestivalHoliday festivalHoliday = this.baseMapper.selectOne(new QueryWrapper<>(entity));
		if(festivalHoliday == null) {
			return false;
		}else {
			if(festivalHoliday.getOpenFlag() == 1) {
				return true;
			}else {
				return false;
			}
		}
	}
	
	
}
