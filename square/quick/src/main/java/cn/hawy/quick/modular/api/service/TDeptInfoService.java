package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.TDeptInfo;
import cn.hawy.quick.modular.api.mapper.TDeptInfoMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 渠道信息表 服务实现类
 * </p>
 *
 * @author hawy
 * @since 2021-07-13
 */
@Service
public class TDeptInfoService extends ServiceImpl<TDeptInfoMapper, TDeptInfo> {

    public List<Map<String, Object>> findAll(Page page, String  id, String account, String balance, String agentId, String beginTime, String endTime, String deptName) {
        return this.baseMapper.findAll(  page,  id, account, balance, agentId, beginTime, endTime, deptName );
    }



}
