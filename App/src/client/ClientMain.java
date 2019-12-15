package client;

import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {

        String ip = "127.0.0.1";
        int port = 7000;

        Client client = new Client();

        client.startConnection(ip, port);

        Scanner sc = new Scanner(System.in);


        while (true) {
            String message = sc.nextLine();
            client.sendMessage(message);
        }
    }

}