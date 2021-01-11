package com.owlsdonttalk.archive;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class CLI {

    //TODO учу сервак смотреть первый байт и если это S то работать со стрингой - F - как с файлом
    private String serverIP;
    private int serverPort;
    private Scanner scanner = new Scanner(System.in);
    private String activeDirectory = "client/dir/";
    DataInputStream in;
    DataOutputStream out;
    Socket socket;

    public static void main(String[] args) throws IOException {

        try {
            Socket socket = new Socket("localhost", 8189);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Scanner in = new Scanner(socket.getInputStream());
            out.write(new byte[]{115, 21, 31});
            System.out.println(in.nextLine());
            //System.out.println("A: " + x);
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        CLI cli = new CLI();
//        cli.start();
    }

    public void start() throws IOException {
        setup();
        System.out.println("[START] Hello and welcome to CommandLineInterface");
        System.out.println("Enter command or type help to get it");

        String command;

        do{
            command = getNextCommand();
            executeCommand(command);
        }while(!command.equals("end"));

    }

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
                printDirectoryContent();
                break;
            case ("cd"):
                //changeFolder();
                break;
            case ("rename"):
                //renameFile("filename");
                break;
            case ("connect"):
                connectToServer();
                break;
            default:
                System.out.println("[SYSTEM] Command not found, try again.");
        }
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 8189);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Scanner in = new Scanner(socket.getInputStream());
            out.write(new byte[]{115, 21, 31});
            out.close();
            String x = in.nextLine();
            System.out.println("A: " + x);
            //in.close();
            //out.close();
            //socket.close();
        } catch (IOException e) {
            System.out.println("[ERROR] " + e.getClass() + ", cause: " + e.getMessage());
        }

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
}
