package com.swolo.daodian.utils;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串方法类 Created by abu on 2015/12/26.
 */
public class StringUtils {
    public static final String EMPTY = "";

    /**
     * 判断一个字符串是否全部为空白字符，空白字符指由空格' '、制表符'\t'、回车符'\r'、换行符'\n'组成的字符串
     *
     * @param input 输入字符集
     * @return boolean
     */
    public static boolean isBlank(CharSequence input) {
        if (input == null || "".equals(input)) {
            return true;
        }
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            // 只要有一个字符不是制表 空格 换行 回车中的一个,就不是空字符
            if (c != '\t' && c != ' ' && c != '\n' && c != '\r') {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否包含字母
     * @param str
     * @return
     */
    public static boolean containsLetter(String str) {
        if (str == null || str.equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile("[a-zA-Z]");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * 是否包含数字
     *
     * @param str
     * @return
     */
    public static boolean containsNumber(String str) {
        if (str == null || str.equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * 是否包含特殊字符
     *
     * @param str
     * @return
     */
    public static boolean containsSpecialLetter(String str) {
        if (str == null || str.equals("")) {
            return false;
        }
        String limitEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@①#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern pattern = Pattern.compile(limitEx);
        Matcher m = pattern.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为纯数字
     *
     * @param str
     * @return
     */
    public static boolean isDigit(String str) {

        if (str == null || str.equals("")) {
            return false;
        }

        Pattern pat = Pattern.compile("\\d*");
        return pat.matcher(str).matches();
    }

    public static boolean isContainsDigit(String str) {
        if (str == null || str.equals("")) {
            return false;
        }
        str = str.replaceAll("\\s*", "");
        Pattern pat = Pattern.compile(".*\\d+.*");
        return pat.matcher(str.replaceAll(" ", "")).matches();
    }

    /**
     * 字符是否为空，有"null"的判断
     *
     * @param s 待测字符串
     * @return 如果字符串为空或者它的长度为零或者仅由空白字符(whitespace)组成时, 返回true;否则返回false
     */
    public static boolean isEmpty(String s) {
        if (s == null) {
            return true;
        }
        s = s.trim();
        return ((s.length() == 0) || s.equals("null"));
    }

    /**
     * 字符是否为空，有"null"的判断
     *
     * @param s
     * @return
     */
    public static boolean notEmpty(String s) {
        return !isEmpty(s);
    }

    /**
     * 如果字符串为null或者"null"，则赋值为空
     *
     * @param parsedStr 待测字符串
     * @return Str 如果字符串为null，返回""；否则，直接返回str。
     */
    public static String nullToEmpty(String parsedStr) {
        if (parsedStr == null || "null".equals(parsedStr.toLowerCase())) {
            parsedStr = "";
        }
        return parsedStr;
    }

    /**
     * 判断Editable类型的数据是否为空
     *
     * @param text Editable类型的待测数据
     * @return 如果为空, 返回true;否则返回false
     */
    public static boolean isEmpty(Editable text) {
        if (text == null) {
            return true;
        }

        String s = charSequence2String(text, null);

        return isEmpty(s);
    }

    /**
     * 字符串中是否包含汉字
     *
     * @param str 字符串
     * @return boolean 有汉字，返回true；否则，返回false
     */
    public static boolean isContainsChineseChar(String str) {
        boolean ret = false;
        if (str == null || str.length() < 1) {
            return ret;
        }
        for (int i = 0; i < str.length(); i++) {
            if (isChinese(str.charAt(i))) {
                return true;
            }
        }
        return ret;
    }

    /**
     * 判断字符串里是否包含中文
     *
     * @param str 字符串
     * @return boolean true:包含中文;false:不包含中文;
     */
    public static boolean isChinese(String str) {
        Pattern p = Pattern.compile("[\\u4E00-\\u9FFF]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 判断一个字符是否为中文字符
     *
     * @param c 待判断的字符
     * @return boolen 是中文字符，返回true；否则，返回false
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 验证是否是一个邮箱
     *
     * @param email 待验证邮箱号
     * @return boolean
     */
    public static boolean isEmail(String email) {
        Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        if (StringUtils.isEmpty(email)) {
            return false;
        }
        return emailer.matcher(email).matches();
    }

    /**
     * 验证字符串是否是纯数字组成
     *
     * @param number 待测字符串
     * @return boolean true，是纯数字；false,不是纯数字
     */
    public static boolean isNumber(String number) {
        if (StringUtils.isEmpty(number)) {
            return false;
        }
        for (int i = 0; i < number.length(); i++) {
            char c = number.charAt(i);
            if (!(c >= '0' && c <= '9')) {
                return false;
            }
        }
        return true;
    }

    /**
     * 只验证是11位数字,不做真伪验证
     *
     * @param mobiles 数字串
     * @return boolean true，是11位数字；false,非11位数字
     */
    public static boolean isMobileNO(String mobiles) {
        boolean flag = false;
        try {
            Pattern p = Pattern.compile("^\\d{11}$");
            Matcher m = p.matcher(mobiles);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /***
     * 验证电话
     *
     * @param phoneNumber 待验证手机号码
     */
    public static boolean isPhoneNumber(String phoneNumber) {
        boolean isValid = false;

        String expression = "((^(13|15|17|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
        CharSequence inputStr = phoneNumber;

        Pattern pattern = Pattern.compile(expression);

        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;

    }

    /**
     * 获取字符串的后几位
     *
     * @param txt 带获取的字符串类型
     * @param len 要获取的后面几位的位数
     * @return String 获取后的结果
     */
    public static String getTxtTail(String txt, int len) {
        if (StringUtils.isEmpty(txt)) {
            return "";
        }

        if (txt.length() > len) {
            return txt.substring(txt.length() - len, txt.length());
        }
        return txt;
    }

    /**
     * 小数点处理
     *
     * @param source 字符串数据
     * @param bit    要保留位数
     * @return String 处理后的数据
     */
    public static String getDecimal(String source, int bit) {
        BigDecimal bd = new BigDecimal(source);
        bd = bd.setScale(bit, BigDecimal.ROUND_HALF_UP);
        return bd.toString();
    }

    /**
     * 返回用户视图下的字符串长度。中文算1个字符，英文算0.5个字符。
     *
     * @param str 字符串
     * @return int 用户视图下的字符串的长度
     */
    public static int getStringLengthInUserView(String str) {
        if (str == null) {
            return 0;
        }

        char[] cs = str.toCharArray();
        float count = 0;

        for (char c : cs) {
            if (isChinese(c)) {
                count = count + 1f;
            } else {
                count = count + 0.5f;
            }
        }

        return (int) Math.ceil(count);
    }

    /**
     * 计算字符串长度
     *
     * @param string 待处理的字符串
     * @return int 字符串的长度
     */
    public static int getByteLength(String string) {
        int count = 0;
        for (int i = 0; i < string.length(); i++) {
            if (Integer.toHexString(string.charAt(i)).length() == 4) {
                count += 2;
            } else {
                count++;
            }
        }
        return count;
    }

    /**
     * 获取字符串中的字节数，查找length字节处的字符个数
     *
     * @param str    字符串
     * @param length 字节位置
     * @return length处的字节数
     */
    public static int getPositionFromBytesLength(String str, int length) {

        int num = 0;
        String E1 = "[\u4e00-\u9fa5]";// 中文
        String E2 = "[a-zA-Z]";// 英文
        String E3 = "[0-9]";// 数字

        int chineseCount = 0;// 汉字个数
        int englishCount = 0;// 英文个数
        int numberCount = 0;// 数字个数
        int otherCount = 0;// 其他符合的个数

        String temp;
        for (int i = 0; i < str.length(); i++) {
            temp = String.valueOf(str.charAt(i));
            if (temp.matches(E1)) {
                chineseCount++;
            } else if (temp.matches(E2)) {
                englishCount++;
            } else if (temp.matches(E3)) {
                numberCount++;
            } else {
                otherCount++;
            }
            num = chineseCount * 2 + englishCount + numberCount + otherCount;
            if (num >= length) {
                return i;
            }
        }
        return str.length();
    }

    /**
     * 获取字符串中的字节数，汉字为2个字节，英文数字为1个字节
     *
     * @param str 字符串
     * @return int 字符串字节数
     */
    public static int getStrBytesNumber(String str) {
        if (StringUtils.isEmpty(str)) {
            return 0;
        }
        int num = 0;
        String E1 = "[\u4e00-\u9fa5]";// 中文
        String E2 = "[a-zA-Z]";// 英文
        String E3 = "[0-9]";// 数字

        int chineseCount = 0;// 汉字个数
        int englishCount = 0;// 英文个数
        int numberCount = 0;// 数字个数
        int otherCount = 0;// 其他符合的个数

        String temp;
        for (int i = 0; i < str.length(); i++) {
            temp = String.valueOf(str.charAt(i));
            if (temp.matches(E1)) {
                chineseCount++;
            } else if (temp.matches(E2)) {
                englishCount++;
            } else if (temp.matches(E3)) {
                numberCount++;
            } else {
                otherCount++;
            }
        }
        num = chineseCount * 2 + englishCount + numberCount + otherCount;
        return num;
    }

    /**
     * 根据长度截断字符串，并在文字后补上...
     *
     * @param str 待处理的字符串
     * @param len 要获得的文字长度
     * @return String 截取后的结果
     */
    public static String getFixedText(String str, int len) {
        return getFixedText(str, len, true);
    }

    /**
     * 根据长度截断字符串
     *
     * @param str        待处理的字符串
     * @param len        要获得的文字长度
     * @param isShowDots 是否追加
     * @return String 截取后的字符串
     */
    public static String getFixedText(String str, int len, boolean isShowDots) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        StringBuilder textFixed = new StringBuilder();
        double sizeCount = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9')) {
                sizeCount += 0.5;
            } else {
                sizeCount += 1;
            }
            if (sizeCount <= len) {
                textFixed.append(ch);
            } else {
                if (isShowDots) {
                    textFixed.append("...");
                }
                break;
            }
        }
        return textFixed.toString();
    }

    /**
     * 获取字符串中汉字的个数
     *
     * @param str 待测字符串
     * @return int 字符串中汉字的个数
     */
    public static int getChineseNum(String str) {
        int count = 0;
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find())
            count++;
        return count;
    }

    /**
     * 获取截取后的字符串
     *
     * @param originalText 字符串
     * @param start        开始位置
     * @param end          结束位置
     * @return String 截取的结果
     */
    public static String getSubString(String originalText, int start, int end) {
        if (TextUtils.isEmpty(originalText)) {
            return "";
        }
        int length = originalText.length();
        if (start < 0) {
            start = 0;
        }
        if (end > length) {
            end = length;
        }
        return originalText.substring(start, end);
    }

    /**
     * 获取字符串中的所有中文字符
     *
     * @param src 字符串
     * @return String 字符串中所有的中文字符
     */
    public static String getChineseString(String src) {
        StringBuilder sb = new StringBuilder();

        if (isEmpty(src)) {
            return "";
        }

        for (int i = 0; i < src.length(); i++) {
            if (isChinese(src.charAt(i))) {
                sb.append(src.charAt(i));
            }
        }

        return sb.toString();
    }

    /**
     * 从指定字符串中提取出最后一个中文
     *
     * @param orginStr 待处理的字符串
     * @return str 字符串中的最后一个汉字
     */
    public static String getLastChineseChar(String orginStr) {
        String result = "*";

        if (orginStr == null || orginStr.length() == 0) {
            return result;
        }

        char[] cs = orginStr.toCharArray();

        for (int i = cs.length - 1; i >= 0; i--) {
            char c = cs[i];

            if (StringUtils.isChinese(c)) {
                result = String.valueOf(c);
                break;
            }
        }

        return result;
    }

    /**
     * @param string 原始字符串
     * @param first  第一个改变颜色的位置
     * @param last   最后一个改变颜色的位置
     * @param color  需要改变成的颜色值
     * @return 特定位置改变颜色之后的字符串
     */
    public static SpannableStringBuilder getDifferentColorString(String string, int first, int last, int color) {
        if (StringUtils.isEmpty(string)) {
            return new SpannableStringBuilder();
        }
        if (first < 0) {
            return new SpannableStringBuilder(string);
        }
        if (last > string.length()) {
            return new SpannableStringBuilder(string);
        }
        if (first >= last) {
            return new SpannableStringBuilder(string);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(string);
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(color);
        builder.setSpan(blueSpan, first, last, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * 十六进制String转为Byte
     *
     * @param hexString 16进制字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * Byte类型转为16进制的String类型
     *
     * @param src 字节数组
     * @return String 字符串
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return "";
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 去掉空格
     *
     * @param str 需要去空格的字符串
     * @return String 结果
     */
    public static String splitSpace(String str) {
        if ("".equals(str) || str == null) {
            return "";
        }
        String[] strings = str.split(" ");
        StringBuilder sBuffer = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            sBuffer.append(strings[i]);
        }
        return sBuffer.toString();
    }

    /**
     * 格式化数字，传入的int值为0~9时，会在前面补上0。
     *
     * @param i 要格式的数字。
     * @return String 当i为0到9，则在返回String类型的0i；否则，直接输出i的String类型。
     */
    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    /**
     * 转换时间，将输入的秒数转为 (HH:)MM:SS 格式，传入的秒数最大不能超过 359999
     *
     * @param seconds 秒数
     * @return String seconds对应格式 (HH:)MM:SS
     */
    public static String translateSecondsToString(int seconds) {
        String ret;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (seconds <= 0) {
            ret = "00:00";
        } else {
            minute = seconds / 60;
            if (minute < 60) {
                second = seconds % 60;
                ret = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99) {
                    ret = "99:59:59";
                } else {
                    minute = minute % 60;
                    second = seconds - hour * 3600 - minute * 60;
                    ret = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
                }
            }
        }

        return ret;
    }

    /**
     * 解析Color值
     *
     * @param colorString #RRGGBB #AARRGGBB 'red', 'blue', 'green'
     * @return int color对应的值
     */
    public static int parseColorFromStr(String colorString) {
        int color = Integer.MIN_VALUE;

        if (StringUtils.isEmpty(colorString)) {
            return color;
        }

        try {
            color = Color.parseColor(colorString);
        } catch (Exception e) {
            color = Integer.MIN_VALUE;
        }
        return color;
    }

    /**
     * 将 CharSequence 转成 String 。 不能强转
     *
     * @param m_text       带转换的CharSequence类型的字符串
     * @param defaultValue 若转换失败后，返回的默认值
     * @return String 若转换成功，返回对应的String类型的字符串；如果不能转换，则返回defaultValue值
     */
    public static String charSequence2String(CharSequence m_text, String defaultValue) {
        if (m_text instanceof String) {
            return (String) m_text;
        } else if (m_text != null) {
            return m_text.toString();
        } else {
            return defaultValue;
        }
    }

    /**
     * 将字符串中特定的关键词高亮为指定颜色
     *
     * @param source
     * @param target
     * @param color
     * @return
     */
    public static SpannableStringBuilder highLightPartOfString(String source, String target, int color) {
        if (TextUtils.isEmpty(source) || TextUtils.isEmpty(target)) {
            return null;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder(source);
        int sIndex = source.indexOf(target);
        if (sIndex > 0) {
            int eIndex = sIndex + target.length();
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
            builder.setSpan(colorSpan, sIndex, eIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    /**
     * 去除字符串中的中文
     *
     * @param text 待处理的字符串
     * @return 去除中文后的字符串
     */
    public static String removeCN(String text) {
        if (StringUtils.isEmpty(text)) {
            return "";
        }
        text = text.replaceAll("[\u4e00-\u9fa5]", "");
        return text;
    }

    /**
     * 格式化数字 (11231322.13 --> 11,231,322.13)
     *
     * @param num
     * @return
     */
    public static String stringToFormatNumber(String num) {
        String ret = "";
        String pointpart = "";
        boolean append = false;
        if (num != null) {
            num = num.replaceAll(",", "");
            int pos = num.indexOf(".");
            if (pos > 0) {
                pointpart = num.substring(pos, num.length());
                String t = pointpart;
                t = t.replaceAll("\\.", "");
                t = t.replaceAll("0", "");
                if (t.length() > 0) {
                    append = true;
                }
                num = num.substring(0, pos);
            }
            StringBuilder tmp = new StringBuilder().append(num.replaceAll(",", "")).reverse();
            String retNum = Pattern.compile("(\\d{" + 3 + "})(?=\\d)").matcher(tmp.toString()).replaceAll("$1,");
            ret = new StringBuilder().append(retNum).reverse().toString();
        }
        return ret + (append ? pointpart : "");
    }

    /**
     * 替换一个字符串中的某些指定字符
     *
     * @param strData     String 原始字符串
     * @param regex       String 要替换的字符串
     * @param replacement String 替代字符串
     * @return String 替换后的字符串
     */
    public static String replaceString(String strData, String regex, String replacement) {
        if (strData == null) {
            return null;
        }
        int index;
        index = strData.indexOf(regex);
        String strNew = "";
        if (index >= 0) {
            while (index >= 0) {
                strNew += strData.substring(0, index) + replacement;
                strData = strData.substring(index + regex.length());
                index = strData.indexOf(regex);
            }
            strNew += strData;
            return strNew;
        }
        return strData;
    }

    /**
     * 删除Editable中的最后输入的字符
     *
     * @param edit Editable类型的字符串
     */
    public static void deleteLastInputedEditableString(Editable edit) {
        if (edit == null) {
            return;
        }
        int length = edit.toString().length();
        if (length == 0) {
            return;
        }
        edit.delete(length - 1, length);
    }

    /**
     * 删除Editable中固定区间的的字符
     *
     * @param edit  Editable类型的字符串
     * @param start 要删除区间的开始位置
     * @param end   要删除区间的结束位置
     */
    public static void deleteEditableString(Editable edit, int start, int end) {
        if (edit == null) {
            return;
        }
        int length = edit.toString().length();
        if (length == 0) {
            return;
        }
        if (start >= end) {
            return;
        }
        if (end > length) {
            return;
        }
        edit.delete(start, end);
    }

    /**
     * 方法名称:transStringToMap 传入参数:mapString 形如 username'chenziwen^password'1234
     * 返回值:Map
     */
    public static Map transStringToMap(String mapString, String itemDiver, String keyDiver) {
        Map map = new HashMap();
        StringTokenizer items;
        for (StringTokenizer entrys = new StringTokenizer(mapString, itemDiver); entrys.hasMoreTokens(); map.put(
                items.nextToken(), items.hasMoreTokens() ? ((Object) (items.nextToken())) : null))
            items = new StringTokenizer(entrys.nextToken(), keyDiver);
        return map;
    }

    /**
     * 字符串数组转换成字符串
     *
     * @param strArray
     * @return
     */
    public static String strArray2Str(String[] strArray) {
        if (strArray == null || strArray.length == 0) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < strArray.length; i++) {
            buffer.append(strArray[i]);

        }
        return buffer.toString();
    }

    /**
     * 多数组元素全排列 eg:有多个字符串, 比如: "abcd" "12" "g" "qwe" 分别从每个字符串中取出一个值,
     * 然后组合.("a1gq" "a2gq" ... ...)
     *
     * @param inputList 需要排列的字符串数组
     * @return
     */
    public static List<String> permutation(List<String> inputList) {
        List<String> resList = new ArrayList<>();
        permutationInt(inputList, resList, 0, new char[inputList.size()]);
        return resList;
    }

    /**
     * 把map集合转换为String，格式为“key1=333&key2=444”
     *
     * @param map map集合
     * @return
     */
    public static String transMapToString(Map<String, String> map) {
        if (null == map || map.size() == 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (stringBuffer.length() > 0) {
                stringBuffer.append("&");
            }
            stringBuffer.append(entry.getKey());
            stringBuffer.append("=");
            stringBuffer.append(entry.getValue());
//            stringBuffer.append("&");
        }
        String paramStr = stringBuffer.toString();
        if (paramStr.length() == 0) {
            return "";
        }
        return paramStr;
    }

    private static void permutationInt(List<String> inputList, List<String> resList, int ind, char[] arr) {
        if (ind == inputList.size()) {
            resList.add(new String(arr));
            return;
        }

        for (char c : inputList.get(ind).toCharArray()) {
            arr[ind] = c;
            permutationInt(inputList, resList, ind + 1, arr);
        }
    }

    /**
     * 给一个字符串添加分隔符
     *
     * @param ori      源字符串
     * @param interval 每隔多少个字符添加一个分隔符
     * @param split    分隔符
     * @return
     */
    public static String addSplitToString(String ori, int interval, char split) {
        if (isEmpty(ori)) {
            return "";
        }
        char[] charArray = ori.toCharArray();
        if (charArray == null)
            return "";
        StringBuffer des = new StringBuffer();
        for (int i = 0; i < charArray.length; i++) {
            if (i > 0 && i % interval == 0) {
                des.append(split);
            }
            des.append(charArray[i]);
        }
        return des.toString();
    }

    public static String getFormatedPhoneNumber(String number) {
        if (number == null) {
            return "";
        }

        StringBuilder stringBuffer = new StringBuilder(24);
        String val = number;
        if (val != null && val.length() > 0) {
            String a = "";// 0-3
            String b = "";//
            String c = "";//

            val = val.replace(" ", "");
            if (val.length() >= 3) {
                a = val.substring(0, 3);
            } else if (val.length() < 3) {
                a = val.substring(0, val.length());
            }
            if (val.length() >= 7) {
                b = val.substring(3, 7);
                c = val.substring(7, val.length());
            } else if (val.length() > 3 && val.length() < 7) {
                b = val.substring(3, val.length());
            }
            if (a != null && a.length() > 0) {
                stringBuffer.append(a);
                if (a.length() == 3) {
                    stringBuffer.append(" ");
                }
            }
            if (b != null && b.length() > 0) {
                stringBuffer.append(b);
                if (b.length() == 4) {
                    stringBuffer.append(" ");
                }
            }
            if (c != null && c.length() > 0) {
                stringBuffer.append(c);
            }
        }
        return stringBuffer.toString();
    }

    /**
     * 判断String包含的工具类，防止传入null而造成的crash
     *
     * @param oriStr
     * @param tarStr
     * @return
     */
    public static boolean contains(String oriStr, String tarStr) {
        if (null == tarStr) {
            return false;
        }
        return oriStr.contains(tarStr);
    }

    /**
     * (位数为11位&&开头为1) || (前三位为'+86'&&总位数为14位)
     */
    public static boolean isPhoneNum(String phoneNum) {
        if (StringUtils.notEmpty(phoneNum)) {
            int length = phoneNum.length();
            return (length == 11 && phoneNum.startsWith("1")) || (length == 14 && phoneNum.startsWith("+861"));
        }
        return false;
    }

    /**
     * 将List中的String以format(分隔符，默认“,”)分隔开
     *
     * @param aList
     * @return
     */
    public static String toStringWithComma(List<String> aList, String format) {
        if (null == aList || aList.isEmpty()) {
            return "";
        }
        if (format == null) {
            format = ",";
        }
        StringBuilder tStringBuilder = new StringBuilder();
        for (String tString : aList) {
            if (notEmpty(tString)) {
                tStringBuilder.append(tString).append(format);
            }
        }
        if (tStringBuilder.length() == 0) {
            return "";
        }
        return tStringBuilder.substring(0, tStringBuilder.length() - format.length());
    }

    /**
     * 将String字符串转换为int类型
     */
    public static int toInt(String num, int exceptionDefault) {
        try {
            int tNum = Integer.valueOf(num);
            return tNum;
        } catch (Exception e) {
            return exceptionDefault;
        }
    }

    /**
     * 返回数组中是否含有targetStr
     *
     * @param targetStr
     * @param strArr
     * @return
     */
    public static boolean containInArray(String targetStr, String[] strArr) {
        if (strArr == null) {
            return false;
        }

        for (String s : strArr) {
            if (s == null) {
                continue;
            }

            if (s.equals(targetStr)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param str
     * @return
     */
    public static String trim(String str) {
        return str == null ? EMPTY : str.trim();
    }

    /**
     * Helper function for making null strings safe for comparisons, etc.
     *
     * @return (s == null) ? "" : s;
     */
    public static String makeSafe(String s) {
        return (s == null) ? "" : s;
    }

    public static String dealWithChinese(String content) {
        if (notEmpty(content) && isContainsChineseChar(content)) {
            StringBuffer sb = new StringBuffer();
            for (char c : content.toCharArray()) {
                if (!isChinese(c)) {
                    sb.append(c);
                }
            }
            content = sb.toString();
        }
        return content;
    }

}
