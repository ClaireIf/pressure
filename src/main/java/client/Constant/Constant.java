package client.Constant;

import com.googlecode.protobuf.format.FormatFactory;
import com.googlecode.protobuf.format.JsonFormat;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: TODO
 * Date: 2019-04-29 16:19
 * Author: Claire
 */
public class Constant {


    //token and channel data
    public static Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    //token and channel data
    public static List<String> channelIdList = new CopyOnWriteArrayList<>();

    //Sent user data
    public static List<String> msgIdList = new CopyOnWriteArrayList<>();

    //collection of tokens
    public static List<String> tokenList = new ArrayList<>();

    //collection of users
    public static List<String> userIdList = new ArrayList<>();

    //collection of users
    public static List<String> groupIdList = new ArrayList<>();

    //Judgment criteria
    public static AtomicInteger integer = new AtomicInteger(0);

    //Authorization code
    public static final AttributeKey<String> TOKEN = AttributeKey.newInstance("authToken");

    //Authorization code
    public static final AttributeKey<String> USERID = AttributeKey.newInstance("userId");
    //Authorization code
    public static final AttributeKey<String> SOURCE = AttributeKey.newInstance("source");

    //Get userId according to token
    public static final String GET_USERID_BYTOKEN = "loginToken:userId:%s";

    //Get user information based on UserId
    public static final String GET_USERINFO_BY_USERID = "loginToken:userIdInfo:%s";

    //Get user information based on token
    public static final String GET_USERINFO_BY_TOKEN = "loginToken:userTokenInfo:%s";

    //protobuf serialization
    public static final JsonFormat jsonFormat = (JsonFormat)new FormatFactory().createFormatter(FormatFactory.Formatter.JSON);
}
