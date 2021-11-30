package cn.hawy.quick.modular.system.warpper;

import cn.hawy.quick.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;
import cn.stylefeng.roses.kernel.model.page.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

public class StudentWrapper extends BaseControllerWrapper {

    public StudentWrapper(Map<String, Object> single) {
        super(single);
    }

    public StudentWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    public StudentWrapper(Page<Map<String, Object>> page) {
        super(page);
    }

    public StudentWrapper(PageResult<Map<String, Object>> pageResult) {
        super(pageResult);
    }

    @Override
    protected void wrapTheMap(Map<String, Object> map) {
    }
}
