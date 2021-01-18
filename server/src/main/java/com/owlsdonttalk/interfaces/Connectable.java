package com.owlsdonttalk.interfaces;

public interface Connectable {

    public void connect();
    public boolean checkLogin(String user, String password_hash);
    public boolean registerNewUser(String user, String password);
    //TODO implement filelist method



}
