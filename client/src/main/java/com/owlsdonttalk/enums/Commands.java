package com.owlsdonttalk.enums;

public enum Commands {

    UPLOAD("upload", "upload file", (byte)117, (byte)-1),
    REGISTER("register", "register login password", (byte)72, (byte)-1),
    AUTH("auth", "authenticate", (byte)97, (byte)-1),
    DOWNLOAD("download", "download",  (byte)64, (byte)-1);

    private final String name;
    private final String description;
    private final byte signalByte;
    private final byte failureByte;

    Commands(String name, String description, byte signalByte, byte failureByte) {
        this.name = name;
        this.description = description;
        this.signalByte = signalByte;
        this.failureByte = failureByte;
    }

    public byte getSignalByte() {return signalByte;}

    public byte getFailureByte() {
        return failureByte;
    }

    public String getDescription() {
        return description;
    }

    public static Commands getCommandByByte(byte signalByte) throws Exception {
        for (Commands c: Commands.values()) {
            if(c.getSignalByte() == signalByte) return c;
        }
        throw new Exception("Command not found");
    }
}

