package org.openhealth.limsmw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import static org.openhealth.limsmw.Analyzer.Encoding.ASCII;
import static org.openhealth.limsmw.Analyzer.Encoding.ISO_8859_1;
import static org.openhealth.limsmw.Analyzer.Encoding.UTF_16;
import static org.openhealth.limsmw.Analyzer.Encoding.UTF_8;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.datatype.CX;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.group.ADR_A19_QUERY_RESPONSE;
import ca.uhn.hl7v2.model.v25.message.ADR_A19;
import ca.uhn.hl7v2.model.v25.message.QRY_A19;
import ca.uhn.hl7v2.model.v25.segment.MSA;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.PV1;
import ca.uhn.hl7v2.model.v25.segment.QRD;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.ACK;
import ca.uhn.hl7v2.model.v25.message.ORR_O02;
import ca.uhn.hl7v2.model.v25.segment.ORC;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jdk.internal.joptsimple.internal.Messages.message;

public class TCPServerCommHandler implements Runnable, AnalyzerCommHandler {

    private int port;
    private ServerSocket serverSocket;
    Analyzer analyzer;

    public TCPServerCommHandler(Analyzer analyzer) {
        this.analyzer = analyzer;
        this.port = analyzer.getPort();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection from " + clientSocket.getRemoteSocketAddress());

                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();

                String receivedMessage = readMessageFromStream(inputStream); // You'll need to implement a method to read the message from the input stream
                String responseMessage = processAnalyzerMessage(receivedMessage);
                writeMessageToStream(outputStream, responseMessage); // You'll need to implement a method to write the message to the output stream

                // Process incoming and outgoing data here
                // For example, read data from inputStream and write response to outputStream
                // Don't forget to close the streams and the client socket when you're done
                inputStream.close();
                outputStream.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("TCP Server error: " + e.getMessage());
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing server socket: " + e.getMessage());
                }
            }
        }
    }

    private String readMessageFromStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder messageBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            messageBuilder.append(line).append(System.lineSeparator());
        }

        return messageBuilder.toString();
    }

    private String processAnalyzerMessage(String receivedMessage) {
        Parser parser = new PipeParser();
        try {
            Message message = parser.parse(receivedMessage);
            String messageType = message.getName();

            switch (messageType) {
                case "ADT_A01": // Admit/Visit Notification
                    return processAdtA01(message);
                case "ORU_R01": // Observation Result
                    return processOruR01(message);
                case "QRY_A19": // Query by Location
                    return processQryA19(message);
                case "ADR_A19": // Response to Query by Location
                    return processAdrA19(message);
                // Add more cases here for other message types
                default:
                    return createUnsupportedMessageResponse(messageType);
            }
        } catch (HL7Exception e) {
            e.printStackTrace();
            return createErrorResponse(e.getMessage());
        }
    }

    private String processAdtA01(Message message) {
        return "";
    }

    private String processOruR01(Message message) {
        return "";
    }

    private String createUnsupportedMessageResponse(String messageType) {
       
        return null;
    }

    private String createErrorResponse(String errorMessage) {
        
        return null;
    }

    private String createErrorMessageResponse(Message message, String errorCode, String errorMessage) {
        try {
            ACK ack = (ACK) message.generateACK(errorCode, new HL7Exception(errorMessage));
            return ack.encode();
        } catch (HL7Exception | IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String createErrorMessageResponse(String errorMessage) {
       
        return null;
    }

    

    private void writeMessageToStream(OutputStream outputStream, String message) throws IOException {
        Charset charset;
        switch (analyzer.getEncodingType()) {
            case ASCII:
                charset = StandardCharsets.US_ASCII;
                break;
            case ISO_8859_1:
                charset = StandardCharsets.ISO_8859_1;
                break;
            case UTF_8:
                charset = StandardCharsets.UTF_8;
                break;
            case UTF_16:
                charset = StandardCharsets.UTF_16;
                break;
            default:
                charset = StandardCharsets.UTF_8; // default to UTF-8 if not specified
        }
        byte[] messageBytes = message.getBytes(charset);
        outputStream.write(messageBytes);
        outputStream.flush();
    }

    public void stop() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Error stopping server: " + e.getMessage());
            }
        }
    }

    private String processQryA19(Message message) {
        try {
            // Create a new ORR_O02 message
            ORR_O02 orr = new ORR_O02();

            // Set the MSH segment
            MSH msh = orr.getMSH();
            msh.getFieldSeparator().setValue("|");
            msh.getEncodingCharacters().setValue("^~\\&");
            msh.getVersionID().getVersionID().setValue("2.5");
            msh.getMessageType().getMessageStructure().setValue("ORR_O02");
            msh.getMessageType().getTriggerEvent().setValue("O02");
            msh.getDateTimeOfMessage().getTime().setValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            msh.getMessageControlID().setValue("123");

            // Set the MSA segment
            MSA msa = orr.getMSA();
            msa.getAcknowledgmentCode().setValue("AA");
//            msa.getMessageControlID().setValue(((QRY_A19) message).getQRD().getMessageQueryName().getID().getValue());

            // Set the ORC segment
            ORC orc = orr.getRESPONSE().getORDER().getORC();
            orc.getOrderControl().setValue("NW");
            orc.getPlacerOrderNumber().getEntityIdentifier().setValue("1234");
            orc.getFillerOrderNumber().getEntityIdentifier().setValue("5678");
            orc.getEnteredBy(0).getIDNumber();


            orc.getOrderingProvider(0).getIDNumber().setValue("888");

            // Add dummy data to the ORR message
            // You can replace the dummy data with real data later
            PID pid = orr.getRESPONSE().getPATIENT().getPID();
            pid.getPatientName(0).getFamilyName().getSurname().setValue("Doe");
            pid.getPatientName(0).getGivenName().setValue("John");
            pid.getPatientID().getIDNumber().setValue("123456");

//            PV1 pv1 = 
//            pv1.getPatientClass().setValue("O");
//            pv1.getAssignedPatientLocation().getPointOfCare().setValue("LAB");

            // Encode the response message and return it as a string
            PipeParser parser = new PipeParser();
            return parser.encode(orr);
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorMessageResponse("Error processing QRY_A19 message");
        }
    }

    private String processAdrA19(Message message) {
       return null;
    }

    private String createMessageResponse(MSH msh, MSA msa, Object object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
