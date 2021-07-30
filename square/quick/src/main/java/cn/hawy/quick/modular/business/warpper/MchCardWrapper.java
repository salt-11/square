package cn.hawy.quick.modular.business.warpper;

import cn.stylefeng.roses.core.base.warpper.BaseControllerWrapper;

import java.util.List;
import java.util.Map;

public class MchCardWrapper extends BaseControllerWrapper {
    public MchCardWrapper(Map<String, Object> single) {
        super(single);
    }

    public MchCardWrapper(List<Map<String, Object>> multi) {
        super(multi);
    }

    @Override
    protected void wrapTheMap(Map<String, Object> map) {

    }
}
