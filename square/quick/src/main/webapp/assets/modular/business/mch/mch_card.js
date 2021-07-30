layui.use(['layer', 'table', 'admin', 'ax', 'laydate'], function () {
    var $ = layui.$;
    var $ax = layui.ax;
    var layer = layui.layer;
    var table = layui.table;
    var laydate = layui.laydate;
    var admin = layui.admin;

    /**
     * 系统管理--操作日志
     */
    var MchCard = {
        tableId: "mchCardTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    MchCard.initColumn = function () {
        return [[
            {type: 'checkbox'},
            {field: 'mchId', width: 180, title: '商户号'},
            {field: 'mchName', width: 180, title: '商户名称'},
            {field: 'bankCardNo', width: 180, title: '银行卡号'},
            {field: 'bankCode', width: 180, title: '银行编号'},
            {field: 'bankCardType', width: 180, title: '银行卡类型'},
            {field: 'createTime', width: 180, title: '创建时间'},
        ]];
    };

    /**
     * 点击查询按钮
     */
    MchCard.search = function () {
        var queryData = {};
        queryData['mchId'] = $("#mchId").val();
        queryData['bankCardNo'] = $("#bankCardNo").val();
        table.reload(MchCard.tableId, {where: queryData});
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + MchCard.tableId,
        url: Feng.ctxPath + '/business/mch/mchCardList',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: MchCard.initColumn()
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
        MchCard.search();
    });
});
