package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.dao.DeptOrderReportExcel;
import cn.hawy.quick.modular.api.entity.TDeptOrderReport;
import cn.hawy.quick.modular.api.mapper.TDeptOrderReportMapper;
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
 * @since 2020-05-06
 */
@Service
public class TDeptOrderReportService extends ServiceImpl<TDeptOrderReportMapper, TDeptOrderReport> {


    public List<Map<String, Object>> findAll(Page page, String join, String deptId, String beginTime, String endTime,String channelNo){
        return this.baseMapper.findAll(page, join,deptId, beginTime, endTime,channelNo);
    }

    public List<DeptOrderReportExcel> find(String beginTime, String endTime, String deptId, String channelNo){
        return this.baseMapper.find(beginTime, endTime,deptId, channelNo);
    }

    public int insertDeptOrderReport(){
        return this.baseMapper.insertDeptOrderReport();
    }
}
