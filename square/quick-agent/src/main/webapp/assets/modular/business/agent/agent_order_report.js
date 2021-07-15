layui.use(['layer', 'table', 'ax', 'laydate'], function () {
    var $ = layui.$;
    var $ax = layui.ax;
    var layer = layui.layer;
    var table = layui.table;
    var laydate = layui.laydate;

    /**
     * 系统管理--操作日志
     */
    var AgentOrderReport = {
        tableId: "agentOrderReportTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    AgentOrderReport.initColumn = function () {
        return [[
            {field: 'reportDate', width:180, align:'center',title: '统计日期'},
            {field: 'agentId', width:150, align:'center',title: '渠道号'},
            {field: 'agentName', width:150, align:'center',title: '渠道名称'},
            {field: 'channelNo', templet: '#channelNoTpl', width:150, align:'center',title: '支付类型'},
            {field: 'orderNum', width:150, align:'center',title: '交易笔数'},
            {field: 'orderAmount', width:150, align:'center',title: '交易金额'},
            {field: 'orderAgentAmount', width:150, align:'center',title: '交易渠道利润'},
            {field: 'orderCostAmount', width:150, align:'center',title: '交易平台利润'},
            {field: 'cashNum', width:150, align:'center',title: '提现笔数'},
            {field: 'cashAmount', width:150, align:'center',title: '提现金额'},
            {field: 'cashAgentAmount',width:150,  align:'center', title: '提现渠道利润'},
            {field: 'cashCostAmount', width:150,  align:'center',title: '提现平台利润'},
            {field: 'createTime', align:'center',title: '创建时间'}
        ]];
    };



    /**
     * 点击查询按钮
     */
    AgentOrderReport.search = function () {
        var queryData = {};
        queryData['beginTime'] = $("#beginTime").val();
        queryData['endTime'] = $("#endTime").val();
        queryData['agentId'] = $("#agentId").val();
        queryData['channelNo'] = $("#channelNo").val();
        table.reload(AgentOrderReport.tableId, {where: queryData});
    };



  //渲染时间选择框
    laydate.render({
        elem: '#beginTime',
        format: 'yyyy-MM-dd'
       // value: new Date()
    });

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + AgentOrderReport.tableId,
        url: Feng.ctxPath + '/agent/agentOrderReportList',
        //where:{beginTime:Feng.currentDate()},
        page: true,
        limits:[10,50,100,200,500,1000],
        height: "full-158",
        cellMinWidth: 100,
        cols: AgentOrderReport.initColumn()
    });



    //渲染时间选择框
    laydate.render({
        elem: '#endTime'
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
        AgentOrderReport.search();
    });

    $('#btnExp').click(function () {
        AgentOrderReport.exportExcel();
    });

    AgentOrderReport.exportExcel = function () {
        DownLoadFile({
            url: Feng.ctxPath + '/agent/agentOrderReportListExcel?beginTime=' + $("#beginTime").val() + '&endTime=' + $("#endTime").val()
                + '&agentId=' + $("#agentId").val() + '&channelNo=' + $("#channelNo").val()
        });
    };

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

});
