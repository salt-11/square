layui.use(['layer', 'table', 'ax', 'laydate'], function () {
    var $ = layui.$;
    var $ax = layui.ax;
    var layer = layui.layer;
    var table = layui.table;
    var laydate = layui.laydate;



    /**
     * 系统管理--操作日志
     */
    var MchCashFlow = {
        tableId: "mchCashFlowTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    MchCashFlow.initColumn = function () {
        return [[
            {field: 'cashId', align: 'center', width:180, title: '提现号'},
            {field: 'mchId', align: 'center', width:180, title: '商户号'},
            {field: 'mchName', align: 'center', title: '商户名称'},
            {field: 'bankCardNo', align: 'center', width:180, title: '银行卡号'},
            {field: 'deptId', align: 'center', width:100, title: '渠道号'},
            {field: 'cashAmount', align: 'center', width:100, title: '提现金额'},
            {field: 'cashStatusName', align: 'center', width:100, title: '提现状态'},
            {field: 'returnMsg', align: 'center', width:100, title: '错误原因'},
            {field: 'cashFee', align: 'center', width:100, title: '提现手续费'},
            {field: 'cashRate', align: 'center', width:100, title: '渠道商提现成本'},
            {field: 'deptAmount', align: 'center', width:100, title: '渠道商利润'},
            {field: 'outAmount', align: 'center', width:100, title: '出款金额'},
            {field: 'outTradeNo', align: 'center', width:180, title: '外部提现号'},
            {field: 'createTime', align: 'center', width:180, title: '创建时间'}
            //{align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 100}
        ]];
    };

    /**
     * 点击查询按钮
     */
    MchCashFlow.search = function () {
        var queryData = {};
        queryData['beginTime'] = $("#beginTime").val();
        queryData['endTime'] = $("#endTime").val();
        queryData['deptId'] = $("#deptId").val();
        queryData['cashId'] = $("#cashId").val();
        queryData['outTradeNo'] = $("#outTradeNo").val();
        queryData['cashStatus'] = $("#cashStatus").val();
        queryData['mchName'] = $("#mchName").val();
        queryData['bankCardNo'] = $("#bankCardNo").val();
        table.reload(MchCashFlow.tableId, {where: queryData});
    };

    /**
     * 导出excel按钮
     */
    MchCashFlow.exportExcel = function () {
        DownLoadFile({
            url: Feng.ctxPath + '/business/mch/mchCashExcelList?beginTime=' + $("#beginTime").val() + '&endTime=' + $("#endTime").val()
                + '&deptId=' + $("#deptId").val() + '&cashId=' + $("#cashId").val() + '&outTradeNo=' + $("#outTradeNo").val() + '&cashStatus=' + $("#cashStatus").val()
                + '&mchName=' + $("#mchName").val() + '&bankCardNo=' + $("#bankCardNo").val()
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
        elem: '#' + MchCashFlow.tableId,
        url: Feng.ctxPath + '/business/mch/mchCashFlowList',
        where:{beginTime:Feng.currentDate()},
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: MchCashFlow.initColumn()
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
    	MchCashFlow.search();
    });

    // 搜索按钮点击事件
    $('#btnExp').click(function () {
        MchCashFlow.exportExcel();
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
