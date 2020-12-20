package com.owlsdonttalk;

import org.apache.commons.codec.digest.DigestUtils;

public class ServerLauncher {


    public static void main(String[] args) {
        Server server = new Server();
        server.connect();
        server.checkLogin("root", "12345");



    }
}
