package com.owlsdonttalk.handlers;

import com.owlsdonttalk.enums.Commands;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDownloadService {
    private final DataOutput out;
    private final DataInput in;
    private String clientDirectory;

    public FileDownloadService(DataOutput out, DataInput in,String clientDirectory) {
        this.out = out;
        this.in = in;
        this.clientDirectory = clientDirectory;
    }

    public void downloadFile(String filename) throws IOException {

        System.out.println("Trying to download " + filename + " from server");

        out.writeByte(Commands.DOWNLOAD.getSignalByte());
        out.writeInt(filename.length());
        out.write(filename.getBytes());

        long fileSize = in.readLong();
        System.out.println("filesize: " + fileSize);

        Path pathToFileToBeDownloaded = Paths.get(clientDirectory+filename);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(pathToFileToBeDownloaded.toFile()))) {
            for (long i = 0; i < fileSize; i++) {bos.write(in.readByte());}
        }
    }
}
