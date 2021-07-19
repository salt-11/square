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
    var AgentAccountFlow = {
        tableId: "agentAccountFlowTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    AgentAccountFlow.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'id', hide: true, width:80, title: 'ID'},
            {field: 'agentId',  title: '代理号'},
            {field: 'agentName', title: '代理名称'},
            {field: 'balance', width:100, title: '余额'},
            {field: 'amount', width:100, title: '变动金额'},
            {field: 'bizTypeName', width:160, title: '业务类型'},
            {field: 'directionName', width:100, title: '变动方向'},
            {field: 'tradeNo', width:180, title: '内部单号'},
            {field: 'createTime', width:180, title: '创建时间'}
        ]];
    };

    /**
     * 点击查询按钮
     */
    AgentAccountFlow.search = function () {
        var queryData = {};
        queryData['beginTime'] = $("#beginTime").val();
        queryData['endTime'] = $("#endTime").val();
        queryData['agentId'] = $("#agentId").val();
        queryData['bizTypeName'] = $("#bizTypeName").val();
        queryData['directionName'] = $("#directionName").val();
        table.reload(AgentAccountFlow.tableId, {where: queryData});
    };

    /**
     * 导出excel按钮
     */
    AgentAccountFlow.exportExcel = function () {
        DownLoadFile({
            url: Feng.ctxPath + '/agent/agentAccountFlowExcelList?beginTime=' + $("#beginTime").val() + '&endTime=' + $("#endTime").val()
                + '&agentId=' + $("#agentId").val() + '&bizTypeName=' + $("#bizTypeName").val() + '&directionName=' + $("#directionName").val()
        });
    };

    /**
     * 导出报表
     * @param options
     */
    var DownLoadFile = function (options) {
        var config = $.extend(true, { method: 'post' }, options);
        var $iframe = $('<iframe id="down-file-iframe" />');
        var $form = $('<form target="down-file-iframe" method="' + config.method + '" />');
        $form.attr('action', config.url);
        for (var key in config.data) {
            $form.append('<input type="hidden" name="' + key + '" value="' + config.data[key] + '" />');
        }
        $iframe.append($form);
        $(document.body).append($iframe);
        $form[0].submit();
        $iframe.remove();
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + AgentAccountFlow.tableId,
        url: Feng.ctxPath + '/agent/agentAccountFlowList',
        where:{beginTime:Feng.currentDate()},
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: AgentAccountFlow.initColumn()
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
    	AgentAccountFlow.search();
    });
    
    // 搜索按钮点击事件
    $('#btnCash').click(function () {
    	admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '提现申请',
            content: Feng.ctxPath + '/agent/agentCashApply',
            end: function () {
                admin.getTempData('formOk') && table.reload(AgentAccountFlow.tableId);
            }
        });
        //PartnerAccountFlow.search();
    });

    // 搜索按钮点击事件
    $('#btnExp').click(function () {
        AgentAccountFlow.exportExcel();
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
