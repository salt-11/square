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
    var DeptRateChannel = {
        tableId: "deptRateChannelTable"   //表格id
    };

    /**
     * 初始化表格的列
     */
    DeptRateChannel.initColumn = function () {
        return [[
            {field: 'id', hide: true, width:80, title: 'ID'},
            {field: 'channel',  width:150,title: '通道'},
            {field: 'deptId',  width:150,title: '渠道号'},
            {field: 'bankCode', width:150,title: '银行编码'},
            {field: 'bankName', width:150, title: '银行名称'},
            {field: 'costRate', width:150, title: '交易费率'},
            {field: 'cashRate', width:150, title: '提现手续费'},
            {field: 'cardAuthRate', width:240, title: '银行卡鉴权手续费'},
            {field: 'createTime', title: '创建时间'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 160}
        ]];
    };

    /**
     * 点击查询按钮
     */
    DeptRateChannel.search = function () {
        var queryData = {};
        queryData['channel'] = $("#channel").val();
        queryData['deptId'] = $("#deptId").val();
        table.reload(DeptRateChannel.tableId, {where: queryData});
    };
    /**
     * 弹出添加对话框
     */
    DeptRateChannel.add = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '添加平台通道费率',
            content: Feng.ctxPath + '/partner/deptRateChannelAdd',
            end: function () {
                admin.getTempData('formOk') && table.reload(DeptRateChannel.tableId);
            }
        })
    };
    /**
     * 点击编辑按钮时
     *
     * @param data 点击按钮时候的行数据
     */
    DeptRateChannel.onEdit = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            // area: ['480px', '500px'],//设置窗口大小
            title: '编辑平台通道费率',
            content: Feng.ctxPath + '/partner/deptRateChannelEdit?id='+data.id,
            end: function () {
                admin.getTempData('formOk') && table.reload(DeptRateChannel.tableId);
            }
        });
    };
    /**
     * 删除操作
     */
    DeptRateChannel.onDelete = function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/partner/delete", function (data) {
                if(data.success){
                    Feng.success("通道删除成功!");
                    table.reload(DeptRateChannel.tableId);
                }else{
                    Feng.error(data.message);
                }
            }, function (data) {
                Feng.error("通道删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("id", data.id);
            ajax.start();
        };
        Feng.confirm("确定要删除改通道吗？", operation);
    };


    // 渲染表格
    var tableResult = table.render({
        elem: '#' + DeptRateChannel.tableId,
        url: Feng.ctxPath + '/partner/deptRateChannelList',
        where:{beginTime:Feng.currentDate()},
        page: true,
        height: "full-158",
        cellMinWidth: 100,
        cols: DeptRateChannel.initColumn()
    });


    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
    	DeptRateChannel.search();
    });
    // 添加按钮点击事件
    $('#btnAdd').click(function () {
        DeptRateChannel.add();
    });
    // 工具条点击事件
    table.on('tool(' + DeptRateChannel.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;

        if (layEvent === 'edit') {
            DeptRateChannel.onEdit(data);
        } else if (layEvent === 'delete') {
            DeptRateChannel.onDelete(data);
        }
    });
});
