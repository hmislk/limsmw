/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openhealth.limsmw;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author buddh
 */
public class Prefs {

    private static final String URL = "url";
    private static final String PRINTER = "printer";
    private static final String PRINTER_COMMAND_SAMPLE = "printeCommandSample";

    private static String urlValue = "";
    private static String username;
    private static String password;
    private static String departmentName;
    private static Long departmentId;
    private static String institutionName;
    private static Long institutionId;
    private static String usersName;
    private static Long usersId;
    private static boolean login;
    private static String printer;
    private static String printSample;

    private static boolean succes = false;
    private static String message = "";

    static String analyzer1Name = "analyzer1Name";
    static String analyzer1Type = "analyzer1Type";
    static String analyzer1Port = "analyzer1Port";

    static String analyzer2Name = "analyzer2Name";
    static String analyzer2Type = "analyzer2Type";
    static String analyzer2Port = "analyzer2Port";

    static String analyzer3Name = "analyzer3Name";
    static String analyzer3Type = "analyzer3Type";
    static String analyzer3Port = "analyzer3Port";

    static String analyzer4Name = "analyzer4Name";
    static String analyzer4Type = "analyzer4Type";
    static String analyzer4Port = "analyzer4Port";

    static String analyzer5Name = "analyzer5Name";
    static String analyzer5Type = "analyzer5Type";
    static String analyzer5Port = "analyzer5Port";

    static String analyzer6Name = "analyzer6Name";
    static String analyzer6Type = "analyzer6Type";
    static String analyzer6Port = "analyzer6Port";

    static String analyzer1NameValue;
    static String analyzer1TypeValue;
    static String analyzer1PortValue;

    static String analyzer2NameValue;
    static String analyzer2TypeValue;
    static String analyzer2PortValue;

    static String analyzer3NameValue;
    static String analyzer3TypeValue;
    static String analyzer3PortValue;

    static String analyzer4NameValue;
    static String analyzer4TypeValue;
    static String analyzer4PortValue;

    static String analyzer5NameValue;
    static String analyzer5TypeValue;
    static String analyzer5PortValue;

    static String analyzer6NameValue;
    static String analyzer6TypeValue;
    static String analyzer6PortValue;

    public static void loadAnalyzerPrefs() {
        System.out.println("load Analyer prefs");
        Preferences prefs = Preferences.userNodeForPackage(Prefs.class);
        analyzer1NameValue = prefs.get(analyzer1Name, analyzer1NameValue);
        analyzer1TypeValue = prefs.get(analyzer1Type, analyzer1TypeValue);
        analyzer1PortValue = prefs.get(analyzer1Port, analyzer1PortValue);

        analyzer2NameValue = prefs.get(analyzer2Name, analyzer2NameValue);
        analyzer2TypeValue = prefs.get(analyzer2Type, analyzer2TypeValue);
        analyzer2PortValue = prefs.get(analyzer2Port, analyzer2PortValue);

        analyzer3NameValue = prefs.get(analyzer3Name, analyzer3NameValue);
        analyzer3TypeValue = prefs.get(analyzer3Type, analyzer3TypeValue);
        analyzer3PortValue = prefs.get(analyzer3Port, analyzer3PortValue);

        analyzer4NameValue = prefs.get(analyzer4Name, analyzer4NameValue);
        analyzer4TypeValue = prefs.get(analyzer4Type, analyzer4TypeValue);
        analyzer4PortValue = prefs.get(analyzer4Port, analyzer4PortValue);

        analyzer5NameValue = prefs.get(analyzer5Name, analyzer5NameValue);
        analyzer5TypeValue = prefs.get(analyzer5Type, analyzer5TypeValue);
        analyzer5PortValue = prefs.get(analyzer5Port, analyzer5PortValue);

        analyzer6NameValue = prefs.get(analyzer6Name, analyzer6NameValue);
        analyzer6TypeValue = prefs.get(analyzer6Type, analyzer6TypeValue);
        analyzer6PortValue = prefs.get(analyzer6Port, analyzer6PortValue);
    }

    public static void loadPrefs() {
        System.out.println("load prefs");
        Preferences prefs = Preferences.userNodeForPackage(Prefs.class);
        urlValue = prefs.get(URL, urlValue);
        printer = prefs.get(PRINTER, printer);
        printSample = prefs.get(PRINTER_COMMAND_SAMPLE, printSample);
    }

