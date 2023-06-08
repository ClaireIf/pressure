package client.utils;

import java.util.UUID;

/**
 * Description: TODO
 * Date: 2019-04-29 12:30
 * Author: Claire
 */
public class RandomUtils {

    //String concatenation produced by uuid
    private static final String UUID_CONNECTOR_CHART = "-";

    //empty string
    private static final String EMPTY_STRING = "";

    /**
     * Description: get random string
     * @Author: Claire
     * @param
     * @date: 2019-04-17
     * @return: java.lang.String
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString().replace(UUID_CONNECTOR_CHART, EMPTY_STRING);
    }
}
