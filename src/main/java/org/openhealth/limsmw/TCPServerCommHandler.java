package org.openhealth.limsmw;

import org.json.JSONObject;
import java.nio.charset.Charset;
import static org.openhealth.limsmw.Analyzer.Encoding.ASCII;
import static org.openhealth.limsmw.Analyzer.Encoding.ISO_8859_1;
import static org.openhealth.limsmw.Analyzer.Encoding.UTF_16;
import static org.openhealth.limsmw.Analyzer.Encoding.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.nio.charset.StandardCharsets;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.*;

public class TCPServerCommHandler implements Runnable, AnalyzerCommHandler {

    private ServerSocket serverSocket;
    Analyzer analyzer;

    private static final Logger LOGGER = Logger.getLogger(TCPServerCommHandler.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("application.log", true);  // will log to 'application.log'
            fileHandler.setFormatter(new SimpleFormatter());  // log in text, not xml
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.INFO);  // log all INFO and higher messages
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize logger", e);
        }
    }

    public TCPServerCommHandler(Analyzer analyzer) {
        this.analyzer = analyzer;
        LOGGER.info("analyzer.getPort() = " + analyzer.getPort());
    }

    @Override
    public void run() {
//        LOGGER.info("going to run " + analyzer.getName() + " on port " + analyzer.getPort() + ".");
        try {
            serverSocket = new ServerSocket(analyzer.getPort());
            LOGGER.info("TCP Server started on port " + analyzer.getPort());
            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = null;
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    clientSocket = serverSocket.accept();
//                    LOGGER.info("New connection from " + clientSocket.getRemoteSocketAddress());
                    clientSocket.setSoTimeout(5000); // 5 seconds timeout

                    inputStream = clientSocket.getInputStream();
//                    LOGGER.info("inputStream = " + new Date());
                    outputStream = clientSocket.getOutputStream();
//                    LOGGER.info("outputStream = " + new Date());
                    outputStream.write('\n');
//                    LOGGER.info("write new line = " + new Date());
                    outputStream.flush();
//                    LOGGER.info("flushed = " + new Date());

                    String receivedMessage = null;
                    String responseMessage = null;

                    byte[] buffer = new byte[1024];
//                    LOGGER.info("buffer = " + new Date());

                    int bytesRead;
                    try {
                        bytesRead = inputStream.read(buffer);
//                        LOGGER.info("Socket timeout: " + new Date());
                    } catch (SocketTimeoutException e) {
//                        LOGGER.info("SocketTimeoutException " + new Date());
                        continue;
                    }
//                    LOGGER.info("bytesRead = " + new Date());
//                    LOGGER.info("bytesRead = " + new Date());
                    if (bytesRead != -1) {
                        receivedMessage = new String(buffer, 0, bytesRead);
                        char firstChar = receivedMessage.charAt(0);
                        int firstCharAsciiValue = (int) firstChar;

//                        if (firstCharAsciiValue < 32) {
//                            String[] controlCharacters = {
//                                "NUL", "SOH", "STX", "ETX", "EOT", "ENQ", "ACK", "BEL",
//                                "BS", "HT", "LF", "VT", "FF", "CR", "SO", "SI",
//                                "DLE", "DC1", "DC2", "DC3", "DC4", "NAK", "SYN", "ETB",
//                                "CAN", "EM", "SUB", "ESC", "FS", "GS", "RS", "US"
//                            };
//                            LOGGER.info("First character: " + controlCharacters[firstCharAsciiValue]);
//                        } else {
//                            LOGGER.info("First character: " + firstChar);
//                        }
                        receivedMessage = receivedMessage.replaceAll("^[^\\x20-\\x7E]+", "");
//                        LOGGER.info("receivedMessage after replace= " + new Date());
                        responseMessage = processAnalyzerMessage(receivedMessage);
//                        LOGGER.info("responseMessage= " + new Date());
                    }

//                    LOGGER.info("responseMessage = " + responseMessage);
                    if (responseMessage != null) {
//                        LOGGER.info("going to writeMessageToStream= " + new Date());
                        writeMessageToStream(outputStream, responseMessage);
//                        LOGGER.info("completed writeMessageToStream= " + new Date());
                    }
                } catch (IOException e) {
                    LOGGER.info("TCP Server error: " + e.getMessage());
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
                    } catch (IOException e) {
                        // handle exception
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.info("TCP Server error: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                // handle exception
            }
        }
    }

    private String processAnalyzerMessage(String receivedMessage) {
        LOGGER.info("Process Analyzer Message");
        try {
            LOGGER.info("receivedMessage = " + receivedMessage);
            String msgType = null;
            msgType = findMessageType(receivedMessage);
            LOGGER.info("Received msgType = " + msgType);
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
                LOGGER.info("base64EncodedMessage = " + base64EncodedMessage);
                OutputStream outputStream = connection.getOutputStream();
                String requestBodyString = requestBodyJson.toString();
                outputStream.write(requestBodyString.getBytes());
                outputStream.flush();
                outputStream.close();
                int responseCode = connection.getResponseCode();
//                LOGGER.info("responseCode = " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
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
                        msgType = findMessageType(decodedResultMessage);
                        LOGGER.info("decodedResultMessage = " + decodedResultMessage);
                        LOGGER.info("Response msgType = " + msgType);
                        return decodedResultMessage;
                    }
                } else {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                        StringBuilder responseBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseBuilder.append(line).append("\n");
                        }
                        String response = responseBuilder.toString().trim();
//                        LOGGER.info("response = " + response);
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
        String messageType = null;
        for (String segment : segments) {
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
                LOGGER.info("Error stopping server: " + e.getMessage());
            }
        }
    }

}
