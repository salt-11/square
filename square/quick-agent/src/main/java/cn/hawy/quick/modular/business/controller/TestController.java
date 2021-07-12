package cn.hawy.quick.modular.business.controller;

import java.io.Writer;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hawy.quick.modular.api.dto.MchBalanceQueryDto;
import cn.hawy.quick.modular.api.entity.PayOrderContact;
import cn.hawy.quick.modular.api.entity.TPayOrder;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


import cn.hawy.quick.modular.api.service.TPayOrderService;
import cn.hawy.quick.modular.api.utils.DateUtils;
import cn.hawy.quick.modular.api.utils.ExportExcelUtil;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/test")
public class TestController {
    @Autowired
    TPayOrderService payOrderService;

    @RequestMapping(value = "/exportContactList")
    public void exportContactList(@RequestParam(required = false) String beginTime,
                                  @RequestParam(required = false) String endTime,
                                  @RequestParam(required = false) String deptId,
                                  @RequestParam(required = false) String outTradeNo,
                                  @RequestParam(required = false) String mchId,
                                  @RequestParam(required = false) Integer orderStatus,
                                  @RequestParam(required = false) String deptType,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
//


    }

    public static void main(String[] args) {
        //JSONArray jsonArray = new JSONArray();
        List<MchBalanceQueryDto> list = new ArrayList<>();
        MchBalanceQueryDto a = new MchBalanceQueryDto();
        a.setMchId("1");
        a.setPartnerId("1");
        a.setReqTime("1");
        a.setSignature("1");
        list.add(a);
        MchBalanceQueryDto b = new MchBalanceQueryDto();
        b.setMchId("2");
        b.setPartnerId("2");
        b.setReqTime("2");
        b.setSignature("2");
        list.add(b);
        JSONObject json = new JSONObject();
        json.put("order",list);
        //jsonArray.add(json);
        //System.out.println(XmlUtil.);
        System.out.println(JSONUtil.toXmlStr(json));
    }


}


