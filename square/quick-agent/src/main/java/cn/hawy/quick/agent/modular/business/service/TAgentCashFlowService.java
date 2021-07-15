package cn.hawy.quick.agent.modular.business.service;


import cn.hawy.quick.agent.modular.business.entity.TAgentCashFlow;
import cn.hawy.quick.agent.modular.business.mapper.TAgentCashFlowMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hawy
 * @since 2021-07-13
 */
@Service
public class TAgentCashFlowService extends ServiceImpl<TAgentCashFlowMapper, TAgentCashFlow> {

    public List<Map<String, Object>> findAll(Page page, String beginTime, String endTime, String cashStatusName, String agentId, String name){
        return this.baseMapper.findAll(page, beginTime, endTime, cashStatusName, agentId, name);
    }

    public List<TAgentCashFlow> find(String beginTime, String endTime, String deptType, String cashStatusName, String agentId, String name){
        return this.baseMapper.find(beginTime, endTime, deptType, cashStatusName, agentId, name);
    }

}
