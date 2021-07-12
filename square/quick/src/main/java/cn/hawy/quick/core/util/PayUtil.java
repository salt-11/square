package cn.hawy.quick.core.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;

import java.math.BigDecimal;

public class PayUtil {

	private static String[] tacArr = {"01267100","01315400","01333600","01362500","01367200","35156506","35175104","35202206","35203703","35220806","35220906","35265106","35277201","35282506","35302506","35354403","35357206","35399505","35417106","35417806","35433207","35437707","35443206","35496807","35537208","35563705","35570009","35583008","35638104","35648905","35671708","35677204","35677301","35739005","35740407","35755505","35831304","35855204","35861603","35876803","35877203","35896704","35916004","35959606","35983604","35988106","86028000","86059401","86149503","86161703","86230702","86294902","86309102","86315702","86350802","86351101","86386502","86391402","86392002","86393602","86395203","86403602","86405102","86407802","86408502","86409702","86415302","86426402","86433002","86437502","86452551","86453502","86485602","86505012","86516602","86522002","86524302","86534302","86538602","86553802","86559202","86562202","86570702","86589528","86593102","86627801","86629802","86647902","86660702","86672301","86736102","86774601","86803002","86858501","86885302","86886102","86886902","86915604","86963001","86972302"};

	 /**
	  * @param value 需要科学计算的数据
	  * @param digit 保留的小数位
	  * @return
	  * 功能:四舍六入五成双计算法、
	**/
	public static String sciCal(double value, int digit){
		String result = "0";
		try {
			double ratio = Math.pow(10, digit);
			double _num = value * ratio;
			double mod = _num % 1;
			double integer = Math.floor(_num);
			double returnNum;
			if(mod > 0.5){
				returnNum=(integer + 1) / ratio;
			}else if(mod < 0.5){
				returnNum=integer / ratio;
			}else{
				returnNum=(integer % 2 == 0 ? integer : integer + 1) / ratio;
			}
			BigDecimal bg = new BigDecimal(returnNum);
			result = bg.setScale((int)digit, BigDecimal.ROUND_HALF_UP).toString();
		} catch (RuntimeException e) {
			throw e;
		}
		return result;
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
	 * 将金额分转元
	 * @param str
	 * @return
	 */
	public static String movePointRight(String str,int length) {
		if(str == null || "".equals(str.trim()))
			return "";
		BigDecimal bigDecimal = new BigDecimal(str.trim());
		return bigDecimal.movePointRight(length).toString();
	}

	public static String genIMEI(){
		String tac = RandomUtil.randomEle(tacArr);
		String snr = RandomUtil.randomNumbers(6);
		String result = tac + snr;
		char[] strArr = result.toCharArray();
		ArrayUtil.reverse(strArr);
		int s = 0;
		for(int i=0;i<result.length();i++){
			int t = Integer.parseInt(String.valueOf(strArr[i]));
			if(i % 2 == 0){
				t = t*2;
				if(t<10){
					s += t;
				}else {
					s += t % 10 + t / 10;
				}

			}else {
				s += t;
			}

		}
		s = 10 - s % 10;
		return result + s;
	}

	public static void main(String[] args) {
		System.out.println(genIMEI());
	}




}
