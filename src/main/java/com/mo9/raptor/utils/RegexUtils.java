package com.mo9.raptor.utils;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zma
 * @date 2018/7/9
 */
public class RegexUtils {

    private static String emailRegex = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    private static String chinaMobileRegex = "^1[3456789]\\d{9}$";
    private static final String SYMBOL = "*";
    private static final int SIZE = 4;
    private static final String ETH_START_SYMBOL = "0x";
    private static final int ETH_LENGTH = 42;

    /**
     * 验证eth地址是否合法
     * @param ethAddress
     * @return
     */
    public static Boolean checkEthAddress(String ethAddress) {
        if (StringUtils.isEmpty(ethAddress) || !ethAddress.startsWith(ETH_START_SYMBOL) || ethAddress.length() != ETH_LENGTH) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 手机邮箱脱敏
     *
     * @param value
     * @return
     */
    public static String toConceal(String value) {
        if (null == value || "".equals(value)) {
            return value;
        }
        if (value.contains("@")) {
            String[] split = value.split("@");
            String conceal = getConceal(split[0]);
            return conceal + "@" + split[1];
        }
        return getConceal(value);
    }

    private static String getConceal(String value) {
        int len = value.length();
        int pamaone = len / 2;
        int pamatwo = pamaone - 1;
        int pamathree = len % 2;
        StringBuilder stringBuilder = new StringBuilder();
        if (len <= 2) {
            if (pamathree == 1) {
                return SYMBOL;
            }
            stringBuilder.append(SYMBOL);
            stringBuilder.append(value.charAt(len - 1));
        } else {
            if (pamatwo <= 0) {
                stringBuilder.append(value.substring(0, 1));
                stringBuilder.append(SYMBOL);
                stringBuilder.append(value.substring(len - 1, len));

            } else if (pamatwo >= SIZE / 2 && SIZE + 1 != len) {
                int pamafive = (len - SIZE) / 2;
                stringBuilder.append(value.substring(0, pamafive));
                for (int i = 0; i < SIZE; i++) {
                    stringBuilder.append(SYMBOL);
                }
                if ((pamathree == 0 && SIZE / 2 == 0) || (pamathree != 0 && SIZE % 2 != 0)) {
                    stringBuilder.append(value.substring(len - pamafive, len));
                } else {
                    stringBuilder.append(value.substring(len - (pamafive + 1), len));
                }
            } else {
                int pamafour = len - 2;
                stringBuilder.append(value.substring(0, 1));
                for (int i = 0; i < pamafour; i++) {
                    stringBuilder.append(SYMBOL);
                }
                stringBuilder.append(value.substring(len - 1, len));
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            Pattern regex = Pattern.compile(emailRegex);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证中国手机号码，11位数字，1开通，第二位数必须是3456789这些数字之一 *
     *
     * @param mobileNumber
     * @return
     */
    public static boolean checkChinaMobileNumber(String mobileNumber) {
        boolean flag = false;
        try {
            Pattern regex = Pattern.compile(chinaMobileRegex);
            Matcher matcher = regex.matcher(mobileNumber);
            flag = matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;

        }
        return flag;
    }

    public static void main(String[] args) {
        String idCard = "91150693793aaa885N";
        StringBuffer buffer = new StringBuffer();
        int length = idCard.length();
        String cardStart = idCard.substring(0, length - 7);
        String cardEnd = idCard.substring(cardStart.length() + 3, length);
        idCard = buffer.append(cardStart).append("****").append(cardEnd).toString();
        System.out.println(idCard);
    }
}
