package cn.hawy.quick.modular.system.service;

import cn.hawy.quick.core.common.page.LayuiPageFactory;
import cn.hawy.quick.modular.system.entity.Student;
import cn.hawy.quick.modular.system.mapper.StudentMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StudentService extends ServiceImpl<StudentMapper, Student> {
    public Page<Map<String, Object>> list(String condition, String id) {
        Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, condition, id);
    }
}
