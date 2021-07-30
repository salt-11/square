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
package cn.hawy.quick.core.common.constant.factory;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hawy.quick.core.common.constant.cache.Cache;
import cn.hawy.quick.core.common.constant.cache.CacheKey;
import cn.hawy.quick.core.common.constant.state.ManagerStatus;
import cn.hawy.quick.core.common.constant.state.MenuStatus;
import cn.hawy.quick.core.log.LogObjectHolder;
import cn.hawy.quick.modular.system.entity.*;
import cn.hawy.quick.modular.system.mapper.*;
import cn.stylefeng.roses.core.util.SpringContextHolder;
import cn.stylefeng.roses.core.util.ToolUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 常量的生产工厂
 *
 * @author fengshuonan
 * @date 2017年2月13日 下午10:55:21
 */
@Component
@DependsOn("springContextHolder")
public class ConstantFactory implements IConstantFactory {

    private RoleMapper roleMapper = SpringContextHolder.getBean(RoleMapper.class);
    private DeptMapper deptMapper = SpringContextHolder.getBean(DeptMapper.class);
    private DictMapper dictMapper = SpringContextHolder.getBean(DictMapper.class);
    private UserMapper userMapper = SpringContextHolder.getBean(UserMapper.class);
    private MenuMapper menuMapper = SpringContextHolder.getBean(MenuMapper.class);
    private NoticeMapper noticeMapper = SpringContextHolder.getBean(NoticeMapper.class);

    public static IConstantFactory me() {
        return SpringContextHolder.getBean("constantFactory");
    }

