layui.use(['layer', 'table', 'ax', 'laydate'], function () {
    var $ = layui.$;
    var $ax = layui.ax;
    var layer = layui.layer;
    var table = layui.table;
    var laydate = layui.laydate;

    /**
     * 系统管理--操作日志
     */
    var CardAuth = {
        tableId: "cardAuthTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    CardAuth.initColumn = function () {
        return [[
            {field: 'id', width:80, align:'center',title: '编号'},
            {field: 'deptId', width:100, align:'center',title: '渠道号'},
            {field: 'authType', templet: '#authTypeTpl', width:130, align:'center',title: '鉴权类型'},
            {field: 'amount', width:100, align:'center',title: '鉴权费'},
            {field: 'status', templet: '#statusTpl', width:130, align:'center',title: '鉴权状态'},
            {field: 'realname', width:100, align:'center',title: '姓名'},
            {field: 'cardNo', width:170, align:'center',title: '银行卡号'},
            {field: 'cardType', templet: '#cardTypeTpl', width:130, align:'center',title: '卡类型'},
            {field: 'idNo', width:170, align:'center',title: '证件号'},
            {field: 'createTime', width:180, align:'center',title: '订单时间'}
        ]];
    };



    /**
     * 点击查询按钮
     */
    CardAuth.search = function () {
        var queryData = {};
        queryData['beginTime'] = $("#beginTime").val();
        queryData['endTime'] = $("#endTime").val();
        queryData['deptId'] = $("#deptId").val();
        table.reload(CardAuth.tableId, {where: queryData});
    };




  //渲染时间选择框
    laydate.render({
        elem: '#beginTime',
        format: 'yyyy-MM-dd',
        value: new Date()
    });

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + CardAuth.tableId,
        url: Feng.ctxPath + '/business/mch/cardAuthList',
        where:{beginTime:Feng.currentDate()},
        page: true,
        limits:[10,50,100,200,500,1000],
        height: "full-158",
        cellMinWidth: 100,
        cols: CardAuth.initColumn()
    });



    //渲染时间选择框
    laydate.render({
        elem: '#endTime'
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	CardAuth.search();
    });

    CardAuth.tongji = function(){
    	var ajax = new $ax(Feng.ctxPath + "/business/mch/cardAuthTj", function (data) {
    		top.layui.admin.open({
                area: '600px',
                title: '鉴权统计',
                content: ''+data.data
            });
	    }, function (data) {
	        Feng.error("2");
	    });
    	ajax.set("beginTime", $("#beginTime").val());
	    ajax.set("endTime", $("#endTime").val());
	    ajax.set("deptId", $("#deptId").val());
	    ajax.start();
    }


 // 搜索按钮点击事件
    $('#btnTj').click(function () {
    	CardAuth.tongji();
    });



//    // 工具条点击事件
//    table.on('tool(' + PayOrder.tableId + ')', function (obj) {
//        var data = obj.data;
//        var layEvent = obj.event;
//
//        if (layEvent === 'query') {
//        	PayOrder.onQuery(data);
//        }
//    });
});
