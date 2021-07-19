package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.TAgentAccountFlow;
import cn.hawy.quick.modular.api.mapper.TAgentAccountFlowMapper;
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
public class TAgentAccountFlowService extends ServiceImpl<TAgentAccountFlowMapper, TAgentAccountFlow> {

    public List<Map<String, Object>> findAll(Page page, String beginTime, String endTime, String agentId, String bizTypeName, String directionName){
        return this.baseMapper.findAll(page, beginTime, endTime, agentId, bizTypeName, directionName);
    }

    public List<TAgentAccountFlow> find(String beginTime, String endTime, String agentId, String bizTypeName, String directionName){
        return this.baseMapper.find(beginTime, endTime, agentId, bizTypeName, directionName);
    }

}
