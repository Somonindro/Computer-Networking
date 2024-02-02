package Threading;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

public class HTTPFileServer {
    private String directory;
    private static final String root = "C:\\Users\\User\\IdeaProjects\\NetworkingOfffline\\src\\root";
    private int count;
    private static final String log = "C:\\Users\\User\\IdeaProjects\\NetworkingOfffline\\src\\log directory";

    public static void main(String[] args) throws IOException {

        File logDirectory = new File(log);
        if(logDirectory.exists()) {
            String[] entries = logDirectory.list();
            for(String entry: entries){
                File toBeDeleted = new File(logDirectory.getPath(), entry);
                toBeDeleted.delete();
            }
            logDirectory.delete();
        }
        else {
            System.out.println("previous log directory doesn't exist");
        }
        logDirectory.mkdir();


        ServerSocket serverSocket = new ServerSocket(5049);
        System.out.println(">>...http file server waiting for connection on port no: "+5049+"...");

        while(true) {
            new ServerThread(serverSocket.accept(), root, log, 5049);
        }
    }
}
