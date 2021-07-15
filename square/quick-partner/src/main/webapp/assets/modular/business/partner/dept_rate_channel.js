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
    var DeptRateChannel = {
        tableId: "deptRateChannelTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    DeptRateChannel.initColumn = function () {
        return [[
            {field: 'id', hide: true, width:80, title: 'ID'},
            {field: 'channel',  width:150,title: '通道'},
            {field: 'bankCode', width:150,title: '银行编码'},
            {field: 'bankName', width:150, title: '银行名称'},
            {field: 'costRate', width:150, title: '交易费率'},
            {field: 'cashRate', width:150, title: '提现手续费'},
            {field: 'cardAuthRate', width:240, title: '银行卡鉴权手续费'},
            {field: 'createTime', title: '创建时间'}
        ]];
    };

    /**
     * 点击查询按钮
     */
    DeptRateChannel.search = function () {
        var queryData = {};
        queryData['channel'] = $("#channel").val();
        table.reload(DeptRateChannel.tableId, {where: queryData});
    };



    // 渲染表格
    var tableResult = table.render({
        elem: '#' + DeptRateChannel.tableId,
        url: Feng.ctxPath + '/partner/deptRateChannelList',
        where:{beginTime:Feng.currentDate()},
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: DeptRateChannel.initColumn()
    });


    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	DeptRateChannel.search();
    });

});
