package org.openhealth.limsmw;

import com.fazecast.jSerialComm.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialCommHandler implements AnalyzerCommHandler, Runnable {

    private String portName;
    private int baudRate;
    private SerialPort comPort;
    private static final byte ENQ = 0x05;
    private static final byte ACK = 0x06;
    private Thread listeningThread;

    public SerialCommHandler(String portName, int baudRate) {
        this.portName = portName;
        this.baudRate = baudRate;
    }

    public void connect() {
        comPort = SerialPort.getCommPort(portName);
        comPort.setBaudRate(baudRate);
        comPort.setNumDataBits(8);
        comPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        comPort.setParity(SerialPort.NO_PARITY);
        
        if (!comPort.openPort()) {
            throw new RuntimeException("Error: Unable to open the port");
        }

        startListening();
    }

    public void disconnect() {
        if (comPort != null) {
            stopListening();
            comPort.closePort();
        }
    }

    public InputStream getInputStream() {
        if (comPort != null) {
            return comPort.getInputStream();
        } else {
            throw new RuntimeException("Not connected to the serial port");
        }
    }

    public OutputStream getOutputStream() {
        if (comPort != null) {
            return comPort.getOutputStream();
        } else {
            throw new RuntimeException("Not connected to the serial port");
        }
    }

    public boolean sendENQAndCheckACK() throws IOException {
        System.out.println("sendENQAndCheckACK = " );
        if (comPort == null) {
            throw new IOException("Not connected to the serial port");
        }

        OutputStream outputStream = getOutputStream();
        outputStream.write(ENQ);
        outputStream.flush();

        InputStream inputStream = getInputStream();
        byte[] buffer = new byte[1];
        int readBytes = inputStream.read(buffer);

        if (readBytes < 1) {
            throw new IOException("No response from the device");
        }

        return buffer[0] == ACK;
    }

    public void startListening() {
        System.out.println("startListening");
        listeningThread = new Thread(this);
        listeningThread.start();
    }

    public void stopListening() {
        if (listeningThread != null) {
            listeningThread.interrupt();
        }
    }

    @Override
    public void run() {
        System.out.println("run");
        try {
            // Send ENQ at the start
            if (!sendENQAndCheckACK()) {
                throw new IOException("Failed to send ENQ or didn't receive ACK");
            }
            System.out.println("1");
            InputStream inputStream = getInputStream();
            OutputStream outputStream = getOutputStream();
            byte[] buffer = new byte[1024];
            int len;

            while ((len = inputStream.read(buffer)) > -1) {
                System.out.println("2");
                for (int i = 0; i < len; i++) {
                    System.out.println("i = " + i);
                    if (buffer[i] == ENQ) {
                        // Respond to ENQ with ACK
                        outputStream.write(ACK);
                        outputStream.flush();
                        System.out.println("Received ENQ, sent ACK");
                    }
                }
                System.out.println("to do");
                // Here you can implement your action based on the input data
                // For this example, we just print the received bytes to the console
               
                System.out.println(new String(buffer, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
