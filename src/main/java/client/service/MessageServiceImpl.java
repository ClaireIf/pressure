package client.service;

import client.ClientPoolHandler;
import client.Constant.Constant;
import client.protobuf.MsgBody;
import client.utils.HttpUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Description: TODO
 * Date: 2019-05-09 11:27
 * Author: Claire
 */
public class MessageServiceImpl {


    /**
     * Description: initialization
     * @Author: Claire
     * @param
     * @date: 2019-06-27
     * @return: client.service.MessageServiceImpl
     */
    public static MessageServiceImpl getInstance() {
        return new MessageServiceImpl();
    }

    /**
     * Description: initialization
     * @Author: Claire
     * @param
     * @date: 2019-06-27
     * @return: client.service.MessageServiceImpl
     */
    public static MessageServiceImpl getInstance(String host , int port) {
        MessageServiceImpl messageService = new MessageServiceImpl();
        messageService.HOST = host;
        messageService.PORT =port;
        return messageService;
    }

    String HOST = "127.0.0.1";
    int PORT = 54321;
    String loginUrl="http://127.0.0.1:8099/v2/user/login";
    String md5LoginUrl="http://127.0.0.1:8099/v2/user/md5PwdLogin";

    //api_token name
    private static final String API_TOKEN_NAME = "appToken";
    //user id
    private static final String USER_ID_NAME = "userId";
    //user id
    private static final String USER_NAME = "user";


    /**
     * Description: Retry
     * @Author: Claire
     * @param
     * @date: 2019-05-27
     * @return: void
     */
    public void reconnect(String token, String userId, String source){
        System.out.println("Retry Mechanism Execution："+token+", userId:"+ userId+", source: "+source);
        if(token==null|| token.isEmpty()){
            System.out.println("token is null");
            return;
        }
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap cb = new Bootstrap();
        cb.group(group).channel(NioSocketChannel.class);
        cb.remoteAddress(HOST, PORT);
        FixedChannelPool fixedChannelPool = new FixedChannelPool(cb,
                new ClientPoolHandler(), 1);
        Future<Channel> f = fixedChannelPool.acquire();

        f.addListener((FutureListener<Channel>) f1 -> {
            if (f1.isSuccess()) {
                Channel ch = f1.getNow();
                ch.attr(Constant.TOKEN).set(token);
                ch.attr(Constant.USERID).set(userId);
                ch.attr(Constant.SOURCE).set(source);
                //login
                MsgBody.Head.Builder heartMsgBuilder = MsgBody.Head.newBuilder();
                heartMsgBuilder.setType(5).setToken(token).setId(String.valueOf(userId))
                        .setVersion("1.0").setSendUserId(userId);
                heartMsgBuilder.setSource(source);
                MsgBody.Head heartMsg = heartMsgBuilder.build();

                MsgBody.Msg msg = MsgBody.Msg.newBuilder().setHead(heartMsg).build();
                ch.writeAndFlush(msg);

            }else{
                //Connection establishment failed
                System.out.println("105 Connection establishment failed");
                //Channel channel = f1.getNow();
                //channel.close();
            }
        });
    }


    /**
     * Description: single chat
     * @Author: Claire
     * @param userId
     * @param msg
     * @date: 2019-06-25
     * @return: void
     */
    public boolean sendSingleMsg(String msg, String userId, String receiver, String sender){
        if(Constant.channelMap.size()<=0){
            System.out.println("No content is obtained, return");
            return false;
        }
        Channel channel = null;
        for (String key: Constant.channelMap.keySet()) {
            channel = Constant.channelMap.get(key);

            break;
        }
        if(channel ==null){
            System.out.println("channel is empty, return");
            return false;
        }
        String token = channel.attr(Constant.TOKEN).get();
        String sendUserId = channel.attr(Constant.USERID).get();
        String source = channel.attr(Constant.SOURCE).get();
        
        MsgBody.Msg sendMsg= getMsg(msg, 1, token, userId, source, sendUserId, receiver, sender);
        System.out.println("The content of the single chat message sent is:"+msg+",receiverid："+userId+",content："+sendMsg.toString());
        channel.writeAndFlush(sendMsg);
        return true;
    }


