package com.owlsdonttalk.handlers;

import com.owlsdonttalk.archive.ClientHandler;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class CommandProcessing {
    private static final org.apache.log4j.Logger log = Logger.getLogger(ClientHandler.class);
    private String activeDirectory = "client/dir/";
    DataOutputStream out;
    DataInputStream in;
    private final Scanner scanner = new Scanner(System.in);

    public CommandProcessing(DataOutputStream out, DataInputStream in) throws IOException {
        this.out = out;
        this.in = in;
        startCommandProcessing();
    }


    private void startCommandProcessing() throws IOException {
        System.out.println("Command processing started. Print 'help' to get help ;) ");
        String[] command;

        do {
            command = getNextCommand();
            executeCommand(command);
        } while (!command[0].equals("end"));
    }

    private String[] getNextCommand() {
        String[] command = scanner.nextLine().split(" ");
        return command;
    }


    private void executeCommand(String[] command) throws IOException {
        switch (command[0]) {
            case ("help"):
                printHelp();
                break;
            case ("end"):
                System.out.println("[SYSTEM] Shutting down..");
                log.info("Stopping input processing");
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
                    connectToServer(command[1], command[2]);
                } else {
                    System.out.println("command structure: connect [login] [password]");
                }
                break;
            case ("register"):
                if (command.length >= 3) {
                    registerUser(command[1], command[2]);
                } else {
                    System.out.println("command structure: register [login] [password]");
                }
                break;
            case ("remove"):
                removeLocalFile(command[1]);
                break;
            case ("send"):
                sendFileToServer(command[1]);
                break;
            default:
                System.out.println("[SYSTEM] Command not found, try again.");
        }
    }


    private void registerUser(String login, String password) throws IOException {
        String s = "sreg " + login + " " + password;
        byte[] arr = s.getBytes();
        this.out.write(arr);
    }

    private void sendFileToServer(String filename) throws IOException {

    }

    private void connectToServer(String login, String password) throws IOException {
        String connectionString = "sauth " + login + " " + password;
        byte[] arr = connectionString.getBytes();
        this.out.write(arr);
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
        log.info("User requested help file");
        System.out.println("[SYSTEM] Printing help file.");
        List<String> lines = null;

        try {
            Path path = Paths.get("client/help.md");

            lines = Files.readAllLines(path);
        } catch (IOException e) {
            log.error("Client - Printing help.md fail");
            System.out.println("[ERROR] " + e.getMessage());
        }

        if (lines != null) {
            for (String s : lines) {
                System.out.println(s);
            }
        }
    }
}
