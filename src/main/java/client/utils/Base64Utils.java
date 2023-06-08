package client.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

/**
 * Description: TODO
 * Date: 2019-04-24 11:37
 * Author: Claire
 */
public class Base64Utils {

    /**
     * file read buffer size
     */
    private static final int CACHE_SIZE = 1024;

    /**
     * <p>
     * BASE64 string decoding to binary data
     * </p>
     *
     * @param base64
     * @return
     * @throws Exception
     */
    public static byte[] decode(String base64) throws Exception {
        BASE64Decoder decoder = new BASE64Decoder();
        return decoder.decodeBuffer(base64);
    }

    /**
     * <p>
     * Binary data is encoded as a BASE64 string
     * </p>
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    public static String encode(byte[] bytes) throws Exception {
        BASE64Encoder encoder = new BASE64Encoder();
        return new String(encoder.encode(bytes));
    }

    /**
     * <p>
     * Encode the file as a BASE64 string
     * </p>
     * <p>
     * Use with caution for large files, it may cause memory overflow
     * </p>
     *
     * @param filePath
     *            file absolute path
     * @return
     * @throws Exception
     */
    public static String encodeFile(String filePath) throws Exception {
        byte[] bytes = fileToByte(filePath);
        return encode(bytes);
    }

    /**
     * <p>
     * BASE64 string back to file
     * </p>
     *
     * @param filePath
     *            file absolute path
     * @param base64
     *            encoded string
     * @throws Exception
     */
    public static void decodeToFile(String filePath, String base64) throws Exception {
        byte[] bytes = decode(base64);
        byteArrayToFile(bytes, filePath);
    }

    /**
     * <p>
     * Convert the file to a binary array
     * </p>
     *
     * @param filePath
     *            file path
     * @return
     * @throws Exception
     */
    public static byte[] fileToByte(String filePath) throws Exception {
        byte[] data = new byte[0];
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead = 0;
            while ((nRead = in.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
            out.close();
            in.close();
            data = out.toByteArray();
        }
        return data;
    }

    /**
     * <p>
     * binary data write file
     * </p>
     *
     * @param bytes
     *            binary data write file
     * @param filePath
     *            file generation directory
     */
    public static void byteArrayToFile(byte[] bytes, String filePath) throws Exception {
        InputStream in = new ByteArrayInputStream(bytes);
        File destFile = new File(filePath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        destFile.createNewFile();
        OutputStream out = new FileOutputStream(destFile);
        byte[] cache = new byte[CACHE_SIZE];
        int nRead = 0;
        while ((nRead = in.read(cache)) != -1) {
            out.write(cache, 0, nRead);
            out.flush();
        }
        out.close();
        in.close();
    }

}
