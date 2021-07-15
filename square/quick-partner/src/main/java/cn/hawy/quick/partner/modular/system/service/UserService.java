package cn.hawy.quick.partner.modular.system.service;

import cn.hawy.quick.partner.core.common.exception.BizExceptionEnum;
import cn.hutool.core.bean.BeanUtil;
import cn.hawy.quick.partner.core.common.node.MenuNode;
import cn.hawy.quick.partner.core.shiro.ShiroKit;
import cn.hawy.quick.partner.core.shiro.ShiroUser;
import cn.hawy.quick.partner.core.shiro.service.UserAuthService;
import cn.hawy.quick.partner.core.util.ApiMenuFilter;
import cn.hawy.quick.partner.modular.system.entity.User;
import cn.hawy.quick.partner.modular.system.mapper.UserMapper;

import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 管理员表 服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-07
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserAuthService userAuthService;

    /**
     * 修改密码
     *
     * @author fengshuonan
     * @Date 2018/12/24 22:45
     */
    public void changePwd(String oldPassword, String newPassword) {
        String userId = ShiroKit.getUserNotNull().getId();
        User user = this.getById(userId);

        String oldMd5 = ShiroKit.md5(oldPassword, user.getSalt());

        if (user.getPassword().equals(oldMd5)) {
            String newMd5 = ShiroKit.md5(newPassword, user.getSalt());
            user.setPassword(newMd5);
            this.updateById(user);
        } else {
            throw new ServiceException(BizExceptionEnum.OLD_PWD_NOT_RIGHT);
        }
    }



    /**
     * 通过账号获取用户
     *
     * @author fengshuonan
     * @Date 2018/12/24 22:46
     */
    public User getByAccount(String account) {
        return this.baseMapper.getByAccount(account);
    }

    /**
     * 获取用户菜单列表
     *
     * @author fengshuonan
     * @Date 2018/12/24 22:46
     */
    public List<MenuNode> getUserMenuNodes() {
            List<MenuNode> menus = menuService.getMenusByRoleIds();
            List<MenuNode> titles = MenuNode.buildTitle(menus);
            return ApiMenuFilter.build(titles);
    }

    /**
     * 刷新当前登录用户的信息
     *
     * @author fengshuonan
     * @Date 2019/1/19 5:59 PM
     */
    public void refreshCurrentUser() {
        ShiroUser user = ShiroKit.getUserNotNull();
        String id = user.getId();
        User currentUser = this.getById(id);
        ShiroUser shiroUser = userAuthService.shiroUser(currentUser);
        ShiroUser lastUser = ShiroKit.getUser();
        BeanUtil.copyProperties(shiroUser, lastUser);
    }

}
