package client;

import client.protobuf.MsgBody;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Description: TODO
 * Date: 2019-04-11 11:26
 * Author: Claire
 */
public class ClientPoolHandler implements ChannelPoolHandler {
    @Override
    public void channelReleased(Channel channel) throws Exception {
        System.out.println("Channel release");

    }

    @Override
    public void channelAcquired(Channel channel) throws Exception {
        System.out.println("Channel request");

    }

    @Override
    public void channelCreated(Channel channel) throws Exception {
        SocketChannel ch = (SocketChannel)channel;
        ch.config().setKeepAlive(true);
        ch.config().setOption(ChannelOption.SO_BACKLOG, 8192);
        ch.config().setConnectTimeoutMillis(100000);
        ch.pipeline().addLast(
                new IdleStateHandler(60, 60, 75, TimeUnit.SECONDS));

        /*ch.pipeline().addLast("decoder",new FixedLengthFrameDecoder(10))
                .addLast(new StringDecoder());
        ch.pipeline().addLast("encoder", new StringEncoder());*/
        ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
        ch.pipeline().addLast(new ProtobufDecoder(MsgBody.Msg.getDefaultInstance()));
        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
        ch.pipeline().addLast(new ProtobufEncoder());
        ch.pipeline().addLast(new ClientChannelHandler());
        ch.pipeline().addLast(new LoggingHandler());
    }
}
