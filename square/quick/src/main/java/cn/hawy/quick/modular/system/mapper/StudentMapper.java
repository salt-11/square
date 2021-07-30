package cn.hawy.quick.modular.system.mapper;

import cn.hawy.quick.modular.system.entity.Student;
import cn.stylefeng.roses.core.datascope.DataScope;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface StudentMapper extends BaseMapper<Student> {

    Page<Map<String, Object>> selectStudents(@Param("page") Page page, @Param("dataScope") DataScope dataScope, @Param("studentName") String studentName, @Param("studentId") Long studentId);

}
