layui.use(['layer', 'table', 'ax', 'laydate'], function () {
    var $ = layui.$;
    var $ax = layui.ax;
    var layer = layui.layer;
    var table = layui.table;
    var laydate = layui.laydate;
    
    /**
     * 系统管理--操作日志
     */
    var PayOrder = {
        tableId: "payOrderTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    PayOrder.initColumn = function () {
        return [[
            {field: 'orderId', width:180, align:'center',title: '订单号'},
            {field: 'mchId', width:180, align:'center',title: '商户号'},
            {field: 'mchName', align:'center',title: '商户名称'},
            {field: 'bankCardNo', width:160, align:'center',title: '银行卡号'},
            {field: 'channelNo', templet: '#channelNoTpl', width:130, align:'center',title: '支付类型'},
            {field: 'deptId', width:100, align:'center',title: '渠道号'},
            {field: 'orderAmount', width:100, align:'center',title: '订单金额'},
            {field: 'mchRate', width:100, align:'center',title: '商户费率'},
            {field: 'mchFee', width:100, align:'center',title: '商户手续费'},
            {field: 'deptRate', width:100, align:'center',title: '渠道费率'},
            {field: 'deptAmount', width:100, align:'center',title: '渠道利润'},
            {field: 'agentId', width:100, align:'center',title: '代理号'},
            {field: 'agentRate', width:100, align:'center',title: '代理费率'},
            {field: 'agentAmount', width:100, align:'center',title: '代理利润'},
            {field: 'orderStatus', width:100, align:'center',templet: '#orderStatusTpl', title: '订单状态'},
            {field: 'returnMsg', align:'center', title: '错误原因'},
            {field: 'outTradeNo', align:'center', width:180, title: '外部订单号'},
            {field: 'orderTime', width:180, align:'center',title: '订单时间'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 100}
        ]];
    };
    
 

    /**
     * 点击查询按钮
     */
    PayOrder.search = function () {
        var queryData = {};
        queryData['beginTime'] = $("#beginTime").val();
        queryData['endTime'] = $("#endTime").val();
        queryData['deptId'] = $("#deptId").val();
        queryData['orderId'] = $("#orderId").val();
        queryData['outTradeNo'] = $("#outTradeNo").val();
        queryData['mchId'] = $("#mchId").val();
        queryData['orderStatus'] = $("#orderStatus").val();
        queryData['channelNo'] = $("#channelNo").val();
        table.reload(PayOrder.tableId, {where: queryData});
    };

    /**
     * 导出excel按钮
     */
    PayOrder.exportExcel = function () {
        DownLoadFile({
            url: Feng.ctxPath + '/order/payOrderExcelList?beginTime=' + $("#beginTime").val() + '&endTime=' + $("#endTime").val()
                                + '&deptId=' + $("#deptId").val() + '&orderId=' + $("#orderId").val() + '&outTradeNo=' + $("#outTradeNo").val() + '&mchId=' + $("#mchId").val()
                                + '&orderStatus=' + $("#orderStatus").val() + '&channelNo=' + $("#channelNo").val()
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


  //渲染时间选择框
    laydate.render({
        elem: '#beginTime',
        format: 'yyyy-MM-dd',
        value: new Date()
    });

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + PayOrder.tableId,
        url: Feng.ctxPath + '/order/payOrderList',
        where:{beginTime:Feng.currentDate()},
        page: true,
        limits:[10,50,100,200,500,1000],
        height: "full-158",
        cellMinWidth: 100,
        cols: PayOrder.initColumn()
    });
    
    

    //渲染时间选择框
    laydate.render({
        elem: '#endTime'
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	PayOrder.search();
    });

    // 搜索按钮点击事件
    $('#btnExp').click(function () {
    	PayOrder.exportExcel();
    });
    
    PayOrder.onNotify = function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/order/notify", function () {
                table.reload(PayOrder.tableId);
                Feng.success("发送成功!");
            }, function (data) {
                Feng.error("发送失败!" + data.responseJSON.message + "!");
            });
            ajax.set("orderId", data.orderId);
            ajax.start();
        };
        Feng.confirm("是否异步通知" + data.orderId + "?", operation);
    };
    
    PayOrder.onQuery = function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/order/query", function () {
                table.reload(PayOrder.tableId);
                Feng.success("查询成功!");
            }, function (data) {
                Feng.error("查询失败!" + data.responseJSON.message + "!");
            });
            ajax.set("orderId", data.orderId);
            ajax.start();
        };
        Feng.confirm("是否查询" + data.orderId + "?", operation);
    };


    // 工具条点击事件
    table.on('tool(' + PayOrder.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;

        if (layEvent === 'query') {
        	PayOrder.onQuery(data);
        }
    });
});
