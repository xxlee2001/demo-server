package com.yonyou.iuap.demo.utils;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.iuap.utils.CookieUtil;

public class CookiesUtils {
	
	private static Logger logger = LoggerFactory.getLogger(CookiesUtils.class);
	
	public static String URLDecoderString(String str) {
        String result = "";
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException exp) {
        	logger.info(exp.getMessage());
        }
        return result;
    }
	public static String getUnameByCookies(HttpServletRequest request){
		 String value = CookieUtil.findCookieValue(request.getCookies(), "yonyou_uname");
	     if (value != null) {
	         value = URLDecoderString(value);
	     } else {
	         value = "";
	     }
	     return value;
	}
	
}
