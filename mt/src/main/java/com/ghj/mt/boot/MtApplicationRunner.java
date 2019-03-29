package com.ghj.mt.boot;

import com.ghj.mt.handler.MtMessageServerHandler;
import com.ghj.mt.message.MessageManager;
import com.ghj.mt.message.UserManager;
import com.ghj.mt.seriable.KryoDecoder;
import com.ghj.mt.seriable.KryoEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class MtApplicationRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args)  {
        log.info("MtApplicationRunner Running");
       try {
        new Thread(()->MessageManager.getInstance().start()).start();
            startConnect(PORT);
        } catch (InterruptedException e) {
           MessageManager.getInstance().stop();
           UserManager.getInstance().clearAll();
           this.stop();
            e.printStackTrace();
        }
    }

    public static int PORT = 10000;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public void startConnect(int port) throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    ChannelPipeline pipe = socketChannel.pipeline();
                    pipe.addLast("decoder", new KryoDecoder());
                    pipe.addLast("encoder", new KryoEncoder());
                    pipe.addLast(new MtMessageServerHandler());
                }
            });
            ChannelFuture f = bootstrap.bind(port).sync();
            if (f.isSuccess()) {
                log.info("server start success... port: " + port + ", main work thread: " + Thread.currentThread().getId());
            }

            f.channel().closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public synchronized void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
