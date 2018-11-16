package classes;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Server server = new UDPServer();
        String command;

        while (true) {
            command = server.readCommand();
            try {
                switch (command) {
                    case "echo":
                        server.echo();
                        break;
                    case "time":
                        server.time();
                        break;
                    case "upload":
                        server.upload();
                        break;
                    case "download":
                        server.download();
                        break;
                    case "close":
                        server.close();
                        server.connect();
                        break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }
}
