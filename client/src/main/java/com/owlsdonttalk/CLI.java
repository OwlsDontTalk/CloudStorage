package com.owlsdonttalk;

import com.owlsdonttalk.handlers.CommandProcessing;
import com.owlsdonttalk.handlers.FileDownloadService;
import com.owlsdonttalk.handlers.FileUploadService;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

public class CLI {


    private String username;
    private static String serverIP = "";
    private static int serverPort = -1;
    private String activeDirectory = "client/dir/";
    private static final org.apache.log4j.Logger log = Logger.getLogger(CommandProcessing.class);
    DataOutputStream out;
    DataInputStream in;
    FileDownloadService fds;
    FileUploadService fus;

    public static void main(String[] args) throws Exception {
        CLI cli = new CLI();
        cli.start();

    }

    CLI() {
        log.info("command line interface started");
        File file = new File("config.properties");
        Properties properties = new Properties();
        try {
            log.info("reading property file");
            properties.load(new FileReader(file));
            this.serverIP = properties.getProperty("server.ip");
            this.serverPort = Integer.valueOf(properties.getProperty("server.port"));
            System.out.println("Client setup. Server IP: " + serverIP + " , Server PORT: " + serverPort);
            log.info("Server setup. IP: " + serverIP + " , PORT: " + serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        try (Socket socket = new Socket(serverIP, serverPort)) {
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
            fds = new FileDownloadService(out, in, activeDirectory);
            fus = new FileUploadService(out, in);
            System.out.println("Connection to server established");
            log.info("client " + this + " connected to server");
            CommandProcessing cmd = new CommandProcessing(out, in, fus, fds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
