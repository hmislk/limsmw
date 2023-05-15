package org.openhealth.limsmw;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialCommHandler implements AnalyzerCommHandler, Runnable {

    private String portName;
    private int baudRate;
    private SerialPort serialPort;
    private static final byte ENQ = 0x05;
    private static final byte ACK = 0x06;
    private Thread listeningThread;

    public SerialCommHandler(String portName, int baudRate) {
        this.portName = portName;
        this.baudRate = baudRate;
    }

    public void connect() throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            throw new Exception("Error: Port is currently in use");
        }

        CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
        if (commPort instanceof SerialPort) {
            serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            startListening();
        } else {
            throw new Exception("Error: Only serial ports are supported");
        }
    }

    public void disconnect() throws IOException {
        if (serialPort != null) {
            stopListening();
            serialPort.close();
        }
    }

    public InputStream getInputStream() throws IOException {
        if (serialPort != null) {
            return serialPort.getInputStream();
        } else {
            throw new IOException("Not connected to the serial port");
        }
    }

    public OutputStream getOutputStream() throws IOException {
        if (serialPort != null) {
            return serialPort.getOutputStream();
        } else {
            throw new IOException("Not connected to the serial port");
        }
    }

    public boolean sendENQAndCheckACK() throws IOException {
        if (serialPort == null) {
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
        try {
            // Send ENQ at the start
            if (!sendENQAndCheckACK()) {
                throw new IOException("Failed to send ENQ or didn't receive ACK");
            }

            InputStream inputStream = getInputStream();
            OutputStream outputStream = getOutputStream();
            byte[] buffer = new byte[1024];
            int len;

            while ((len = inputStream.read(buffer)) > -1) {
                for (int i = 0; i < len; i++) {
                    if (buffer[i] == ENQ) {
                        // Respond to ENQ with ACK
                        outputStream.write(ACK);
                        outputStream.flush();
                        System.out.println("Received ENQ, sent ACK");
                    }
                }

                // Here you can implement your action based on the input data
                // For this example, we just print the received bytes to the console
                System.out.println(new String(buffer, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
