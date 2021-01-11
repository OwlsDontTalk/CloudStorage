package com.owlsdonttalk.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private Scanner scanner = new Scanner(System.in);
    private String activeDirectory = "client/dir/";
    private static String serverIP = "";
    private static int serverPort = -1;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException {
        System.out.println("[START] Hello and welcome to CommandLineInterface");
        System.out.println("Enter command or type help to get it");
        String[] command;

        do {
            command = getNextCommand();
            executeCommand(command, ctx);
        } while (!command[0].equals("end"));
//       ChannelFuture future = ctx.writeAndFlush("hey");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        String s = buf.toString(Charset.defaultCharset());
        System.out.println(s);
        ctx.close();
    }

    private void executeCommand(String[] command, ChannelHandlerContext ctx) throws IOException {
        switch (command[0]) {
            case ("help"):
                printHelp();
                break;
            case ("end"):
                System.out.println("[SYSTEM] Shutting down..");
                break;
            case ("ls"):
                printDirectoryContent();
                break;
            case ("cd"):
                changeFolder(command[1]);
                break;
            case ("rename"):
                if (command.length >= 3) {
                    renameLocalFile(command[1], command[2]);
                } else {
                    System.out.println("command structure: rename [file] [new name]");
                }
                break;
            case ("connect"):
                if (command.length >= 3) {
                    connectToServer(command[1], command[2], ctx);
                } else {
                    System.out.println("command structure: connect [login] [password]");
                }
                break;
            case ("remove"):
                removeLocalFile(command[1]);
                break;
            default:
                System.out.println("[SYSTEM] Command not found, try again.");
        }
    }

    private void connectToServer(String login, String password, ChannelHandlerContext ctx) {
        ctx.writeAndFlush("sauth " + login + " " + password);
    }

    private void renameLocalFile(String file, String newName) throws IOException {
        file = activeDirectory + file;
        newName = activeDirectory + newName;

        if (!Files.exists(Path.of(file))) {
            System.out.println("no such file " + file);
            return;
        }

        if (Files.exists(Path.of(newName))) {
            System.out.println("file " + newName + " already exist");
            return;
        }

        System.out.println("renaming " + file + " into " + newName);
        FileUtils.moveFile(FileUtils.getFile(file),
                FileUtils.getFile(newName));
    }

    private void removeLocalFile(String s) {
        Path path0 = Path.of(activeDirectory + "/" + s);
        if (Files.exists(path0)) {
            try {
                System.out.println(path0);
                Files.delete(path0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File " + path0 + " not found");
        }
    }

    private String[] getNextCommand() {
        String[] command = scanner.nextLine().split(" ");
        return command;
    }

    private void changeFolder(String directory) {
        Path path0 = Path.of(activeDirectory + directory);

        if (Files.exists(path0)) {
            System.out.println("new directory: " + directory);
            activeDirectory = activeDirectory + directory;
        } else {
            System.out.println("no such directory " + directory);
        }


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

    private void printHelp() {
        System.out.println("[SYSTEM] Printing help file.");
        List<String> lines = null;

        try {
            Path path = Paths.get("client/help.md");

            lines = Files.readAllLines(path);
        } catch (IOException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

        if (lines != null) {
            for (String s : lines) {
                System.out.println(s);
            }
        }
    }
}