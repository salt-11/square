layui.use(['layer', 'form', 'admin', 'ax'], function () {
    var $ = layui.jquery;
    var $ax = layui.ax;
    var form = layui.form;
    var admin = layui.admin;
    var layer = layui.layer;

    // 让当前iframe弹层高度适应
    admin.iframeAuto();

    //获取详情信息，填充表单
    var ajax = new $ax(Feng.ctxPath + "/agent/agentDetail?id=" + Feng.getUrlParam("id"));
    var result = ajax.start();
    form.val('agentInfoForm', result.data);
    // 表单提交事件
    form.on('submit(btnSubmit)', function (data) {
        var ajax = new $ax(Feng.ctxPath + "/agent/agentEdit", function (data) {
            Feng.success("修改成功！");

            //传给上个页面，刷新table用
            admin.putTempData('formOk', true);

            //关掉对话框
            admin.closeThisDialog();
        }, function (data) {
            Feng.error("修改失败！" + data.responseJSON.message)
        });
        ajax.set(data.field);
        ajax.start();
    });
    form.verify({
        positiveNumber: [
            /^[+]{0,1}(\d+)$|^[+]{0,1}(\d+\.\d+)$/
            ,'只能填写正数'
        ],
        positiveInteger: [
            /^[1-9][0-9]{19}$/
            ,'20位存数字'
        ]
        ,content: function(value){
            layedit.sync(editIndex);
        }
    });
});
