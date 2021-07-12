
layui.use(['layer', 'form', 'admin', 'laydate', 'ax', 'table'], function () {
    var $ = layui.jquery;
    var $ax = layui.ax;
    var form = layui.form;
    var admin = layui.admin;
    var table = layui.table;
    var laydate = layui.laydate;
    var layer = layui.layer;

    // 让当前iframe弹层高度适应
    admin.iframeAuto();

    var ajax = new $ax(Feng.ctxPath + "/dept/balance");
    var result = ajax.start();
    form.val('deptForm', result);

    // var DeptCashFlow = {
    //     tableId: "deptCashTable"   //表格id
    // };

    /**
     * 初始化表格的列
     */
    // DeptCashFlow.initColumn = function () {
    //     return [[
    //         {field: 'id', width:180, title: '提现号'},
    //         {field: 'deptId', title: '渠道号'},
    //         {field: 'deptName',  title: '渠道名称'},
    //         {field: 'cashAmount', width:100, title: '提现金额'},
    //         {field: 'cashStatusName', width:100, title: '提现状态'},
    //         {field: 'cashFee', width:100, title: '提现手续费'},
    //         {field: 'outAmount', width:100, title: '出款金额'},
    //         {field: 'name', title: '出款账户名'},
    //         {field: 'cardNo', title: '出款账户号'},
    //         {field: 'bankName', title: '出款银行'},
    //         {field: 'createTime', width:180, title: '创建时间'}
    //     ]];
    // };

    // 渲染表格
    // var tableResult = table.render({
    //     elem: '#' + DeptCashFlow.tableId,
    //     url: Feng.ctxPath + '/partner/deptCashFlowList',
    //     page: true,
    //     height: "full-158",
    //     cellMinWidth: 100,
    //     cols: DeptCashFlow.initColumn()
    // });



    // 表单提交事件
    form.on('submit(btnSubmit)', function (data) {
        var ajax = new $ax(Feng.ctxPath + "/partner/deptCash", function (data) {
            Feng.success("提现申请成功！");

            //传给上个页面，刷新table用
            admin.putTempData('formOk', true);

            //关掉对话框
            admin.closeThisDialog();
        }, function (data) {
            Feng.error("提现申请失败！" + data.responseJSON.message)
        });
        if(data.field.cashAmount != null && data.field.cashAmount != "" && data.field.cashAmount !="0"){
        	var exp = /^(([1-9]\d*)|\d)(\.\d{1,2})?$/;
        	if(!exp.test(data.field.cashAmount)){
        		Feng.error('金额格式不对');
        		return;
        	}
        }else{
        	Feng.error('金额不能为空或0');
        	return;
        }
        ajax.set(data.field);
        ajax.start();
    });
});