package cn.hawy.quick.modular.api.mapper;

import cn.hawy.quick.modular.api.entity.TMchCard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商户表 Mapper 接口
 * </p>
 *
 * @author hawy
 * @since 2019-07-09
 */
public interface TMchCardMapper extends BaseMapper<TMchCard> {
    List<Map<String, Object>> findAll(@Param("page") Page page, @Param("join") String join, @Param("mchId") String mchId, @Param("bankCardNo") String bankCardNo);

}
