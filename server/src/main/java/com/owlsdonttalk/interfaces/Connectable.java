package com.owlsdonttalk.interfaces;

import java.sql.SQLException;

public interface Connectable {

    public void connect();
    public boolean checkLogin(String user, String password_hash);
}
