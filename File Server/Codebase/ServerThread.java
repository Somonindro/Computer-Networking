package Threading;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;

public class ServerThread implements Runnable {
    private Socket socket;
    private String root;
    private String log;
    private int server_port;
    private static int request_no = 0;
    private Thread thread;

    public ServerThread(Socket socket, String root, String log, int server_port) {
        this.socket = socket;
        this.root = root;
        this.log = log;
        this.server_port = server_port;
        thread = new Thread(this);
        thread.start();
    }



    @Override
    public void run() {

        DataInputStream bufferedReader = null;
        String httpRequest = null;

        try {
            bufferedReader = new DataInputStream(socket.getInputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }

        /* receiving request message from client */
        try {
            assert bufferedReader != null;
            httpRequest = bufferedReader.readLine();
        } catch(IOException e) {
            e.printStackTrace();
        }

        /* starting main process (checking whether GET or UPLOAD request) */
        if(httpRequest==null || httpRequest.startsWith("GET")) {

            if(httpRequest == null) {
                try {
                    bufferedReader.close();
                    socket.close();
                } catch(IOException e) {
                    e.printStackTrace();
                } finally {
                    return ;
                }
            }

            PrintWriter printWriter=null;
            PrintWriter fileWriter=null;

            try {
                printWriter = new PrintWriter(socket.getOutputStream());
                fileWriter = new PrintWriter(log+"\\http_log_"+(++request_no)+".log");
            } catch(IOException e) {
                e.printStackTrace();
            }


            System.out.println(">> http request line from client: "+httpRequest);
            assert fileWriter != null;
            fileWriter.println("HTTP REQUEST LINE FROM CLIENT:\n"+httpRequest+"\n");

            String path="";
            String[] array = httpRequest.split("/");
            File fileContent;
            File[] listOfContent = new File[0];

            for(int i=1; i<array.length-1; i++) {
                if(i == (array.length-2)) {
                    path += array[i].replace(" HTTP","");
                } else {
                    path += array[i]+"\\";
                }
            }

            if(path.equals("")) {
                fileContent = new File(root);
            } else {
                path = path.replace("%20", " ")+"\\";
                fileContent = new File(root+"\\"+path);
            }


            StringBuilder stringBuilder = new StringBuilder();
            Process.showList( fileContent, listOfContent, stringBuilder, path, server_port);

            /* sending http response line, headers, and body */
            fileWriter.println("HTTP RESPONSE TO CLIENT:");
            String httpResponse = "";

            String extension = Process.getExtension(fileContent);
            //System.out.println(extension);

            if(httpRequest.startsWith("GET")) {

                if(fileContent.exists() && fileContent.isDirectory()) {
                    assert printWriter != null;
                    Process.showDirectory( httpResponse, fileWriter, printWriter, stringBuilder);
                }
                else if(fileContent.exists() && extension.equals("txt"))//showing txt file in html
                {
                    //System.out.println("text");
                    Process.showTextFile(fileContent, fileWriter, printWriter, httpResponse);

                }
                else if(fileContent.exists() && ( extension.equals("img") || extension.equals("jpg")))//showing image file in html
                {
                    Process.showImage(fileContent, fileWriter, printWriter, stringBuilder);
                }
                else if(fileContent.exists() && fileContent.isFile())
                {
                    //for all other type files, downloading it
                    Process.downloadFile( httpResponse, fileContent, fileWriter, printWriter);

                    int count;
                    byte[] buffer = new byte[1024];
                    try {
                        OutputStream out = socket.getOutputStream();
                        BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileContent));

                        while((count=in.read(buffer)) > 0) {
                            //System.out.print("#");
                            out.write(buffer, 0, count);//prev
                            //out.write(buffer);
                            out.flush();
                        }
                        System.out.println(">> download finished");
                        in.close();
                        out.close();

                    } catch(IOException e) {
                        //e.printStackTrace();
                    }
                }
                else if(!fileContent.exists()) {
                    assert printWriter != null;
                    Process.fileNotExist( httpResponse, fileWriter, printWriter, stringBuilder);

                }
            }

            try {
                bufferedReader.close();
                assert printWriter != null;
                printWriter.close();
                fileWriter.close();
                socket.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        //uploading...
        if(httpRequest.startsWith("UPLOAD"))
        {
            UploadService.uploadFile( httpRequest, bufferedReader,socket,root);
        }

    }
}
