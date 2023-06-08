package client.utils;

import client.protobuf.MsgBody;
import com.alibaba.fastjson.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Description: RSA Tools
 * Date: 2019-04-16 15:20
 * Author: Claire
 */
public class RSAAESUtils {

    /**
     * Define the encryption method RSA
     */
    public  final static String KEY_RSA = "RSA";

    /**
     * Define the encryption method AES
     */
    public  final static String KEY_AES = "AES";





    /**
     * Description: public key decryption
     * @Author: Claire
     * @param keySource Encryption
     * @param data Data to be decrypted
     * @param key public key
     * @date: 2019-04-16
     * @return: byte[]
     */
    public static String decryptByPublicKey(String keySource, String data, String key) {
        byte[] result = null;
        try {
            // public key
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key.getBytes("utf-8"));
            KeyFactory factory = KeyFactory.getInstance(keySource);
            PublicKey publicKey = factory.generatePublic(keySpec);
            // decrypt data
            Cipher cipher = Cipher.getInstance(factory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            result = cipher.doFinal(data.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(result);
    }



    /**
     * Description: private key encryption
     * @Author: Claire
     * @param data data to be encrypted
     * @param key private key
     * @date: 2019-04-16
     * @return: byte[]
     */
    public static String encryptByPrivateKey(String keySource, String data, String key) {
        byte[] result = null;
        try {
            // private key
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key.getBytes("utf-8"));
            KeyFactory factory = KeyFactory.getInstance(keySource);
            PrivateKey privateKey = factory.generatePrivate(keySpec);
            // Encrypt data
            Cipher cipher = Cipher.getInstance(factory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            result = cipher.doFinal(data.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(result);
    }

    public static final String CONTENT_PRIVATE_KEY = "prk";

    public static final String CONTENT_PUBLIC_KEY = "puk";

    public static final String CONTENT_DATA = "data";


    /**
     * Description: The server encrypts the content of the response to the APP
     * @Author: Claire
     * @param servicePublicKeyStr
     * @param content
     * @date: 2019-04-17
     * @return: java.lang.String
     */
    public static String serverEncrypt(String servicePublicKeyStr, String content) throws Exception{
        PublicKey appPublicKey = RSAUtils.string2PublicKey(servicePublicKeyStr);
        String aesKeyStr = AESUtils.genKeyAES();
        SecretKey aesKey = AESUtils.loadKeyAES(aesKeyStr);
        byte[] encryptAesKey = RSAUtils.publicEncrypt(aesKeyStr.getBytes(), appPublicKey);
        byte[] encryptContent = AESUtils.encryptAES(content.getBytes(), aesKey);

        JSONObject result = new JSONObject();
        result.put(CONTENT_PRIVATE_KEY, RSAUtils.byte2Base64(encryptAesKey).replaceAll("\r\n", ""));
        result.put(CONTENT_DATA, RSAUtils.byte2Base64(encryptContent).replaceAll("\r\n", ""));
        return result.toJSONString();
    }

    /**
     * Description: The server decrypts the request content of the APP
     * @Author: Claire
     * @param content
     * @param serverPriKey
     * @date: 2019-04-17
     * @return: java.lang.String
     */
    public static String serverDecrypt(String serverPriKey, String content) throws Exception{
        JSONObject result = JSONObject.parseObject(content);
        String encryptAesKeyStr = (String) result.get(CONTENT_PRIVATE_KEY);
        String encryptAppPublicKeyStr = (String) result.get(CONTENT_PUBLIC_KEY);
        String encryptContent = (String) result.get(CONTENT_DATA);

        PrivateKey serverPrivateKey = RSAUtils.string2PrivateKey(serverPriKey);
        byte[] aesKeyBytes = RSAUtils.privateDecrypt(RSAUtils.base642Byte(encryptAesKeyStr), serverPrivateKey);
        SecretKey aesKey = AESUtils.loadKeyAES(new String(aesKeyBytes));
        byte[] appPublicKeyBytes = AESUtils.decryptAES(RSAUtils.base642Byte(encryptAppPublicKeyStr), aesKey);
        byte[] request = AESUtils.decryptAES(RSAUtils.base642Byte(encryptContent), aesKey);

        JSONObject result2 = new JSONObject();
        result2.put(CONTENT_PRIVATE_KEY, new String(aesKeyBytes));
        result2.put(CONTENT_PUBLIC_KEY, new String(appPublicKeyBytes));
        result2.put(CONTENT_DATA, new String(request));
        return result2.toJSONString();
    }


    //APP encryption request content
    public static MsgBody.Body appEncrypt(String content, String publicKey) throws Exception{
        PublicKey serverPublicKey = RSAUtils.string2PublicKey(publicKey.replaceAll("\r\n", ""));
        String aesKeyStr = AESUtils.genKeyAES();
        SecretKey aesKey = AESUtils.loadKeyAES(aesKeyStr);
        byte[] encryptAesKey = RSAUtils.publicEncrypt(aesKeyStr.getBytes(), serverPublicKey);
        byte[] encryptRequest = AESUtils.encryptAES(content.getBytes(), aesKey);
        MsgBody.Body body = MsgBody.Body.newBuilder().setPrk(RSAUtils.byte2Base64(encryptAesKey).replaceAll("\r\n", ""))
                .setData(RSAUtils.byte2Base64(encryptRequest).replaceAll("\r\n", ""))
                .build();

        return body;
    }

    public static void appEncrypt(String content, String publicKey, MsgBody.Body.Builder bodyBuilder) throws Exception{
        PublicKey serverPublicKey = RSAUtils.string2PublicKey(publicKey.replaceAll("\r\n", ""));
        String aesKeyStr = AESUtils.genKeyAES();
        SecretKey aesKey = AESUtils.loadKeyAES(aesKeyStr);
        byte[] encryptAesKey = RSAUtils.publicEncrypt(aesKeyStr.getBytes(), serverPublicKey);
        byte[] encryptRequest = AESUtils.encryptAES(content.getBytes(), aesKey);

        bodyBuilder.setPrk(RSAUtils.byte2Base64(encryptAesKey).replaceAll("\r\n", ""))
                .setData(RSAUtils.byte2Base64(encryptRequest).replaceAll("\r\n", ""));

    }

    //APP encryption request content
    public static MsgBody.Body appEncryptWithPrivate(String content, String privateKey) throws Exception{
        PrivateKey serverPrivateKey = RSAUtils.string2PrivateKey(privateKey.replaceAll("\r\n", ""));
        String aesKeyStr = AESUtils.genKeyAES();
        SecretKey aesKey = AESUtils.loadKeyAES(aesKeyStr);
        byte[] encryptAesKey = RSAUtils.privateEncrypt(aesKeyStr.getBytes(), serverPrivateKey);
        byte[] encryptRequest = AESUtils.encryptAES(content.getBytes(), aesKey);

        MsgBody.Body body = MsgBody.Body.newBuilder().setPrk(RSAUtils.byte2Base64(encryptAesKey).replaceAll("\r\n", ""))
                .setData(RSAUtils.byte2Base64(encryptRequest).replaceAll("\r\n", ""))
                .build();

        return body;
    }

    //The APP decrypts the response content of the server
    public static String appDecrypt(String publicKey, String content,  String pk) throws Exception {
        PrivateKey serverPrivateKey = RSAUtils.string2PrivateKey(publicKey.replaceAll("\r\n", ""));
        byte[] aesKeyBytes = RSAUtils.privateDecrypt(RSAUtils.base642Byte(pk), serverPrivateKey);
        SecretKey aesKey = AESUtils.loadKeyAES(new String(aesKeyBytes));
        byte[] response = AESUtils.decryptAES(RSAUtils.base642Byte(content), aesKey);

        return new String(response);

    }

    //The APP decrypts the response content of the server
    public static String appDecryptWithPuk(String publicKey, String content,  String pk) throws Exception {
        PublicKey serverPublicKey = RSAUtils.string2PublicKey(publicKey.replaceAll("\r\n", ""));
        byte[] aesKeyBytes = RSAUtils.publicDecrypt(RSAUtils.base642Byte(pk), serverPublicKey);
        SecretKey aesKey = AESUtils.loadKeyAES(new String(aesKeyBytes));
        byte[] response = AESUtils.decryptAES(RSAUtils.base642Byte(content), aesKey);

        return new String(response);
    }

    /**
     * RSA maximum decryption ciphertext size
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * RSA maximum encrypted plaintext size
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;


    /**
     * <p>
     * public key decryption
     * </p>
     *
     * @param encryptedData encrypted data
     * @param publicKey Public key (BASE64 encoded)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // Decrypt data segments
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }


    /**
     * <p>
     * private key encryption
     * </p>
     *
     * @param data source data
     * @param privateKey Private key (BASE64 encoded)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // Encrypt data segments
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }



    public static final String privatekey = "";


}
