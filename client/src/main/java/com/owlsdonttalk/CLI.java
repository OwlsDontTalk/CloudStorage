package com.owlsdonttalk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CLI {

    private Scanner scanner = new Scanner(System.in);


    public void start() throws InterruptedException, IOException {
        System.out.println("[SYSTEM] Hello and welcome to command line interface. Enter command or type -help to get it.");
        String command;

        do{
            command = getNextCommand();

            switch (command){
                case ("-help"):
                    printHelp();
                    break;
                case ("--end"):
                    System.out.println("[SYSTEM] ending..");
                    break;
                default:
                    System.out.println("[SYSTEM] waiting for command...");
            }

        }while(!command.equals("--end"));

    }

    private void printHelp() throws IOException {
        System.out.println("[SYSTEM]  Printing help file.");
        Path path = Paths.get("help.md");
        List<String> lines = Files.readAllLines(path);

        for (String s: lines) {
            System.out.println(s);
        }
    }

    private String getNextCommand() {
        String command = "";
        do{
            System.out.println("[INPUT] Type command");
            command = scanner.nextLine();
        }while(!isCommandValid());
            
        return command;
    }

    private boolean isCommandValid() {
        return true;
    }
}
