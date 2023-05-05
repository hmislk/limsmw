package org.openhealth.limsmw;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialCommHandler  implements AnalyzerCommHandler {

    private String portName;
    private int baudRate;
    private SerialPort serialPort;

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
        } else {
            throw new Exception("Error: Only serial ports are supported");
        }
    }

    public void disconnect() throws IOException {
        if (serialPort != null) {
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
}
