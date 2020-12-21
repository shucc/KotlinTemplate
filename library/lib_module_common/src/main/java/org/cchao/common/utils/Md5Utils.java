package org.cchao.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
public class Md5Utils {

    private static MessageDigest md5;

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Md5Utils() {
    }

    public static String getMd5(String string) {
        byte[] bs = md5.digest(string.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(40);
        for (byte x : bs) {
            if ((x & 0xff) >> 4 == 0) {
                sb.append("0").append(Integer.toHexString(x & 0xff));
            } else {
                sb.append(Integer.toHexString(x & 0xff));
            }
        }
        return sb.toString();
    }
}