    /**
     * Description: group chat
     * @Author: Claire
     * @param groupId
     * @param msg
     * @param source
     * @date: 2019-06-25
     * @return: void
     */
    public boolean sendGroupMsg(String msg, String groupId,  String receiver, String sender){
        if(Constant.channelMap.size()<=0){
            System.out.println("No content is obtained, return");
            return false;
        }
        Channel channel = null;
        for (String key: Constant.channelMap.keySet()) {
            channel = Constant.channelMap.get(key);

            break;
        }
        if(channel ==null){
            System.out.println("channel is empty, return");
            return false;
        }
        String token = channel.attr(Constant.TOKEN).get();
        String userId = channel.attr(Constant.USERID).get();
        String source = channel.attr(Constant.SOURCE).get();
        System.out.println("The content of the group chat message sent is:"+msg+",group id："+groupId);
        MsgBody.Msg sendMsg= getMsg(msg, 2, token, groupId, source, userId, receiver, sender);
        channel.writeAndFlush(sendMsg);
        return true;
    }


    /**
     * Description: get message
     * @Author: Claire
     * @param msg
     * @param type
     * @param userId
     * @date: 2019-06-25
     * @return: client.protobuf.MsgBody.Msg
     */
    public MsgBody.Msg getMsg(String msg, int type, String token,
                              String userId,  String source, String sendUserId,
                              String receiver, String sender){
        MsgBody.Head.Builder headBuilder = MsgBody.Head.newBuilder();
        String messageId = UUID.randomUUID().toString();
        headBuilder.setId(userId).setContentType(1).setSource(source).setVersion("1.0")
                .setToken(token).setType(type).setSendUserId(sendUserId).setMessageId(messageId);
        
        Map dataMap = new HashMap();
        Map contentMap = new HashMap();
        contentMap.put("text", msg);
        contentMap.put("uuid", messageId);
        dataMap.put("content", JSONObject.toJSONString(contentMap));
        dataMap.put("receiver", receiver);
        dataMap.put("sender", sender);
        MsgBody.Body.Builder bodyBuilder = MsgBody.Body.newBuilder().setData(JSONObject.toJSONString(dataMap));
        return MsgBody.Msg.newBuilder().setBody(bodyBuilder).setHead(headBuilder).build();
    }


    /**
     * Description: login
     * @Author: Claire
     * @param pwd
     * @param account
     * @date: 2019-06-25
     * @return: void
     */
    public boolean login(String account, String pwd, String source) {

        Map<String, String> accountMap = new HashMap<>();
        accountMap.put("username",account);
        accountMap.put("password",pwd);
        accountMap.put("loginType","sanxing");
        String json = HttpUtils.doPostWithJson(loginUrl, JSONObject.toJSONString(accountMap), source);
        System.out.println(json);
        JSONObject jsonObject = JSON.parseObject(json).getJSONObject("data");
        String apiToken = jsonObject.getString("appToken");
		/*
		 * JSONObject userJsonObject = JSON.parseObject(jsonObject.getString("userId"));
		 * if(null == userJsonObject){ System.out.println("There is no user information in the returned data, and the id information cannot be obtained");
		 * return false; }
		 */
        String userId = jsonObject.getString("userId");
        if(apiToken==null|| apiToken.isEmpty()){
            System.out.println("There is no token information in the returned data");
            return false;
        }
        //connect to the server
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap cb = new Bootstrap();
        cb.group(group).channel(NioSocketChannel.class);
        cb.remoteAddress(HOST, PORT);
        FixedChannelPool fixedChannelPool = new FixedChannelPool(cb,
                new ClientPoolHandler(), 1);
        Future<Channel> f = fixedChannelPool.acquire();
        f.addListener((FutureListener<Channel>) f1 -> {
            if (f1.isSuccess()) {
                System.out.println("Send a message");
                Channel ch = f1.getNow();

                ch.attr(Constant.TOKEN).set(apiToken);
                ch.attr(Constant.USERID).set(userId);
                ch.attr(Constant.SOURCE).set(source);
                //login
                MsgBody.Head heartMsg = MsgBody.Head.newBuilder().setType(5).setToken(apiToken)
                        .setVersion("1.0").setSource(source).setSendUserId(userId).build();

                MsgBody.Msg msg = MsgBody.Msg.newBuilder().setHead(heartMsg).build();
                ch.writeAndFlush(msg);
            }else{
                Channel channel = f1.getNow();
                if(channel!=null){
                    System.out.println("Make sure there is a channel:");
                    channel.attr(Constant.TOKEN).set(apiToken);
                    channel.close();
                }else{
                    reconnect(apiToken, userId, source);
                }
            }

        });
        return true;
    }
    
