package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.dto.PartnerDto;
import cn.hawy.quick.modular.api.entity.TDeptInfo;
import cn.hawy.quick.modular.api.entity.TDeptRateChannel;
import cn.hawy.quick.modular.api.mapper.TDeptInfoMapper;
import cn.hawy.quick.modular.api.param.DeptInfoParam;
import cn.hawy.quick.modular.api.param.DeptRateChannelParam;
import cn.hawy.quick.modular.system.entity.Dept;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.io.Serializable;
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

    public List<Map<String, Object>> findAll(Page page, String id, String account, String balance, String deptId, String beginTime, String endTime, String deptName) {
        return this.baseMapper.findAll(page, id, account, balance, deptId, beginTime, endTime, deptName);
    }

        public boolean getDeptInfo(String DeptId) {
            TDeptInfo tDeptInfo = this.baseMapper.selectById(DeptId);
            if (BeanUtil.isEmpty(tDeptInfo)) {
                throw new ServiceException(400, "该代理商 不存在");
            }
            return true;
        }

        public boolean getDept(String DeptId) {
            TDeptInfo tDeptInfo = this.baseMapper.selectById(DeptId);
            if (!BeanUtil.isEmpty(tDeptInfo)) {
                throw new ServiceException(400, "该代理商已存在");
            }
            return true;
        }

        public void add(PartnerDto param) {
            getDept(param.getId());
            TDeptInfo entity = getEntity(param);
            this.save(entity);
        }
    public boolean getDeptInfo(Long deptId){
        TDeptInfo dept = this.baseMapper.selectById(deptId);
        if(BeanUtil.isEmpty(dept)){
            throw new ServiceException(400, "该渠道不存在!");
        }else{
            return true;
        }
    }
    public TDeptInfo findByDeptIdAndAgentId(String deptId, String agentId) {
        TDeptInfo dept = new TDeptInfo();
        dept.setId(deptId);
        dept.setAgentId(agentId);
        return this.baseMapper.selectOne(new QueryWrapper <>(dept));
    }

    /**
     * 修改渠道信息
     * @param channelParam
     */
        public void update(DeptInfoParam channelParam){
        TDeptInfo TDeptRateChannel =new TDeptInfo();
        BeanUtil.copyProperties(channelParam, TDeptRateChannel);
        this.updateById(TDeptRateChannel);
    }
        private TDeptInfo getEntity(PartnerDto param) {
            TDeptInfo entity = new TDeptInfo();
            ToolUtil.copyProperties(param, entity);
            entity.setSalt(BCrypt.gensalt());
            entity.setPassword(BCrypt.hashpw(param.getPassword(), entity.getSalt()));
            return entity;
        }

        private TDeptInfo getOldEntity(PartnerDto param) {
            return this.getById(getKey(param));
        }

        private Serializable getKey(PartnerDto param) {
            return param.getId();
        }

        public void update(PartnerDto param) {
            TDeptInfo oldEntity = getOldEntity(param);
            TDeptInfo newEntity = getEntity(param);
            ToolUtil.copyProperties(newEntity, oldEntity);
            this.updateById(newEntity);
        }


}
