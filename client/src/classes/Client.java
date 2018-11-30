package classes;

import java.io.IOException;

public interface Client {
    void echo(String message) throws IOException;

    void time() throws IOException;

    void close() throws IOException;

    void upload(String filename) throws IOException, InterruptedException;

    void download(String filename) throws IOException;
}
