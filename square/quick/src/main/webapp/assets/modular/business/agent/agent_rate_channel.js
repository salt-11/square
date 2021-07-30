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
    var AgentRateChannel = {
        tableId: "agentRateChannelTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    AgentRateChannel.initColumn = function () {
        return [[
            {field: 'id', hide: true, width:80, title: 'ID'},
            {field: 'channel',  width:150,title: '通道'},
            {field: 'agentId',  width:150,title: '代理号'},
            {field: 'bankCode', width:150,title: '银行编码'},
            {field: 'bankName', width:150, title: '银行名称'},
            {field: 'costRate', width:150, title: '交易费率'},
            {field: 'createTime', title: '创建时间'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 160}
        ]];
    };

    /**
     * 点击查询按钮
     */
    AgentRateChannel.search = function () {
        var queryData = {};
        queryData['channel'] = $("#channel").val();
        queryData['agentId'] = $("#agentId").val();
        table.reload(AgentRateChannel.tableId, {where: queryData});
    };

    /**
     * 弹出添加对话框
     */
    AgentRateChannel.add = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '添加代理通道费率',
            content: Feng.ctxPath + '/agent/agentRateChannelAdd',
            end: function () {
                admin.getTempData('formOk') && table.reload(AgentRateChannel.tableId);
            }
        })
    };
    /**
     * 点击编辑按钮时
     *
     * @param data 点击按钮时候的行数据
     */
    AgentRateChannel.onEdit = function (data) {
        console.log(123);
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '编辑代理通道费率',
            content: Feng.ctxPath + '/agent/agentRateChannelEdit?id='+data.id,
            end: function () {
                admin.getTempData('formOk') && table.reload(AgentRateChannel.tableId);
            }
        });
    };
    /**
     * 删除操作
     */
    AgentRateChannel.onDelete = function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/agent/delete", function (data) {
                if(data.success){
                    Feng.success("通道删除成功!");
                    table.reload(AgentRateChannel.tableId);
                }else{
                    Feng.error(data.message);
                }
            }, function (data) {
                Feng.error("通道删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("id", data.id);
            ajax.start();
        };
        Feng.confirm("确定要删除该通道吗？", operation);
    };

    // 渲染表格
    var tableResult = table.render({
        elem: '#' + AgentRateChannel.tableId,
        url: Feng.ctxPath + '/agent/agentRateChannelList',
        where:{beginTime:Feng.currentDate()},
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: AgentRateChannel.initColumn()
    });


    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	AgentRateChannel.search();
    });
    // 添加按钮点击事件
    $('#btnAdd').click(function () {
        AgentRateChannel.add();
    });
    // 工具条点击事件
    table.on('tool(' + AgentRateChannel.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;

        if (layEvent === 'edit') {
            AgentRateChannel.onEdit(data);
        } else if (layEvent === 'delete') {
            AgentRateChannel.onDelete(data);
        }
    });
});
