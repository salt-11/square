package cn.hawy.quick.agent.modular.system.mapper;

import cn.hawy.quick.agent.modular.system.entity.User;
import cn.stylefeng.roses.core.datascope.DataScope;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 管理员表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-07
 */
public interface UserMapper extends BaseMapper<User> {


    /**
     * 通过账号获取用户
     */
    User getByAccount(@Param("account") String account);

    List<String> getDeptIdsByUserId(@Param("userId") String userId);

}
