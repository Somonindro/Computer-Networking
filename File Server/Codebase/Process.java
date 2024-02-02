package Threading;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;

class Process {
    public static String getExtension(File file)
    {
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        String extension = fileName.substring(index+1);
        return extension;
    }

    public static void downloadFile(String httpResponse, File fileContent, PrintWriter fileWriter, PrintWriter printWriter)
    {

        httpResponse += "HTTP/1.1 200 OK\r\n";
        httpResponse += "Server: Java HTTP Server: 1.0\r\n";
        httpResponse += "Date: "+new Date()+"\r\n";
        httpResponse += "Content-Type: application/force-download\r\n";
        httpResponse += "Content-Length: "+fileContent.length()+"\r\n";
        fileWriter.println(httpResponse);
        printWriter.write(httpResponse);
        printWriter.write("\r\n");
        printWriter.flush();


    }

    public static void showDirectory(String httpResponse, PrintWriter fileWriter, PrintWriter printWriter, StringBuilder stringBuilder)
    {
        String content = stringBuilder.toString();
        httpResponse += "HTTP/1.1 200 OK\r\n";
        httpResponse += "Server: Java HTTP Server: 1.0\r\n";
        httpResponse += "Date: "+new Date()+"\r\n";
        httpResponse += "Content-Type: text/html\r\n";
        httpResponse += "Content-Length: "+content.length()+"\r\n";

        fileWriter.println(httpResponse);
        printWriter.write(httpResponse);
        printWriter.write("\r\n");
        printWriter.write(stringBuilder.toString());
        printWriter.flush();
    }

    public static void fileNotExist(String httpResponse,PrintWriter fileWriter,PrintWriter printWriter,StringBuilder stringBuilder)
    {
        httpResponse += "HTTP/1.1 404 NOT FOUND\r\nServer: Java HTTP Server: 1.0\r\nDate: "+new Date()+"\r\nContent-Type: text/html\r\nContent-Length: "+stringBuilder.toString().length()+"\r\n";
        fileWriter.println(httpResponse);

        assert printWriter != null;
        printWriter.write(httpResponse);
        printWriter.write("\r\n");
        printWriter.write(stringBuilder.toString());
        printWriter.flush();
        System.out.println(">> 404: Page not found");
    }

    public static void showTextFile(File fileContent,PrintWriter fileWriter,PrintWriter printWriter,String httpResponse)
    {
        InputStream is = null;
        try {
            is = new FileInputStream(fileContent);
        } catch (FileNotFoundException e) {

        }
        assert is != null;
        Scanner sc = new Scanner(is, StandardCharsets.UTF_8);


        StringBuilder strbuilder = new StringBuilder();
        strbuilder.append("<html>\r\n<head>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\r\n</head>\r\n<body>\r\n<h1>\r\n");
        while (sc.hasNextLine()) {
            //System.out.println("abc i");
            strbuilder.append(sc.nextLine()+ "\r\n");
        }
        strbuilder.append("</h1>\r\n</body>\r\n</html>");

        //System.out.println(strbuilder.toString());
        String content= strbuilder.toString();
        httpResponse += "HTTP/1.1 200 OK\r\n";
        httpResponse += "Server: Java HTTP Server: 1.0\r\n";
        httpResponse += "Date: "+new Date()+"\r\n";
        httpResponse += "Content-Type: text/html\r\n";
        httpResponse += "Content-Length: "+content.length()+"\r\n";

        fileWriter.println(httpResponse);

        printWriter.write(httpResponse);
        printWriter.write("\r\n");
        printWriter.write(content);
        printWriter.flush();

        //System.out.println("texttt");
    }

    public static void showImage(File fileContent,PrintWriter fileWriter,PrintWriter printWriter,StringBuilder stringBuilder)
    {
        byte[] buf;
        try {
            buf = Files.readAllBytes(Paths.get(fileContent.getAbsolutePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String base64EncodedImageBytes = Base64.getEncoder().encodeToString(buf);
        String responseStr="HTTP/1.1 200 OK\r\n";
        responseStr += "Server: Java HTTP Server: 1.0\r\n";
        responseStr += "Date: "+new Date()+"\r\n";
        responseStr+="Content-Type: text/html" +"\r\n";

        String content = "<img src=\"data: text/html;charset:utf-8;base64,"+base64EncodedImageBytes+"\">";
        responseStr+="Content-Length: "+content.length()+"\r\n\r\n";

        fileWriter.println(responseStr);
        responseStr += content;
        printWriter.write(responseStr);
        printWriter.write("\r\n");
        printWriter.write(stringBuilder.toString());
        printWriter.flush();
    }



    public static void showList(File fileContent,File[] listOfContent,StringBuilder stringBuilder,String path,int server_port)
    {
        if(fileContent.exists()) {
            if(fileContent.isDirectory()) {
                listOfContent = fileContent.listFiles();
                stringBuilder.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<link rel=\"icon\" href=\"data:,\">\n</head>\n<body>\n");

                for(int i=0; i<listOfContent.length; i++) {
                    if(listOfContent[i].isDirectory()) {
                        stringBuilder.append("<font size=\"7\"><b><i><a href=\"http://localhost:"+server_port+"/"+path.replace("\\", "/")+listOfContent[i].getName()+"\"> "+listOfContent[i].getName()+" </a></i></b></font><br>\n");
                    }
                    if(listOfContent[i].isFile()) {
                        stringBuilder.append("<font size=\"7\"><a href=\"http://localhost:"+server_port+"/"+path.replace("\\", "/")+listOfContent[i].getName()+"\"> "+listOfContent[i].getName()+" </a></font><br>\n");
                    }
                }
                stringBuilder.append("</body>\n</html>");
            }
        } else {
            /* requested content does not exist in current directory */
            stringBuilder.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<link rel=\"icon\" href=\"data:,\">\n</head>\n<body>\n");
            stringBuilder.append("<h1> 404: Page not found </h1>\n");
            stringBuilder.append("</body>\n</html>");
        }
    }


}
