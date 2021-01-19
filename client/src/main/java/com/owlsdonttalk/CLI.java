package com.owlsdonttalk;

import com.owlsdonttalk.archive.ClientHandler;
import com.owlsdonttalk.handlers.CommandProcessing;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class CLI {

    private static String serverIP = "";
    private static int serverPort = -1;
    private static final org.apache.log4j.Logger log = Logger.getLogger(ClientHandler.class);
    DataOutputStream out;
    DataInputStream in;


    public static void main(String[] args) throws Exception {
        CLI cli = new CLI();
        cli.start();

    }

    CLI(){
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

    private void start(){
        try (Socket socket = new Socket(serverIP, serverPort)){
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
            System.out.println("Connection to server established");
            log.info("client " + this + " connected to server");
            CommandProcessing cmd = new CommandProcessing(out, in);
        } catch (IOException e) {
            e.printStackTrace();
        };
//        } finally {
//            scanner.close();
//        }
    }

}
