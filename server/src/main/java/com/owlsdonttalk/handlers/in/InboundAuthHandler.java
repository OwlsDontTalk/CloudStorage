package com.owlsdonttalk.handlers.in;

import com.owlsdonttalk.interfaces.Connectable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class InboundAuthHandler extends ChannelInboundHandlerAdapter implements Connectable {


    private static final Logger log = Logger.getLogger(InboundAuthHandler.class);
    private String activeDirectory = "server/storage/";
    private final String rootServerDirectory = "server/storage/";
    private Connection conn = null;
    private Statement statement;
    private ResultSet resultSet;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client connected");
        log.info("Client connect");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client disconnected");
        log.info("Client disconnect");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("Server - Inbound Auth handler channel read. Processing started");
        System.out.println("Type of got message: " + msg.getClass());

        ByteBuf buf = (ByteBuf) msg;
        byte command = buf.readByte();
        Long receivedFileLength = 0L;

        if ((char) command == 's') {

            String[] commandsArray = buf.toString(Charset.defaultCharset()).split(" ");
            log.info("String received, working with command: " + commandsArray[0]);

            if (commandsArray[0].equals("auth")) {
                log.info("Trying to auth user " + commandsArray[1] + " with password " + commandsArray[2]);
                if (authUser(commandsArray[1], commandsArray[2])) {
                    log.info("auth success, flushing message to client");
                    ctx.writeAndFlush("Auth success");
                } else {
                    ctx.writeAndFlush("Auth fail");
                }
            }
            if (commandsArray[0].equals("reg")) {
                log.info("Trying to register user " + commandsArray[1] + " with password " + commandsArray[2]);
                if (registerNewUser(commandsArray[1], commandsArray[2])) {
                    log.info("Register success");
                    try{
                        Path newUserDir = Path.of(rootServerDirectory + commandsArray[1]);
                        Files.createDirectory(newUserDir);
                        log.info("user " + commandsArray[1] + " folder created");
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                        log.error(e.getMessage());
                    }
                    ctx.writeAndFlush("register success");
                } else {
                    log.error("register fail");
                    ctx.writeAndFlush("register fail");
                }
            }
        }
        if ((char) command == 'f') {
            //1.
            log.info("File expected, working with file");

            //2. receiving filname length
            int filenameLength = buf.readInt();
            log.info("[RECEIVED] filename length: " + filenameLength);

            //3. receiving file name
            byte[] filename = new byte[filenameLength];
            buf.readBytes(filename);
            String serverFileName = activeDirectory.concat(new String(filename, StandardCharsets.UTF_8));
            log.info("[RECEIVED] filename: " + serverFileName);

            //4. receiving file size
            //byte[] allBytes = new byte[buf.readableBytes()];
            long size = buf.readLong();
            log.info("[RECEIVED] readable bytes: " + buf.readableBytes() + ", file size: " + size);

            //5. receiving file
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(serverFileName));
            while (buf.readableBytes() > 0) {

                out.write(buf.readByte());
                receivedFileLength++;
                if (size == receivedFileLength) {
                    log.info("File received");
                    out.close();
                    break;
                }

            }
//            if (buf.readableBytes() == 0) {
//                buf.release();
//            }
        }
    }

    private boolean authUser(String s, String s1) {
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

        if (this.conn == null) connect();

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

        if (this.conn == null) connect();

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
