package cn.hawy.quick.modular.system.mapper;

import cn.hawy.quick.modular.system.entity.Student;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Map;
import java.util.*;

public interface StudentMapper extends BaseMapper<Student> {
    Page<Map<String, Object>> list(@Param("page") Page page, @Param("condition") String condition, @Param("id") String id);
}
