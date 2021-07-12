package cn.hawy.quick.modular.api.mapper;

import cn.hawy.quick.modular.api.dao.DeptOrderReportExcel;
import cn.hawy.quick.modular.api.entity.TDeptOrderReport;
import cn.hawy.quick.modular.api.entity.TPayOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hawy
 * @since 2020-05-06
 */
public interface TDeptOrderReportMapper extends BaseMapper<TDeptOrderReport> {

    int insertDeptOrderReport();

    List<Map<String, Object>> findAll(@Param("page") Page page, @Param("join") String join, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("deptId") String deptId, @Param("channelNo") String channelNo);

    List<DeptOrderReportExcel> find(@Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("deptId") String deptId, @Param("channelNo") String channelNo);
}
