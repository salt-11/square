layui.use(['layer', 'table', 'admin', 'ax', 'laydate'], function () {
    var $ = layui.$;
    var $ax = layui.ax;
    var layer = layui.layer;
    var table = layui.table;
    var laydate = layui.laydate;
    var admin = layui.admin;

    /**
     * 系统管理--操作日志
     */
    var DeptInfo = {
        tableId: "deptInfoTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    DeptInfo.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'id', width: 180, title: '渠道号'},
            {field: 'deptName', title: '渠道名称'},
            {field: 'account', title: '登录账户'},
            {field: 'balance', title: '余额'},
            {field: 'agentId', title: '代理商id'},
            {field: 'name', title: '银行卡姓名'},
            {field: 'cardNo', title: '银行卡号'},
            {field: 'bankName', title: '开户行'},
            {field: 'createTime', width: 250, title: '创建时间'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 100}
        ]];
    };

    /**
     * 点击查询按钮
     */
    DeptInfo.search = function () {
        var queryData = {};
        queryData['id'] = $("#id").val();
        queryData['account'] = $("#account").val();
        queryData['deptName'] = $("#deptName").val();
        queryData['beginTime'] = $("#beginTime").val();
        queryData['endTime'] = $("#endTime").val();

        table.reload(DeptInfo.tableId, {
            where: queryData
        });
    };
    /**
     *  编辑
     *
     * @param data 点击按钮时候的行数据
     */
    DeptInfo.onEdit = function (data) {
        console.log(123);
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '渠道信息',
            content: Feng.ctxPath + '/partner/deptInfoEdit?id='+data.id,
            end: function () {
                admin.getTempData('formOk') && table.reload(DeptInfo.tableId);
            }
        });
    };
    // 渲染表格
    var tableResult = table.render({
        elem: '#' + DeptInfo.tableId,
        url: Feng.ctxPath + '/partner/deptInfoList',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: DeptInfo.initColumn()
    });


    // 添加按钮点击事件
    $('#btnAdd').click(function () {
        DeptInfo.jumpAddPage();

    });

    /**
     * 跳转到添加页面
     */
    DeptInfo.jumpAddPage = function () {
        window.location.href = Feng.ctxPath + '/partner/deptAdd'
    };

    //渲染时间选择框
    laydate.render({
        elem: '#beginTime',
        format: 'yyyy-MM-dd',
    });

    //渲染时间选择框
    laydate.render({
        elem: '#endTime'
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	DeptInfo.search();
    });

    // DeptInfo.onDetails = function (data) {
    //     top.layui.admin.open({
    //         type: 2,
    //         area: '1000px',
    //         title: '代理详情',
    //         content: Feng.ctxPath + '/business/dept/deptInfoDetails?deptId=' + data.deptId
    //     });
    // };


    // 工具条点击事件
    table.on('tool(' + DeptInfo.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details') {
            DeptInfo.onDetails(data);
        }else if (layEvent === 'edit') {
            DeptInfo.onEdit(data);
        }
    });

});
