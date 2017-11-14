package com.fruit.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class StringFormatUtil {
	/**
	 * Whether the e-mail address
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 验证是否为手机号码
	 * 
	 * @param no
	 * @return
	 */
	public static boolean isTel(String tel) {
		// String reg = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
//		String reg = "1[0-9]{10}";
		String reg = "[0-9]+";
		return tel.matches(reg);
	}
	
	public static String getAccountName(String account){
		String userName = account;
		if (isEmail(account)) {
			String[] s = account.split("@");
			if (s != null && s.length >= 2) {
				userName = s[0];
			}
		}
		return userName;
	}

	public static boolean isEmpty(String s){
		if (s == null || s.trim().equals("")) {
			return true;
		}
		return false;
	}
	
	public static JSONObject getResultTask(Boolean flag,String s){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("reason", !isEmpty(s) ? s : "");
		jsonObject.put("flag", flag);
		return jsonObject;
	}
	
	public static JSONObject getResultTask(Boolean flag,Object o){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("reason", o != null ? o : "");
		jsonObject.put("flag", flag);
		return jsonObject;
	}
	
	public static JSONObject getResultTask(String key,Object o){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", o != null ? o : "");
		jsonObject.put("key", key);
		return jsonObject;
	}
}
