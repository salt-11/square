package cn.hawy.quick.core.common.constant.dictmap;

import cn.hawy.quick.core.common.constant.dictmap.base.AbstractDictMap;

public class StudentMap extends AbstractDictMap {

    @Override
    public void init() {
        put("studentId", "学生id");
        put("studentName", "姓名");
        put("studentSex", "性别");
        put("studentAge","年龄");
        put("studentPhone","电话");
    }

    @Override
    protected void initBeWrapped() {
    }
}
