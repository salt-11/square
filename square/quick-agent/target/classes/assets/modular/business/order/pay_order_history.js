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
            {field: 'deptRate', width:100, align:'center',title: '渠道商费率'},
            {field: 'deptAmount', width:100, align:'center',title: '渠道商利润'},
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





  //渲染时间选择框
    laydate.render({
        elem: '#beginTime'
    });

    //渲染表格
    var tableResult = table.render({
        elem: '#' + PayOrder.tableId,
        url: Feng.ctxPath + '/order/payOrderHistoryList',
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







    // 工具条点击事件
    table.on('tool(' + PayOrder.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;

    });
});
