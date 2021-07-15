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
    var AgentRateChannel = {
        tableId: "agentRateChannelTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    AgentRateChannel.initColumn = function () {
        return [[
            {field: 'id', hide: true, width:80, title: 'ID'},
            {field: 'channel',  width:150,title: '通道'},
            {field: 'bankCode', width:150,title: '银行编码'},
            {field: 'bankName', width:150, title: '银行名称'},
            {field: 'costRate', width:150, title: '交易费率'},
            {field: 'createTime', title: '创建时间'}
        ]];
    };

    /**
     * 点击查询按钮
     */
    AgentRateChannel.search = function () {
        var queryData = {};
        queryData['channel'] = $("#channel").val();
        table.reload(AgentRateChannel.tableId, {where: queryData});
    };



    // 渲染表格
    var tableResult = table.render({
        elem: '#' + AgentRateChannel.tableId,
        url: Feng.ctxPath + '/agent/agentRateChannelList',
        where:{beginTime:Feng.currentDate()},
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: AgentRateChannel.initColumn()
    });


    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	AgentRateChannel.search();
    });

});
