package classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;

public class UDPServer implements Server {

    private DatagramSocket socket;
    private byte[] buf = new byte[4092];
    private DatagramPacket packet;
    private InetAddress address;
    private int port;

    public UDPServer() {
        try {
            socket = new DatagramSocket(4445);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void echo() throws IOException {
        String received = receiveString();
        buf = received.toUpperCase().getBytes();

        address = packet.getAddress();
        int port = packet.getPort();
        packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
    }

    @Override
    public void time() throws IOException {
        buf = new Date().toString().getBytes();
        address = packet.getAddress();
        int port = packet.getPort();
        packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void upload() throws IOException {
        String filename = receiveString();
        Long length = bytesToLong(receiveBytes());
        FileOutputStream fileOutputStream = new FileOutputStream("server/files/" + filename);

        int n;
        buf = new byte[4092];
        while(length != 0){
            try {
                receiveBytes();
                if ((n = packet.getLength()) < 4092) {
                    break;
                }
            } catch (Exception e) {
                continue;
            }
            fileOutputStream.write(buf,0, n);
            fileOutputStream.flush();
            length -= n;
        }
        fileOutputStream.close();

        sendString("done");
    }

    @Override
    public void download() throws IOException {
        String filename = receiveString();
        File file;
        try {
            file = new File("server/files/" + filename);
            sendBytes(longToBytes(1));
        } catch (Exception e) {
            sendBytes(longToBytes(0));
            return;
        }
        long length = file.length();
        sendBytes(longToBytes(length));

        FileInputStream fileInputStream = new FileInputStream(file);
        int n;
        buf = new byte[4092];
        while((n = fileInputStream.read(buf)) != -1){
            buf = Arrays.copyOfRange(buf, 0, n);
            sendBytes(buf);
            System.out.println(n);
        }
    }

    @Override
    public void connect() {

    }

    @Override
    public String readCommand() {
        try {
            String command = receiveString();
            System.out.println("UDP " + command);
            return command;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
