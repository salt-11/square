package cn.hawy.quick.partner.modular.business.service;

import cn.hawy.quick.partner.core.shiro.ShiroKit;
import cn.hawy.quick.partner.core.shiro.ShiroUser;
import cn.hawy.quick.partner.core.util.PayUtil;
import cn.hawy.quick.partner.modular.business.entity.TDeptAccountFlow;
import cn.hawy.quick.partner.modular.business.entity.TDeptCashFlow;
import cn.hawy.quick.partner.modular.business.entity.TDeptInfo;
import cn.hawy.quick.partner.modular.business.mapper.TDeptAccountFlowMapper;
import cn.hawy.quick.partner.modular.business.mapper.TDeptCashFlowMapper;
import cn.hawy.quick.partner.modular.business.mapper.TDeptInfoMapper;
import cn.hawy.quick.partner.modular.system.model.DeptDto;
import cn.hutool.core.util.NumberUtil;
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
    @Autowired
    TDeptInfoMapper deptInfoMapper;

    @Transactional
    public void deptCash(String cashAmount) {
        ShiroUser shiroUser = ShiroKit.getUserNotNull();
        TDeptInfo dept = deptInfoMapper.selectById(shiroUser.getId());
        cashAmount = PayUtil.transYuanToFen(cashAmount);
        if (NumberUtil.parseLong(cashAmount) > dept.getBalance()) {
            throw new ServiceException(400, "渠道商账户余额不足!");
        }
        String cashRate = "0";
        Long cashFee = NumberUtil.parseLong("0");
        //增加提现流水
        TDeptCashFlow deptCashFlow = new TDeptCashFlow();
        deptCashFlow.setDeptId(dept.getId());
        deptCashFlow.setDeptName(dept.getDeptName());
        deptCashFlow.setCashAmount(NumberUtil.parseLong(cashAmount));
        deptCashFlow.setCashStatus(1);
        deptCashFlow.setCashRate(cashRate);
        deptCashFlow.setCashFee(cashFee);
        deptCashFlow.setOutAmount(NumberUtil.parseLong(cashAmount));
        deptCashFlow.setName(dept.getName());
        deptCashFlow.setCardNo(dept.getCardNo());
        deptCashFlow.setBankName(dept.getBankName());
        deptCashFlow.setCreateTime(LocalDateTime.now());
        deptCashFlowMapper.insert(deptCashFlow);
        //增加渠道商账户流水
        TDeptAccountFlow deptAccountFlow = new TDeptAccountFlow();
        deptAccountFlow.setDeptId(dept.getId());
        deptAccountFlow.setDeptName(dept.getDeptName());
        deptAccountFlow.setBalance(dept.getBalance());
        deptAccountFlow.setAmount(NumberUtil.parseLong(cashAmount));
        deptAccountFlow.setBizType(3);
        deptAccountFlow.setDirection(2);
        deptAccountFlow.setTradeNo(deptCashFlow.getId().longValue());
        deptAccountFlow.setCreateTime(LocalDateTime.now());
        deptAccountFlowMapper.insert(deptAccountFlow);
        //减少渠道商余额
        int count = this.baseMapper.minusBalance(dept.getId(), NumberUtil.parseLong(cashAmount));
        if(count == 0) {
            throw new ServiceException(400, "渠道商账户余额不足!");
        }
    }

}
