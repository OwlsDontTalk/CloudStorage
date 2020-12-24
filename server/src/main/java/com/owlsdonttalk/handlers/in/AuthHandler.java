package com.owlsdonttalk.handlers.in;

import com.owlsdonttalk.interfaces.Connectable;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.*;

public class AuthHandler  extends ChannelInboundHandlerAdapter implements Connectable {

    private Connection conn = null;
    private Statement statement;
    private ResultSet resultSet;

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
