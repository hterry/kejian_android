package com.weiguan.kejian.util;

/**
 * Created by Administrator on 2016/1/25 0025.
 */
public class HexStringUtil {
    public static String byteToString(byte b) {
       byte high, low;
        byte maskHigh = (byte)0xf0;
        byte maskLow = 0x0f;

        high = (byte)((b & maskHigh) >> 4);
        low = (byte)(b & maskLow);

        StringBuffer buf = new StringBuffer();
        buf.append(findHex(high));
        buf.append(findHex(low));

        return buf.toString();
    }

     /* byte to String */
     private static char findHex(byte b) {
        int t = new Byte(b).intValue();
        t = t < 0 ? t + 16 : t;

        if ((0 <= t) &&(t <= 9)) {
           return (char)(t + '0');
        }

        return (char)(t-10+'A');
     }
}
