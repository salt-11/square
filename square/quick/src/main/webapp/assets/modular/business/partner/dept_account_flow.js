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
    var DeptAccountFlow = {
        tableId: "deptAccountFlowTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    DeptAccountFlow.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'id', hide: true, width:80, title: 'ID'},
            {field: 'deptId',  title: '渠道号'},
            {field: 'deptName', title: '渠道名称'},
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
    DeptAccountFlow.search = function () {
        var queryData = {};
        queryData['beginTime'] = $("#beginTime").val();
        queryData['endTime'] = $("#endTime").val();
        queryData['deptId'] = $("#deptId").val();
        queryData['bizTypeName'] = $("#bizTypeName").val();
        queryData['directionName'] = $("#directionName").val();
        queryData['deptType'] = $("#deptType").val();
        table.reload(DeptAccountFlow.tableId, {where: queryData});
    };

    /**
     * 导出excel按钮
     */
    DeptAccountFlow.exportExcel = function () {
        DownLoadFile({
            url: Feng.ctxPath + '/partner/deptAccountFlowExcelList?beginTime=' + $("#beginTime").val() + '&endTime=' + $("#endTime").val()
                + '&deptId=' + $("#deptId").val() + '&bizTypeName=' + $("#bizTypeName").val() + '&directionName=' + $("#directionName").val()
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
        elem: '#' + DeptAccountFlow.tableId,
        url: Feng.ctxPath + '/partner/deptAccountFlowList',
        where:{beginTime:Feng.currentDate()},
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: DeptAccountFlow.initColumn()
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
    	DeptAccountFlow.search();
    });
    
    // 搜索按钮点击事件
    $('#btnCash').click(function () {
    	admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '提现申请',
            content: Feng.ctxPath + '/partner/deptCashApply',
            end: function () {
                admin.getTempData('formOk') && table.reload(DeptAccountFlow.tableId);
            }
        });
        //PartnerAccountFlow.search();
    });

    // 搜索按钮点击事件
    $('#btnExp').click(function () {
        DeptAccountFlow.exportExcel();
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
