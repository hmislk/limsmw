package org.openhealth.limsmw;

/**
 *
 * @author Buddhika
 */
public class Analyzer {
    private String name;
    private String manufacturer;
    private String model;
    private String serialNumber;
    private InterfaceType interfaceType;
    private InterfaceProtocol interfaceProtocol;
    private CommunicationType communicationType;
    private int baudRate; // only applicable for serial communication
    
    public Analyzer(String name, String manufacturer, String model, String serialNumber, InterfaceType interfaceType, InterfaceProtocol interfaceProtocol, CommunicationType communicationType, int baudRate) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.model = model;
        this.serialNumber = serialNumber;
        this.interfaceType = interfaceType;
        this.interfaceProtocol = interfaceProtocol;
        this.communicationType = communicationType;
        this.baudRate = baudRate;
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
    
    public enum InterfaceType {
        SERIAL,
        TCP_IP
    }
    
    public enum InterfaceProtocol {
        ASTM,
        HL7V2_5,
        FHIR
    }
    
    public enum CommunicationType {
        SERVER,
        CLIENT
    }
}

