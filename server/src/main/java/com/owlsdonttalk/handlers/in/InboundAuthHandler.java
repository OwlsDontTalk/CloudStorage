package com.owlsdonttalk.handlers.in;

import com.owlsdonttalk.interfaces.Connectable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.Charset;
import java.sql.*;
import java.util.Arrays;

public class InboundAuthHandler  extends ChannelInboundHandlerAdapter implements Connectable {

    private Connection conn = null;
    private Statement statement;
    private ResultSet resultSet;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client disconnected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("[HANDLER] AuthHandler");
        ByteBuf buf = (ByteBuf) msg;
        byte command = buf.readByte();

        if((char)command == 's'){
            System.out.println("String recived, working with commands");
            String[] commandsArray = buf.toString(Charset.defaultCharset()).split(" ");

            if(commandsArray[0].equals("auth")){
                System.out.println("Trying to auth user " + commandsArray[1] + " with password " + commandsArray[2]);
                if(authUser(commandsArray[1], commandsArray[2])){
                    System.out.println("auth success, flushing message to clinet");
                    ctx.fireChannelRead("auth success");
                } else {
                    ctx.fireChannelRead("auth fail");
                }
            } else {
                System.out.println("execute command.. " + commandsArray[0]);
            }
        }
        if((char)command == 'f'){
            System.out.println("File expected, working with file");
        }


       // System.out.println(buf.toString(Charset.defaultCharset()));
//        if (buf.readableBytes() < 3) {
//            buf.release();
//            ctx.writeAndFlush("cannot decide what to do");
//        }

        byte[] data = new byte[3];
        buf.readBytes(data);
        buf.release();

//        System.out.println(Arrays.toString(data));
      //  ctx.fireChannelRead(data);
    }

    private boolean authUser(String s, String s1) {
        connect();
        return checkLogin(s, s1);
    }

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
