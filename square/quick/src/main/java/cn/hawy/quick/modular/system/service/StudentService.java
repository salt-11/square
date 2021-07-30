package cn.hawy.quick.modular.system.service;

import cn.hawy.quick.core.common.page.LayuiPageFactory;
import cn.hawy.quick.modular.system.entity.Student;
import cn.hawy.quick.modular.system.mapper.StudentMapper;
import cn.stylefeng.roses.core.datascope.DataScope;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StudentService extends ServiceImpl<StudentMapper, Student> {

    public Page<Map<String, Object>> selectStudents(DataScope dataScope, String studentName, Long studentId) {
        Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.selectStudents(page, dataScope, studentName, studentId);
    }

}
