package org.openhealth.limsmw;

import com.fazecast.jSerialComm.*;
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

    public void connect() {
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(baudRate);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setParity(SerialPort.NO_PARITY);

        if (!serialPort.openPort()) {
            throw new RuntimeException("Error: Unable to open the port");
        }
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 5000, 0);

        startListening();
    }

    public void disconnect() {
        if (serialPort != null) {
            stopListening();
            serialPort.closePort();
        }
    }

    public InputStream getInputStream() {
        if (serialPort != null) {
            return serialPort.getInputStream();
        } else {
            throw new RuntimeException("Not connected to the serial port");
        }
    }

    public OutputStream getOutputStream() {
        if (serialPort != null) {
            return serialPort.getOutputStream();
        } else {
            throw new RuntimeException("Not connected to the serial port");
        }
    }

    public boolean sendENQAndCheckACK() {
        if (serialPort == null) {
            throw new RuntimeException("Not connected to the serial port");
        }

        OutputStream outputStream = getOutputStream();
        InputStream inputStream = getInputStream();
        byte[] buffer = new byte[1];
        int readBytes = -1;
        int retries = 0;

        while (retries < 3) {  // Retry up to 3 times
            try {
                outputStream.write(ENQ);
                outputStream.flush();

                readBytes = inputStream.read(buffer);
                if (readBytes < 1) {
                    throw new IOException("No response from the device");
                }

                if (buffer[0] == ACK) {
                    return true;
                }
            } catch (SerialPortTimeoutException e) {
                // System.out.println("Timeout waiting for ACK, retrying...");
                retries++;
            } catch (IOException e) {
                throw new RuntimeException("IO Exception during sendENQAndCheckACK", e);
            }
        }

        throw new RuntimeException("Failed to receive ACK after 3 attempts");
    }

    public void startListening() {
        // System.out.println("startListening");
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
        // System.out.println("run");
        try {
            // Send ENQ at the start
            if (!sendENQAndCheckACK()) {
                throw new IOException("Failed to send ENQ or didn't receive ACK");
            }
            // System.out.println("1");
            InputStream inputStream = getInputStream();
            OutputStream outputStream = getOutputStream();
            byte[] buffer = new byte[1024];
            int len;

            while ((len = inputStream.read(buffer)) > -1) {
                // System.out.println("2");
                for (int i = 0; i < len; i++) {
                    // System.out.println("i = " + i);
                    if (buffer[i] == ENQ) {
                        // Respond to ENQ with ACK
                        outputStream.write(ACK);
                        outputStream.flush();
                        // System.out.println("Received ENQ, sent ACK");
                    }
                }
                // System.out.println("to do");
                // Here you can implement your action based on the input data
                // For this example, we just print the received bytes to the console

                // System.out.println(new String(buffer, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
