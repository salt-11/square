package cn.hawy.quick.modular.api.controller;

import cn.hawy.quick.core.common.RestResponse;
import cn.hawy.quick.core.common.exception.RestException;
import cn.hawy.quick.core.util.IdGenerator;
import cn.hawy.quick.core.util.MapUtils;
import cn.hawy.quick.core.util.RSA;
import cn.hawy.quick.modular.api.channel.EfpsChannel;
import cn.hawy.quick.modular.api.dto.*;
import cn.hawy.quick.modular.api.entity.*;
import cn.hawy.quick.modular.api.mq.MqMchCashNotify;
import cn.hawy.quick.modular.api.service.*;
import cn.hawy.quick.modular.api.validate.MchValidate;
import cn.hawy.quick.modular.system.entity.Dept;
import cn.hawy.quick.modular.system.service.DeptService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/shop")
public class ShopController {

	private Logger log = LoggerFactory.getLogger(this.getClass());



	@RequestMapping(value = "/wowG",method = RequestMethod.POST)
	@ResponseBody
	public Object wowG(String url,String bodyMap,String key) {
		log.info("下游请求报文-wowG:wowG={}",JSON.toJSONString(bodyMap));
		Map body = JSONUtil.parseObj(bodyMap);
		String result = HttpRequest.get(url)
				.header("Authorization","Bearer "+key)
				.header("Content-Type","application/x-www-form-urlencoded")
				.form(body)
				.execute().body();
		return RestResponse.success(result);
	}

	@RequestMapping(value = "/letP1",method = RequestMethod.POST)
	@ResponseBody
	public Object letP1() {
		HashMap<String, Object> paramMap = new HashMap<>();
		paramMap.put("dateType","1");
		paramMap.put("startDatetime ","2020-06-10 00:00:00");
		paramMap.put("endDatetime","2020-06-10 23:59:59");
		String result = HttpRequest.post("https://api.rms.rakuten.co.jp/es/2.0/order/searchOrder")
				.header("Authorization","ESA U1AzMjE1MzZfRWZEcE9oTUZ0UnFwVllPRTpTTDMyMTUzNl91WldPQnBaY3pENldoM3Z2")
				.header("Content-Type","application/json; charset=utf-8")
				.form(paramMap)
				.execute().body();
		return RestResponse.success(result);
	}

	@RequestMapping(value = "/letP2",method = RequestMethod.POST)
	@ResponseBody
	public Object letP2() {
		JSONObject paramJson = new JSONObject();
		paramJson.put("dateType","1");
		paramJson.put("startDatetime ","2020-06-10 00:00:00");
		paramJson.put("endDatetime","2020-06-10 23:59:59");
		String result = HttpRequest.post("https://api.rms.rakuten.co.jp/es/2.0/order/searchOrder")
				.header("Authorization","ESA U1AzMjE1MzZfRWZEcE9oTUZ0UnFwVllPRTpTTDMyMTUzNl91WldPQnBaY3pENldoM3Z2")
				.header("Content-Type","application/json; charset=utf-8")
				.form(paramJson)
				.execute().body();
		return RestResponse.success(result);
	}

	@RequestMapping(value = "/letP3",method = RequestMethod.POST)
	@ResponseBody
	public Object letP3() {
		JSONObject paramJson = new JSONObject();
		paramJson.put("dateType",1);
		paramJson.put("startDatetime","2020-06-10T07:50:37+0900");
		paramJson.put("endDatetime","2020-06-10T23:59:59+0900");
		String result = HttpRequest.post("https://api.rms.rakuten.co.jp/es/2.0/order/searchOrder")
				.header("Authorization","ESA U1AzMjE1MzZfRWZEcE9oTUZ0UnFwVllPRTpTTDMyMTUzNl91WldPQnBaY3pENldoM3Z2")
				.header("Content-Type","application/json; charset=utf-8")
				.body(paramJson)
				.execute().body();
		return RestResponse.success(result);
	}

	@RequestMapping(value = "/letP4",method = RequestMethod.POST)
	@ResponseBody
	public Object letP4() {
		String dateStr = "2020-06-10T00:00:00+0900";
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+0900");
		DateTime date = DateUtil.parse(dateStr,df);
		JSONObject paramJson = new JSONObject();
		paramJson.put("dateType",1);
		paramJson.put("startDatetime", date);
		paramJson.put("endDatetime","2020-06-10 23:59:59");
		String result = HttpRequest.post("https://api.rms.rakuten.co.jp/es/2.0/order/searchOrder")
				.header("Authorization","ESA U1AzMjE1MzZfRWZEcE9oTUZ0UnFwVllPRTpTTDMyMTUzNl91WldPQnBaY3pENldoM3Z2")
				.header("Content-Type","application/json; charset=utf-8")
				.body(paramJson.toString())
				.execute().body();
		return RestResponse.success(result);
	}

	@RequestMapping(value = "/letP5",method = RequestMethod.POST)
	@ResponseBody
	public Object letP5(String bodyMap) {

		String result = HttpRequest.post("https://api.rms.rakuten.co.jp/es/2.0/order/searchOrder")
				.header("Authorization","ESA U1AzMjE1MzZfRWZEcE9oTUZ0UnFwVllPRTpTTDMyMTUzNl91WldPQnBaY3pENldoM3Z2")
				.header("Content-Type","application/json; charset=utf-8")
				.body(bodyMap)
				.execute().body();
		return RestResponse.success(result);
	}



	@RequestMapping(value = "/letP6",method = RequestMethod.POST)
	@ResponseBody
	public Object letP6(String url,String bodyMap) {
		String result = HttpRequest.post(url)
				.header("Authorization","ESA U1AzMjE1MzZfRWZEcE9oTUZ0UnFwVllPRTpTTDMyMTUzNl91WldPQnBaY3pENldoM3Z2")
				.header("Content-Type","application/json; charset=utf-8")
				.body(bodyMap)
				.execute().body();
		return RestResponse.success(result);
	}








}
