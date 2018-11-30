package classes;

public class ConnectionHandler {

    private final int SECONDS_TO_RECCONECT = 20;
    private int secondsLeft;

    public ConnectionHandler() {
        secondsLeft = SECONDS_TO_RECCONECT;
    }

    public boolean closeConnection() throws InterruptedException {
        Thread.sleep(1000);
        secondsLeft -= 1;
        if (secondsLeft <= 0) {
            return true;
        }
        System.out.println("Trying to reconnect " + secondsLeft);
        return false;
    }

}
