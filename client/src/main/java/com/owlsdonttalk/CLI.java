package com.owlsdonttalk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class CLI {

    final private String IP_ADPRESS = "localhost";
    final private int PORT = 8189;
    private Scanner scanner = new Scanner(System.in);
    private String activeDirectory = "client/dir/";
    DataInputStream in;
    DataOutputStream out;
    Socket socket;


    public void start() throws IOException {
        System.out.println("[START] Hello and welcome to CommandLineInterface");
        System.out.println("Enter command or type help to get it");

        String command;

        do{
            command = getNextCommand();
            executeCommand(command);
        }while(!command.equals("end"));

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
            socket = new Socket(IP_ADPRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            out.write(new byte[]{115, 21, 31});

            System.out.println(in.read());
            in.close();
            out.close();
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
