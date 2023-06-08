package client;

import client.Constant.Constant;
import client.protobuf.MsgBody;
import client.service.MessageServiceImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * Description: TODO
 * Date: 2019-04-11 10:31
 * Author: Claire
 */
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    /** The heartbeat command requested by the client  */
    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("HEAR_TBEAT",
            CharsetUtil.UTF_8));

    private MessageServiceImpl msgService = MessageServiceImpl.getInstance();


    private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    /** Idle times */
    private int idle_count = 1;


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println("error disconnected");
        ctx.channel().close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("establish connection");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        deleteSize(ctx);
        System.out.println("lost connection");
        //Establish a reconnection mechanism
        String token = ctx.channel().attr(Constant.TOKEN).get();
        String userId = ctx.channel().attr(Constant.USERID).get();
        String source = ctx.channel().attr(Constant.SOURCE).get();
        
        if(!(token==null|| token.isEmpty())&&!(userId==null|| userId.isEmpty())){
        	System.out.println("Retry Mechanism Execution:"+token+", userId:"+ userId+", source: "+source);
            msgService.reconnect(token,userId, source);
        }else {
        	System.out.print("Lost connection reconnection data incomplete, unable to reconnect,token:"+token+",userId:"+userId);
        }


    }

    /**
     * Description: If there is no write operation at the specified time, execute the following method
     * @Author: Claire
     * @param ctx
     * @param evt
     * @date: 2019-04-08
     * @return: void
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.WRITER_IDLE)){
                String token = ctx.channel().attr(Constant.TOKEN).get();
                String userId = ctx.channel().attr(Constant.USERID).get();
                String source = ctx.channel().attr(Constant.SOURCE).get();
                MsgBody.Head head = MsgBody.Head.newBuilder().setType(0).setToken(token)
                		.setSendUserId(userId).setMessageId(UUID.randomUUID().toString())
                		.setSource(source).build();
                MsgBody.Msg msg = MsgBody.Msg.newBuilder().setHead(head).build();
                ctx.channel().writeAndFlush(msg);
            }

        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MsgBody.Msg message = (MsgBody.Msg) msg;
        if(message.getHead().getType() == 5006){
            addSize(message, ctx);
        }
        if(message.getHead().getType()!=0){
            System.out.println("The number of connections currently established is:"+Constant.integer.get()+",The message received is:"+Constant.jsonFormat.printToString(message));
        }
        String source = ctx.channel().attr(Constant.SOURCE).get();
        MsgBody.Head.Builder headBuilder = MsgBody.Head.newBuilder().mergeFrom(message.getHead())
        		.setSource(source);
        if(message.getHead().getType()==1){
            headBuilder.setType(5001);
        }else if(message.getHead().getType()==2){
            headBuilder.setType(5002);
        }else if(message.getHead().getType()==3){
            headBuilder.setType(5003);
        }else if(message.getHead().getType()==4){
            headBuilder.setType(5004);
        }else if(message.getHead().getType()==8){
            headBuilder.setType(5010);
        }else if(message.getHead().getType()==0){
            return;
        }
        MsgBody.Msg msgs = MsgBody.Msg.newBuilder().setHead(headBuilder).build();
        ctx.channel().writeAndFlush(msgs);
    }

    public synchronized void addSize(MsgBody.Msg message, ChannelHandlerContext ctx){
        if(!Constant.channelMap.containsKey(message.getHead().getToken())){
            Constant.integer.incrementAndGet();
            Constant.channelMap.put(message.getHead().getToken(), ctx.channel());
            Constant.channelIdList.add(ctx.channel().id().asLongText());
        }

    }


    public synchronized void addMsgSize(MsgBody.Msg message){
        Constant.msgIdList.remove(message.getHead().getMessageId());
    }

    public synchronized void deleteSize(ChannelHandlerContext ctx){
        if(!Constant.channelIdList.contains(ctx.channel().id().asLongText())){
            Constant.integer.decrementAndGet();
            Constant.channelIdList.remove(ctx.channel().id().asLongText());
            for(String key: Constant.channelMap.keySet()){
                if(Constant.channelMap.get(key).equals(ctx.channel())){
                    Constant.channelMap.remove(key);
                }
            }
        }

    }
}
