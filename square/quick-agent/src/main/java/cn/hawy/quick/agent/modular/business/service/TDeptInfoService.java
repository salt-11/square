package cn.hawy.quick.agent.modular.business.service;


import cn.hawy.quick.agent.modular.business.entity.TDeptInfo;
import cn.hawy.quick.agent.modular.business.mapper.TDeptInfoMapper;
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

    public List<Map<String, Object>> findAll(Page page, String agentId){
        return this.baseMapper.findAll(page, agentId);
    }



}
