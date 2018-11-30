package classes;

import java.net.Socket;
import java.util.Scanner;

public class ConnectionHandler {

    private static Scanner inputScanner = new Scanner(System.in);

    private final int SECONDS_TO_RECCONECT = 10;
    private int secondsLeft;

    public ConnectionHandler() {
        secondsLeft = SECONDS_TO_RECCONECT;
    }

    public boolean closeConnection() throws InterruptedException {
        Thread.sleep(1000);
        secondsLeft -= 1;
        if (secondsLeft <= 0) {
            if (!tryToRecconect()) {
                return true;
            }
        }
        System.out.println("Trying to reconnect " + secondsLeft);
        return false;
    }

    private boolean tryToRecconect() {
        System.out.println("Server not responding. Try to reconnect? (yes/no)");
        if (inputScanner.next().equals("yes")) {
            secondsLeft = SECONDS_TO_RECCONECT;
            return true;
        }
        return false;
    }

}
