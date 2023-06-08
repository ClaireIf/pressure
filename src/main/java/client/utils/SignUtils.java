package client.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class SignUtils {

    public static final String TOKEN_NAME = "authorization";

    public static final String BEARER = "Bearer";

    public static final String LOGIN_TYPE = "cli-os";

    public static final String APP_VERSION = "app-version";

    public static final String API_VERSION = "api-version";

    public static final String REQUEST_TIME = "request-time";

    public static final String HTTP = "http://";

    public static final String HTTPS = "https://";

    public static final String SIGNATURE = "signature";

    private static final String REPLACE_EMPTY = "";

    private static final String SPACE = " ";

    private static final String EXCLAMATION_MARK_SPACE = "! ";

    private static final String Y_AXIS_SPACE = " |";

    private static final String PERCENT_SPACE = " %";

    private static final String PLUS_SPACE = " +";

    private static final String QUESTION_MARK_SPACE = " ？";

    private static final String ENCRYPT_TYPE_HMACSHA256 = "HmacSHA256";

    private static final String STRING_CONNECT_COMMA = ",";

    private static final String URL_PARAM_SEPARATOR = "&";

    private static final String STRING_MONTAGE_EQUAL = "=";

    private static final String STRING_MONTAGE_AND = "&";

    private static final String URL_REPLACE_CHAR = "/";

    private static final String URL_REPLACE_END_CHAR = "\\\\/";

    /**
     * Verify signature
     * @param request
     * @return
     */
    public static boolean checkSign(HttpServletRequest request) {
        String clientType = request.getHeader(LOGIN_TYPE);
        if (clientType == null) {
            return false;
        }
        boolean flag = false;
        String appVersion = getHeaderParams(request, APP_VERSION);
        String apiVersion = getHeaderParams(request, API_VERSION);
        String bearerToken = getHeaderParams(request, TOKEN_NAME).replaceFirst(BEARER, REPLACE_EMPTY).trim();
        String requestTime = getHeaderParams(request, REQUEST_TIME);
//        String paramStr = request.getQueryString() == null ? "" : removeSignParam(request.getQueryString());
        Map<String, String> params = getParameterStringMap(request);
        String paramStr = createLinkString(params);
        String url = request.getRequestURL().toString();
        url = url.replaceFirst(HTTP, "").replaceFirst(HTTPS, "");

        String signature = request.getParameter(SIGNATURE);
        if (signature==null||signature.isEmpty()) {
            return false;
        }

        if (clientType.contains("android")) {
            flag = hasValidAndroidSignature(url, appVersion, apiVersion, bearerToken, requestTime, paramStr, signature);
        } else if (clientType.contains("ios")) {
            flag = hasValidIosSignature(url, appVersion, apiVersion, bearerToken, requestTime, paramStr, signature);
        } else {
            flag = hasValidDesktopSignature(url, appVersion, apiVersion, bearerToken, requestTime, paramStr, signature);
        }
        return flag;
    }


    public static boolean hasValidDesktopSignature(String url, String appVersion, String apiVersion, String bearerToken, String requestTime, String paramStr, String signature) {

        // Source string before signature encryption
        String original = url + SPACE + EXCLAMATION_MARK_SPACE + appVersion + SPACE + apiVersion + SPACE + bearerToken
                + SPACE + requestTime + paramStr;
        System.out.println(original);
        String newSignature = hashHmac(ENCRYPT_TYPE_HMACSHA256, original, "windows");
        return signature.equals(newSignature);
    }

    public static boolean hasValidIosSignature(String url, String appVersion, String apiVersion, String bearerToken, String requestTime, String paramStr, String signature) {

        String original = url + Y_AXIS_SPACE + apiVersion + SPACE + paramStr + PERCENT_SPACE + appVersion + SPACE
                + requestTime + SPACE + bearerToken;
        System.out.println(original);
        // HMAC encryption HmacSHA256
        String newSignature = hashHmac(ENCRYPT_TYPE_HMACSHA256, original, "ios");
        return signature.equals(newSignature);
    }

    public static boolean hasValidAndroidSignature(String url, String appVersion, String apiVersion, String bearerToken, String requestTime, String paramStr, String signature) {

        String original = paramStr + SPACE + url + PLUS_SPACE + bearerToken + SPACE + apiVersion + SPACE + requestTime
                + QUESTION_MARK_SPACE + appVersion;
        System.out.println(original);
        // HMAC encryption
        String newSignature = hashHmac(ENCRYPT_TYPE_HMACSHA256, original, "android");
        return signature.equals(newSignature);
    }

    /**
     * Consistent with php hash_hmac method
     *
     * @param macT
     * @param data
     * @param key
     * @return
     */
    private static String hashHmac(String macT, String data, String key) {
        try {
            Mac mac = Mac.getInstance(macT);
            SecretKeySpec secret = new SecretKeySpec(key.getBytes("UTF-8"), mac.getAlgorithm());
            mac.init(secret);
            return byteArrayToHexString(mac.doFinal(data.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * Convert byte array to hexadecimal string
     *
     * @param b byte array
     * @return hexadecimal string
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    //The return value type is Map<String, String>
    public static Map<String, String> getParameterStringMap(HttpServletRequest request) {
        Map<String, String[]> properties = request.getParameterMap();//Encapsulate request parameters into Map<String, String[]>
        Map<String, String> returnMap = new HashMap<String, String>();
        String name = "";
        String value = "";
        for (Map.Entry<String, String[]> entry : properties.entrySet()) {
            name = entry.getKey();
            String[] values = entry.getValue();
            if (null == values) {
                value = "";
            } else if (values.length > 1) {
                for (int i = 0; i < values.length; i++) {
                    value = values[i] + STRING_CONNECT_COMMA;
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = values[0];//Used for the unique request parameter name in the request parameter
            }
            returnMap.put(name, value);

        }
        return returnMap;
    }

    public static String removeSignParam(String str) {
        String[] temps = str.split(URL_PARAM_SEPARATOR);
        StringBuffer stringBuffer = new StringBuffer();
        for (String temp : temps) {
            if (temp.indexOf(SIGNATURE) != -1) {
                continue;
            }
            stringBuffer.append(temp + URL_PARAM_SEPARATOR);
        }
        String newStr = "";
        if (stringBuffer.length() > 0) {
            newStr = stringBuffer.substring(0, stringBuffer.length() - 1);
        }
        return newStr;
    }

    /**
     * Sort all the elements of the array and concatenate them into a string with the "&" character according to the pattern of "parameter = parameter value"
     *
     * @param params Parameter groups that need to be sorted and participate in character splicing
     * @return concatenated string
     */
    public static String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        keys.remove(SIGNATURE);
        Collections.sort(keys);
        String prestr = "";
        for (String key : keys) {
            String value = String.valueOf(params.get(key));
            prestr = prestr + key + STRING_MONTAGE_EQUAL + value + STRING_MONTAGE_AND;
        }
        if (prestr.length() > 0)
            prestr = prestr.substring(0, prestr.length() - 1);
        return prestr;
    }


    /**
     * Sort all the elements of the array and concatenate them into a string with the "&" character according to the pattern of "parameter = parameter value"
     *
     * @param params Parameter groups that need to be sorted and participate in character splicing
     * @return concatenated string
     */
    public static String mapToSortString(Map params) {
        List<String> keys = new ArrayList<>(params.keySet());
        keys.remove(SIGNATURE);
        Collections.sort(keys);
        StringBuffer sb = new StringBuffer();
        String substring = "";
        for (String key : keys) {
            Object obj = params.get(key);
            if (obj instanceof ArrayList) {
                ArrayList<Object> arrayList = (ArrayList<Object>) obj;
                JSONArray jsonArray = new JSONArray();
                for (Object s : arrayList) {
                    if (s instanceof String) {
                        jsonArray.add(s);
                    } else if (s instanceof Map) {
                        HashMap map = (HashMap) s;
                        Iterator iter = map.entrySet().iterator();
                        JSONObject jsonObject = new JSONObject();
                        while (iter.hasNext()) {
                            Map.Entry entrys = (Map.Entry) iter.next();
                            String linkKey = entrys.getKey().toString();
                            String val = entrys.getValue().toString();
                            jsonObject.put(linkKey, val);
                        }
                        jsonArray.add(jsonObject);
                    }
                }
                sb.append(key).append(STRING_MONTAGE_EQUAL).append(jsonArray.toString());
            } else {
                if (obj instanceof String) {
                    sb.append(key).append(STRING_MONTAGE_EQUAL).append(obj.toString().trim());
                } else {
                    sb.append(key).append(STRING_MONTAGE_EQUAL).append(obj);
                }
            }
            sb.append(STRING_MONTAGE_AND);
        }
        if (sb.length()>0) {
            substring = sb.substring(0, sb.length() - 1).replaceAll(URL_REPLACE_CHAR,URL_REPLACE_END_CHAR).trim();
        }
        return substring;
    }

    public static String createOldSign(String url, Map<String, String> params, HttpServletRequest request) {
        String clientType = request.getHeader(LOGIN_TYPE);
        String appVersion = getHeaderParams(request, APP_VERSION);
        String apiVersion = getHeaderParams(request, API_VERSION);
        String paramStr = createLinkString(params);
        String bearerToken = getHeaderParams(request, TOKEN_NAME).replaceFirst(BEARER, REPLACE_EMPTY).trim();
        // request time
        String requestTime = getHeaderParams(request, REQUEST_TIME);

        url = url.replaceFirst(HTTP, REPLACE_EMPTY).replaceFirst(HTTPS, REPLACE_EMPTY);

        String signature = null;
        if (clientType.contains("android")) {
            String original = url + SPACE + paramStr + SPACE + bearerToken + SPACE + apiVersion + SPACE + appVersion
                    + SPACE + requestTime;
            // HMAC 
            signature = hashHmac(ENCRYPT_TYPE_HMACSHA256, original, "android");
        } else if (clientType.contains("ios")) {
            String original = url + SPACE + paramStr + SPACE + apiVersion + SPACE + appVersion + SPACE
                    + bearerToken + SPACE + requestTime;
            signature = hashHmac(ENCRYPT_TYPE_HMACSHA256, original, "ios");
        } else {
            String original = url + SPACE + paramStr + SPACE + apiVersion + SPACE + bearerToken + SPACE
                    + appVersion + SPACE + requestTime;
            System.out.println(original);
            signature = hashHmac(ENCRYPT_TYPE_HMACSHA256, original, "windows");
        }
        return signature;
    }


    public static String createOldSignNotType(String url, Map params, HttpServletRequest request) {
        String clientType = request.getHeader(LOGIN_TYPE);
        String appVersion = getHeaderParams(request, APP_VERSION);
        String apiVersion = getHeaderParams(request, API_VERSION);
        String paramStr = mapToSortString(params);
        String bearerToken = getHeaderParams(request, TOKEN_NAME).replaceFirst(BEARER, REPLACE_EMPTY).trim();
        // request time
        String requestTime = getHeaderParams(request, REQUEST_TIME);

        url = url.replaceFirst(HTTP, REPLACE_EMPTY).replaceFirst(HTTPS, REPLACE_EMPTY);

        String signature = null;
        if (clientType.contains("android")) {
            String original = url + SPACE + paramStr + SPACE + bearerToken + SPACE + apiVersion + SPACE
                    + appVersion + SPACE + requestTime;
            signature = hashHmac(ENCRYPT_TYPE_HMACSHA256, original, "android");
        } else if (clientType.contains("ios")) {
            String original = url + SPACE + paramStr + SPACE + apiVersion + SPACE + appVersion + SPACE
                    + bearerToken + SPACE + requestTime;
            signature = hashHmac(ENCRYPT_TYPE_HMACSHA256, original, "ios");
        } else {
            String original = url + SPACE + paramStr + SPACE + apiVersion + SPACE + bearerToken + SPACE
                    + appVersion + SPACE + requestTime;
            signature = hashHmac(ENCRYPT_TYPE_HMACSHA256, original, "windows");
        }
        return signature;
    }

    /**
     * Description: Get the value in the header
     * @Author: Claire
     * @param request
     * @param key
     * @date: 2019-06-04
     * @return: java.lang.String
     */
    public static String getHeaderParams(HttpServletRequest request, String key) {
        return request.getHeader(key) == null ? "" : request.getHeader(key);
    }

    /**
     * Description: Get the value in the header
     * @Author: Claire
     * @param request
     * @param key
     * @date: 2019-06-04
     * @return: java.lang.String
     */
    public static String getHeaderToken(HttpServletRequest request, String key) {
        String token = getHeaderParams(request, key);
        if(token==null||token.isEmpty()){
            return null;
        }
        return token.replaceFirst(BEARER, REPLACE_EMPTY).trim();

    }


    /**
     * Description: Get login source
     * @Author: Claire
     * @param request
     * @date: 2019-06-13
     * @return: java.lang.String
     */
    public static String getHeaderSource(HttpServletRequest request){
        return request.getHeader(LOGIN_TYPE);
    }

}
