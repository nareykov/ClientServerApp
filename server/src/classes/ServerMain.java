package classes;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class ServerMain {

    public static void main(String[] args) throws IOException {
        Server server = new TCPServer();
        String command;

        while (true) {
            if ((command = server.readCommand()) != null) {
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
                    e.printStackTrace();
                }
            }
        }
    }
}
