package com.owlsdonttalk.handlers.in;

import com.owlsdonttalk.interfaces.Connectable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class InboundAuthHandler extends ChannelInboundHandlerAdapter implements Connectable {

    private String activeDirectory = "server/storage/";
    private String rootServerDirectory = "server/storage/";
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
        Long receivedFileLength = 0L;

        System.out.println("[RECEIVED] commands: " + (char) command);

        if ((char) command == 's') {
            System.out.println("String received, working with commands");
            String[] commandsArray = buf.toString(Charset.defaultCharset()).split(" ");

            if (commandsArray[0].equals("auth")) {
                System.out.println("Trying to auth user " + commandsArray[1] + " with password " + commandsArray[2]);
                if (authUser(commandsArray[1], commandsArray[2])) {
                    System.out.println("auth success, flushing message to client");
                    ctx.fireChannelRead("auth success");
                } else {
                    ctx.fireChannelRead("auth fail");
                }
            }
            if (commandsArray[0].equals("reg")) {
                System.out.println("Trying to register user " + commandsArray[1] + " with password " + commandsArray[2]);
                if (registerNewUser(commandsArray[1], commandsArray[2])) {
                    System.out.println("register success");
                    try{
                        Path newUserDir = Path.of(rootServerDirectory + commandsArray[1]);
                        Files.createDirectory(newUserDir);
                        System.out.println("user " + commandsArray[1] + " folder created");
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    ctx.fireChannelRead("register success");
                } else {
                    System.out.println("register fail");
                    ctx.writeAndFlush("register fail");
                }
            }
        }
        if ((char) command == 'f') {
            //1.
            System.out.println("File expected, working with file");

            //2. receiving filname length
            int filenameLength = buf.readInt();
            System.out.println("[RECEIVED] filename length: " + filenameLength);

            //3. receiving file name
            byte[] filename = new byte[filenameLength];
            buf.readBytes(filename);
            String serverFileName = activeDirectory.concat(new String(filename, StandardCharsets.UTF_8));
            System.out.println("[RECEIVED] filename: " + serverFileName);

            //4. receiving file size
            //byte[] allBytes = new byte[buf.readableBytes()];
            long size = buf.readLong();
            System.out.println("[RECEIVED] readable bytes: " + buf.readableBytes() + ", file size: " + size);

            //5. receiving file
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(serverFileName));
            while (buf.readableBytes() > 0) {

                out.write(buf.readByte());
                receivedFileLength++;
                if (size == receivedFileLength) {
                    System.out.println("File received");
                    out.close();
                    break;
                }

            }
            if (buf.readableBytes() == 0) {
                buf.release();
            }
        }
    }

    private boolean authUser(String s, String s1) {
        connect();
        return checkLogin(s, s1);
    }

    public boolean registerNewUser(String login, String password) {
        String password_hash = DigestUtils.md5Hex(password);
        String request = "INSERT INTO users_tbl (login_fld, password_hash_fld) VALUES ('"
                + login
                + "', '"
                + password_hash
                + "')";
        System.out.println(request);

        if (this.conn == null) {
            System.out.println("no active db connection");
            connect();
        }
        try {
            statement = conn.createStatement();
            statement.execute(request);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
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

        if (this.conn == null) {
            System.out.println("no active db connection");
            return false;
        }
        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery(request);
            if (resultSet.next()) {
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
