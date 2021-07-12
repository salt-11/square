layui.use(['layer', 'table', 'ax', 'laydate'], function () {
    var $ = layui.$;
    var $ax = layui.ax;
    var layer = layui.layer;
    var table = layui.table;
    var laydate = layui.laydate;

    /**
     * 系统管理--操作日志
     */
    var DeptCashFlow = {
        tableId: "deptCashFlowTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    DeptCashFlow.initColumn = function () {
        return [[
            {field: 'id', align:'center', width:180, title: '提现号'},
            {field: 'deptId', align:'center',title: '渠道号'},
            {field: 'deptName',  align:'center',title: '渠道名称'},
            {field: 'cashAmount', align:'center',width:100, title: '提现金额'},
            {field: 'cashStatusName', align:'center',width:100, title: '提现状态'},
            {field: 'cashFee', align:'center',width:100, title: '提现手续费'},
            {field: 'outAmount', align:'center',width:100, title: '出款金额'},
            {field: 'name', align:'center',title: '出款账户名'},
            {field: 'cardNo', align:'center',width:170, title: '出款账户号'},
            {field: 'bankName', align:'center',title: '出款银行'},
            {field: 'createTime', align:'center',width:180, title: '创建时间'}
        ]];
    };

    /**
     * 点击查询按钮
     */
    DeptCashFlow.search = function () {
        var queryData = {};
        queryData['beginTime'] = $("#beginTime").val();
        queryData['endTime'] = $("#endTime").val();
        queryData['deptId'] = $("#deptId").val();
        queryData['deptType'] = $("#deptType").val();
        queryData['cashStatusName'] = $("#cashStatusName").val();
        queryData['name'] = $("#name").val();
        table.reload(DeptCashFlow.tableId, {where: queryData});
    };

    /**
     * 导出excel按钮
     */
    DeptCashFlow.exportExcel = function () {
        DownLoadFile({
            url: Feng.ctxPath + '/partner/deptCashFlowExcelList?beginTime=' + $("#beginTime").val() + '&endTime=' + $("#endTime").val()
                + '&cashStatusName=' + $("#cashStatusName").val() + '&deptId=' + $("#deptId").val()
                + '&name=' + $("#name").val()
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
        elem: '#' + DeptCashFlow.tableId,
        url: Feng.ctxPath + '/partner/deptCashFlowList',
        where:{beginTime:Feng.currentDate()},
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: DeptCashFlow.initColumn()
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
    	DeptCashFlow.search();
    });

    // 搜索按钮点击事件
    $('#btnExp').click(function () {
        DeptCashFlow.exportExcel();
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
