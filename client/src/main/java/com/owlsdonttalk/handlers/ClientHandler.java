package com.owlsdonttalk.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import org.apache.commons.io.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
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
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println(123);
        ByteBuf buf = (ByteBuf) msg;
        String s = buf.toString(Charset.defaultCharset());
        System.out.println(s);
        ctx.close();
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

    private String[] getNextCommand() {
        String[] command = scanner.nextLine().split(" ");
        return command;
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
            case ("register"):
                if (command.length >= 3) {
                    registerUser(command[1], command[2], ctx);
                } else {
                    System.out.println("command structure: register [login] [password]");
                }
                break;
            case ("remove"):
                removeLocalFile(command[1]);
                break;
            case ("send"):
                sendFileToServer(command[1], ctx);
                break;
            default:
                System.out.println("[SYSTEM] Command not found, try again.");
        }
    }

    private void registerUser(String login, String password, ChannelHandlerContext ctx) {
        ctx.writeAndFlush("sreg " + login + " " + password);
    }

    private void sendFileToServer(String filename, ChannelHandlerContext ctx) throws IOException {
        Path path = Paths.get(activeDirectory + filename);
        BufferedOutputStream out;

        if (!Files.exists(path)) {
            System.out.println("no such local file " + filename);
            return;
        }
        FileRegion region = new DefaultFileRegion(path.toFile(), 0, Files.size(path));
        System.out.println(Files.size(path));

        ByteBuf buf = null;
        byte[] allBytes = Files.readAllBytes(path);

        //1. send F to tell server it shoud expect file
        buf = ByteBufAllocator.DEFAULT.directBuffer(1);
        buf.writeByte(102);
        ctx.writeAndFlush(buf);

        //2. send filenameLength to server
        byte[] filenameBytes = path.getFileName().toString().getBytes(StandardCharsets.UTF_8);
        buf = ByteBufAllocator.DEFAULT.directBuffer(4);
        buf.writeInt(filenameBytes.length);
        ctx.writeAndFlush(buf);

        //3. send filename to server
        buf = ByteBufAllocator.DEFAULT.directBuffer(filenameBytes.length);
        buf.writeBytes(filenameBytes);
        ctx.writeAndFlush(buf);

        //4. send fileSize to server
        buf = ByteBufAllocator.DEFAULT.directBuffer(4);
        buf.writeLong(Files.size(path));
        ctx.writeAndFlush(buf);

        //5. send file to server
        ctx.writeAndFlush(region);
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

    private void changeFolder(String directory) throws NoSuchFileException, NotDirectoryException {
        Path path0 = Path.of(activeDirectory + directory);

        //TODO rewrite changeFolderMethod
//        if (directory.equals("/")) {
//            while (path0.getParent() != null) {
//                path0 = path0.getParent();
//            }
//            System.out.println(path0.toString());
//        } else if(directory.equals("..")) {
//            if (path0.getParent() == null) {
//                System.out.println(path0.toString());
//            }
//            path0 = path0.getParent();
//        } else if (directory.equals(".")) {
//            System.out.println(path0.toString());
//        } else {
//            //path0 = Path.of(rootPath, destinationDir);
//        }
//        if (Files.exists(path0)) {
//            if (Files.isDirectory(path0)) {
//                activeDirectory = activeDirectory + directory;
//                System.out.println(path0.toString());
//            } else {
//                throw new NotDirectoryException(directory);
//            }
//        } else {
//            throw new NoSuchFileException(directory);
//        }


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
}