package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.TAgentInfo;
import cn.hawy.quick.modular.api.mapper.TAgentInfoMapper;
import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
}
