package client;


import client.Constant.Constant;
import client.service.MessageServiceImpl;
import io.netty.channel.Channel;

/**
 * Description: TODO
 * Date: 2019-06-27 09:55
 * Author: Claire
 */
public class Test {

    public static void main(String[] args) throws InterruptedException{
        MessageServiceImpl messageService = MessageServiceImpl.getInstance();
        messageService.md5Login("test001","test001","ios");
//        testing
//        messageService.tokenConnect("test001", "test001", "computer");
        while (Constant.channelMap.size()<=0){
        	
        }
        for(int i=0; i<1; i++){
//        	single chat test
        	messageService.sendSingleMsg(String.valueOf(i),"102", "102", "102");
        	
//        	group chat test
        	messageService.sendGroupMsg(String.valueOf(i),"102", "102", "102");      	
        }
        
//        Channel channel = null;
//        for (String key: Constant.channelMap.keySet()) {
//            channel = Constant.channelMap.get(key);
//
//            break;
//        }
//        if(channel!=null) {
//        	channel.close();
//        }
    }
}
