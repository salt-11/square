package cn.hawy.quick.agent.modular.business.utils;

import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SysStringUtil {
	static boolean dubugFlag = false;
	/**
	 * 将金额分转元
	 * @param str
	 * @return
	 */
	public static String transFenToYuan(String str) {
		if(str == null || "".equals(str.trim()))
			return "";
		BigDecimal bigDecimal = new BigDecimal(str.trim());
		return bigDecimal.movePointLeft(2).toString();
	}
	
	/**
	 * 将金额元转分
	 * @param str
	 * @return
	 */
	public static String transYuanToFen(String str) {
		if(str == null || "".equals(str.trim()))
			return "";
		BigDecimal bigDecimal = new BigDecimal(str.trim());
		return bigDecimal.movePointRight(2).toString();
	}
	
	/**
	 * 填补字符串
	 * @param str
	 * @param fill
	 * @param len
	 * @param isEnd
	 * @return
	 */
	public static String fillString(String str,char fill,int len,boolean isEnd) {
		int fillLen = 0;
		if (isNull(str)) {
			fillLen = len;
			str = "";
		} else {
			fillLen = len - str.getBytes().length;
		}
		//int fillLen = len - str.getBytes().length;
		if(fillLen <= 0) {
			return str;
		}
		for(int i = 0; i < fillLen; i++) {
			if(isEnd) {
				str += fill;
			} else {
				str = fill + str;
			}
		}
		return str;
	}
	
	/**
	 * 判断object是否为空
	 */
	public static boolean isNull(Object object) {
		if (object instanceof String) {
			return isEmpty(object.toString());
		}
		return object == null;
	}
	
	public static boolean isEmpty(final String value) {
		return value == null || value.trim().length() == 0
				|| "null".endsWith(value);
	}
	/**
	 * 字符串算术运算 支持运算符(+-*除)
	 * @param num1
	 * @param operator
	 * @param num2
	 * @return
	 */
	public static String strArithmetic(String num1,String operator,String num2){
		if(operator!=null&&"+".equals(operator.trim())){
			return String.valueOf(Math.round((String2Double(num1)+String2Double(num2))*100)/100d);
		}
		if(operator!=null&&"-".equals(operator.trim())){
			return String.valueOf(Math.round((String2Double(num1)-String2Double(num2))*100)/100d);
		}
		if(operator!=null&&"*".equals(operator.trim())){
			return String.valueOf(Math.round((String2Double(num1)*String2Double(num2))*100)/100d);
		}
		if(operator!=null&&"/".equals(operator.trim())){
			return String.valueOf(Math.round((String2Double(num1)/String2Double(num2))*100)/100d);
		}
		return null;
	}
	public static double String2Double(String num){
		if (num==null||"".equals(num)){
			return 0;
		}
		return Double.parseDouble(num);
	}
	public static String double2String(double num){
		return String.valueOf(Math.round(num*100)/100d);
	}
	/**
	 * 获取当下时间
	 * @param format 格式
	 * @return
	 */
	public static String getNowTime(String format){
		return new SimpleDateFormat(format).format(new Date());
	}

	/**
	 * 字符串填充
	 * @param direction 方向left,right
	 * @param len 填充后字符串全长
	 * @param element 填充元素
	 * @param str 原字符串
	 * @return
	 */
	public static String padding(String direction,int len,String element,String str){
		String retStr = str;
		while(retStr.length()<len){
			if("left".equals(direction)){
				retStr = element+retStr;
			}
			if("right".equals(direction)){
				retStr = retStr+element;
			}
		}
		return retStr;
	}
	/**
	 * 字符串空检查
	 * @param str
	 * @return 为空true 不为空false
	 */
	public static boolean nullCheck(String str){
		if(str==null||"".equals(str.trim())||"null".equals(str)||"undefined".equals(str)){
			return true;
		}
		return false;
	}
	
	public static JSONObject getErrRetJSON(String msg){
		JSONObject retJSON = new JSONObject();
		retJSON.put("respCode", "01");
		retJSON.put("respMsg", msg);
		retJSON.put("packageData", "");
		retJSON.put("respDate", getNowTime("yyyyMMdd"));
		retJSON.put("respTime", getNowTime("hhmmss"));
		return retJSON;
	}
	
	public static JSONObject getExceptionRetJSON(String msg){
		JSONObject retJSON = new JSONObject();
		retJSON.put("respCode", "02");
		retJSON.put("respMsg", msg);
		retJSON.put("packageData", "");
		retJSON.put("respDate", getNowTime("yyyyMMdd"));
		retJSON.put("respTime", getNowTime("hhmmss"));
		return retJSON;
	}
	
	/**
	 * 传入参数密文转为String格式
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String paramDecode2Str(String str) throws Exception{
		if(dubugFlag){
			return str;
		}
		return URLDecoder.decode(new String(Base64.decodeBase64(str), "UTF-8"),"UTF-8");
	}
	/**
	 * 传入参数密文转为JSON格式
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static JSONObject paramDecode2JSON(String str) throws Exception{
		if(dubugFlag){
			return JSONObject.fromObject(str);
		}
		String decodeStr = URLDecoder.decode(new String(Base64.decodeBase64(str), "UTF-8"),"UTF-8");
		return JSONObject.fromObject(decodeStr);
	}
	/**
	 * 返回参数加密
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String encodeParam(String str) throws Exception{
		if(dubugFlag){
			return str;
		}
		return Base64.encodeBase64String(URLEncoder.encode(str,"UTF-8").getBytes("UTF-8"));
	}
	/**
	 * 保留两位小数
	 * @param number
	 * @return
	 */
	public static double keep2decimal(double number){
		return Math.round(number*100)/100d;
	}
	/**
	 * pojo转化为字符串，默认获取所有字段的值
	 * @param pojo
	 * @param classType
	 * @return
	 */
	public static String pojoToString(Object pojo){
		StringBuffer buffer = new StringBuffer();
		try{
			Method[] methodAry = pojo.getClass().getMethods();
			for(Method method:methodAry){
				String methodName = method.getName();
				if(methodName.indexOf("get")==0){
					buffer.append(methodName).append(":").append(method.invoke(pojo)).append("|");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			if(pojo!=null){
				return "pojoToString Exception! "+pojo.getClass().getName();
			}else{
				return "pojoToString Exception! "+pojo;
			}
		}
		return buffer.toString();
	}

}
