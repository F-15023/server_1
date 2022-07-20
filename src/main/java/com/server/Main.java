package com.server;

import com.server.routing.Router;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import com.server.netty.MainHandler;

public class Main {

    private static int PORT = 12345;
    private static Router router;


    public static void main(String[] args) {
        try {
            router = new Router();
            startNetty();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void startNetty() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("coder", new HttpServerCodec());
                        pipeline.addLast("aggregator", new HttpObjectAggregator(10485760));
                        pipeline.addLast("handler", new MainHandler(router));
                    }
                });
            ChannelFuture future = b.bind(PORT).sync();
            System.out.println("Server started at port: " + PORT);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
