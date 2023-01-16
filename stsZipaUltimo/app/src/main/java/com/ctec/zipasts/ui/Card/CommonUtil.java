package com.ctec.zipasts.ui.Card;


import android.text.format.Time;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * ***********************************
 * 类描述： 工具类
 * 创建者： 瞿峰
 * 创建时间： 2016-8-29 下午4:40:30
 *************************************
 */
public class CommonUtil {
    /**
     * 将byte数组转化为Hex字符
     *
     * @param byteArr
     *            待转换的数组
     * @return Hex字符
     */
    public static String bytes2HexStr(byte[] byteArr) {
        if (byteArr == null || byteArr.length == 0)
            return "";
        StringBuffer strBufTemp = new StringBuffer("");
        for (int i = 0; i < byteArr.length; i++) {
            String stmp = Integer.toHexString(byteArr[i] & 0XFF);
            if (stmp.length() == 1)
                strBufTemp.append("0" + stmp);
            else
                strBufTemp.append(stmp);
        }
        return strBufTemp.toString().toUpperCase(Locale.getDefault());
    }

    /**
     * 获取系统时间，6位，例如：2012-11-1 6:11:21，返回的就是20130717144032
     *
     * @return
     */
    public static String getSysDate() {

        Time t = new Time();
        t.setToNow();
        int year = t.year;
        int month = t.month + 1;
        int day = t.monthDay;

        int hour = t.hour; // 0-23
        int minute = t.minute;
        int second = t.second;

        return String.format("%04d%02d%02d%02d%02d%02d", year, month, day, hour, minute, second, Locale.getDefault());
    }

    /**
     * 判断是否纯数字
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
    /**
     * 将输入hex类型的String 数据转为byte�?
     *
     * @param src
     *            输入String是Hex类型数据�?长度�?��2的整数�?
     * @return byte型数�?
     */
    public static byte[] hexStr2Bytes(String hexSrc) {
        if (hexSrc == null || hexSrc.length() == 0 || hexSrc.length() % 2 != 0)
            return null;
        int nSrcLen = hexSrc.length();
        byte byteArrayResult[] = new byte[nSrcLen / 2];
        for (int i = 0; i < hexSrc.length() - 1; i += 2) {
            String strTemp = hexSrc.substring(i, i + 2);
            byteArrayResult[i / 2] = (byte) Integer.parseInt(strTemp, 16);
        }
        return byteArrayResult;
    }

    /**
     * 将输入hex类型的String 数据转为byte�?
     *
     * @param hexSrc
     * @param hexByte
     * @return
     */
    public static int hexStr2Bytes(String hexSrc, byte[] hexByte) {
        if (hexSrc == null || hexSrc.length() == 0 || hexSrc.length() % 2 != 0)
            return 0;

        int nSrcLen = hexSrc.length();

        if (hexByte == null || hexByte.length < nSrcLen / 2)
            return 0;

        for (int i = 0; i < nSrcLen; i += 2) {
            String strTemp = hexSrc.substring(i, i + 2);
            hexByte[i / 2] = (byte) Integer.parseInt(strTemp, 16);
        }
        return nSrcLen / 2;
    }

    public static byte[] createMasterKey(int length) {
        byte[] array = new byte[length];
        for (int i = 0; i < length; i++) {
            array[i] = (byte) 0x38;
        }
        return array;
    }

    public static int transferErrorCode(int errorCode) {
        int a = -errorCode;
        int b = a & 0x0000FF;
        return -b;
    }


    public static String getMethodName() {
        StackTraceElement[] eles = Thread.currentThread().getStackTrace();
//        for (StackTraceElement stackTraceElement : eles) {
//            Log.e("stackTraceElement", stackTraceElement.getMethodName());
//        }
        return eles[5].getMethodName();
    }

}

