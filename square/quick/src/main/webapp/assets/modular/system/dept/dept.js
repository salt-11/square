layui.use(['layer', 'form', 'ztree', 'laydate', 'admin', 'ax', 'table', 'treetable'], function () {
    var layer = layui.layer;
    var form = layui.form;
    var $ZTree = layui.ztree;
    var $ax = layui.ax;
    var laydate = layui.laydate;
    var admin = layui.admin;
    var table = layui.table;
    var treetable = layui.treetable;

    /**
     * 系统管理--专业管理
     */
    var Dept = {
        tableId: "deptTable",
        condition: {
            deptId: ""
        }
    };

    /**
     * 初始化表格的列
     */
    Dept.initColumn = function () {
        return [[
            {type: 'numbers'},
            {field: 'deptId', sort: true, title: '专业编号'},
            {field: 'fullName', sort: true, title: '专业名称'},
            {align: 'center', toolbar: '#tableBar', title: '操作', minWidth: 200}
        ]];
    };

    /**
     * 点击查询按钮
     */
    Dept.search = function () {
        var queryData = {};
        queryData['condition'] = $("#name").val();
        queryData['deptId'] = Dept.condition.deptId;
        Dept.initTable(Dept.tableId, {where: queryData});
    };

    /**
     * 选择部门时
     */
    Dept.onClickDept = function (e, treeId, treeNode) {
        Dept.condition.deptId = treeNode.id;
        Dept.search();
    };

    /**
     * 弹出添加
     */
    Dept.openAddDept = function () {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '添加专业',
            content: Feng.ctxPath + '/dept/dept_add',
            end: function () {
                admin.getTempData('formOk') && Dept.initTable(Dept.tableId);
            }
        });
    };

    /**
     * 导出excel按钮
     */
    Dept.exportExcel = function () {
        var checkRows = table.checkStatus(Dept.tableId);
        if (checkRows.data.length === 0) {
            Feng.error("请选择要导出的数据");
        } else {
            table.exportFile(tableResult.config.id, checkRows.data, 'xls');
        }
    };

    /**
     * 点击编辑专业
     *
     * @param data 点击按钮时候的行数据
     */
    Dept.onEditDept = function (data) {
        admin.putTempData('formOk', false);
        top.layui.admin.open({
            type: 2,
            title: '修改专业',
            content: Feng.ctxPath + '/dept/dept_update?deptId=' + data.deptId,
            end: function () {
                admin.getTempData('formOk') && Dept.initTable(Dept.tableId);
            }
        });
    };

    /**
     * 点击删除专业
     *
     * @param data 点击按钮时候的行数据
     */
    Dept.onDeleteDept = function (data) {
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/dept/delete", function () {
                Feng.success("删除成功!");
                Dept.initTable(Dept.tableId);
            }, function (data) {
                Feng.error("删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("deptId", data.deptId);
            ajax.start();
        };
        Feng.confirm("是否删除专业 " + data.simpleName + "?", operation);
    };

    Dept.initTable = function (deptId, data) {
        return treetable.render({
            elem: '#' + deptId,
            url: Feng.ctxPath + '/dept/listTree',
            where: data,
            page: false,
            height: "full-158",
            cellMinWidth: 100,
            cols: Dept.initColumn(),
            treeColIndex: 2,
            treeSpid: "0",
            treeIdName: 'deptId',
            treePidName: 'pid',
            treeDefaultClose: false,
            treeLinkage: true

        });
    };
    var tableResult = Dept.initTable(Dept.tableId);

    //初始化左侧部门树
    var ztree = new $ZTree("deptTree", "/dept/tree");
    ztree.bindOnClick(Dept.onClickDept);
    ztree.init();

    // 搜索按钮点击事件
    $('#btnSearch').click(function () {
        Dept.search();
    });

    // 添加按钮点击事件
    $('#btnAdd').click(function () {
        Dept.openAddDept();
    });

    // 导出excel
    $('#btnExp').click(function () {
        Dept.exportExcel();
    });

    // 工具条点击事件
    table.on('tool(' + Dept.tableId + ')', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;

        if (layEvent === 'edit') {
            Dept.onEditDept(data);
        } else if (layEvent === 'delete') {
            Dept.onDeleteDept(data);
        }
    });
});