    public static void savePrefs() {
        System.out.println("save prefs");
        Preferences prefs = Preferences.userNodeForPackage(Prefs.class);
        prefs.put(URL, getUrlValue());
    }

    public static void saveAnalyzerPrefs() {
        System.out.println("save Analyzer prefs");
        Preferences prefs = Preferences.userNodeForPackage(Prefs.class);

        prefs.put(analyzer1Name, analyzer1NameValue);
        prefs.put(analyzer1Type, analyzer1TypeValue);
        prefs.put(analyzer1Port, analyzer1PortValue);

        prefs.put(analyzer2Name, analyzer2NameValue);
        prefs.put(analyzer2Type, analyzer2TypeValue);
        prefs.put(analyzer2Port, analyzer2PortValue);

        prefs.put(analyzer3Name, analyzer3NameValue);
        prefs.put(analyzer3Type, analyzer3TypeValue);
        prefs.put(analyzer3Port, analyzer3PortValue);

        prefs.put(analyzer4Name, analyzer4NameValue);
        prefs.put(analyzer4Type, analyzer4TypeValue);
        prefs.put(analyzer4Port, analyzer4PortValue);

        prefs.put(analyzer5Name, analyzer5NameValue);
        prefs.put(analyzer5Type, analyzer5TypeValue);
        prefs.put(analyzer5Port, analyzer5PortValue);

        prefs.put(analyzer6Name, analyzer6NameValue);
        prefs.put(analyzer6Type, analyzer6TypeValue);
        prefs.put(analyzer6Port, analyzer6PortValue);
    }

    public static void getDataFromResponse(String response) {
        String patternStart = "#{";
        String patternEnd = "}";
        String regexString = Pattern.quote(patternStart) + "(.*?)" + Pattern.quote(patternEnd);
        String text = response;
        Pattern p = Pattern.compile(regexString);
        Matcher m = p.matcher(text);
        List<String> strBlocks = new ArrayList<>();
        while (m.find()) {
            String block = m.group(1);
            if (!block.trim().equals("")) {

                if (block.contains("|")) {
                    String[] blockParts = block.split("\\|");
                    for (int i = 0; i < blockParts.length; i++) {
                        System.out.println("blockParts[i] = " + blockParts[i]);
                        String[] parameterValueSet = blockParts[i].split("=");
                        if (parameterValueSet.length == 2) {
                            String para = parameterValueSet[0];
                            String paraVal = parameterValueSet[1];
                            switch (para) {
                                case "Login":
                                    if (paraVal.equals("1")) {
                                        login = true;
                                    } else if (paraVal.equals("0")) {
                                        login = false;
                                        return;
                                    }
                                    break;
                                case "Department":
                                    departmentName = paraVal;
                                    break;
                                case "DepartmentId":
                                    departmentId = stringToLong(paraVal);
                                    break;
                                case "Institution":
                                    institutionName = paraVal;
                                    break;
                                case "InstitutionId":
                                    institutionId = stringToLong(paraVal);
                                    break;
                                case "User":
                                    username = paraVal;
                                    break;
                                case "UserId":
                                    usersId = stringToLong(paraVal);
                                    break;
                            }
                        }
                    }

                }

            }
        }
    }

    public static void getMessageFromResponse(String response) {
//        JOptionPane.showMessageDialog(null, response);
        String patternStart = "#{";
        String patternEnd = "}#";
        int startIndex = response.indexOf(patternStart);
        System.out.println("startIndex = " + startIndex);
        int endIndex = response.indexOf(patternEnd);
        System.out.println("endIndex = " + endIndex);
        String temStr = response.substring(startIndex + patternStart.length(), (endIndex));
//        JOptionPane.showMessageDialog(null, temStr);

        if (temStr.contains("|")) {
            String[] blockParts = temStr.split("\\|");
            for (int i = 0; i < blockParts.length; i++) {
                System.out.println("blockParts[i] = " + blockParts[i]);
                String[] parameterValueSet = blockParts[i].split("=");
                if (parameterValueSet.length == 2) {

                    String para = parameterValueSet[0];
                    String paraVal = parameterValueSet[1];

                    switch (para) {
                        case "Login":
                            if (paraVal.equals("1")) {
                                succes = true;
                            } else if (paraVal.equals("0")) {
                                succes = false;
                                return;
                            }
                            break;
                        case "message":
                            message = paraVal;
                            break;
                    }
                }
            }

        }

    }

