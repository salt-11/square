package cn.hawy.quick.modular.api.test;

import java.io.File;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import cn.hawy.quick.core.util.Base64Utils;
import cn.hawy.quick.core.util.IdGenerator;
import cn.hawy.quick.core.util.RSA;
import cn.hawy.quick.modular.api.dto.sum.OrderNotifyDto;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.format.annotation.DateTimeFormat;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SumTestController {

	public static String PrivateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCkpiK7FMpkCGH+ZNGVC2Ng6sVw35xI61R2CZiaOVRAXs1lX1pz/T+xyXB1CHUox6SmSvkVHS+fu6kqFqTEpAgcyCbCmmLmqcsWeRVSX0MfIYs3u7wy7IwH9/A2ECCGedAZcJBmJPuoPUcyZ9/O2JTK4hBDrxibg+rjjGiD5A85Rdu7hAUEteeV/fEH8MamyWHps4unVKK6cOa4ZWtMZcMu7Ni3/VdPJ9D8FB8jvqOX6/d18fuqkiZ3rA9wsUvBngJlLx4C+XvkBwgx7rvU2J7pJBF90rtp7GXlgJ4wJ/fxz2pdQ88jCP+M1IKNsMjfIrUHip232d9c0wVEhizP5QwzAgMBAAECggEAHtnieBuVMNlBQ2Gww7S2klprVJMRvbgaw2NY7M0BNG6PHgeX81Pos2+DWuqSyWlfOKmjsokde1i2geRsS0xRGNOIL76t+XqnSza1ABJJiXPHmlHbGc+pDXUv1XGAJrJ8g8fLxtf3L7cLuC4uiZubhciGM6iDe+BkCu8sRkpdb+WQmflc6o3bRB1HWmVj/cyIcJai+JjBmNbGmnr3btv5oAUZ4TifNqgw0ak6W0wn2GrUQouFyj/cJnU8sXNJ76lId01wv/auTRAdBY4FWBm0Jw2Ym175QHwOLCXNZiFkx8r/J1GHrzQZazFS6P8ixPxt/SRgOQU/nXpDEKAyakZhAQKBgQD7VJ3omfnqQt6uDZWyFKwhHiG0Y8LebgMKhL7zEA0FiCYpgTmMcGbq1GddLEmxxUqBlvDjXa45VRYbrDlxvEpPiApQPlr2J81mxOOx99LC0J1IKL3xBnkpniTEGPJcUBC+ZOvIEGIeg9Q5YwFLX8StBB4PYf79+HcpxqPYHs7U8QKBgQCntT4GUikbIumnjNoyW2arwhUvgQghqNCaTKJCqa4eoaH0SzncOtNsCXIixMnEiAbDJI3Yo37k1Nbtynjyzw7RXxr2g+wb6/S1fNLGW8q7jxE3jPWsjAhcS5yx+4zFTQcS/SqEzlymrbfHM1Cu8aADJOWF4lUdCm2GNZo4dIfjYwKBgQCPNkhEonil5DS/OT5zmxP6SxEpmS8RT96rv7iPCDZjUJFuVRRV0jbS2PWNQg88HQ+3iCr7ZAZC5RkT669P/9rYNsyprN5oJYYFaWvVnUGbgQQeehjBns6ryFFq7EwuDfF8jEVOWweOG9ByYtf5+zg23oHCgjSqw0ojo+z42ZVCAQKBgQCDvnoSBxrpiVyZ1alW47xc8yMnZSqZHn94pvjUe20f/QM34Jx8Z+3MMgNqUsLZ063UQHP5mj5FzvlkZajrvUK7sQWCYBHrwUlEBOGjF1rESlhRFjesK3kp3/AhVSW5nJARF7X+DAp2mYMERkh8la2IqsRvj2QjYxy00IjtDoXtVwKBgAPt5jxnxfp7Zwl98wM88KNg0H24/4Z0Ura5azGtCbQhQyWiMjovmWvrk8x80XqNkNhGJM1p4SZXgm6XRJGw9/KJL74QehGlSC838PTzQ9LwmIoy/DRrF0kqHi37jVNo8tFGH2aPCANeUccdY390vweYRwbTUVT30FIa4V1Ydr/7";
	public static String PublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApKYiuxTKZAhh/mTRlQtjYOrFcN+cSOtUdgmYmjlUQF7NZV9ac/0/sclwdQh1KMekpkr5FR0vn7upKhakxKQIHMgmwppi5qnLFnkVUl9DHyGLN7u8MuyMB/fwNhAghnnQGXCQZiT7qD1HMmffztiUyuIQQ68Ym4Pq44xog+QPOUXbu4QFBLXnlf3xB/DGpslh6bOLp1SiunDmuGVrTGXDLuzYt/1XTyfQ/BQfI76jl+v3dfH7qpImd6wPcLFLwZ4CZS8eAvl75AcIMe671Nie6SQRfdK7aexl5YCeMCf38c9qXUPPIwj/jNSCjbDI3yK1B4qdt9nfXNMFRIYsz+UMMwIDAQAB";
	//public static String url = "http://localhost:7010/quick/api";
	public static String url = "http://39.100.6.242:6001/quick/api";
	//public static String url = "http://39.98.166.14:6001/quick/api";

	public static void main(String[] args) throws  Exception{
		// TODO Auto-generated method stub
		recharge();




//		HashMap<String, Object> paramMap = new HashMap<>();
//		paramMap.put("url","https://api.manager.wowma.jp/wmshopapi/searchTradeInfoProc");
//		HashMap<String, Object> bodyMap = new HashMap<>();
//		bodyMap.put("shopId","46375797");
//		bodyMap.put("orderId","320074772");
//		paramMap.put("bodyMap",JSONUtil.toJsonStr(bodyMap));
//		paramMap.put("key","f0655ff1a03742a12dca5ea5506674824a892fab2659831d4b6ab7ab74f43a53");
//		String result= HttpUtil.post("http://47.56.136.208:8001/quick/api/shop/wowG", paramMap);
//		String data = JSONUtil.parseObj(result).getStr("data");
//		System.out.println(data);



//		HashMap<String, Object> paramMap = new HashMap<>();
//		JSONObject paramJson = new JSONObject();
//		paramJson.put("dateType",1);
//		paramJson.put("startDatetime","2020-06-10T00:00:01+0900");
//		paramJson.put("endDatetime","2020-06-10T23:59:59+0900");
//		paramMap.put("bodyMap",JSONUtil.toJsonStr(paramJson));
//		String result= HttpUtil.post("http://47.56.136.208:8001/quick/api/shop/letP5",paramMap);
//		System.out.println(result);
		//XmlUtil
		//b();
		//System.out.println(Base64.encode("SP321536_EfDpOhMFtRqpVYOE:SL321536_uZWOBpZczD6Wh3vv"));
		//System.out.println(date);

		//System.out.println(DateUtil.parse(DateUtil.now(),"yyyy-MM-dd'T'HH:mm:ss"));

		//System.out.println(DateUtil.format(DateUtil.date(),"yyyyMMdd"));
	}



	public static void  a(){
		HashMap<String, Object> paramMap = new HashMap<>();
		paramMap.put("url","https://api.rms.rakuten.co.jp/es/2.0/order/searchOrder");
		JSONObject paramJson = new JSONObject();
		paramJson.put("dateType","1");
		paramJson.put("startDatetime","2020-07-06T00:00:01+0900");
		paramJson.put("endDatetime","2020-07-06T23:59:59+0900");
		JSONObject PaginationRequestModel = new JSONObject();
		PaginationRequestModel.put("requestRecordsAmount",100);
		PaginationRequestModel.put("requestPage",1);
		paramJson.put("PaginationRequestModel",PaginationRequestModel);
		paramMap.put("bodyMap",JSONUtil.toJsonStr(paramJson));
		String result= HttpUtil.post("http://47.56.136.208:8001/quick/api/shop/letP6",paramMap);
		JSONObject resultJson = JSONUtil.parseObj(result);
		System.out.println(resultJson);
		JSONObject dataJson = resultJson.getJSONObject("data");
		System.out.println(dataJson);
		JSONObject PaginationResponseModelJson = dataJson.getJSONObject("PaginationResponseModel");
		System.out.println(PaginationResponseModelJson.getInt("totalPages",0));
		JSONArray orderNumberListJson = dataJson.getJSONArray("orderNumberList");
		List<String> orderList = JSONUtil.toList(orderNumberListJson,String.class);
		System.out.println(orderList);

	}

	public static void b(){
		HashMap<String, Object> paramMap = new HashMap<>();
		paramMap.put("url","https://api.rms.rakuten.co.jp/es/2.0/order/getOrder");
		JSONObject paramJson = new JSONObject();
		List<String> list = new ArrayList<>();
		list.add("321536-20200706-00069835");
		//list.add("321536-20200629-00282723");
		//list.add("321536-20200622-00053813");
		paramJson.put("orderNumberList",list);
		paramMap.put("bodyMap",JSONUtil.toJsonStr(paramJson));
		String result= HttpUtil.post("http://47.56.136.208:8001/quick/api/shop/letP6",paramMap);
		System.out.println(JSONUtil.parseObj(result)
				.getJSONObject("data")
				.getJSONArray("OrderModelList")
				.get(0));
		System.out.println(result);
	}

	public static void d(){
		HashMap<String, Object> paramMap = new HashMap<>();
		paramMap.put("url","https://api.rms.rakuten.co.jp/es/2.0/order/updateOrderShipping");
		HashMap<String, Object> bodayMap = new HashMap<>();
		bodayMap.put("orderNumber","321536-20200706-00069835");
		List<JSONObject> basketidModelList = new ArrayList<>();
		JSONObject basketidModel = new JSONObject();
		basketidModel.put("basketId","1201341491");
		List<JSONObject> shippingModelList = new ArrayList<>();
		JSONObject shippingModel = new JSONObject();
		shippingModel.put("shippingDetailId","606976380");
		shippingModel.put("shippingDate","2020-07-07");
		shippingModel.put("deliveryCompany","1001");
		shippingModel.put("shippingNumber","440510777022");
		shippingModelList.add(shippingModel);
		basketidModel.put("ShippingModelList",shippingModelList);
		basketidModelList.add(basketidModel);
		bodayMap.put("BasketidModelList",basketidModelList);
		paramMap.put("bodyMap",JSONUtil.toJsonStr(bodayMap));
		String result= HttpUtil.post("http://47.56.136.208:8001/quick/api/shop/letP6",paramMap);

		System.out.println(result);
	}

	public static void c(){
		HashMap<String, Object> paramMap = new HashMap<>();
		paramMap.put("url","https://api.rms.rakuten.co.jp/es/2.0/order/getPayment/");
		JSONObject paramJson = new JSONObject();
		//List<String> list = new ArrayList<>();
		//list.add("321536-20200622-00057832");
		//list.add("321536-20200622-00052804");
		//list.add("321536-20200622-00053813");
		paramJson.put("orderNumber","321536-20200622-00057832");
		paramMap.put("bodyMap",JSONUtil.toJsonStr(paramJson));
		String result= HttpUtil.post("http://47.56.136.208:8001/quick/api/shop/letP6",paramMap);
		System.out.println(JSONUtil.parseObj(result)
				.getJSONObject("data")
				.getJSONObject("OrderModel"));
		System.out.println(result);
	}

    public static void GetShippingInfo(){

        StringBuilder result= new StringBuilder();
		result.append("<ShippingAPIService version=\"1.0\">");
		result.append("<ResultCode>0</ResultCode>");
		result.append("<TotalOrder>91</TotalOrder>");
		result.append("<Order1>");
		result.append("<shippingStatus>Delivered(5)</shippingStatus>");
		result.append("<sellerID>huangguoqiang</sellerID>");
		result.append("</Order1>");
		result.append("<Order2>");
		result.append("<shippingStatus>Delivered(4)</shippingStatus>");
		result.append("<sellerID>huangguoqiang</sellerID>");
		result.append("</Order2>");
		result.append("</ShippingAPIService>");
        System.out.println(result);
		Map<String, Object> resultMap = XmlUtil.xmlToMap(result.toString());
		//System.out.println(JSONUtil.toJsonStr(resultMap));
		System.out.println(resultMap);
    }

	public static void register() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "3000801");

    	//paramMap.put("mchRate", "0.0055");
    	paramMap.put("realName", "郑孝津");
    	paramMap.put("idNo", "350721199007201317");
    	paramMap.put("idCardFront", "1203179651453521922");
    	paramMap.put("idCardBack", "1203179651453521922");
    	paramMap.put("idStartDate", "20160503");
    	paramMap.put("idEndDate", "20360503");
    	paramMap.put("loginPassword", "hrh@18305975931");
    	paramMap.put("payPassword", "hrh@123456789");
    	paramMap.put("gender", "1");
    	paramMap.put("career", "马龙");
    	paramMap.put("address", "test123456789");
    	paramMap.put("mobile", "18059147925");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
    	System.out.println(signContent);
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/vs/mch/register", paramMap);
    	System.out.println(result);
	}

	public static void modifyMchInfo() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "2000801");
    	paramMap.put("mchId", "1215195946705260545");
    	paramMap.put("career", "计算机");
    	paramMap.put("address", "福建省福州市工业路23号");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
    	System.out.println(signContent);
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/vs/mch/modifyMchInfo", paramMap);
    	System.out.println(result);
	}

	public static void queryMchStatus() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "2000801");
    	paramMap.put("mchId", "1215195946705260545");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
    	System.out.println(signContent);
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/vs/mch/queryMchStatus", paramMap);
    	System.out.println(result);
	}

	public static void bindCard() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());//6221550907300960
    	paramMap.put("partnerId", "3000801");
    	paramMap.put("mchId", "1252901543923736577");
    	paramMap.put("mobile", "18059147925");
    	paramMap.put("cardNo", "6226890131804550");
    	paramMap.put("bankCode", "CNCB");
    	paramMap.put("cardType", "credit");
    	paramMap.put("cvv", "400");
    	paramMap.put("validYear", "2022");
    	paramMap.put("validMonth", "08");
    	paramMap.put("returnUrl", "http://47.111.15.255:8000/img/bindcard.html");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/vs/mch/bindCard", paramMap);
    	System.out.println(result);
	}

	public static void bindCardConfirm() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "3000801");
    	paramMap.put("mchId", "1252901543923736577");
    	paramMap.put("cardNo", "6226890131804550");
    	paramMap.put("verifyCode", "126540");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/vs/mch/bindCardConfirm", paramMap);
    	System.out.println(result);
	}


	public static void omonUploadImg() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "2000601");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("picture", new File("E:\\123456789\\1564562221.jpg"));
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/vs/mch/uploadImg", paramMap);
    	System.out.println(result);
