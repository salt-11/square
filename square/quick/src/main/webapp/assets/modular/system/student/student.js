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
        tableId: "studentTable",   //表格id
    };

    /**
     * 初始化表格的列
     */
    Student.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'studentId', sort: true, title: 'id'},
            {field: 'studentName', sort: true, title: '姓名'},
            {field: 'studentSex', sort: true, title: '性别'},
            {field: 'studentAge', sort: true, title: '年龄'},
            {field: 'studentPhone', sort: true, title: '电话'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 200}
        ]];
    };

    /**
     * 点击查询按钮
     */
    Student.search = function () {
        var queryData = {};
        queryData['studentId'] = $("#studentId").val();
        table.reload(Student.tableId, {where: queryData});
    };

    /**
     * 弹出添加通知
     */
    Student.openAddStudent = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '添加学生',
            content: Feng.ctxPath + '/student/student_add',
            end: function () {
                admin.getTempData('formOk') && table.reload(Student.tableId);
            }
        });
    };

    /**
     * 点击编辑通知
     *
     * @param data 点击按钮时候的行数据
     */
    Student.onEditStudent = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '信息详情',
            content: Feng.ctxPath + '/student/student_update/' + data.studentId,
            end: function () {
                admin.getTempData('formOk') && table.reload(Student.tableId);
            }
        });
    };

    /**
     * 点击删除通知
     *
     * @param data 点击按钮时候的行数据
     */
    Student.onDeleteStudent = function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/student/delete", function (data) {
                Feng.success("删除成功!");
                table.reload(Student.tableId);
            }, function (data) {
                Feng.error("删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("studentId", data.studentId);
            ajax.start();
        };
        Feng.confirm("是否删除 " + data.studentName + "?", operation);
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + Student.tableId,
        url: Feng.ctxPath + '/student/selectStudents',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: Student.initColumn()
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
        Student.search();
    });

    // 添加按钮点击事件
    $('#btnAdd').click(function () {
        Student.openAddStudent();
    });

    // 工具条点击事件
    table.on('tool(' + Student.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;

        if (layEvent === 'edit') {
            Student.onEditStudent(data);
        } else if (layEvent === 'delete') {
            Student.onDeleteStudent(data);
        }
    });
});
