package cn.hawy.quick.modular.system.mapper;

import cn.hawy.quick.core.common.node.TreeviewNode;
import cn.hawy.quick.core.common.node.ZTreeNode;
import cn.hawy.quick.modular.system.entity.Dept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 专业表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-07
 */
public interface DeptMapper extends BaseMapper<Dept> {

    /**
     * 获取ztree的节点列表
     */
    List<ZTreeNode> tree();

    /**
     * 获取所有专业列表
     */
    Page<Map<String, Object>> list(@Param("page") Page page, @Param("condition") String condition, @Param("deptId") String deptId);

    /**
     * 获取所有专业树列表
     */
    List<TreeviewNode> treeviewNodes();

    List<Map<String, Object>> selectDeptTree(@Param("condition") String condition, @Param("deptId") String deptId);
}
