package com.example.opsc7312task2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import User.UserSettings;

//-----------------------------------------------------------------------------------------------------------------------
//settings class
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    //variables
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private UserSettings userSettings;
    private static final String SETTINGS_TAG = "SETTINGS";

    //-----------------------------------------------------------------------------------------------------------------------
    //onCreate
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        //getting the main activity
        MainActivity activity = (MainActivity) getActivity();
        //firebase
        user = activity.getUser();
        mDatabase = FirebaseDatabase.getInstance("https://opsc7312-poe-8e798-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        userSettings = new UserSettings();
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //method to push the data to firebase
    public void pushToFirebase() {
        if (userSettings != null) {
            mDatabase.child("UserSettings").child(user.getUid()).setValue(userSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        //success
                    } else{
                        //failure
                    }
                }
            });

        } else {
            Toast.makeText(getActivity(), "User settings null", Toast.LENGTH_SHORT).show();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //method to check which measurement system the user is using
    public boolean checkSystem(String system) {
        if (system.equals("Metric")) {
            return true;
        } else {
            return false;
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //registering the preference change listener for garbage collection
    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    //-----------------------------------------------------------------------------------------------------------------------
    // Unregister the listener
    @Override
    public void onPause() {
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Whenever user settings are changed
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        String measureSystem = sharedPreferences.getString("measureSystem", "Metric");
        String favType = sharedPreferences.getString("favouriteLandmark", "Statues");

        //setting userSettings
        userSettings.setMetric(checkSystem(measureSystem));
        userSettings.setFavLandmarkType(favType);
        pushToFirebase();
    }
    //--
}
//-------------------------------------------------End Of File----------------------------------------------------------