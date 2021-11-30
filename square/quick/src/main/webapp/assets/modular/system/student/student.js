layui.use(['layer', 'form', 'table', 'admin', 'ax'], function () {
    var $ = layui.$;
    var layer = layui.layer;
    var form = layui.form;
    var table = layui.table;
    var $ax = layui.ax;
    var admin = layui.admin;

    /**
     * 系统管理--消息管理
     */
    var Student = {
        tableId: "studentTable"    //表格id
    };

    /**
     * 初始化表格的列
     */
    Student.initColumn = function () {
        return [[
            {field: 'studentId', align: "center", title: '学号'},
            {field: 'studentName', align: "center", title: '姓名'},
            {field: 'studentPhone', align: "center", title: '手机号'},
            {field: 'fullName', align: "center", title: '专业'},
            {field: 'studentClass', align: "center", title: '班级'},
            {field: 'studentCredit', align: "center", title: '应修学分'},
            {field: 'studentCreditNow', align: "center", title: '在修学分'},
            {field: 'studentCreditOld', align: "center", title: '已修学分'},
        ]];
    };


    // 渲染表格
    var tableResult = table.render({
        elem: '#' + Student.tableId,
        url: Feng.ctxPath + '/student/list',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: Student.initColumn()
    });

});
