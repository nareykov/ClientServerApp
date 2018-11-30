package classes;

import java.io.IOException;

public interface Server {
    void echo() throws IOException;

    void time() throws IOException;

    void close() throws IOException;

    void upload() throws IOException, InterruptedException;

    void download() throws IOException;

    void connect();

    String readCommand();
}
