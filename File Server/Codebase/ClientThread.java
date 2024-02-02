package Threading;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientThread implements Runnable {
    private Thread t;
    private Socket socket;
    private File inputFile;


    public ClientThread(String filename) {
        inputFile = new File(filename);
        t = new Thread(this);
        t.start();
    }


    @Override
    public void run() {
        try {
            socket = new Socket("localhost", 5049);
            PrintWriter printWriter = null;
            printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.write("UPLOAD "+inputFile.getName()+"\r\n");
            printWriter.flush();
            ClientService.checkValidity(printWriter,socket,inputFile);
            ClientService.upload(printWriter,socket,inputFile);
        } catch(IOException e) {
            e.printStackTrace();
        }


    }
}
