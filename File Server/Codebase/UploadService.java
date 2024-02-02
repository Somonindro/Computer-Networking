package Threading;

import java.io.*;
import java.net.Socket;

public class UploadService {
    public static void uploadFile(String httpRequest, DataInputStream bufferedReader, Socket socket, String root)
    {
        try {
            String isValid = bufferedReader.readLine();

            if(isValid.equals("invalid")) {
                System.out.println(">> given file name is invalid");

                bufferedReader.close();
                socket.close();
                return ;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        byte[] buffer = new byte[1024];
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(root+"\\"+httpRequest.substring(7)));
            InputStream in = socket.getInputStream();

            while(in.read(buffer) > 0){
                fileOutputStream.write(buffer);
            }

            in.close();
            fileOutputStream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        try {
            bufferedReader.close();
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
