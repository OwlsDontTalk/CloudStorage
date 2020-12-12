package com.owlsdonttalk;

import java.io.IOException;
import java.util.Scanner;

public class CLI {

    private Scanner scanner = new Scanner(System.in);


    public void start() throws InterruptedException, IOException {
        System.out.println("Hello and welcome to command line interface");
        String command;
        do{
            command = getNextCommand();
            switch (command){
                case ("-help"):
                    typeHelp();
                    break;
                case ("--end"):
                    System.out.println("ending..");
                    break;
                default:
                    System.out.println("waiting for command...");
            }
        }while(!command.equals("--end"));

    }

    private void typeHelp() {
        
    }

    private String getNextCommand() {
        String command = "";
        do{
            System.out.println("Type command. -help for command list");
            command = scanner.nextLine();
        }while(!isCommandValid());
            
        return command;
    }

    private boolean isCommandValid() {
        return true;
    }
}
