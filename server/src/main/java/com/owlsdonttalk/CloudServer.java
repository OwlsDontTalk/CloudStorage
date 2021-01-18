package com.owlsdonttalk;

import com.owlsdonttalk.handlers.in.InboundAuthHandler;

import java.io.*;
import java.util.Properties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class CloudServer {

    String serverIP = "";
    int serverPort = -1;

    public static void main(String[] args) throws Exception {
        CloudServer server = new CloudServer();
        server.run();
    }

    public void run() throws Exception {
        setup();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<io.netty.channel.socket.SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                    .addLast(new ObjectEncoder())
                                    .addLast(new InboundAuthHandler());
                        }
                    });
            ChannelFuture f = b.bind(serverPort).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /***
     * Read properties from config.properties
     * @server.ip
     * @server.port
     * @throws IOException
     */
    private void setup() throws IOException {
        File file = new File("config.properties");
        Properties properties = new Properties();
        properties.load(new FileReader(file));
        this.serverIP = properties.getProperty("server.ip");
        this.serverPort = Integer.valueOf(properties.getProperty("server.port"));
        System.out.println("Server setup. IP: " +  serverIP + " , PORT: " + serverPort);
    }
}

