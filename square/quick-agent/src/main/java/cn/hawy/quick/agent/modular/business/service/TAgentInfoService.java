package cn.hawy.quick.agent.modular.business.service;


import cn.hawy.quick.agent.core.shiro.ShiroKit;
import cn.hawy.quick.agent.core.shiro.ShiroUser;
import cn.hawy.quick.agent.core.util.PayUtil;
import cn.hawy.quick.agent.modular.business.entity.TAgentInfo;
import cn.hawy.quick.agent.modular.business.entity.TAgentAccountFlow;
import cn.hawy.quick.agent.modular.business.entity.TAgentCashFlow;
import cn.hawy.quick.agent.modular.business.mapper.TAgentInfoMapper;
import cn.hawy.quick.agent.modular.business.mapper.TAgentAccountFlowMapper;
import cn.hawy.quick.agent.modular.business.mapper.TAgentCashFlowMapper;
import cn.hutool.core.util.NumberUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 代理商信息表 服务实现类
 * </p>
 *
 * @author hawy
 * @since 2021-07-13
 */
@Service
public class TAgentInfoService extends ServiceImpl<TAgentInfoMapper, TAgentInfo> {

    @Autowired
    TAgentCashFlowMapper agentCashFlowMapper;
    @Autowired
    TAgentAccountFlowMapper agentAccountFlowMapper;


    @Transactional
    public void agentCash(String cashAmount) {
        ShiroUser shiroUser = ShiroKit.getUserNotNull();
        TAgentInfo agent = this.baseMapper.selectById(shiroUser.getId());
        cashAmount = PayUtil.transYuanToFen(cashAmount);
        if (NumberUtil.parseLong(cashAmount) > agent.getBalance()) {
            throw new ServiceException(400, "代理商账户余额不足!");
        }
        String cashRate = "0";
        Long cashFee = NumberUtil.parseLong("0");
        //增加提现流水
        TAgentCashFlow agentCashFlow = new TAgentCashFlow();
        agentCashFlow.setAgentId(agent.getId());
        agentCashFlow.setAgentName(agent.getAgentName());
        agentCashFlow.setCashAmount(NumberUtil.parseLong(cashAmount));
        agentCashFlow.setCashStatus(1);
        agentCashFlow.setCashRate(cashRate);
        agentCashFlow.setCashFee(cashFee);
        agentCashFlow.setOutAmount(NumberUtil.parseLong(cashAmount));
        agentCashFlow.setName(agent.getName());
        agentCashFlow.setCardNo(agent.getCardNo());
        agentCashFlow.setBankName(agent.getBankName());
        agentCashFlow.setCreateTime(LocalDateTime.now());
        agentCashFlowMapper.insert(agentCashFlow);
        //增加渠道商账户流水
        TAgentAccountFlow agentAccountFlow = new TAgentAccountFlow();
        agentAccountFlow.setAgentId(agent.getId());
        agentAccountFlow.setAgentName(agent.getAgentName());
        agentAccountFlow.setBalance(agent.getBalance());
        agentAccountFlow.setAmount(NumberUtil.parseLong(cashAmount));
        agentAccountFlow.setBizType(3);
        agentAccountFlow.setDirection(2);
        agentAccountFlow.setTradeNo(agentCashFlow.getId().longValue());
        agentAccountFlow.setCreateTime(LocalDateTime.now());
        agentAccountFlowMapper.insert(agentAccountFlow);
        //减少渠道商余额
        int count = this.baseMapper.minusBalance(agent.getId(), NumberUtil.parseLong(cashAmount));
        if(count == 0) {
            throw new ServiceException(400, "渠道商账户余额不足!");
        }
    }

}
