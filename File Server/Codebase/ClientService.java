package Threading;

import java.io.*;
import java.net.Socket;

public class ClientService {
    public static void checkValidity(PrintWriter printWriter, Socket socket , File inputFile)
    {
        try {
            if (inputFile.exists() && (Process.getExtension(inputFile).equals("txt") || Process.getExtension(inputFile).equals("png") || Process.getExtension(inputFile).equals("jpg") || Process.getExtension(inputFile).equals("mp4")))
            {
                printWriter.write("valid\r\n");
                printWriter.flush();
            }
            else {
                //for invalid
                printWriter.write("invalid\r\n");
                printWriter.flush();
                System.out.println(">>...given file name is invalid...");
                printWriter.close();
                socket.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void upload(PrintWriter printWriter,Socket socket, File inputFile)
    {
        int count;
        byte[] buffer = new byte[1024];

        try {
            OutputStream out = socket.getOutputStream();
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputFile));

            while((count=in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
                out.flush();
            }
            System.out.println(">> upload Finished");
            in.close();
            out.close();
            printWriter.close();
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
