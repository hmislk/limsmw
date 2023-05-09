package org.openhealth.limsmw;

import org.json.JSONObject;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import static org.openhealth.limsmw.Analyzer.Encoding.ASCII;
import static org.openhealth.limsmw.Analyzer.Encoding.ISO_8859_1;
import static org.openhealth.limsmw.Analyzer.Encoding.UTF_16;
import static org.openhealth.limsmw.Analyzer.Encoding.UTF_8;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        Socket clientSocket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            serverSocket = new ServerSocket(analyzer.getPort());
            System.out.println("TCP Server started on port " + analyzer.getPort());

            while (!Thread.currentThread().isInterrupted()) {
                clientSocket = serverSocket.accept();
                System.out.println("New connection from " + clientSocket.getRemoteSocketAddress());

                inputStream = clientSocket.getInputStream();
                System.out.println("inputStream = " + new Date());
                outputStream = clientSocket.getOutputStream();
                System.out.println("outputStream = " + new Date());

                String receivedMessage = null;
                String responseMessage = null;

                byte[] buffer = new byte[1024];
                System.out.println("buffer = " + new Date());
                
                int bytesRead = inputStream.read(buffer);
                System.out.println("bytesRead = " + new Date());
                if (bytesRead != -1) {
                    receivedMessage = new String(buffer, 0, bytesRead);
                    System.out.println("receivedMessage = " + new Date());
                    receivedMessage = receivedMessage.replaceAll("^[^\\x20-\\x7E]+", "");
                    System.out.println("receivedMessage after replace= " + new Date());
                    responseMessage = processAnalyzerMessage(receivedMessage);
                    System.out.println("responseMessage= " + new Date());
                }

                System.out.println("responseMessage = " + responseMessage);
                if (responseMessage != null) {
                    writeMessageToStream(outputStream, responseMessage);
                    outputStream.close();
                }

                // Don't forget to close the streams and the client socket when you're done
//                outputStream.close();
//                inputStream.close();
//                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("TCP Server error: " + e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (clientSocket != null) {
                    clientSocket.close();
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                // handle exception
            }
        }
    }

    private String processAnalyzerMessage(String receivedMessage) {
        try {
            System.out.println("receivedMessage = " + receivedMessage);
            String msgType = null;
            msgType = findMessageType(receivedMessage);
            System.out.println("msgType = " + msgType);
            String restApiUrl = PrefsController.getPreference().getUrl() + "api/limsmw/limsProcessAnalyzerMessage";
            String username = PrefsController.getPreference().getUserName();
            String password = PrefsController.getPreference().getPassword();
            try {
                URL url = new URL(restApiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                if (username != null && password != null) {
                    String credentials = username + ":" + password;
                    String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
                    connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
                }
                connection.setDoOutput(true);
                JSONObject requestBodyJson = new JSONObject();
                String base64EncodedMessage = Base64.getEncoder().encodeToString(receivedMessage.getBytes(StandardCharsets.UTF_8));
                requestBodyJson.put("message", base64EncodedMessage);
                OutputStream outputStream = connection.getOutputStream();
                String requestBodyString = requestBodyJson.toString();
                outputStream.write(requestBodyString.getBytes());
                outputStream.flush();
                outputStream.close();
                int responseCode = connection.getResponseCode();
                System.out.println("responseCode = " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try ( BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        StringBuilder responseBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseBuilder.append(line).append("\n");
                        }
                        String response = responseBuilder.toString().trim();
                        JSONObject responseJson = new JSONObject(response);
                        String base64EncodedResultMessage = responseJson.getString("result");
                        byte[] decodedResultMessageBytes = Base64.getDecoder().decode(base64EncodedResultMessage);
                        String decodedResultMessage = new String(decodedResultMessageBytes, StandardCharsets.UTF_8);
                        System.out.println("decodedResultMessage = " + decodedResultMessage);
                        return decodedResultMessage;
                    }
                } else {
                    try ( BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                        StringBuilder responseBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseBuilder.append(line).append("\n");
                        }
                        String response = responseBuilder.toString().trim();
                        System.out.println("response = " + response);
                        return createErrorResponse(response);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return createErrorResponse(e.getMessage());
            }
        } catch (Exception ex) {
            Logger.getLogger(TCPServerCommHandler.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
    }

    public static String findMessageType(String hl7Message) {
        String[] segments = hl7Message.split("\r");
        System.out.println("segments = " + segments);
        String messageType = null;
        for (String segment : segments) {
            System.out.println("segment = " + segment);
            if (segment.startsWith("MSH|")) {
                String[] fields = segment.split("\\|");
                messageType = fields[8];
                break;
            }
        }
        return messageType;
    }

    private String createErrorResponse(String errorMessage) {

        return null;
    }

    private void writeMessageToStream(OutputStream outputStream, String message) throws IOException {
        Charset charset;
        if (analyzer != null) {
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
