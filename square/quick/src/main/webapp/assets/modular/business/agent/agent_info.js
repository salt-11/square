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
    var AgentInfo = {
        tableId: "agentInfoTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    AgentInfo.initColumn = function () {
        return [[
            // {type: 'checkbox'},
            {field: 'id', width: 180, title: '代理号'},
            {field: 'agentName', title: '代理名称'},
            {field: 'account', title: '登录账户'},
            {field: 'balance', title: '余额'},
            {field: 'name', title: '银行卡姓名'},
            {field: 'cardNo', title: '银行卡号'},
            {field: 'bankName', title: '开户行'},
            {field: 'createTime', width: 250, title: '创建时间'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 100}
        ]];
    };

    /**
     * 点击查询按钮
     */
    AgentInfo.search = function () {
        var queryData = {};
        queryData['id'] = $("#id").val();
        queryData['account'] = $("#account").val();
        queryData['agentName'] = $("#agentName").val();
        queryData['beginTime'] = $("#beginTime").val();
        queryData['endTime'] = $("#endTime").val();

        table.reload(AgentInfo.tableId, {
            where: queryData
        });
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + AgentInfo.tableId,
        url: Feng.ctxPath + '/agent/agentInfoList',
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: AgentInfo.initColumn()
    });

    //渲染时间选择框
    laydate.render({
        elem: '#beginTime',
        format: 'yyyy-MM-dd',
    });

    //渲染时间选择框
    laydate.render({
        elem: '#endTime'
    });

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	AgentInfo.search();
    });

    // 添加按钮点击事件
    $('#btnAdd').click(function () {
        AgentInfo.jumpAddPage();

    });

    /**
     * 跳转到添加页面
     */
    AgentInfo.jumpAddPage = function () {
        window.location.href = Feng.ctxPath + '/agent/agentAdd'
    };

    // 工具条点击事件
    table.on('tool(' + AgentInfo.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;

        if (layEvent === 'details') {
            AgentInfo.onDetails(data);
        } else if (layEvent === 'delete') {
            AgentInfo.onDelete(data);
        }else if (layEvent === 'edit') {
            AgentInfo.onEdit(data);
        }
    });

    /**
     *  编辑
     *
     * @param data 点击按钮时候的行数据
     */
    AgentInfo.onEdit = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '代理信息',
            content: Feng.ctxPath + '/agent/agentInfoEdit?id='+data.id,
            end: function () {
                admin.getTempData('formOk') && table.reload(AgentInfo.tableId);
            }
        });
    };

    /**
     * 删除操作
     */
    AgentInfo.onDelete = function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/agent/deleteAgent", function (data) {
                if(data.success){
                    Feng.success("删除成功!");
                    table.reload(AgentInfo.tableId);
                }else{
                    Feng.error(data.message);
                }
            }, function (data) {
                Feng.error("删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("id", data.id);
            ajax.start();
        };
        Feng.confirm("确定要删除吗？", operation);
    };


});
