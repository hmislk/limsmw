package org.openhealth.limsmw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.OperationOutcome;

public class RestClient {

    private String baseUrl;
    private String authToken;
    private String username;
    private String password;

    public RestClient() {
    }

    public RestClient(String username, String password) {
        this.username = username;
        this.password = password;
    }

    
    
    public RestClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setBasicAuthCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String sendRequestToRestServer(String apiUrl, String requestBody) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            if (username != null && password != null) {
                String credentials = username + ":" + password;
                String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
                connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
            }

            connection.setDoOutput(true);
            connection.getOutputStream().write(requestBody.getBytes());

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try ( BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                }
            } else {
                // Check for OperationOutcome resource in response body
                String responseBody = null;
                try ( BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    responseBody = sb.toString();
                }
                IParser parser = FhirContext.forR4().newJsonParser();
                OperationOutcome outcome = parser.parseResource(OperationOutcome.class, responseBody);
                if (outcome != null) {
                    System.out.println("Operation failed: " + outcome.getIssueFirstRep().getDetails().getText());
                } else {
                    System.out.println("Operation failed. Response code: " + responseCode);
                }
                return null;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
