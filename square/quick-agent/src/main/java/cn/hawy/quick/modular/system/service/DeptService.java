package cn.hawy.quick.modular.system.service;

import cn.hawy.quick.core.common.exception.BizExceptionEnum;
import cn.hawy.quick.core.common.node.TreeviewNode;
import cn.hawy.quick.core.common.node.ZTreeNode;
import cn.hawy.quick.core.common.page.LayuiPageFactory;
import cn.hawy.quick.modular.api.entity.TDeptAccountFlow;
import cn.hawy.quick.modular.api.entity.TDeptCashFlow;
import cn.hawy.quick.modular.api.mapper.TDeptAccountFlowMapper;
import cn.hawy.quick.modular.api.mapper.TDeptCashFlowMapper;
import cn.hawy.quick.modular.system.entity.Dept;
import cn.hawy.quick.modular.system.mapper.DeptMapper;
import cn.hawy.quick.modular.system.model.DeptDto;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-07
 */
@Service
public class DeptService extends ServiceImpl<DeptMapper, Dept> {

    @Resource
    private DeptMapper deptMapper;
    @Autowired
    TDeptCashFlowMapper deptCashFlowMapper;
    @Autowired
    TDeptAccountFlowMapper deptAccountFlowMapper;
    /**
     * 新增部门
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:00 PM
     */
    @Transactional(rollbackFor = Exception.class)
    public void addDept(Dept dept) {

        if (ToolUtil.isOneEmpty(dept, dept.getSimpleName(), dept.getFullName(), dept.getPid())) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }

        //完善pids,根据pid拿到pid的pids
        this.deptSetPids(dept);

        this.save(dept);
    }

    /**
     * 修改部门
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:00 PM
     */
    @Transactional(rollbackFor = Exception.class)
    public void editDept(Dept dept) {

        if (ToolUtil.isOneEmpty(dept, dept.getDeptId(), dept.getSimpleName(), dept.getFullName(), dept.getPid(), dept.getDescription())) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }

        //完善pids,根据pid拿到pid的pids
        this.deptSetPids(dept);

        this.updateById(dept);
    }

    /**
     * 删除部门
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:16 PM
     */
    @Transactional
    public void deleteDept(Long deptId) {
    	Dept dept = deptMapper.selectById(deptId);

        //根据like查询删除所有级联的部门
        QueryWrapper<Dept> wrapper = new QueryWrapper<>();
        wrapper = wrapper.like("PIDS", "%[" + dept.getDeptId() + "]%");
        List<Dept> subDepts = deptMapper.selectList(wrapper);
        for (Dept temp : subDepts) {
            this.removeById(temp.getDeptId());
        }

        this.removeById(dept.getDeptId());
    }

    /**
     * 获取ztree的节点列表
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:16 PM
     */
    public List<ZTreeNode> tree() {
        return this.baseMapper.tree();
    }

    /**
     * 获取ztree的节点列表
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:16 PM
     */
    public List<TreeviewNode> treeviewNodes() {
        return this.baseMapper.treeviewNodes();
    }

    /**
     * 获取所有部门列表
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:16 PM
     */
    public Page<Map<String, Object>> list(String condition, String deptId) {
        Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, condition, deptId);
    }

    /**
     * 设置部门的父级ids
     *
     * @author fengshuonan
     * @Date 2018/12/23 4:58 PM
     */
    private void deptSetPids(Dept dept) {
        if (ToolUtil.isEmpty(dept.getPid()) || dept.getPid().equals(0L)) {
            dept.setPid(0L);
            dept.setPids("[0],");
        } else {
            Long pid = dept.getPid();
            Dept temp = this.getById(pid);
            String pids = temp.getPids();
            dept.setPid(pid);
            dept.setPids(pids + "[" + pid + "],");
        }
    }

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
