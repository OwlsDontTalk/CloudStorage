package com.owlsdonttalk;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CLITest {

    @Test
    void checkHelpGuideExistance(){
        Path path = Paths.get("/client/help.md");
        assertTrue(Files.exists(path));
    }

    @Test
    void checkConfigFileExist(){
        Path path = Paths.get("../config.properties");
        assertTrue(Files.exists(path));
    }

    void checkServerConnection(){
    }

    @Test
    void sendCommandToServer(){
    }

    @Test
    void recieveServerResponse(){
    }


    @Test
    void getLocalClientFiles(){
    }

    @Test
    void renameLocalFile(){
    }

    @Test
    void moveLocalFile(){
    }



}
