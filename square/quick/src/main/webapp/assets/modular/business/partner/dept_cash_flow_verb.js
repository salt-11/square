layui.use(['layer', 'table', 'ax', 'laydate'], function () {
    var $ = layui.$;
    var $ax = layui.ax;
    var layer = layui.layer;
    var table = layui.table;
    var laydate = layui.laydate;
    var admin = layui.admin;
    /**
     * 系统管理--操作日志
     */
    var DeptCashFlowVerb = {
        tableId: "deptCashFlowVerbTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    DeptCashFlowVerb.initColumn = function () {
        return [[
            {field: 'id', align: 'center', width:180, title: '提现号'},
            {field: 'deptId', align: 'center', title: '渠道号'},
            {field: 'deptName',  align: 'center', title: '渠道名称'},
            {field: 'cashAmount', align: 'center', width:100, title: '提现金额'},
            {field: 'cashStatusName', align: 'center', width:100, title: '提现状态'},
            {field: 'cashFee', align: 'center', width:100, title: '提现手续费'},
            {field: 'outAmount', align: 'center', width:100, title: '出款金额'},
            {field: 'name', align: 'center', title: '出款账户名'},
            {field: 'cardNo', align: 'center', width:160, title: '出款账户号'},
            {field: 'bankName', align: 'center', title: '出款银行'},
            {field: 'createTime', align: 'center', width:180, title: '创建时间'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 160}
        ]];
    };

    /**
     * 点击查询按钮
     */
    DeptCashFlowVerb.search = function () {
        var queryData = {};
        queryData['partnerId'] = $("#partnerId").val();
        table.reload(DeptCashFlowVerb.tableId, {where: queryData});
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + DeptCashFlowVerb.tableId,
        url: Feng.ctxPath + '/partner/deptCashFlowList',
        where:{cashStatusName:1},
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: DeptCashFlowVerb.initColumn()
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	DeptCashFlowVerb.search();
    });
    
    DeptCashFlowVerb.onAccept = function (data) {
        if (data.id == null){
            Feng.error("请选择订单！");
        } else {
        	var operation = function () {
                var ajax = new $ax(Feng.ctxPath + "/partner/deptCashFlowVerbAccept", function () {
                    Feng.success("通过成功!");
                }, function (data) {
                    Feng.error("通过失败!" + data.responseJSON.message + "!");
                });
                ajax.set("id", data.id);
                ajax.start();
                table.reload(DeptCashFlowVerb.tableId);
            }
        	Feng.confirm("是否通过提现订单?"+ data.id + "?" , operation);
        }
    };
    
    DeptCashFlowVerb.onRefuse = function (data) {
        if (data.id == null){
            Feng.error("请选择订单");
        } else {
            var operation = function () {
                var ajax = new $ax(Feng.ctxPath + "/partner/deptCashFlowVerbRefuse", function () {
                    Feng.success("拒绝成功!");
                }, function (data) {
                    Feng.error("拒绝失败!" + data.responseJSON.message + "!");
                });
                ajax.set("id", data.id);
                ajax.start();
                table.reload(DeptCashFlowVerb.tableId);
            };
            Feng.confirm("是否拒绝提现订单" + data.id + "?", operation);
        }
    };



    // 工具条点击事件
    table.on('tool(' + DeptCashFlowVerb.tableId + ')', function (obj) {
    	var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'accept') {
        	DeptCashFlowVerb.onAccept(data);
        }
        if (layEvent === 'refuse') {
        	DeptCashFlowVerb.onRefuse(data);
        }
    });
});
