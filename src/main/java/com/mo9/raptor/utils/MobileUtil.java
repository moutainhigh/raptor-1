package com.mo9.raptor.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yngong
 */
public class MobileUtil {


    public static String processMobileWithBlank(String tel) {
        if (StringUtils.isBlank(tel)) {
            return "";
        }
        return processMobile(tel);
    }

    public static String processMobile(String tel) {
        String oldTel = tel;
        try {
            if (StringUtils.isBlank(tel)) {
                return null;
            }
            // 手机号正则
            String reg = "^[0-9\\+]*?1[3456789]\\d{9}$";
            Pattern pattern = Pattern.compile(reg);
            tel = tel.replaceAll("\\D", "");// 过滤非数字字符 如 158-517 234 34 =》 15851723434
            // 如果是手机号
            Matcher matcher = pattern.matcher(tel);
            if (tel.length() >= 11 && matcher.find()) {// 如果是手机号
                tel = tel.substring(tel.length() - 11);
            } else if (tel.length() >= 7) {
                tel = tel.substring(tel.length() - 7);
            }
            return tel;
        } catch (Exception e) {
            return oldTel;
        }
    }

    public static void main(String[] args) {
        System.out.println(processMobile("+86 13916340665"));
    }
}