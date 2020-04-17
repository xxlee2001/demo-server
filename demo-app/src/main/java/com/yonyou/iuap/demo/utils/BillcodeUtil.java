package com.yonyou.iuap.demo.utils;

import com.alibaba.fastjson.JSONObject;
//import com.yonyou.iuap.demo.contract.po.ContractPO;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillcodeUtil {
//    /**
//     *  把对象的属性值封装成我们需要的json对象
//     */
//    public static JSONObject getBillObject() {
//        Field[] fields = ContractPO.class.getDeclaredFields();
//        JSONObject json = new JSONObject();
//        for (int i = 0; i < fields.length; i++) {
//            String name = fields[i].getName();
//            String value = camel4underline(name);
//            json.put(name,value);
//        }
//        return json;
//    }

    /**
     *  驼峰转变量·
     */
    public static String camel4underline(String param) {
        Pattern p = Pattern.compile("[A-Z]");
        if (param == null || param.equals("")) {
            return "";
        }
        StringBuilder builder = new StringBuilder(param);
        Matcher mc = p.matcher(param);
        int i = 0;
        while (mc.find()) {
            builder.replace(mc.start() + i, mc.end() + i, "_" + mc.group().toLowerCase());
            i++;
        }

        if ('_' == builder.charAt(0)) {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }
}
