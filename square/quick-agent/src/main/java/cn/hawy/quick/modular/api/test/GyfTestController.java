package cn.hawy.quick.modular.api.test;

import cn.hawy.quick.core.util.RSA;
import cn.hawy.quick.modular.api.dto.sum.OrderNotifyDto;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import java.io.File;
import java.util.HashMap;

public class GyfTestController {

	public static String PrivateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCkpiK7FMpkCGH+ZNGVC2Ng6sVw35xI61R2CZiaOVRAXs1lX1pz/T+xyXB1CHUox6SmSvkVHS+fu6kqFqTEpAgcyCbCmmLmqcsWeRVSX0MfIYs3u7wy7IwH9/A2ECCGedAZcJBmJPuoPUcyZ9/O2JTK4hBDrxibg+rjjGiD5A85Rdu7hAUEteeV/fEH8MamyWHps4unVKK6cOa4ZWtMZcMu7Ni3/VdPJ9D8FB8jvqOX6/d18fuqkiZ3rA9wsUvBngJlLx4C+XvkBwgx7rvU2J7pJBF90rtp7GXlgJ4wJ/fxz2pdQ88jCP+M1IKNsMjfIrUHip232d9c0wVEhizP5QwzAgMBAAECggEAHtnieBuVMNlBQ2Gww7S2klprVJMRvbgaw2NY7M0BNG6PHgeX81Pos2+DWuqSyWlfOKmjsokde1i2geRsS0xRGNOIL76t+XqnSza1ABJJiXPHmlHbGc+pDXUv1XGAJrJ8g8fLxtf3L7cLuC4uiZubhciGM6iDe+BkCu8sRkpdb+WQmflc6o3bRB1HWmVj/cyIcJai+JjBmNbGmnr3btv5oAUZ4TifNqgw0ak6W0wn2GrUQouFyj/cJnU8sXNJ76lId01wv/auTRAdBY4FWBm0Jw2Ym175QHwOLCXNZiFkx8r/J1GHrzQZazFS6P8ixPxt/SRgOQU/nXpDEKAyakZhAQKBgQD7VJ3omfnqQt6uDZWyFKwhHiG0Y8LebgMKhL7zEA0FiCYpgTmMcGbq1GddLEmxxUqBlvDjXa45VRYbrDlxvEpPiApQPlr2J81mxOOx99LC0J1IKL3xBnkpniTEGPJcUBC+ZOvIEGIeg9Q5YwFLX8StBB4PYf79+HcpxqPYHs7U8QKBgQCntT4GUikbIumnjNoyW2arwhUvgQghqNCaTKJCqa4eoaH0SzncOtNsCXIixMnEiAbDJI3Yo37k1Nbtynjyzw7RXxr2g+wb6/S1fNLGW8q7jxE3jPWsjAhcS5yx+4zFTQcS/SqEzlymrbfHM1Cu8aADJOWF4lUdCm2GNZo4dIfjYwKBgQCPNkhEonil5DS/OT5zmxP6SxEpmS8RT96rv7iPCDZjUJFuVRRV0jbS2PWNQg88HQ+3iCr7ZAZC5RkT669P/9rYNsyprN5oJYYFaWvVnUGbgQQeehjBns6ryFFq7EwuDfF8jEVOWweOG9ByYtf5+zg23oHCgjSqw0ojo+z42ZVCAQKBgQCDvnoSBxrpiVyZ1alW47xc8yMnZSqZHn94pvjUe20f/QM34Jx8Z+3MMgNqUsLZ063UQHP5mj5FzvlkZajrvUK7sQWCYBHrwUlEBOGjF1rESlhRFjesK3kp3/AhVSW5nJARF7X+DAp2mYMERkh8la2IqsRvj2QjYxy00IjtDoXtVwKBgAPt5jxnxfp7Zwl98wM88KNg0H24/4Z0Ura5azGtCbQhQyWiMjovmWvrk8x80XqNkNhGJM1p4SZXgm6XRJGw9/KJL74QehGlSC838PTzQ9LwmIoy/DRrF0kqHi37jVNo8tFGH2aPCANeUccdY390vweYRwbTUVT30FIa4V1Ydr/7";
	public static String PublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApKYiuxTKZAhh/mTRlQtjYOrFcN+cSOtUdgmYmjlUQF7NZV9ac/0/sclwdQh1KMekpkr5FR0vn7upKhakxKQIHMgmwppi5qnLFnkVUl9DHyGLN7u8MuyMB/fwNhAghnnQGXCQZiT7qD1HMmffztiUyuIQQ68Ym4Pq44xog+QPOUXbu4QFBLXnlf3xB/DGpslh6bOLp1SiunDmuGVrTGXDLuzYt/1XTyfQ/BQfI76jl+v3dfH7qpImd6wPcLFLwZ4CZS8eAvl75AcIMe671Nie6SQRfdK7aexl5YCeMCf38c9qXUPPIwj/jNSCjbDI3yK1B4qdt9nfXNMFRIYsz+UMMwIDAQAB";
	//public static String url = "http://localhost:7010/quick/api/gyf";
	public static String url = "http://39.100.6.242:6001/quick/api/gyf";
	//public static String url = "http://39.98.166.14:6001/quick/api";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println(IdGenerator.getId());
		//register();
		//bindCard();
		balanceQuery();
		//recharge();
		//query();
		//withdraw();
		//System.out.println("123456".substring(0,4));

	}

	public static void register() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "2100701");
		paramMap.put("realName", "肖会");
    	paramMap.put("settleCardNo", "6214855914759952");
    	paramMap.put("idNo", "429001198911125912");
    	paramMap.put("address", "test123456789");
    	paramMap.put("mobile", "15280003192");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
    	System.out.println(signContent);
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/mch/register", paramMap);
    	System.out.println(result);
	}

	public static void bindCard() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());//6221550907300960
    	paramMap.put("partnerId", "2100701");
    	paramMap.put("mchId", "1214444477975871490");
    	paramMap.put("mobile", "18305975931");
		paramMap.put("cardType", "credit");
    	paramMap.put("cardNo", "6225768721583068");
    	paramMap.put("cvv", "694");
    	paramMap.put("validYear", "21");
    	paramMap.put("validMonth", "07");
		//paramMap.put("cityCode", "3501");
    	//paramMap.put("returnUrl", "http://47.111.15.255:8000/img/bindcard.html");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/mch/bindCard", paramMap);
    	System.out.println(result);
	}

	public static void balanceQuery() {
		HashMap<String, Object> paramMap = new HashMap<>();
		paramMap.put("reqTime",DateUtil.now());
		paramMap.put("partnerId", "2100701");
		paramMap.put("mchId", "1214444477975871490");
		String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
		System.out.println(signContent);
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
		String result= HttpUtil.post(url+"/mch/balanceQuery", paramMap);
		System.out.println(result);
	}


	public static void recharge() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "2100701");
    	paramMap.put("mchId", "1214444477975871490");
    	paramMap.put("cardNo", "6225768721583068");
    	paramMap.put("outTradeNo", "202001160001");
    	paramMap.put("orderAmount", "1000");
    	paramMap.put("notifyUrl", "http://qpay.shineroon.com:6001/quick/api/pay/test");
    	paramMap.put("cityCode", "3910");
		paramMap.put("mcc", "5812");
    	paramMap.put("mchRate", "0.006");
    	//paramMap.put("mchFee", "5");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
    	System.out.println(signContent);
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/pay/recharge", paramMap);
    	System.out.println(result);
	}

	public static void query() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "2100701");
    	paramMap.put("mchId", "1214444477975871490");
    	paramMap.put("outTradeNo", "20200108002");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
    	System.out.println(signContent);
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/pay/query", paramMap);
    	System.out.println(result);
	}

	public static void withdraw() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "2100701");
    	paramMap.put("mchId", "1214444477975871490");
    	paramMap.put("cardNo", "6225768721583068");
    	paramMap.put("outTradeNo", "df202001160001");
		paramMap.put("mobile", "18305975931");
    	paramMap.put("cashAmount", "994");
    	paramMap.put("cashFee", "94");
    	paramMap.put("notifyUrl", "http://qpay.shineroon.com:6001/quick/api/pay/test");
    	String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
    	System.out.println(signContent);
		String signature = RSA.sign(signContent, PrivateKey);
		paramMap.put("signature", signature);
    	String result= HttpUtil.post(url+"/mch/withdraw", paramMap);
    	System.out.println(result);
	}

	public static void withdrawQuery() {
		HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "2000601");
    	paramMap.put("mchId", "1150961962861772801");
    	paramMap.put("outTradeNo", "20190713003");
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
		OrderNotifyDto orderNotifyDto = new OrderNotifyDto();
		orderNotifyDto.setResp_code("000000");
		String result= HttpUtil.post(url+"/vs/pay/orderNotify", JSONUtil.toJsonStr(orderNotifyDto));
    	System.out.println(result);
	}

	public static void send() {
		String result= HttpUtil.post("http://127.0.0.1:7010/quick/api/vs/pay/mqTest", JSONUtil.toJsonStr("123"));
		System.out.println(result);
	}
}
