/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.hawy.quick.modular.system.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hawy.quick.core.common.annotion.BussinessLog;
import cn.hawy.quick.core.common.annotion.Permission;
import cn.hawy.quick.core.common.constant.Const;
import cn.hawy.quick.core.common.constant.dictmap.DeleteDict;
import cn.hawy.quick.core.common.constant.dictmap.MenuDict;
import cn.hawy.quick.core.common.constant.factory.ConstantFactory;
import cn.hawy.quick.core.common.exception.BizExceptionEnum;
import cn.hawy.quick.core.common.node.ZTreeNode;
import cn.hawy.quick.core.common.page.LayuiPageFactory;
import cn.hawy.quick.core.common.page.LayuiPageInfo;
import cn.hawy.quick.core.log.LogObjectHolder;
import cn.hawy.quick.modular.system.entity.Menu;
import cn.hawy.quick.modular.system.model.MenuDto;
import cn.hawy.quick.modular.system.service.MenuService;
import cn.hawy.quick.modular.system.service.UserService;
import cn.hawy.quick.modular.system.warpper.MenuWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * ???????????????
 *
 * @author fengshuonan
 * @Date 2017???2???12???21:59:14
 */
@Controller
@RequestMapping("/menu")
public class MenuController extends BaseController {

    private static String PREFIX = "/modular/system/menu/";

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;

    /**
     * ?????????????????????????????????
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:53 PM
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "menu.html";
    }

    /**
     * ?????????????????????????????????
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:53 PM
     */
    @RequestMapping(value = "/menu_add")
    public String menuAdd() {
        return PREFIX + "menu_add.html";
    }

    /**
     * ?????????????????????????????????
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:53 PM
     */
    @Permission(Const.ADMIN_NAME)
    @RequestMapping(value = "/menu_edit")
    public String menuEdit(@RequestParam Long menuId) {
        if (ToolUtil.isEmpty(menuId)) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }

        //??????????????????????????????????????????
        Menu menu = this.menuService.getById(menuId);
        LogObjectHolder.me().set(menu);

        return PREFIX + "menu_edit.html";
    }

    /**
     * ????????????
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:53 PM
     */
    @Permission(Const.ADMIN_NAME)
    @RequestMapping(value = "/edit")
    @BussinessLog(value = "????????????", key = "name", dict = MenuDict.class)
    @ResponseBody
    public ResponseData edit(MenuDto menu) {

        //????????????????????????????????????????????????????????????????????????
        this.menuService.updateMenu(menu);

        //????????????????????????
        this.userService.refreshCurrentUser();

        return SUCCESS_TIP;
    }

    /**
     * ??????????????????
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:53 PM
     */
    @Permission(Const.ADMIN_NAME)
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(@RequestParam(required = false) String menuName,
                       @RequestParam(required = false) String level,
                       @RequestParam(required = false) Long menuId) {
        Page<Map<String, Object>> menus = this.menuService.selectMenus(menuName, level, menuId);
        Page<Map<String, Object>> wrap = new MenuWrapper(menus).wrap();
        return LayuiPageFactory.createPageInfo(wrap);
    }

    /**
     * ?????????????????????s?????????
     *
     * @author fengshuonan
     * @Date 2019???2???23???22:01:47
     */
    @Permission(Const.ADMIN_NAME)
    @RequestMapping(value = "/listTree")
    @ResponseBody
    public Object listTree(@RequestParam(required = false) String menuName,
                           @RequestParam(required = false) String level) {
        List<Map<String, Object>> menus = this.menuService.selectMenuTree(menuName, level);
        List<Map<String, Object>> menusWrap = new MenuWrapper(menus).wrap();

        LayuiPageInfo result = new LayuiPageInfo();
        result.setData(menusWrap);
        return result;
    }

    /**
     * ????????????
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:53 PM
     */
    @Permission(Const.ADMIN_NAME)
    @RequestMapping(value = "/add")
    @BussinessLog(value = "????????????", key = "name", dict = MenuDict.class)
    @ResponseBody
    public ResponseData add(MenuDto menu) {
        this.menuService.addMenu(menu);
        return SUCCESS_TIP;
    }

    /**
     * ????????????
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:53 PM
     */
    @Permission(Const.ADMIN_NAME)
    @RequestMapping(value = "/remove")
    @BussinessLog(value = "????????????", key = "menuId", dict = DeleteDict.class)
    @ResponseBody
    public ResponseData remove(@RequestParam Long menuId) {
        if (ToolUtil.isEmpty(menuId)) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }

        //?????????????????????
        LogObjectHolder.me().set(ConstantFactory.me().getMenuName(menuId));

        this.menuService.delMenuContainSubMenus(menuId);

        return SUCCESS_TIP;
    }

    /**
     * ????????????
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:53 PM
     */
    @RequestMapping(value = "/view/{menuId}")
    @ResponseBody
    public ResponseData view(@PathVariable Long menuId) {
        if (ToolUtil.isEmpty(menuId)) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        Menu menu = this.menuService.getById(menuId);
        return ResponseData.success(menu);
    }

    /**
     * ??????????????????
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:53 PM
     */
    @RequestMapping(value = "/getMenuInfo")
    @ResponseBody
    public ResponseData getMenuInfo(@RequestParam Long menuId) {
        if (ToolUtil.isEmpty(menuId)) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }

        Menu menu = this.menuService.getById(menuId);

        MenuDto menuDto = new MenuDto();
        BeanUtil.copyProperties(menu, menuDto);

        //??????pid???????????????
        menuDto.setPid(ConstantFactory.me().getMenuIdByCode(menuDto.getPcode()));
        menuDto.setPcodeName(ConstantFactory.me().getMenuNameByCode(menuDto.getPcode()));

        return ResponseData.success(menuDto);
    }

    /**
     * ??????????????????(?????????)
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:54 PM
     */
    @RequestMapping(value = "/menuTreeList")
    @ResponseBody
    public List<ZTreeNode> menuTreeList() {
        return this.menuService.menuTreeList();
    }

    /**
     * ??????????????????(?????????????????????)
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:54 PM
     */
    @RequestMapping(value = "/selectMenuTreeList")
    @ResponseBody
    public List<ZTreeNode> selectMenuTreeList() {
        List<ZTreeNode> roleTreeList = this.menuService.menuTreeList();
        roleTreeList.add(ZTreeNode.createParent());
        return roleTreeList;
    }

    /**
     * ???????????????????????????
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:54 PM
     */
    @RequestMapping(value = "/menuTreeListByRoleId/{roleId}")
    @ResponseBody
    public List<ZTreeNode> menuTreeListByRoleId(@PathVariable Long roleId) {
        List<Long> menuIds = this.menuService.getMenuIdsByRoleId(roleId);
        if (ToolUtil.isEmpty(menuIds)) {
            return this.menuService.menuTreeList();
        } else {
            return this.menuService.menuTreeListByMenuIds(menuIds);
        }
    }

}
