package cn.hawy.quick.agent.modular.system.service;

import cn.hawy.quick.agent.core.common.node.MenuNode;
import cn.hawy.quick.agent.core.listener.ConfigListener;
import cn.hawy.quick.agent.modular.system.entity.Menu;
import cn.hawy.quick.agent.modular.system.mapper.MenuMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-07
 */
@Service
public class MenuService extends ServiceImpl<MenuMapper, Menu> {



    /**
     * 根据角色获取菜单
     *
     * @param
     * @return
     * @date 2017年2月19日 下午10:35:40
     */
    public List<MenuNode> getMenusByRoleIds() {
        List<MenuNode> menus = this.baseMapper.getMenusByRoleIds();

        //给所有的菜单url加上ctxPath
        for (MenuNode menuItem : menus) {
            menuItem.setUrl(ConfigListener.getConf().get("contextPath") + menuItem.getUrl());
        }

        return menus;
    }

}
