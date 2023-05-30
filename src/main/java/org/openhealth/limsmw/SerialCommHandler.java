package org.openhealth.limsmw;

import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SerialCommHandler implements AnalyzerCommHandler, Runnable {

    private String portName;
    private int baudRate;
    private SerialPort comPort;
    private int frameNumber;

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

    public void startListening() {
        Thread listeningThread = new Thread(this);
        listeningThread.start();
    }

    public void stopListening() {
        // Implement the necessary logic to stop the listening thread, if required
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = comPort.getInputStream();
            byte[] buffer = new byte[1024];
            int len;

            while ((len = inputStream.read(buffer)) > -1) {
                String receivedMessage = new String(buffer, 0, len).trim();
                System.out.println("Received message: " + receivedMessage);

                // Send ACK
                comPort.writeBytes(new byte[]{0x06}, 1);
                System.out.println("Sent ACK.");

                processReceivedData(receivedMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processReceivedData(String receivedData) {
        String[] frames = splitIntoFrames(receivedData, 240);
        frameNumber = 1;

        for (String frame : frames) {
            sendFrame(frame, frameNumber);
            frameNumber++;
        }

        System.out.println("Received data: " + receivedData);
    }

    private String[] splitIntoFrames(String text, int maxLength) {
        int textLength = text.length();
        int numOfFrames = textLength / maxLength;
        if (textLength % maxLength != 0) {
            numOfFrames++;
        }
        String[] frames = new String[numOfFrames];
        for (int i = 0; i < numOfFrames; i++) {
            int startIndex = i * maxLength;
            int endIndex = Math.min((i + 1) * maxLength, textLength);
            frames[i] = text.substring(startIndex, endIndex);
        }
        return frames;
    }

    private void sendFrame(String frameText, int frameNumber) {
        String frame = "F" + frameNumber + " " + frameText;
        System.out.println("Sent frame: " + frame);
    }

    @Override
    public String processAnalyzerMessage(String receivedMessage) {
        // Implement your logic to process the received message
        // Modify this method according to your requirements
        // Return the processed message
        return "Processed: " + receivedMessage;
    }
}
