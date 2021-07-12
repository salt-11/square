


layui.use(['layer', 'form', 'admin', 'laydate', 'ax'], function () {
    var $ = layui.jquery;
    var $ax = layui.ax;
    var form = layui.form;
    var admin = layui.admin;
    var laydate = layui.laydate;
    var layer = layui.layer;

    // 让当前iframe弹层高度适应
    admin.iframeAuto();

    //获取用户信息
    var ajax = new $ax(Feng.ctxPath + "/business/mch/getMchInfo?mchId=" + Feng.getUrlParam("mchId"));
    var result = ajax.start();
    $("#mchId").html(result.data.mchId);
    $("#mchName").html(result.data.mchName);
    $("#deptId").html(result.data.partnerId);
    $("#mobile").html(result.data.mobile);
    $("#mchAddress").html(result.data.mchAddress);
    $("#customerName").html(result.data.customerName);
    $("#customerIdentNo").html(result.data.customerIdentNo);

});