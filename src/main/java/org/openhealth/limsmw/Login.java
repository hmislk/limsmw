/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openhealth.limsmw;

import ca.uhn.fhir.context.FhirContext;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.hl7.fhir.r5.model.OperationOutcome;

import ca.uhn.fhir.parser.IParser;
import com.google.gson.Gson;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hl7.fhir.r5.model.CodeableConcept;

/**
 *
 * @author buddh
 */
public class Login extends javax.swing.JFrame {

    /**
     * Creates new form Sampling
     */
    public Login() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        txtUrl = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnLogin = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sampling");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText("Username");

        jLabel2.setText("Password");

        txtUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsernameActionPerformed(evt);
            }
        });

        txtPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordActionPerformed(evt);
            }
        });

        jLabel3.setText("URL");

        jLabel4.setText("Example of a URL is http://35.185.185.235:8080/arogya/");

        btnLogin.setText("Login");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(201, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                    .addComponent(txtUsername))
                .addGap(193, 193, 193))
            .addGroup(layout.createSequentialGroup()
                .addGap(360, 360, 360)
                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(125, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLogin)
                .addGap(134, 134, 134)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(17, 17, 17))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void getSettings() {
        PrefsController.loadPrefs();
        txtUrl.setText(PrefsController.getPreference().getUrl());
    }

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        getSettings();
    }//GEN-LAST:event_formWindowOpened

    private static String getBasicAuthHeaderValue(String username, String password) {
        String credentials = username + ":" + password;
        byte[] encodedBytes = Base64.getEncoder().encode(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes, StandardCharsets.UTF_8);
    }

    private static void sendRequestWithBasicAuth(String apiUrl, String username, String password) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", getBasicAuthHeaderValue(username, password));

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try ( BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } else {
            System.out.println("Request failed. Response code: " + responseCode);
        }
        connection.disconnect();
    }

    public static String ensureTrailingSlashAfterTrim(String input) {
        if (input == null) {
            return "/";
        }

        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty() || trimmedInput.charAt(trimmedInput.length() - 1) == '/') {
            return trimmedInput;
        }
        return trimmedInput + "/";
    }

    public boolean isOperationOutcomeForFailure(String json) {
        FhirContext ctx = FhirContext.forR4();
        OperationOutcome outcome = (OperationOutcome) ctx.newJsonParser().parseResource(json);
        if (outcome.getIssueFirstRep().getSeverity() == OperationOutcome.IssueSeverity.ERROR) {
            return true;
        }
        return false;
    }

    public OperationOutcome createOperationOutcomeForFailure(String details) {
        System.out.println("createOperationOutcomeForFailure");
        // Create a new CodeableConcept to hold the error details
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.setText(details);

        // Create a new OperationOutcomeIssueComponent to represent the failure
        OperationOutcome.OperationOutcomeIssueComponent issue = new OperationOutcome.OperationOutcomeIssueComponent();
        issue.setSeverity(OperationOutcome.IssueSeverity.ERROR);
        issue.setCode(OperationOutcome.IssueType.INVALID);
        issue.setDetails(codeableConcept);

        // Create a new OperationOutcome resource and add the issue component to it
        OperationOutcome outcome = new OperationOutcome();
        outcome.addIssue(issue);

        // Return the new OperationOutcome resource
        return outcome;
    }


    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        if (txtPassword.getText().trim().equals("")) {
            txtPassword.requestFocus();
        } else {
            btnLoginActionPerformed(evt);
        }

    }//GEN-LAST:event_txtUsernameActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        if (txtPassword.getText().trim().equals("")) {
            txtUsername.requestFocus();
        } else {
            btnLoginActionPerformed(evt);
        }
    }//GEN-LAST:event_txtPasswordActionPerformed

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed

        if (txtUrl.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Enter the URL", "Error", JOptionPane.ERROR_MESSAGE);
            txtUrl.requestFocus();
            return;
        }
        if (txtUsername.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Enter the Username", "Error", JOptionPane.ERROR_MESSAGE);
            txtUsername.requestFocus();
            return;
        }
        if (txtPassword.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Enter the Username", "Error", JOptionPane.ERROR_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        String apiUrl = txtUrl.getText().trim();
        apiUrl = ensureTrailingSlashAfterTrim(apiUrl);
        apiUrl += "api/limsmw/login";
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        HttpURLConnection connection = null;
        try {
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", getBasicAuthHeaderValue(username, password));

            LoginRequest lr = new LoginRequest();
            lr.setPassword(password);
            lr.setUsername(username);
            String requestBody = loginRequestToJson(lr);
            connection.setDoOutput(true);
            connection.getOutputStream().write(requestBody.getBytes());

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("HTTP OK");
                try ( BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }
                executeSuccessfulLoginActions();
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
                System.out.println("responseBody = " + responseBody);
                OperationOutcome outcome = parser.parseResource(OperationOutcome.class, responseBody);
                if (outcome != null) {
                    System.out.println("Login failed: " + outcome.getIssueFirstRep().getDetails().getText());
                } else {
                    System.out.println("Login failed. Response code: " + responseCode);
                }
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
    }//GEN-LAST:event_btnLoginActionPerformed

    private void executeSuccessfulLoginActions() {
        PrefsController.getPreference().setUserName(txtUsername.getText());
        PrefsController.getPreference().setPassword(txtPassword.getText());
        PrefsController.getPreference().setUrl(txtUrl.getText().trim());
        PrefsController.savePrefs();
        Main interfacing = new Main();
        interfacing.setVisible(true);
        this.dispose();
    }

    private static String loginRequestToJson(LoginRequest loginRequest) {
        // Initialize the Gson object
        Gson gson = new Gson();

        // Convert the Preference object to JSON
        String json = gson.toJson(loginRequest);

        return json;
    }

    public String sendRestfulRequest(String url) {
        String output = "";
        try {

            Client client = Client.create();

            WebResource webResource = client
                    .resource(url);

            ClientResponse response = webResource.accept("application/json")
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            output = response.getEntity(String.class);

            return output;

        } catch (Exception e) {

            e.printStackTrace();

        }
        return "";
    }

    public boolean parseJsonAndReportLoginStatus(String json) {
        try {
            JSONObject userObject = new JSONObject(json);
            String result = userObject.getString("result");
            if (result.equals("error")) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            return false;
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUrl;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
