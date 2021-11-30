package cn.hawy.quick.modular.system.controller;

import cn.hawy.quick.core.common.page.LayuiPageFactory;
import cn.hawy.quick.core.shiro.ShiroUser;
import cn.hawy.quick.modular.system.service.StudentService;
import cn.hawy.quick.modular.system.warpper.StudentWrapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.catalina.security.SecurityUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/student")
public class StudentController extends BaseController {

    private String PREFIX = "/modular/system/student/";

    @Autowired
    private StudentService studentService;

    @RequestMapping("")
    public String index(){
        return PREFIX + "student.html";
    }

    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(String condition) {
        ShiroUser temp = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
        String id = temp.getAccount();
        Page<Map<String, Object>> list = this.studentService.list(condition,id);
        Page<Map<String, Object>> wrap = new StudentWrapper(list).wrap();
        return LayuiPageFactory.createPageInfo(wrap);
    }
}
