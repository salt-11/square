layui.use(['layer', 'table', 'ax', 'admin', 'laydate'], function () {
    var $ = layui.$;
    var $ax = layui.ax;
    var admin = layui.admin;
    var layer = layui.layer;
    var table = layui.table;
    var laydate = layui.laydate;

    /**
     * 系统管理--操作日志
     */
    var AgentDeptInfo = {
        tableId: "agentDeptInfoTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    AgentDeptInfo.initColumn = function () {
        return [[
            {field: 'deptId', width:150, title: '商户id'},
            {field: 'deptName',  width:150,title: '商户名称'},
            {field: 'createTime', title: '创建时间'}
        ]];
    };

    /**
     * 点击查询按钮
     */
    AgentDeptInfo.search = function () {
        var queryData = {};
        table.reload(AgentDeptInfo.tableId, {where: queryData});
    };



    // 渲染表格
    var tableResult = table.render({
        elem: '#' + AgentDeptInfo.tableId,
        url: Feng.ctxPath + '/agent/agentDeptInfoList',
        where:{beginTime:Feng.currentDate()},
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: AgentDeptInfo.initColumn()
    });


    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	AgentDeptInfo.search();
    });

});
