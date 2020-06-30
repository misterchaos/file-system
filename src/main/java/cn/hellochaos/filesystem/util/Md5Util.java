package cn.hellochaos.filesystem.util;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 进行密码加密
 *
 * @author 黄钰朝
 */
public class Md5Util {

    /**
     * 对字符串进行MD5加密
     *
     * @param str 一般为用户密码
     * @return : java.lang.String
     * @author : huange7
     * @date : 2020-05-07 14:59
     */
    public static String getMd5String(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return (new BigInteger(1, md.digest())).toString(16);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }
}