package com.ghj.mt.handler;



import com.ghj.mt.boot.MtApplicationRunner;
import com.ghj.mt.message.Message;
import com.ghj.mt.message.MessageType;
import com.ghj.mt.message.User;
import com.ghj.mt.ui.LoginController;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.util.List;


public class MtMessageClientHandler extends SimpleChannelInboundHandler<Message> {
    private String TAG = "ChatClient";
    private int pingCount = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext cxt, Object event) {
        if (event instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) event;
            switch (e.state()) {
                case ALL_IDLE:
                    cxt.writeAndFlush(MtApplicationRunner.getInstance().createMessage(MessageType.PING, null));
                    pingCount++;
                    if(pingCount > 3){
                        cxt.close();
                    }
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext cxt, Throwable cause) {
        cause.printStackTrace();
        cxt.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        switch(message.getMsgType()){
            case PING:
                pingCount = 0;
                break;
            case MESSAGE:
                break;
            case LOGIN:
                LoginController.con.setUserList(message);
                break;
        }
        ReferenceCountUtil.release(message);
    }
}
