package com.owlsdonttalk;

import java.io.IOException;

public class CLILauncher {
    public static void main(String[] args) throws InterruptedException, IOException {
        CLI cli = new CLI();
        cli.start();
    }
}