    /**
     * Description: login
     * @Author: Claire
     * @param pwd
     * @param account
     * @date: 2019-06-25
     * @return: void
     */
    public boolean md5Login(String account, String pwd, String source) {

        Map<String, String> accountMap = new HashMap<>();
        accountMap.put("userAccount",account);
        accountMap.put("password",pwd);
        accountMap.put("loginType","sanxing");
        String json = HttpUtils.doPostWithJson(md5LoginUrl, JSONObject.toJSONString(accountMap), source);
        System.out.println(json);
        JSONObject jsonObject = JSON.parseObject(json).getJSONObject("data");
        String apiToken = jsonObject.getString(API_TOKEN_NAME);
        
        String userId = jsonObject.getString(USER_ID_NAME);
        if(apiToken==null|| apiToken.isEmpty()){
            System.out.println("There is no token information in the returned data");
            return false;
        }
        //connect to the server
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap cb = new Bootstrap();
        cb.group(group).channel(NioSocketChannel.class);
        cb.remoteAddress(HOST, PORT);
        FixedChannelPool fixedChannelPool = new FixedChannelPool(cb,
                new ClientPoolHandler(), 1);
        Future<Channel> f = fixedChannelPool.acquire();
        f.addListener((FutureListener<Channel>) f1 -> {
            if (f1.isSuccess()) {
                System.out.println("Send a message");
                Channel ch = f1.getNow();

                ch.attr(Constant.TOKEN).set(apiToken);
                ch.attr(Constant.USERID).set(userId);
                ch.attr(Constant.SOURCE).set(source);
                //login
                MsgBody.Head heartMsg = MsgBody.Head.newBuilder().setType(5).setToken(apiToken)
                        .setVersion("1.0").setSource(source).setSendUserId(userId).build();

                MsgBody.Msg msg = MsgBody.Msg.newBuilder().setHead(heartMsg).build();
                ch.writeAndFlush(msg);
            }else{
                Channel channel = f1.getNow();
                if(channel!=null){
                    System.out.println("Make sure there is a channel:");
                    channel.attr(Constant.TOKEN).set(apiToken);
                    channel.close();
                }else{
                    reconnect(apiToken, userId, source);
                }
            }

        });
        return true;
    }
    
    
    /**
     * Directly use the token link
     */
    public void tokenConnect(String token, String userId, String source) {
    	if(token==null || userId==null || source==null) {
    		System.out.println("The link parameter is not empty, return");
    	}
    	//connect to the server
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap cb = new Bootstrap();
        cb.group(group).channel(NioSocketChannel.class);
        cb.remoteAddress(HOST, PORT);
        FixedChannelPool fixedChannelPool = new FixedChannelPool(cb,
                new ClientPoolHandler(), 1);
        Future<Channel> f = fixedChannelPool.acquire();
        f.addListener((FutureListener<Channel>) f1 -> {
            if (f1.isSuccess()) {
                System.out.println("Send a message");
                Channel ch = f1.getNow();

                ch.attr(Constant.TOKEN).set(token);
                ch.attr(Constant.USERID).set(userId);
                ch.attr(Constant.SOURCE).set(source);
                //login
                MsgBody.Head heartMsg = MsgBody.Head.newBuilder().setType(5).setToken(token)
                        .setVersion("1.0").setSource(source).setSendUserId(userId).build();

                MsgBody.Msg msg = MsgBody.Msg.newBuilder().setHead(heartMsg).build();
                ch.writeAndFlush(msg);
            }else{
                Channel channel = f1.getNow();
                if(channel!=null){
                    System.out.println("Make sure there is a channel:");
                    channel.attr(Constant.TOKEN).set(token);
                    channel.close();
                }else{
                    reconnect(token, userId, source);
                }
            }

        });
    }
}
