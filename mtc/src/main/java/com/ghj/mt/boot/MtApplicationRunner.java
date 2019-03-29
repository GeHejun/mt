package com.ghj.mt.boot;



import com.ghj.mt.handler.MtMessageClientHandler;
import com.ghj.mt.message.Message;
import com.ghj.mt.message.MessageType;
import com.ghj.mt.seriable.KryoDecoder;
import com.ghj.mt.seriable.KryoEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;

@Data
public class MtApplicationRunner {
    private String TAG = "ChatClient";
    private static MtApplicationRunner client;
    private EventLoopGroup eventLoopGroup;
    private SocketChannel socketChannel;
    private Integer clientID;
    private Integer groupID;
    private final int HEART_PING_TIME = 180;


    private MtApplicationRunner(){
    }

    public static MtApplicationRunner getInstance(){
        if(client  == null){
            synchronized (MtApplicationRunner.class) {
                if(client  == null){
                    client = new MtApplicationRunner();
                }
            }
        }
        return client;
    }


    public void connect(String serverIP, int port) {
        eventLoopGroup = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap=new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
            bootstrap.group(eventLoopGroup);
            bootstrap.remoteAddress(serverIP, port);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new IdleStateHandler(0, 0, HEART_PING_TIME));
                    socketChannel.pipeline().addLast("decoder", new KryoDecoder());
                    socketChannel.pipeline().addLast("encoder", new KryoEncoder());
                    socketChannel.pipeline().addLast(new MtMessageClientHandler());
                }
            });
            ChannelFuture future =bootstrap.connect(serverIP, port).sync();
            if (future.isSuccess()) {
                socketChannel = (SocketChannel) future.channel();
            }
        }catch(InterruptedException e) {
            e.printStackTrace();
            eventLoopGroup.shutdownGracefully();
        }
    }


    public synchronized void sendMessage(Message message){
        socketChannel.writeAndFlush(message);
    }


    public void sync() throws InterruptedException{
        socketChannel.closeFuture().sync();
    }


    public synchronized void stop(){
        socketChannel.writeAndFlush(createMessage(MessageType.LOGOUT, null));
        eventLoopGroup.shutdownGracefully();
    }



    public Message createMessage(MessageType type, String body){
        Message builder = new Message();
        builder.setClientID(clientID);
        builder.setMsgType(type);
        builder.setGroupID(groupID);
        if(body != null){
            builder.setBody(body);
        }
        return builder;
    }
}
