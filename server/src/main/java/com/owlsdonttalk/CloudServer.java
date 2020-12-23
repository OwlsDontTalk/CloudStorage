package com.owlsdonttalk;

import com.owlsdonttalk.handlers.in.HandshakeHandler;
import com.owlsdonttalk.handlers.out.ResponceToClientHandler;
import com.owlsdonttalk.interfaces.Connectable;
import org.apache.commons.codec.digest.DigestUtils;
import java.sql.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class CloudServer implements Connectable {

    private Connection conn = null;
    private Statement statement;
    private ResultSet resultSet;

    public void run() throws Exception {
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
                                    .addLast(new ResponceToClientHandler())
                                    .addLast(new HandshakeHandler());
                        }
                    });
            ChannelFuture f = b.bind(8189).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    /***
     *
     */
    @Override
    public void connect() {
        try {
            String url = "jdbc:sqlite:cloudstorage.db";
            this.conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /***
     * Check if login is true.
     * Password converts to md5hex(password)
     * @param user
     * @param password
     * @return
     */
    @Override
    public boolean checkLogin(String user, String password) {
        String password_hash = DigestUtils.md5Hex(password);

        String request = "SELECT * from users_tbl WHERE login_fld = \""
                + user
                + "\" and password_hash_fld = \""
                + password_hash
                + "\"";

        if(this.conn == null){
            System.out.println("no active db connection");
            return false;
        }
        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery(request);
            if (resultSet.next()){
                System.out.println("Login found, password correct");
                return true;
            } else {
                System.out.println("login or password incorrect or do not exist");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        return false;
    }
}

