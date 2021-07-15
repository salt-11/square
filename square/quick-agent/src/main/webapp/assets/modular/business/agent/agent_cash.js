
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

    var ajax = new $ax(Feng.ctxPath + "/agent/balance");
    var result = ajax.start();
    form.val('agentForm', result);
    



    // 表单提交事件
    form.on('submit(btnSubmit)', function (data) {
        var ajax = new $ax(Feng.ctxPath + "/agent/agentCash", function (data) {
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