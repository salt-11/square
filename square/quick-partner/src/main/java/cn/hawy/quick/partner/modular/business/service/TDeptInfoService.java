package cn.hawy.quick.partner.modular.business.service;

import cn.hawy.quick.partner.modular.business.entity.TDeptAccountFlow;
import cn.hawy.quick.partner.modular.business.entity.TDeptCashFlow;
import cn.hawy.quick.partner.modular.business.entity.TDeptInfo;
import cn.hawy.quick.partner.modular.business.mapper.TDeptAccountFlowMapper;
import cn.hawy.quick.partner.modular.business.mapper.TDeptCashFlowMapper;
import cn.hawy.quick.partner.modular.business.mapper.TDeptInfoMapper;
import cn.hawy.quick.partner.modular.system.model.DeptDto;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    @Autowired
    TDeptCashFlowMapper deptCashFlowMapper;
    @Autowired
    TDeptAccountFlowMapper deptAccountFlowMapper;

    @Transactional
    public void deptCash(DeptDto deptDto) {
        //增加提现流水
        TDeptCashFlow deptCashFlow = new TDeptCashFlow();
        deptCashFlow.setDeptId(String.valueOf(deptDto.getDeptId()));
        deptCashFlow.setDeptName(deptDto.getFullName());
        //partnerCashFlow.setOutTradeNo(partnerCashDto.getOutTradeNo());
        deptCashFlow.setCashAmount(deptDto.getCashAmount());
        deptCashFlow.setCashStatus(1);
        deptCashFlow.setCashRate(deptDto.getCashRate());
        deptCashFlow.setCashFee(deptDto.getCashFee());
        deptCashFlow.setOutAmount(deptDto.getOutAmount());
        deptCashFlow.setName(deptDto.getName());
        deptCashFlow.setCardNo(deptDto.getCardNo());
        deptCashFlow.setBankName(deptDto.getBankName());
        //partnerCashFlow.setNotifyUrl(partnerCashDto.getNotifyUrl());
        deptCashFlow.setCreateTime(LocalDateTime.now());
        deptCashFlowMapper.insert(deptCashFlow);
        //增加渠道商账户流水
        TDeptAccountFlow deptAccountFlow = new TDeptAccountFlow();
        deptAccountFlow.setDeptId(String.valueOf(deptDto.getDeptId()));
        deptAccountFlow.setDeptName(deptDto.getFullName());
        deptAccountFlow.setBalance(deptDto.getBalance());
        deptAccountFlow.setAmount(deptDto.getCashAmount());
        deptAccountFlow.setBizType(3);
        deptAccountFlow.setDirection(2);
        deptAccountFlow.setTradeNo(deptCashFlow.getId().longValue());
        deptAccountFlow.setCreateTime(LocalDateTime.now());
        deptAccountFlowMapper.insert(deptAccountFlow);
        //减少渠道商余额
        int count = this.baseMapper.minusBalance(deptDto.getDeptId(), deptDto.getCashAmount());
        if(count == 0) {
            throw new ServiceException(400, "渠道商账户余额不足!");
        }
    }

}
