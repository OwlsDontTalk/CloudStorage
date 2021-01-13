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
import java.sql.*;

public class InboundAuthHandler  extends ChannelInboundHandlerAdapter implements Connectable {

    private String activeDirectory = "server/storage/root/";
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

        System.out.println("[RECIVED] commands: " + (char)command);

        if((char)command == 's'){
            System.out.println("String recived, working with commands");
            String[] commandsArray = buf.toString(Charset.defaultCharset()).split(" ");

            if(commandsArray[0].equals("auth")){
                System.out.println("Trying to auth user " + commandsArray[1] + " with password " + commandsArray[2]);
                if(authUser(commandsArray[1], commandsArray[2])){
                    System.out.println("auth success, flushing message to clienet");
                    ctx.fireChannelRead("auth success");
                } else {
                    ctx.fireChannelRead("auth fail");
                }
            } else {
                System.out.println("execute command.. " + commandsArray[0]);
            }
        }
        if((char)command == 'f'){
            //1.
            System.out.println("File expected, working with file");

            //2. receiving filname length
            int filnameLength = buf.readInt();
            System.out.println("[RECEIVED] filename length: " + filnameLength);

            //3. receiving file name
            byte[] filename = new byte[filnameLength];
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

//            if (buf.isReadable()) {
//                buf.readBytes(fos, buf.readableBytes());
//                fos.flush();
//                System.out.println("123");
//            } else {
//                System.out.println("I want to close fileoutputstream!");
//                buf.release();
//                fos.flush();
//                fos.close();
//            }



            //FileUtils.writeByteArrayToFile(new File(activeDirectory+serverFileName), allBytes);
           // buf.release();
        }
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
