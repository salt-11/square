package cn.hawy.quick.modular.system.controller;

import cn.hawy.quick.core.common.annotion.BussinessLog;
import cn.hawy.quick.core.common.constant.dictmap.DeleteDict;
import cn.hawy.quick.core.common.constant.dictmap.StudentMap;
import cn.hawy.quick.core.common.exception.BizExceptionEnum;
import cn.hawy.quick.core.common.page.LayuiPageFactory;
import cn.hawy.quick.core.log.LogObjectHolder;
import cn.hawy.quick.modular.system.entity.Student;
import cn.hawy.quick.modular.system.service.StudentService;
import cn.hawy.quick.modular.system.warpper.StudentWrapper;
import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/student")
public class StudentController extends BaseController {

    private String PREFIX = "/modular/system/student/";

    @Autowired
    private StudentService studentService;

    @RequestMapping("")
    public String index() {
        return PREFIX + "student.html";
    }

    @RequestMapping("/student_add")
    public String myAdd() {
        return PREFIX + "student_add.html";
    }

    @RequestMapping("/student_update/{studentId}")
    public String studentUpdate(@PathVariable Long studentId, Model model) {
        Student student = this.studentService.getById(studentId);
        model.addAllAttributes(BeanUtil.beanToMap(student));
        LogObjectHolder.me().set(student);
        return PREFIX + "student_edit.html";
    }

    @RequestMapping(value = "/selectStudents")
    @ResponseBody
    public Object selectStudents(@RequestParam(required = false) String studentName,
                                 @RequestParam(required = false) Long studentId
    ) {
        Page<Map<String, Object>> selectStudents = this.studentService.selectStudents(null, studentName, studentId);
        Page wrapped = new StudentWrapper(selectStudents).wrap();
        return LayuiPageFactory.createPageInfo(wrapped);
    }

    @RequestMapping(value = "/add")
    @ResponseBody
    @BussinessLog(value = "新增学生", key = "studentId", dict = StudentMap.class)
    public Object add(Student student) {
        if(ToolUtil.isOneEmpty(student, student.getStudentId(), student.getStudentName(),
                student.getStudentSex(),student.getStudentAge(),student.getStudentPhone())) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        this.studentService.save(student);
        return SUCCESS_TIP;
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    @BussinessLog(value = "删除学生", key = "studentId", dict = DeleteDict.class)
    public Object delete(@RequestParam Long studentId) {
        this.studentService.removeById(studentId);
        return SUCCESS_TIP;
    }

    @RequestMapping(value = "/update")
    @ResponseBody
    @BussinessLog(value = "修改信息", key = "studentId", dict = StudentMap.class)
    public Object update(Student student) {
        if(ToolUtil.isOneEmpty(student, student.getStudentId(), student.getStudentName(),
                student.getStudentSex(),student.getStudentAge(),student.getStudentPhone())) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        Student old = this.studentService.getById(student.getStudentId());
        old.setStudentName(student.getStudentName());
        old.setStudentSex(student.getStudentSex());
        old.setStudentAge(student.getStudentAge());
        old.setStudentPhone(student.getStudentPhone());
        this.studentService.updateById(old);
        return SUCCESS_TIP;
    }
}
