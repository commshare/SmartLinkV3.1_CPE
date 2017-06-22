package com.alcatel.smartlinkv3;

import static com.alcatel.smartlinkv3.network.API.AUTHORIZATION_KEY;

/**
 * Created by tao.j on 2017/6/22.
 */

public class EncryptionUtil {

    public static String encrypt(String info){

        char[] key = AUTHORIZATION_KEY.toCharArray();
        char str1[] = new char[info.length() * 2];
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < info.length(); i++) {
            char char_i = info.charAt(i);
            str1[2 * i] = (char) ((key[i % key.length] & 0xf0) | ((char_i & 0xf) ^ (key[i % key.length] & 0xf)));
            str1[2 * i + 1] = (char) ((key[i % key.length] & 0xf0) | ((char_i >> 4) ^ (key[i % key.length] & 0xf)));
        }

        for (char aStr1 : str1) {
            builder.append(aStr1);
        }

        return  builder.toString();
    }
}
