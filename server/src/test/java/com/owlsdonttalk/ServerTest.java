package com.owlsdonttalk;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerTest {

    @Test
    void checkConfigFileExist(){
        Path path = Paths.get("../config.properties");
        assertTrue(Files.exists(path));
    }

    @Test
    void serverConnection(){}

    @Test
    void registerClient(){}

    @Test
    void authClient(){}

    @Test
    void getClientFileList(){}

    @Test
    void sendFileToClient(){}
}
