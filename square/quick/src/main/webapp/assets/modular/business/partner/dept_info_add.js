/**
 * 添加或者修改页面
 */
var DeptInfoDlg = {
    data: {
        id: "",
        deptName: "",
        account: "",
        passWord: "",
        salt: "",
        agentId:"",
        name: "",
        cardNo: "",
        bankName:""
    }
};

layui.use(['form', 'admin', 'ax','laydate','upload','formSelects'], function () {
    var $ = layui.jquery;
    var $ax = layui.ax;
    var form = layui.form;
    var admin = layui.admin;


    //表单提交事件
    form.on('submit(btnSubmit)', function (data) {
        var ajax = new $ax(Feng.ctxPath + "/partner/addItem", function (data) {
            Feng.success("添加成功！");
            window.location.href = Feng.ctxPath + '/partner/deptInfo'
        }, function (data) {
            Feng.error("添加失败！" + data.responseJSON.message)
        });
        ajax.set(data.field);
        ajax.start();

        return false;
    });

    $('#cancel').click(function(){
        window.location.href = Feng.ctxPath + '/partner/deptInfo'
    });
    form.verify({
        positiveNumber: [
            /^[+]{0,1}(\d+)$|^[+]{0,1}(\d+\.\d+)$/
            ,'只能填写正数'
        ],
        positiveInteger: [
            /^[1-9][0-9]{19}$/
            ,'20位纯数字'
        ]
        ,content: function(value){
            layedit.sync(editIndex);
        }
    });
});
