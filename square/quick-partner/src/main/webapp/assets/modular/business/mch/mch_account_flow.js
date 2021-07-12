layui.use(['layer', 'table', 'ax', 'laydate'], function () {
    var $ = layui.$;
    var $ax = layui.ax;
    var layer = layui.layer;
    var table = layui.table;
    var laydate = layui.laydate;

    /**
     * 系统管理--操作日志
     */
    var MchAccountFlow = {
        tableId: "mchAccountFlowTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    MchAccountFlow.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'id', width:80, title: 'ID'},
            {field: 'mchId',  title: '商户号'},
            {field: 'mchName', title: '商户名称'},
            {field: 'partnerId', width:150, title: '渠道号'},
            {field: 'balance', width:100, title: '余额'},
            {field: 'amount', width:100, title: '变动金额'},
            {field: 'bizTypeName', width:100, title: '业务类型'},
            {field: 'directionName', width:100, title: '变动方向'},
            {field: 'tradeNo', title: '内部单号'},
            {field: 'createTime', title: '创建时间'}
            //{align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 100}
        ]];
    };

    /**
     * 点击查询按钮
     */
    MchAccountFlow.search = function () {
        var queryData = {};
        queryData['beginTime'] = $("#beginTime").val();
        queryData['endTime'] = $("#endTime").val();
        queryData['mchId'] = $("#mchId").val();
        table.reload(MchAccountFlow.tableId, {where: queryData});
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + MchAccountFlow.tableId,
        url: Feng.ctxPath + '/mch/mchAccountFlowList',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: MchAccountFlow.initColumn()
    });

  //渲染时间选择框
    laydate.render({
        elem: '#beginTime'
    });

    //渲染时间选择框
    laydate.render({
        elem: '#endTime'
    });
    
    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	MchAccountFlow.search();
    });


    /*// 工具条点击事件
    table.on('tool(' + Mch.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;

        if (layEvent === 'detail') {
            LoginLog.logDetail(data);
        }
    });*/
});
