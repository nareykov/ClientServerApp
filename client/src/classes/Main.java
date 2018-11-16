package classes;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static Scanner inputScanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        Client client = new UDPClient();
        String input;

        while ((input = inputScanner.next()) != null) {
            try {
                switch (input) {
                    case "echo":
                        client.echo(inputScanner.nextLine().substring(1));
                        break;
                    case "time":
                        client.time();
                        break;
                    case "upload":
                        client.upload(inputScanner.nextLine().substring(1));
                        break;
                    case "download":
                        client.download(inputScanner.nextLine().substring(1));
                        break;
                    case "close":
                        client.close();
                        inputScanner.close();
                        return;
                    default:
                        System.out.println("Incorrect command");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }
}
