package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class IOClient {
    final private  String IP_ADDRESS = "localhost";
    final private int PORT = 8080;
    DataInputStream in;
    DataOutputStream out;
    Socket socket;

    IOClient() throws IOException {
        socket = new Socket(IP_ADDRESS, PORT);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void openConnection(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while(true){
                        if (socket.getInetAddress().isReachable(100)){
                            Thread.sleep(1000);
                            out.writeUTF("hi?");
                        } else {
                            System.out.println("server is dead.");
                            break;
                        }

                    }
                } catch (Exception e){
                    System.out.println("ERR: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                    System.out.println("Stop the client");
                }
            }
        }).start();
    }

}
