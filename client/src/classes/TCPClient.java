package classes;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Timer;

public class TCPClient implements Client {

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public TCPClient() {
        try {
           // InetAddress addr = InetAddress.getLocalHost();
            socket = new Socket("", 220);
            socket.setKeepAlive(true);

            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (Exception e) {
            System.out.println("TCPClient connection error");
        }
    }

    @Override
    public void echo(String message) throws IOException {
        dataOutputStream.writeUTF("echo");
        dataOutputStream.flush();
        dataOutputStream.writeUTF(message);
        dataOutputStream.flush();
        System.out.println(dataInputStream.readUTF());
    }

    @Override
    public void time() throws IOException {
        dataOutputStream.writeUTF("time");
        dataOutputStream.flush();
        System.out.println(dataInputStream.readUTF());
    }

    @Override
    public void close() throws IOException {
        dataOutputStream.writeUTF("close");
        dataOutputStream.flush();
        dataInputStream.close();
        dataOutputStream.close();
        socket.close();
    }

    @Override
    public void upload(String filename) throws IOException {
        File file = new File("client/files/" + filename);

        Long startTime = new Date().getTime();

        dataOutputStream.writeUTF("upload");
        dataOutputStream.flush();
        dataOutputStream.writeUTF(file.getName());
        dataOutputStream.flush();
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

        if (dataInputStream.readUTF().equals("done")) {
            Long endTime = new Date().getTime();
            System.out.println("BITRATE: " + (file.length() / (endTime - startTime)) + " KB/s");
        }
    }

    @Override
    public void download(String filename) throws IOException {
        Long startTime = new Date().getTime();

        dataOutputStream.writeUTF("download");
        dataOutputStream.flush();
        dataOutputStream.writeUTF(filename);
        dataOutputStream.flush();

        if (!dataInputStream.readBoolean()) {
            System.out.println("No such file");
            return;
        }

        Long length = dataInputStream.readLong();
        FileOutputStream fileOutputStream = new FileOutputStream("client/files/" + filename);

        int n;
        byte[] buf = new byte[4092];
        Long bytesRemaining = length;
        while(bytesRemaining != 0 && (n = dataInputStream.read(buf)) != -1){
            fileOutputStream.write(buf,0, n);
            fileOutputStream.flush();
            bytesRemaining -= n;
        }
        fileOutputStream.close();

        Long endTime = new Date().getTime();
        System.out.println("BITRATE: " + (length / (endTime - startTime)) + " KB/s");
    }
}
