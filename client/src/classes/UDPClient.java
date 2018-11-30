package classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;

public class UDPClient implements Client {

    private DatagramSocket socket;
    private DatagramPacket packet;
    private InetAddress address;
    private int port = 4445;

    private byte[] buf = new byte[4092];

    public UDPClient() {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void echo(String message) throws IOException {
        sendString("echo");
        sendString(message);
        System.out.println(receiveString());
    }

    @Override
    public void time() throws IOException {
        sendString("time");
        System.out.println(receiveString());
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public void upload(String filename) throws IOException {
        File file = new File("client/files/" + filename);

        Long startTime = new Date().getTime();

        sendString("upload");
        sendString(file.getName());
        sendBytes(longToBytes(file.length()));

        FileInputStream fileInputStream = new FileInputStream(file);
        int n;
        buf = new byte[4092];
        while((n = fileInputStream.read(buf)) != -1){
            try {
                buf = Arrays.copyOfRange(buf, 0, n);
                sendBytes(buf);
                System.out.println(n);
            } catch (Exception e) {
                Long position = fileInputStream.getChannel().position();
                fileInputStream.getChannel().position(position - n);
            }
        }

        if (receiveString().equals("done")) {
            Long endTime = new Date().getTime();
            System.out.println("BITRATE: " + (file.length() / (endTime - startTime)) + " KB/s");
        }
    }

    @Override
    public void download(String filename) throws IOException {
        Long startTime = new Date().getTime();

        sendString("download");
        sendString(filename);

        if (bytesToLong(receiveBytes()) == 0) {
            System.out.println("No such file");
            return;
        }

        Long length = bytesToLong(receiveBytes());
        FileOutputStream fileOutputStream = new FileOutputStream("client/files/" + filename);

        int n = 4092;
        buf = new byte[4092];
        Long bytesRemaining = length;
        while(bytesRemaining <= 0) {
            receiveBytes();
            fileOutputStream.write(buf,0, n);
            fileOutputStream.flush();
            bytesRemaining -= n;
            if ((n = packet.getLength()) < 4092) {
                break;
            }
        }
        fileOutputStream.close();

        Long endTime = new Date().getTime();
        System.out.println("BITRATE: " + (length / (endTime - startTime)) + " KB/s");
    }

    private void sendBytes(byte[] bytes) throws IOException {
        buf = bytes;
        packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
    }

    private void sendString(String message) throws IOException {
        buf = message.getBytes();
        packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
    }

    private String receiveString() throws IOException {
        buf = new byte[256];
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        address = packet.getAddress();
        port = packet.getPort();
        return new String(packet.getData(), 0, packet.getLength());
    }

    private byte[] receiveBytes() throws IOException {
        buf = new byte[4092];
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        address = packet.getAddress();
        port = packet.getPort();
        return packet.getData();
    }

    private byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    private long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        byte[] d = Arrays.copyOfRange(bytes, 0, 8);
        buffer.put(d);
        buffer.flip();//need flip
        return buffer.getLong();
    }
}
