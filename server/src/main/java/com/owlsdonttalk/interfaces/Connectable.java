package com.owlsdonttalk.interfaces;

import java.sql.SQLException;

public interface Connectable {

    public void connect();
    public boolean checkLogin(String user, String password_hash);

    //TODO implement bd methods
    //public void registerNewClient(String user, String password);
    //public void getUserFileList()

}
