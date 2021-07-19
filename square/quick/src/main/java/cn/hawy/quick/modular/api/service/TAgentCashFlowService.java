package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.*;
import cn.hawy.quick.modular.api.mapper.TAgentAccountFlowMapper;
import cn.hawy.quick.modular.api.mapper.TAgentCashFlowMapper;
import cn.hawy.quick.modular.api.mapper.TAgentInfoMapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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

    @Autowired
    private TAgentInfoMapper agentInfoMapper;
    @Autowired
    private TAgentAccountFlowMapper agentAccountFlowMapper;

    public List<Map<String, Object>> findAll(Page page, String beginTime, String endTime, String cashStatusName, String agentId, String name){
        return this.baseMapper.findAll(page, beginTime, endTime, cashStatusName, agentId, name);
    }

    public List<TAgentCashFlow> find(String beginTime, String endTime, String cashStatusName, String agentId, String name){
        return this.baseMapper.find(beginTime, endTime, cashStatusName, agentId, name);
    }

    @Transactional
    public void updateCashStatus(TAgentCashFlow agentCashFlow) {
        this.baseMapper.updateById(agentCashFlow);
    }

    @Transactional
    public void refuse(String id) {
        TAgentCashFlow agentCashFlow = baseMapper.selectById(id);
        agentCashFlow.setCashStatus(3);
        UpdateWrapper<TAgentCashFlow> updateWrapper = new UpdateWrapper<TAgentCashFlow>();
        updateWrapper.eq("id", agentCashFlow.getId());
        updateWrapper.eq("cash_status", 1);
        Boolean flag = this.update(agentCashFlow, updateWrapper);
        if(flag) {
            TAgentInfo agent = agentInfoMapper.selectById(agentCashFlow.getAgentId());
            //增加渠道商账户流水
            TAgentAccountFlow agentAccountFlow = new TAgentAccountFlow();
            agentAccountFlow.setAgentId(agentCashFlow.getAgentId());
            agentAccountFlow.setAgentName(agent.getAgentName());
            agentAccountFlow.setBalance(agent.getBalance());
            agentAccountFlow.setAmount(agentCashFlow.getCashAmount());
            agentAccountFlow.setBizType(4);
            agentAccountFlow.setDirection(1);
            agentAccountFlow.setTradeNo(agentCashFlow.getId().longValue());
            agentAccountFlow.setCreateTime(LocalDateTime.now());
            agentAccountFlowMapper.insert(agentAccountFlow);
            //增加渠道商账户余额
            agentInfoMapper.addBalance(agentCashFlow.getAgentId(), agentCashFlow.getCashAmount());
        }

    }

}
