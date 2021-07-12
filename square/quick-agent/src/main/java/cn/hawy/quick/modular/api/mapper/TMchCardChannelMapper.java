package cn.hawy.quick.modular.api.mapper;

import cn.hawy.quick.modular.api.entity.TMchCardChannel;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 商户表 Mapper 接口
 * </p>
 *
 * @author hawy
 * @since 2019-07-09
 */
public interface TMchCardChannelMapper extends BaseMapper<TMchCardChannel> {

	int updateStatus(@Param("status") Integer status,@Param("cardId") Integer cardId,@Param("channel") String channel);

	int updateNotifyCount(@Param("id") Integer id, @Param("notifyCount") int notifyCount,@Param("notifyResult") String notifyResult);

}
