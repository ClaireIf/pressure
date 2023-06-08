package client.utils;

import client.protobuf.MsgBody;
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

    //aes encryption
    private static  final String AES = "AES";

    // Encryption and decryption algorithm/mode/filling method
    private static  final String AES_ALGORITHMSTR = "AES/ECB/PKCS7Padding";

    //number of digits
    private static final int KEY_INIT_NUM = 128;

    private static final String AES_PARAM = "BC";



    /**
     * Description: Produce regular AES keys
     * @Author: Claire
     * @param
     * @date: 2019-04-17
     * @return: java.lang.String
     */
    public static String genNormalKeyAES() throws Exception{
        KeyGenerator keyGen = KeyGenerator.getInstance(AES);
        keyGen.init(KEY_INIT_NUM);
        SecretKey key = keyGen.generateKey();
        return new String(key.getEncoded());
    }

    /**
     * Description: Generate an AES key, then Base64 encode
     * @Author: Claire
     * @param
     * @date: 2019-04-17
     * @return: java.lang.String
     */
    public static String genKeyAES() throws Exception{
        KeyGenerator keyGen = KeyGenerator.getInstance(AES);
        keyGen.init(KEY_INIT_NUM);
        SecretKey key = keyGen.generateKey();
        String base64Str = byte2Base64(key.getEncoded());
        return base64Str;
    }

    /**
     * Description: Convert AES key to SecretKey object
     * @Author: Claire
     * @param keyVal
     * @date: 2019-04-17
     * @return: javax.crypto.SecretKey
     */
    public static SecretKey loadNormalKeyAES(String keyVal) throws Exception{
        byte[] bytes = keyVal.getBytes("utf-8");
        SecretKeySpec key = new SecretKeySpec(bytes, AES);
        return key;
    }

    /**
     * Description: Convert the Base64-encoded AES key into a SecretKey object
     * @Author: Claire
     * @param base64Key
     * @date: 2019-04-17
     * @return: javax.crypto.SecretKey
     */
    public static SecretKey loadKeyAES(String base64Key) throws Exception{
        byte[] bytes = base642Byte(base64Key);
        SecretKeySpec key = new SecretKeySpec(bytes, AES);
        return key;
    }

    /**
     * Description: Encrypt, return byte array
     * @Author: Claire
     * @param source
     * @param key
     * @date: 2019-04-17
     * @return: byte[]
     */
    public static byte[] encryptAES(byte[] source, SecretKey key) throws Exception{
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(AES_ALGORITHMSTR, AES_PARAM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(source);
    }

    /**
     * Description: Decrypt, return byte array
     * @Author: Claire
     * @param source
     * @param key
     * @date: 2019-04-17
     * @return: byte[]
     */
    public static byte[] decryptAES(byte[] source, SecretKey key) throws Exception{
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(AES_ALGORITHMSTR, AES_PARAM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(source);
    }

    /**
     * Description: Byte array to Base64 encoding
     * @Author: Claire
     * @param bytes
     * @date: 2019-04-17
     * @return: java.lang.String
     */
    public static String byte2Base64(byte[] bytes){
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(bytes);
    }

    /**
     * Description: Base64 encoding to byte array
     * @Author: Claire
     * @param base64Key
     * @date: 2019-04-17
     * @return: byte[]
     */
    public static byte[] base642Byte(String base64Key) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        return decoder.decodeBuffer(base64Key);
    }

    /**
     * Description: encrypted, return string
     * @Author: Claire
     * @param source
     * @param key
     * @date: 2019-04-17
     * @return: java.lang.String
     */
    public static String encryptAESString(byte[] source, SecretKey key) throws Exception{
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(AES_ALGORITHMSTR, AES_PARAM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return new String(cipher.doFinal(source));
    }

    /**
     * Description: Decrypt, return string
     * @Author: Claire
     * @param source
     * @param key
     * @date: 2019-04-17
     * @return: java.lang.String
     */
    public static String decryptAESString(byte[] source, SecretKey key) throws Exception{
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(AES_ALGORITHMSTR, AES_PARAM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(source));
    }


    public static void appEncrypt(String content, String publicKey, MsgBody.Body.Builder bodyBuilder) throws Exception {
        SecretKey aesKey = loadKeyAES(publicKey);
        String dataAesKeyStr = genKeyAES();
        //String dataAesKeyStr = byte2Base64(genKeyAES().getBytes());
        System.out.println(byte2Base64(dataAesKeyStr.getBytes()));
        SecretKey dataSecretKey = loadKeyAES(byte2Base64(dataAesKeyStr.getBytes()));
        byte[] dataBytes = encryptAES(content.getBytes(), dataSecretKey);
        byte[] prkBytes = encryptAES(dataAesKeyStr.getBytes(), aesKey);
        String prk = byte2Base64(prkBytes);
        bodyBuilder.setPrk(prk)
                .setData(byte2Base64(dataBytes));
    }


}
