/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.openhealth.limsmw;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author buddh
 */

public class Preference {

    private List<Analyzer> analyzers;
    private String apiKey;
    private String url;
    private String institution;
    private String department;
    private String userName;
    private String password;

    public Preference() {
        analyzers = new ArrayList<>();
    }

    public List<Analyzer> getAnalyzers() {
        if(analyzers==null){
            analyzers = new ArrayList<>();
        }
        return analyzers;
    }

    public void setAnalyzers(List<Analyzer> analyzers) {
        this.analyzers = analyzers;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
    
}

