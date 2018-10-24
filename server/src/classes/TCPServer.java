package classes;

import java.io.*;
import java.net.*;
import java.util.Date;

public class TCPServer implements Server {

    private ServerSocket serverSocket;
    private Socket socket;

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    public TCPServer() {
        try {
            serverSocket = new ServerSocket(220);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        connect();
    }

    @Override
    public String readCommand() {
        String command;
        try {
            command = dataInputStream.readUTF();
        } catch (Exception e) {
            command = null;
        }
        return command;
    }

    @Override
    public void echo() throws IOException {
        String message = dataInputStream.readUTF();
        dataOutputStream.writeUTF(message.toUpperCase());
        dataOutputStream.flush();
    }

    @Override
    public void time() throws IOException {
        dataOutputStream.writeUTF( new Date().toString());
        dataOutputStream.flush();
    }

    @Override
    public void close() throws IOException {
        dataOutputStream.close();
        dataInputStream.close();
        socket.close();
        System.out.println("Client disconnected");
    }

    @Override
    public void upload() throws IOException {
        String filename = dataInputStream.readUTF();
        Long length = dataInputStream.readLong();
        FileOutputStream fileOutputStream = new FileOutputStream("server/files/" + filename);

        int n;
        byte[] buf = new byte[4092];
        while(length != 0 && (n = dataInputStream.read(buf)) != -1){
            fileOutputStream.write(buf,0, n);
            fileOutputStream.flush();
            length -= n;
        }
        fileOutputStream.close();

        dataOutputStream.writeUTF("done");
        dataOutputStream.flush();
    }

    @Override
    public void download() throws IOException {
        String filename = dataInputStream.readUTF();
        File file;
        try {
            file = new File("server/files/" + filename);
            dataOutputStream.writeBoolean(true);
            dataOutputStream.flush();
        } catch (Exception e) {
            dataOutputStream.writeBoolean(false);
            dataOutputStream.flush();
            return;
        }
        System.out.println(file.length());
        dataOutputStream.writeLong(file.length());
        dataOutputStream.flush();

        FileInputStream fileInputStream = new FileInputStream(file);
        int n;
        byte[] buf = new byte[4092];
        while((n = fileInputStream.read(buf)) != -1){
            dataOutputStream.write(buf,0, n);
            System.out.println(n);
            dataOutputStream.flush();
        }
    }

    @Override
    public void connect() {
        try {
            System.out.println("Waiting for a client...");
            socket = serverSocket.accept();

            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            System.out.println("Client connected");
        } catch (IOException e) {
            System.out.println("Can't accept");
        }
    }
}