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
    var MchInfo = {
        tableId: "mchInfoTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    MchInfo.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'mchId', width: 180, title: '商户号'},
            {field: 'mchName', width: 180, title: '商户名称'},
            {field: 'deptId', width: 180, title: '渠道号'},
            {field: 'mobile', width: 180, title: '手机号'},
            {field: 'mchAddress', width: 180, title: '商户地址'},
            {field: 'customerName', width: 180, title: '客户姓名'},
            {field: 'customerIdentType', width: 180, title: '客户证件类型'},
            {field: 'customerIdentNo', width: 180, title: '客户证件号'},
            {field: 'createTime', width: 180, title: '创建时间'},
        ]];
    };

    /**
     * 点击查询按钮
     */
    MchInfo.search = function () {
        var queryData = {};
        queryData['beginTime'] = $("#beginTime").val();
        queryData['endTime'] = $("#endTime").val();
        queryData['mchId'] = $("#mchId").val();
        queryData['mchName'] = $("#mchName").val();
        queryData['deptId'] = $("#deptId").val();
        queryData['mobile'] = $("#mobile").val();
        table.reload(MchInfo.tableId, {where: queryData});
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + MchInfo.tableId,
        url: Feng.ctxPath + '/business/mch/mchInfoList',
        where:{beginTime:Feng.currentDate()},
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: MchInfo.initColumn()
    });

    //渲染时间选择框
    laydate.render({
        elem: '#beginTime',
        format: 'yyyy-MM-dd',
        value: new Date()
    });

    //渲染时间选择框
    laydate.render({
        elem: '#endTime'
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
        MchInfo.search();
    });
});
