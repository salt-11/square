package cn.hawy.quick.partner.modular.system.mapper;

import cn.hawy.quick.partner.core.common.node.MenuNode;
import cn.hawy.quick.partner.modular.system.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-07
 */
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 根据角色获取菜单
     *
     * @return
     * @date 2017年2月19日 下午10:35:40
     */
    List<MenuNode> getMenusByRoleIds();




}