//		HashMap<String, Object> paramMap = new HashMap<>();
//		paramMap.put("picture", new File("E:\\123456789\\1564562221.jpg"));
//		String result = HttpRequest.post("http://localhost:8080/basic/uploadPhoto").header("Authorization", "Bearer "+"eyJhbGciOiJIUzUxMiJ9.eyJkb3dubG9hZEZsYWciOjAsInN1YiI6IjMxIiwidHlwZSI6MSwiZXhwIjoxNTczNDcxMzExLCJjb250cmFjdEZsYWciOjAsImlhdCI6MTU3Mjg2NjUxMX0.TJNIhsxbvxDHPJmZO4STPY9MNTxIQ2Wfx8vfoOiyEcRZjQyt5jtDgILDgH9k3yrgnzyeOwm3JjbnKnreSpGRww")
//		.form(paramMap)
//		.execute().body();
		System.out.println(result);
	}

	public static void balanceQuery() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "3000801");
    	paramMap.put("mchId", "1252901543923736577");
    	paramMap.put("cardNo", "6226890131804550");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
    	System.out.println(signContent);
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/vs/mch/balanceQuery", paramMap);
    	System.out.println(result);
	}

	public static void recharge() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "3000801");
    	paramMap.put("mchId", "1252901543923736577");
    	paramMap.put("cardNo", "6226890131804550");
    	paramMap.put("outTradeNo", "20201109001");
    	paramMap.put("goodsName", "test0001");
    	paramMap.put("orderAmount", "1000");
    	paramMap.put("notifyUrl", "http://39.100.6.242:6001/quick/api/pay/test");
    	//paramMap.put("provinceCode", "20360503");
    	paramMap.put("payPassword", "hrh@123456789");
    	paramMap.put("provinceCode", "520000");
    	paramMap.put("cityCode", "520400");
    	paramMap.put("cvv", "400");
    	paramMap.put("validYear", "2022");
    	paramMap.put("validMonth", "08");
    	paramMap.put("mchRate", "0.006");
    	//paramMap.put("mchFee", "5");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
    	System.out.println(signContent);
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/vs/pay/recharge", paramMap);
    	System.out.println(result);
	}

	public static void query() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "2000801");
    	paramMap.put("mchId", "1200355596702625793");
    	paramMap.put("outTradeNo", "20200107003");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
    	System.out.println(signContent);
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/vs/pay/query", paramMap);
    	System.out.println(result);
	}

	public static void modifyMchRate() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "2000601");
    	paramMap.put("mchId", "1156856254835855362");
    	paramMap.put("mchRate", "0.0054");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
    	System.out.println(signContent);
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/vs/mch/modifyMchRate", paramMap);
    	System.out.println(result);
	}

	public static void withdraw() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "3000801");
    	paramMap.put("mchId", "1252901543923736577");
    	paramMap.put("cardNo", "6226890131804550");
    	paramMap.put("outTradeNo", "20200428001");
    	paramMap.put("cashAmount", "994");
    	paramMap.put("cashFee", "60");
    	paramMap.put("payPassword", "hrh@123456789");
    	paramMap.put("notifyUrl", "http://qpay.shineroon.com:6001/quick/api/pay/test");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
    	System.out.println(signContent);
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/vs/mch/withdraw", paramMap);
    	System.out.println(result);
	}

	public static void withdrawQuery() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "2000801");
    	paramMap.put("mchId", "1200355596702625793");
    	paramMap.put("outTradeNo", "df20200107001");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
    	System.out.println(signContent);
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/vs/mch/withdrawQuery", paramMap);
    	System.out.println(result);
	}

	public static void signConfirm() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "2000601");
    	paramMap.put("mchId", "1167027136982974465");
    	paramMap.put("cardNo", "6221550907300960");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
    	System.out.println(signContent);
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/vs/mch/signConfirm", paramMap);
    	System.out.println(result);
	}

	public static void orderNotify() {
		HashMap<String, Object> paramMap = new HashMap<>();
		paramMap.put("code","200");
		HashMap<String, Object> dataMap = new HashMap<>();
		dataMap.put("partnerId","2000832");
		dataMap.put("mchId","1252793523294269441");
		dataMap.put("outTradeNo","60662020042414450280160651");
		dataMap.put("orderAmount","191932");
		dataMap.put("orderStatus","2");
		dataMap.put("returnMsg","交易成功");
		dataMap.put("signature","DfvLVuyG8gpoZGve+kKPjzh0rghd1tsWqlIpyQiPEeNr8MCKRrw1RBpmtrW3fXKeSvCM0Rtd5FxK/DC2xCzevZb0sPX7SWobfeWpkfaVTJymgoyKEmtUPrNkkEcxQw6iM/ZPVnQGodbWDE0S40gjs41JtzXgvNu9OacBQNAhhLKm+Rny4o8EeYplqEqhLrd9RLbXuDHRM5UcUgbrIvu2cQhytVMQy4Wx0ubyEDVvj3bcZUpUXAo6DyMEIKIOKLkF5X9wsNCcuVUqHVd80sCWGXkleVSoEIBE0oIF4INDSUH3ZXbThHm1wjjro2q86RkSWcBi2NQ85cWI4JoHpBxn3g==");
		paramMap.put("data",dataMap);
		paramMap.put("message","成功");
		System.out.println(JSONUtil.toJsonStr(paramMap));
		String result= HttpUtil.post("https://code.lianshangyouxuan.com/lsyx/api/plan/sumCunsumeNotify", JSONUtil.toJsonStr(paramMap));
    	System.out.println(result);
	}

	public static void send() {
		String result= HttpUtil.post("http://api.weidunshuju.com:58080/CreditCardAPI/api/rest/tspay/tspay_notify.do", JSONUtil.toJsonStr("123"));
		System.out.println(result);
	}
}