    public static void getMessageFromResponseOld(String response) {
        String patternStart = "#{";
        String patternEnd = "}#";
        String regexString = Pattern.quote(patternStart) + "(.*?)" + Pattern.quote(patternEnd);
        String text = response;
        Pattern p = Pattern.compile(regexString);
        Matcher m = p.matcher(text);
        int c = 0;
        while (m.find()) {
            String block = m.group(c);
            c++;
//            JOptionPane.showMessageDialog(null, block);
            if (!block.trim().equals("")) {
                if (block.contains("|")) {
                    String[] blockParts = block.split("\\|");
                    for (int i = 0; i < blockParts.length; i++) {
                        System.out.println("blockParts[i] = " + blockParts[i]);
                        String[] parameterValueSet = blockParts[i].split("=");
                        if (parameterValueSet.length == 2) {

                            String para = parameterValueSet[0];
                            String paraVal = parameterValueSet[1];

//                            JOptionPane.showMessageDialog(null, para);
//                            JOptionPane.showMessageDialog(null, paraVal);
                            switch (para) {
                                case "Login":
                                    if (paraVal.equals("1")) {
                                        succes = true;
                                    } else if (paraVal.equals("0")) {
                                        succes = false;
                                        return;
                                    }
                                    break;
                                case "message":
                                    message = paraVal;
                                    break;
                            }
                        }
                    }

                }

            }
        }
    }

    public static Long stringToLong(String str) {
        try {
            Long n = Long.parseLong(str);
            return n;
        } catch (Exception e) {
            return null;
        }
    }

    public static String executePost(String targetURL, Map<String, Object> parameters) {
        HttpURLConnection connection = null;
        if (parameters != null && !parameters.isEmpty()) {
            targetURL += "?";
        }
        Set s = parameters.entrySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            Map.Entry m = (Map.Entry) it.next();
            Object pVal = m.getValue();
            String pPara = (String) m.getKey();
            targetURL += pPara + "=" + pVal.toString() + "&";
        }
        if (parameters != null && !parameters.isEmpty()) {
            targetURL += "last=true";
        }
        System.out.println("targetURL = " + targetURL);
        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setUseCaches(false);
            connection.setDoOutput(true);
            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
//            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response  
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String getUrlValue() {
        if (urlValue == null) {
            urlValue = "";
        }
        return urlValue;
    }

    public static void setUrlValue(String urlValue) {
        Prefs.urlValue = urlValue;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String aUsername) {
        username = aUsername;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String aPassword) {
        password = aPassword;
    }

    public static String getDepartmentName() {
        return departmentName;
    }

    public static void setDepartmentName(String aDepartmentName) {
        departmentName = aDepartmentName;
    }

    public static Long getDepartmentId() {
        return departmentId;
    }

    public static void setDepartmentId(Long aDepartmentId) {
        departmentId = aDepartmentId;
    }

    public static String getInstitutionName() {
        return institutionName;
    }

    public static void setInstitutionName(String aInstitutionName) {
        institutionName = aInstitutionName;
    }

    public static Long getInstitutionId() {
        return institutionId;
    }

    public static void setInstitutionId(Long aInstitutionId) {
        institutionId = aInstitutionId;
    }

    public static String getUsersName() {
        return usersName;
    }

    public static void setUsersName(String aUsersName) {
        usersName = aUsersName;
    }

    public static Long getUsersId() {
        return usersId;
    }

    public static void setUsersId(Long aUsersId) {
        usersId = aUsersId;
    }

    public static boolean isLogin() {
        return login;
    }

    public static void setLogin(boolean aLogin) {
        login = aLogin;
    }

    public static String getPrinter() {
        if (printer == null) {
            printer = "";
        }
        return printer;
    }

    public static void setPrinter(String aPrinter) {
        printer = aPrinter;
    }

    public static boolean isSucces() {
        return succes;
    }

    public static void setSucces(boolean aSucces) {
        succes = aSucces;
    }

    public static String getMessage() {
        return message;
    }

    public static void setMessage(String aMessage) {
        message = aMessage;
    }

    public static String getPrintSample() {
        return printSample;
    }

    public static void setPrintSample(String aPrintSample) {
        printSample = aPrintSample;
    }

}
