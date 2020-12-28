package com.owlsdonttalk;

import com.owlsdonttalk.handlers.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class NettyCLI {
    private static String serverIP = "";
    private static int serverPort = -1;
    private Scanner scanner = new Scanner(System.in);
    private String activeDirectory = "client/dir/";

    public static void main(String[] args) throws Exception {
        NettyCLI nettyCLI = new NettyCLI();
        nettyCLI.setup();
        nettyCLI.start();
        nettyCLI.getCommand();
    }

    private void getCommand() throws IOException {
        System.out.println("[START] Hello and welcome to CommandLineInterface");
        System.out.println("Enter command or type help to get it");
        String command;

        do{
            command = getNextCommand();
            executeCommand(command);
        }while(!command.equals("end"));
    }

    public void start() throws IOException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch)
                        throws Exception {
                    ch.pipeline().addLast(new StringDecoder())
                            .addLast(new StringEncoder())
                            .addLast(new ClientHandler());
                }
            });

            ChannelFuture f = b.connect(serverIP, serverPort).sync();

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    /***
     * Read properties from config.properties
     * @server.ip
     * @server.port
     * @throws IOException
     */
    private void setup()  {
        System.out.println("[SYSTEM] Future connection config.");
        File file = new File("config.properties");
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(file));
            this.serverIP = properties.getProperty("server.ip");
            this.serverPort = Integer.valueOf(properties.getProperty("server.port"));
            System.out.println("Server setup. IP: " + serverIP + " , PORT: " + serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeCommand(String command) throws IOException {
        switch (command){
            case ("help"):
                printHelp();
                break;
            case ("end"):
                System.out.println("[SYSTEM] Shutting down..");
                break;
            case ("ls"):
                //printDirectoryContent();
                break;
            case ("cd"):
                //changeFolder();
                break;
            case ("rename"):
                //renameFile("filename");
                break;
            case ("connect"):
                //connectToServer();
                break;
            default:
                System.out.println("[SYSTEM] Command not found, try again.");
        }
    }

    private String getNextCommand() {
        String command = "";
        do{
            System.out.println("[INPUT] Type command:");
            command = scanner.nextLine();
        }while(!isCommandValid());
        return command;
    }

    //TODO implement command validation method
    private boolean isCommandValid() {
        return true;
    }

    //TODO write rename method
    private void renameFile(String filename) {
    }

    //TODO re-watch and refactor activeDirectory var change folder method
    private void changeFolder(String directory) {

    }

    private void printDirectoryContent() {
        File folder = new File(activeDirectory);
        File[] listOfFiles = folder.listFiles();
        System.out.println("Directory [" + this.activeDirectory + "] content:");

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                System.out.println("./" + listOfFile.getName());
            } else if (listOfFile.isDirectory()) {
                System.out.println("." + listOfFile.getName());
            }
        }
    }

    private void printHelp() throws IOException {
        System.out.println("[SYSTEM]  Printing help file.");
        Path path = Paths.get("client/help.md");
        List<String> lines = Files.readAllLines(path);

        for (String s: lines) {
            System.out.println(s);
        }
    }

}