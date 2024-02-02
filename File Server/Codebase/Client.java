package Threading;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        int count;
        Scanner scanner = new Scanner(System.in);

        while(true) {
            String str = scanner.nextLine();
            String filename= "C:\\Users\\User\\IdeaProjects\\NetworkingOfffline\\src\\upload\\"+str;
            new ClientThread(filename);
        }
    }
}



