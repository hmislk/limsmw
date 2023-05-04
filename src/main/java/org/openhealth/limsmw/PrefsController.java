/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openhealth.limsmw;

import com.google.gson.Gson;
import java.util.prefs.*;

/**
 *
 * @author buddh
 */
public class PrefsController {

    private static String prefJsonValue;
    private static final String PREF_JSON_NAME = "prefJsonName";
    private static Preference preference;

    public static void loadPrefs() {
        System.out.println("loadPrefs");
        // Get the user preferences node for this application
        Preferences prefs = Preferences.userNodeForPackage(PrefsController.class);
        System.out.println("prefs = " + prefs);
        // Get the value of the preference
        
        prefJsonValue = prefs.get(PREF_JSON_NAME, null);
        
        if(prefJsonValue==null){
            Preference p = new Preference();
            p.setApiKey("");
            p.setUrl("");
            prefJsonValue = preferenceToJson(p);
        }
        
        System.out.println("prefJsonValue = " + prefJsonValue);
        preference = jsonToPreference(prefJsonValue);
        System.out.println("preference = " + preference);
    }

    public static void savePrefs() {
        // Get the user preferences node for this application
        Preferences prefs = Preferences.userNodeForPackage(PrefsController.class);
        prefJsonValue = preferenceToJson(preference);
        System.out.println("prefJsonValue = " + prefJsonValue);
        prefs.put(PREF_JSON_NAME, prefJsonValue);
    }

    private static String preferenceToJson(Preference preference) {
        // Initialize the Gson object
        Gson gson = new Gson();

        // Convert the Preference object to JSON
        String json = gson.toJson(preference);

        return json;
    }

    private static Preference jsonToPreference(String json) {
        // Initialize the Gson object
        Gson gson = new Gson();
        return gson.fromJson(json, Preference.class);
    }

    public static Preference getPreference() {
        if (preference == null) {
            loadPrefs();
            preference = jsonToPreference(prefJsonValue);
        }
        return preference;
    }

    public static void setPreference(Preference pref) {
        if (pref != null) {
            prefJsonValue = preferenceToJson(pref);
        }
        preference = pref;
    }

}
