package com.testchat;

import com.alibaba.fastjson.JSONObject;
import com.testchat.CommonConstant;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Security;

/**
 * Description: AES tool class
 * Date: 2019-04-16 16:36
 * Author: Claire
 */
public class AESUtils {

    private static  final String AES = "AES";

    private static  final String AES_ALGORITHMSTR = "AES/ECB/PKCS7Padding";

    private static final int KEY_INIT_NUM = 128;

    private static final String AES_PARAM = "BC";

    public static final String CONTENT_PRIVATE_KEY = "prk";



    public static void main(String[] args) throws Exception {
        String data = "NmMd4iylVUcxJV3dcsyjsw==";
        String ss = decryptAESString("YH1f9SIsbpXFFiB2xWW8AA==", data);
        System.out.println(ss);
    }

}
