package com.owlsdonttalk.handlers;

import com.owlsdonttalk.enums.Commands;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUploadService {
    private final DataOutput out;
    private final DataInput in;

    public FileUploadService(DataOutput out, DataInput in) {
        this.out = out;
        this.in = in;
    }

    public void sendFile(String filename) throws IOException {
        Path path = Path.of(filename);

        System.out.println("trying to upload " + filename);
        System.out.println(filename.length());
        System.out.println(filename.getBytes());
        System.out.println(Files.size(path));

        out.writeByte(Commands.UPLOAD.getSignalByte());
        out.writeInt(filename.length());
        out.write(filename.getBytes());
        out.writeLong(Files.size(path));

        byte[] buf = new byte[256];
        try (InputStream inputStream = new FileInputStream(path.toFile())) {
            int n;
            while ((n = inputStream.read(buf)) != -1) {
                System.out.println(n);
                out.write(buf, 0, n);
            }
        }
    }
}
