package com.owlsdonttalk.handlers.in;

import com.owlsdonttalk.enums.Commands;
import com.owlsdonttalk.interfaces.Connectable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class InboundAuthHandler extends ChannelInboundHandlerAdapter implements Connectable {

    private static final Logger log = Logger.getLogger(InboundAuthHandler.class);
    private String activeDirectory = "server/storage/";
    private final String rootServerDirectory = "server/storage/";
    private String user;
    private boolean isAuthenticated = false;
    private Connection conn = null;
    private Statement statement;
    private ResultSet resultSet;
    private long receivedFileLength;
    private int filenameLength;
    private long size = 0L;
    ByteBuf buf, bufOut;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Client connect");
        ctx.writeAndFlush("hello");
        ctx.fireChannelRead("hello");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Client disconnect");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("channelRead: input processing started");

        buf = (ByteBuf)msg;
        byte command = buf.readByte();
        System.out.println(command);
        Long receivedFileLength = 0L;

        if ((char) command == 's') {

            String[] commandsArray = buf.toString(Charset.defaultCharset()).split(" ");
            log.info("String received, working with command: " + commandsArray[0]);

            if (commandsArray[0].equals("auth")) {
                log.info("Trying to auth user " + commandsArray[1] + " with password " + commandsArray[2]);
                if (authUser(commandsArray[1], commandsArray[2])) {
                    log.info("auth success, flushing message to client");
                    bufOut = ByteBufAllocator.DEFAULT.directBuffer(1);
                    bufOut.writeByte(Commands.AUTH.getSignalByte());
                    this.user = commandsArray[1];
                    this.isAuthenticated = true;
                    ctx.writeAndFlush(bufOut);
                } else {
                    bufOut = ByteBufAllocator.DEFAULT.directBuffer(1);
                    bufOut.writeByte(Commands.AUTH.getFailureByte());
                    ctx.writeAndFlush(bufOut);
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
                    bufOut = ByteBufAllocator.DEFAULT.directBuffer(1);
                    bufOut.writeByte(Commands.REGISTER.getSignalByte());
                    ctx.writeAndFlush(bufOut);
                } else {
                    log.error("register fail");
                    ctx.writeAndFlush("register fail");
                }
            }
        }
        if (command == Commands.UPLOAD.getSignalByte()) {

            if(!isAuthenticated){
                bufOut = ByteBufAllocator.DEFAULT.directBuffer(1);
                bufOut.writeByte(Commands.AUTH.getFailureByte());
                ctx.writeAndFlush(bufOut);
                System.out.println("auth to upload");
                return;
            }
            //1.
            System.out.println("File expected, working with file");

            //2. receiving filname length
            filenameLength = -1;
            if(buf.readableBytes() >= 4){
                System.out.println("STATE: Get filename length");
                filenameLength = buf.readInt();
            }
           // int filenameLength = buf.readInt();

            System.out.println("[RECEIVED] filename length: " + filenameLength);

            //3. receiving file name
            byte[] filename = new byte[filenameLength];
            buf.readBytes(filename);
            String serverFileName = activeDirectory.concat(new String(filename, StandardCharsets.UTF_8));
            System.out.println("[RECEIVED] filename: " + serverFileName);

            //4. receiving file size
            //byte[] allBytes = new byte[buf.readableBytes()];
            System.out.println(buf.readableBytes());
            if(buf.readableBytes() >= 8){
                size = buf.readLong();
                System.out.println("[RECEIVED] file size: " + size);
            }


            //5. receiving file
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(serverFileName));
            receivedFileLength = 0L;
            while (buf.readableBytes() > 0) {
                System.out.println(buf.readByte());
                receivedFileLength++;
                if (size == receivedFileLength) {
                    System.out.println(receivedFileLength);
                    System.out.println("File received");
                    out.close();
                    break;
                }
            }
        }
        if (command == Commands.DOWNLOAD.getSignalByte()){
            System.out.println("Client trying to download file");

            filenameLength = buf.readInt();
            byte[] filename = new byte[filenameLength];
            buf.readBytes(filename);
            String serverFileName = activeDirectory.concat(new String(filename, StandardCharsets.UTF_8));
            Path path = Paths.get(serverFileName);
            System.out.println("[DOWNLOAD] filename: " + serverFileName);
            bufOut = ByteBufAllocator.DEFAULT.directBuffer(8);
            System.out.println("sending file size: " + Files.size(path));
            bufOut.writeLong(Files.size(path));
            FileRegion region = new DefaultFileRegion(path.toFile(), 0, Files.size(path));
            ctx.writeAndFlush(region);
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
