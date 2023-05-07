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
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.model.v25.segment.MSA;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.ACK;
import java.util.UUID;

public class TCPServerCommHandler implements Runnable, AnalyzerCommHandler {

    private ServerSocket serverSocket;
    Analyzer analyzer;

    public TCPServerCommHandler(Analyzer analyzer) {
        this.analyzer = analyzer;
        System.out.println("analyzer.getPort() = " + analyzer.getPort());
    }

    @Override
    public void run() {
        System.out.println("going to run " + analyzer.getName() + " on port " + analyzer.getPort() + ".");
        try {
            serverSocket = new ServerSocket(analyzer.getPort());
            System.out.println("TCP Server started on port " + analyzer.getPort());

            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection from " + clientSocket.getRemoteSocketAddress());

                InputStream inputStream = clientSocket.getInputStream();
                System.out.println("inputStream = " + inputStream);
                OutputStream outputStream = clientSocket.getOutputStream();
                System.out.println("outputStream = " + outputStream);

                String receivedMessage = null;
                String responseMessage = null;

                int ch = inputStream.read();
                System.out.println("ch = " + ch);
                System.out.println("ch = " + (Integer)(ch));
                if (ch == 5) {
                    System.out.println("If received message is ENQ, send an ACK");
                    responseMessage = createAcknowledgementMessage();
                } else {
                    System.out.println("If received message is not ENQ, read the message from the input stream");
                    StringBuilder messageBuilder = new StringBuilder();
                    messageBuilder.append((char) ch);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("line = " + line);
                        messageBuilder.append(line).append(System.lineSeparator());
                    }
                    receivedMessage = messageBuilder.toString().trim();
                    System.out.println("receivedMessage = " + receivedMessage);
                    responseMessage = processAnalyzerMessage(receivedMessage);
                }
                System.out.println("receivedMessage = " + receivedMessage);
                System.out.println("responseMessage = " + responseMessage);

                writeMessageToStream(outputStream, responseMessage);
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

    private String createAcknowledgementMessage() {
        return String.valueOf((char) 6); // Return an ACK character (ASCII code 6)
    }

    private String createAcknowledgementMessage(boolean hl7) {
        try {
            // Create a new ACK message
            ACK ack = new ACK();

            MSH msh = ack.getMSH();
            msh.getFieldSeparator().setValue("|");
            msh.getEncodingCharacters().setValue("^~\\&");
            msh.getVersionID().getVersionID().setValue("2.3");
            msh.getMessageType().getMessageCode().setValue("ACK");
            msh.getMessageType().getTriggerEvent().setValue("ACK");
            msh.getMessageControlID().setValue(generateMessageControlId());
            msh.getProcessingID().getProcessingID().setValue("P");
            msh.getCharacterSet(0).setValue("ASCII");

            // Set the MSA segment
            MSA msa = ack.getMSA();
            msa.getAcknowledgmentCode().setValue("AA");
            msa.getMessageControlID().setValue(msh.getMessageControlID().getValue());
            msa.getTextMessage().setValue("Message processed successfully");

            // Encode the response message and return it as a string
            PipeParser parser = new PipeParser();
            return parser.encode(ack);
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorMessageResponse("Error creating acknowledgement message");
        }
    }

    private String createErrorMessageResponse(String errorMessage) {

        return null;
    }

    private String generateMessageControlId() {
        return UUID.randomUUID().toString();
    }

    private String readMessageFromStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder messageBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            messageBuilder.append(line).append(System.lineSeparator());
        }

        return messageBuilder.toString().trim();
    }

    private String processAnalyzerMessage(String receivedMessage) {
        RestClient restClient = new RestClient(PrefsController.getPreference().getUserName(), PrefsController.getPreference().getPassword());
        Parser parser = new PipeParser();
        String restApiUrl = PrefsController.getPreference().getUrl() + "api/limsmw/limsProcessAnalyzerMessage";

        try {
            Message message = parser.parse(receivedMessage);
            String requestBody = "HL7Message=" + receivedMessage;
            String response = restClient.sendRequestToRestServer(restApiUrl, requestBody);
            return response;

        } catch (HL7Exception e) {
            e.printStackTrace();
            return createErrorResponse(e.getMessage());
        }
    }

    private String createErrorResponse(String errorMessage) {

        return null;
    }

    private void writeMessageToStream(OutputStream outputStream, String message) throws IOException {
        Charset charset;
        if (analyzer == null) {
            if (analyzer.getEncodingType() == null) {
                charset = StandardCharsets.UTF_8;
            } else {
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
            }
        } else {
            charset = StandardCharsets.UTF_8;
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

}