    @Override
    public String getUserNameById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            return user.getName();
        } else {
            return "--";
        }
    }

    @Override
    public String getUserAccountById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            return user.getAccount();
        } else {
            return "--";
        }
    }

    @Override
    @Cacheable(value = Cache.CONSTANT, key = "'" + CacheKey.ROLES_NAME + "'+#roleIds")
    public String getRoleName(String roleIds) {
        if (ToolUtil.isEmpty(roleIds)) {
            return "";
        }
        Long[] roles = Convert.toLongArray(roleIds);
        StringBuilder sb = new StringBuilder();
        for (Long role : roles) {
            Role roleObj = roleMapper.selectById(role);
            if (ToolUtil.isNotEmpty(roleObj) && ToolUtil.isNotEmpty(roleObj.getName())) {
                sb.append(roleObj.getName()).append(",");
            }
        }
        return StrUtil.removeSuffix(sb.toString(), ",");
    }

    @Override
    @Cacheable(value = Cache.CONSTANT, key = "'" + CacheKey.SINGLE_ROLE_NAME + "'+#roleId")
    public String getSingleRoleName(Long roleId) {
        if (0 == roleId) {
            return "--";
        }
        Role roleObj = roleMapper.selectById(roleId);
        if (ToolUtil.isNotEmpty(roleObj) && ToolUtil.isNotEmpty(roleObj.getName())) {
            return roleObj.getName();
        }
        return "";
    }

    @Override
    @Cacheable(value = Cache.CONSTANT, key = "'" + CacheKey.SINGLE_ROLE_TIP + "'+#roleId")
    public String getSingleRoleTip(Long roleId) {
        if (0 == roleId) {
            return "--";
        }
        Role roleObj = roleMapper.selectById(roleId);
        if (ToolUtil.isNotEmpty(roleObj) && ToolUtil.isNotEmpty(roleObj.getName())) {
            return roleObj.getDescription();
        }
        return "";
    }

    @Override
    @Cacheable(value = Cache.CONSTANT, key = "'" + CacheKey.DEPT_NAME + "'+#deptId")
    public String getDeptName(Long deptId) {
        if (deptId == null) {
            return "";
        } else if (deptId == 0L) {
            return "顶级";
        } else {
        	Dept dept = deptMapper.selectById(deptId);
            if (ToolUtil.isNotEmpty(dept) && ToolUtil.isNotEmpty(dept.getFullName())) {
                return dept.getFullName();
            }
            return "";
        }
    }

    @Override
    public String getMenuNames(String menuIds) {
        Long[] menus = Convert.toLongArray(menuIds);
        StringBuilder sb = new StringBuilder();
        for (Long menu : menus) {
            Menu menuObj = menuMapper.selectById(menu);
            if (ToolUtil.isNotEmpty(menuObj) && ToolUtil.isNotEmpty(menuObj.getName())) {
                sb.append(menuObj.getName()).append(",");
            }
        }
        return StrUtil.removeSuffix(sb.toString(), ",");
    }

    @Override
    public String getMenuName(Long menuId) {
        if (ToolUtil.isEmpty(menuId)) {
            return "";
        } else {
            Menu menu = menuMapper.selectById(menuId);
            if (menu == null) {
                return "";
            } else {
                return menu.getName();
            }
        }
    }

    @Override
    public Menu getMenuByCode(String code) {
        if (ToolUtil.isEmpty(code)) {
            return new Menu();
        } else if (code.equals("0")) {
            return new Menu();
        } else {
            Menu param = new Menu();
            param.setCode(code);
            QueryWrapper<Menu> queryWrapper = new QueryWrapper<>(param);
            Menu menu = menuMapper.selectOne(queryWrapper);
            if (menu == null) {
                return new Menu();
            } else {
                return menu;
            }
        }
    }

    @Override
    public String getMenuNameByCode(String code) {
        if (ToolUtil.isEmpty(code)) {
            return "";
        } else if (code.equals("0")) {
            return "顶级";
        } else {
            Menu param = new Menu();
            param.setCode(code);
            QueryWrapper<Menu> queryWrapper = new QueryWrapper<>(param);
            Menu menu = menuMapper.selectOne(queryWrapper);
            if (menu == null) {
                return "";
            } else {
                return menu.getName();
            }
        }
    }

    @Override
    public Long getMenuIdByCode(String code) {
        if (ToolUtil.isEmpty(code)) {
            return 0L;
        } else if (code.equals("0")) {
            return 0L;
        } else {
            Menu menu = new Menu();
            menu.setCode(code);
            QueryWrapper<Menu> queryWrapper = new QueryWrapper<>(menu);
            Menu tempMenu = this.menuMapper.selectOne(queryWrapper);
            return tempMenu.getMenuId();
        }
    }

    @Override
    public String getDictName(Long dictId) {
        if (ToolUtil.isEmpty(dictId)) {
            return "";
        } else {
            Dict dict = dictMapper.selectById(dictId);
            if (dict == null) {
                return "";
            } else {
                return dict.getName();
            }
        }
    }

    @Override
    public String getNoticeTitle(Long dictId) {
        if (ToolUtil.isEmpty(dictId)) {
            return "";
        } else {
            Notice notice = noticeMapper.selectById(dictId);
            if (notice == null) {
                return "";
            } else {
                return notice.getTitle();
            }
        }
    }

    @Override
    public String getDictsByName(String name, String code) {
        Dict temp = new Dict();
        temp.setName(name);
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>(temp);
        Dict dict = dictMapper.selectOne(queryWrapper);
        if (dict == null) {
            return "";
        } else {
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper = wrapper.eq("PID", dict.getDictId());
            List<Dict> dicts = dictMapper.selectList(wrapper);
            for (Dict item : dicts) {
                if (item.getCode() != null && item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
    }
    
    @Override
    public String getDictsByCode(String pcode, String code) {
        Dict temp = new Dict();
        temp.setCode(pcode);
       // temp.setName(name);
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>(temp);
        Dict dict = dictMapper.selectOne(queryWrapper);
        if (dict == null) {
            return "";
        } else {
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper = wrapper.eq("PID", dict.getDictId());
            List<Dict> dicts = dictMapper.selectList(wrapper);
            for (Dict item : dicts) {
                if (item.getCode() != null && item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
    }
    

    @Override
    @Cacheable(value = Cache.CONSTANT, key = "methodName +#sexCode")
    public String getSexName(String sexCode) {
        return getDictsByCode("SEX", sexCode);
    }

    @Override
    public String getStatusName(String status) {
        return ManagerStatus.getDescription(status);
    }

    @Override
    public String getMenuStatusName(String status) {
        return MenuStatus.getDescription(status);
    }

    @Override
    public List<Dict> findInDict(Long id) {
        if (ToolUtil.isEmpty(id)) {
            return null;
        } else {
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            List<Dict> dicts = dictMapper.selectList(wrapper.eq("PID", id));
            if (dicts == null || dicts.size() == 0) {
                return null;
            } else {
                return dicts;
            }
        }
    }

    @Override
    public String getCacheObject(String para) {
        return LogObjectHolder.me().get().toString();
    }

    @Override
    public List<Long> getSubDeptId(Long deptId) {
        QueryWrapper<Dept> wrapper = new QueryWrapper<>();
        wrapper = wrapper.like("PIDS", "%[" + deptId + "]%");
        List<Dept> depts = this.deptMapper.selectList(wrapper);

        ArrayList<Long> deptids = new ArrayList<>();

        if (depts != null && depts.size() > 0) {
            for (Dept dept : depts) {
                deptids.add(dept.getDeptId());
            }
        }

        return deptids;
    }

    @Override
    public List<Long> getParentDeptIds(Long deptId) {
    	Dept dept = deptMapper.selectById(deptId);
        String pids = dept.getPids();
        String[] split = pids.split(",");
        ArrayList<Long> parentDeptIds = new ArrayList<>();
        for (String s : split) {
            parentDeptIds.add(Long.valueOf(StrUtil.removeSuffix(StrUtil.removePrefix(s, "["), "]")));
        }
        return parentDeptIds;
    }

	@Override
	@Cacheable(value = Cache.CONSTANT, key = "methodName +#mchTypeCode")
	public String getMchTypeName(String mchTypeCode) {
		// TODO Auto-generated method stub
		return getDictsByCode("MchType", mchTypeCode);
	}
	
	@Override
	@Cacheable(value = Cache.CONSTANT, key = "methodName +#bizTypeCode")
	public String getMchBizTypeName(String bizTypeCode) {
		// TODO Auto-generated method stub
		return getDictsByCode("MchBizType", bizTypeCode);
	}
	
	@Override
	@Cacheable(value = Cache.CONSTANT, key = "methodName +#bizTypeCode")
	public String getDeptBizTypeName(String bizTypeCode) {
		// TODO Auto-generated method stub
		return getDictsByCode("DeptBizType", bizTypeCode);
	}
	
	@Override
	@Cacheable(value = Cache.CONSTANT, key = "methodName +#direction")
	public String getMchDirectionName(String direction) {
		// TODO Auto-generated method stub
		return getDictsByCode("MchDirection", direction);
	}
	
	@Override
	@Cacheable(value = Cache.CONSTANT, key = "methodName +#direction")
	public String getDeptDirectionName(String direction) {
		// TODO Auto-generated method stub
		return getDictsByCode("DeptDirection", direction);
	}
	
	@Override
    @Cacheable(value = Cache.CONSTANT, key = "methodName +#cashStatus")
	public String getMchCashStatusName(String cashStatus) {
		// TODO Auto-generated method stub
		return getDictsByCode("MchCashStatus", cashStatus);
	}
	
	@Override
	@Cacheable(value = Cache.CONSTANT, key = "methodName +#cashStatus")
	public String getDeptCashStatusName(String cashStatus) {
		// TODO Auto-generated method stub
		return getDictsByCode("DeptCashStatus", cashStatus);
	}
	
	@Override
	@Cacheable(value = Cache.CONSTANT, key = "methodName +#userType")
	public String getUserTypeName(String userType) {
		// TODO Auto-generated method stub
		return getDictsByCode("UserType", userType);
	}

}
