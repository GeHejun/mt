package com.ghj.mt.handler;

import com.ghj.mt.dto.UserDTO;
import com.ghj.mt.enums.MessageType;
import com.ghj.mt.message.Message;
import com.ghj.mt.message.MessageManager;
import com.ghj.mt.message.UserManager;
import com.ghj.mt.service.IFriendService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class MtMessageServerHandler extends ChannelInboundHandlerAdapter {

    @Resource
    IFriendService friendService;


    @Override
    public void channelInactive(ChannelHandlerContext cxt)  {
        UserManager.getInstance().removeChannel((SocketChannel)cxt.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext cxt, Object obj) {
        Message message = (Message) obj;
        switch(message.getMsgType()){
            case PING:
//                cxt.channel().writeAndFlush(Message.builder().receiveIDs());
                break;
            case LOGIN:
                UserManager.getInstance().addUser(message.getGroupID(), message.getClientID(), (SocketChannel)cxt.channel());
                UserDTO userDTO = friendService.listFriends(message.getClientID());
                List receiveIDs = new ArrayList();
                receiveIDs.add(message.getClientID());
                Message messageResponse = Message.builder().receiveIDs(receiveIDs).msgType(MessageType.LOGIN).userDTO(userDTO).build();
                MessageManager.getInstance().putMessage(messageResponse);
                break;
            case MESSAGE:
                MessageManager.getInstance().putMessage(message);
                break;
            case LOGOUT:
                UserManager.getInstance().removeUser(message.getGroupID(), message.getClientID());
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext cxt, Throwable cause) {
        UserManager.getInstance().removeChannel((SocketChannel)cxt.channel());
        cause.printStackTrace();
        cxt.close();
    }

}
