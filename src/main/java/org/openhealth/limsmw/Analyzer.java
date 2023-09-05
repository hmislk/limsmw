package org.openhealth.limsmw;

import java.net.SocketImpl;
import java.util.Date;

/**
 *
 * @author Buddhika
 */
public class Analyzer {

    private Date createdAt;
    private String name;
    private String manufacturer;
    private String model;
    private String serialNumber;
    private InterfaceType interfaceType;
    private InterfaceProtocol interfaceProtocol;
    private CommunicationType communicationType;
    private Encoding encodingType;
    private int baudRate; // only applicable for serial communication
    private String ipAddress; // only applicable for TCP/IP communication
    private int port; // applicable for both serial and TCP/IP communication

    public Analyzer() {
    }

    public Analyzer(String name, String manufacturer, String model, String serialNumber, InterfaceType interfaceType, InterfaceProtocol interfaceProtocol, CommunicationType communicationType, int baudRate, String ipAddress, int port) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.model = model;
        this.serialNumber = serialNumber;
        this.interfaceType = interfaceType;
        this.interfaceProtocol = interfaceProtocol;
        this.communicationType = communicationType;
        this.baudRate = baudRate;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    
    
    // Getters and setters for each field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public InterfaceType getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(InterfaceType interfaceType) {
        this.interfaceType = interfaceType;
    }

    public InterfaceProtocol getInterfaceProtocol() {
        return interfaceProtocol;
    }

    public void setInterfaceProtocol(InterfaceProtocol interfaceProtocol) {
        this.interfaceProtocol = interfaceProtocol;
    }

    public CommunicationType getCommunicationType() {
        return communicationType;
    }

    public void setCommunicationType(CommunicationType communicationType) {
        this.communicationType = communicationType;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPortName() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            return "COM" + port;
        } else if (os.contains("linux") || os.contains("mac")) {
            return "/dev/ttyS" + (port - 1); // or "/dev/cu" if needed
        } else {
            throw new UnsupportedOperationException("Unsupported operating system.");
        }
    }

    public Encoding getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(Encoding encodingType) {
        this.encodingType = encodingType;
    }

    public enum Encoding {
        ASCII,
        ISO_8859_1,
        UTF_8,
        UTF_16,
    }

    public enum InterfaceType {
        SERIAL,
        TCP_IP,
        NONE,
    }

    public enum InterfaceProtocol {
        ASTM,
        HL7V2_5,
        FHIR, NONE,
    }

    public enum CommunicationType {
        SERVER,
        CLIENT,
        SERVER_CLIENT,
        NONE,
    }
}
