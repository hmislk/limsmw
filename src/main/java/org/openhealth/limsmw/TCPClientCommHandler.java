package org.openhealth.limsmw;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPClientCommHandler  implements AnalyzerCommHandler {
    private String serverAddress;
    private int serverPort;
    private Socket socket;

    public TCPClientCommHandler(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void connect() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        System.out.println("Connected to server " + serverAddress + ":" + serverPort);
    }

    public void disconnect() throws IOException {
        if (socket != null) {
            socket.close();
            System.out.println("Disconnected from server");
        }
    }

    public void sendData(byte[] data) throws IOException {
        if (socket != null) {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
        } else {
            throw new IOException("Not connected to the server");
        }
    }

    public byte[] receiveData() throws IOException {
        if (socket != null) {
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            byte[] result = new byte[bytesRead];
            System.arraycopy(buffer, 0, result, 0, bytesRead);
            return result;
        } else {
            throw new IOException("Not connected to the server");
        }
    }

    @Override
    public String processAnalyzerMessage(String receivedMessage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
