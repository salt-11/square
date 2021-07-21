package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.dto.AgentDto;
import cn.hawy.quick.modular.api.entity.TAgentInfo;
import cn.hawy.quick.modular.api.mapper.TAgentInfoMapper;
import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.core.util.ToolUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
    public boolean getAgentInfo(String AgentId ){
        TAgentInfo tAgentInfo = this.baseMapper.selectById(AgentId);
        if(BeanUtil.isEmpty(tAgentInfo)){
            throw new ServiceException(400,"该代理商 不存在");
        }
            return true;
    }

    public boolean getAgent(String AgentId ){
        TAgentInfo tAgentInfo = this.baseMapper.selectById(AgentId);
        if(!BeanUtil.isEmpty(tAgentInfo)){
            throw new ServiceException(400,"该代理商已存在");
        }
        return true;
    }
    public List<Map<String, Object>> findAll(Page page, String  id, String account, String balance, String beginTime, String endTime, String agentName) {
        return this.baseMapper.findAll(  page,  id, account, balance, beginTime, endTime, agentName );
    }


    public void add(AgentDto param){
        getAgent(param.getId());
        TAgentInfo entity = getEntity(param);
        this.save(entity);
    }

    private TAgentInfo getEntity(AgentDto param) {
        TAgentInfo entity = new TAgentInfo();
        ToolUtil.copyProperties(param, entity);
        entity.setSalt(BCrypt.gensalt());
        entity.setPassword(BCrypt.hashpw(param.getPassword(), entity.getSalt()));
        return entity;
    }

    private TAgentInfo getOldEntity(AgentDto param) {
        return this.getById(getKey(param));
    }

    private Serializable getKey(AgentDto param){
        return param.getId();
    }

    public void update(AgentDto param){
        TAgentInfo oldEntity = getOldEntity(param);
        TAgentInfo newEntity = getEntity(param);
        ToolUtil.copyProperties(newEntity, oldEntity);
        this.updateById(newEntity);
    }


}
