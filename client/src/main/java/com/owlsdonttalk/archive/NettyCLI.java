package com.owlsdonttalk.archive;

import com.owlsdonttalk.handlers.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.*;
import java.util.Properties;

public class NettyCLI {
    private static String serverIP = "";
    private static int serverPort = -1;


    public static void main(String[] args) throws Exception {
        NettyCLI nettyCLI = new NettyCLI();
        nettyCLI.setup();
        nettyCLI.start();
    }

    public void start() throws IOException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch)
                        throws Exception {
                    ch.pipeline().addLast(new ObjectEncoder())
                            .addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                            .addLast(new ClientHandler());
                }
            });


            ChannelFuture f = b.connect(serverIP, serverPort).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    /***
     * Read properties from config.properties
     * @server.ip
     * @server.port
     * @throws IOException
     */
    private void setup() {
        System.out.println("[SYSTEM] Future connection config.");
        File file = new File("config.properties");
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(file));
            this.serverIP = properties.getProperty("server.ip");
            this.serverPort = Integer.valueOf(properties.getProperty("server.port"));
            System.out.println("Server setup. IP: " + serverIP + " , PORT: " + serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}