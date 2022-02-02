package com.example.opsc7312task2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import User.UserFavouriteLandmarks;
import User.UserSettings;

//-----------------------------------------------------------------------------------------------------------------------
//The google map activity
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout Drawer;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;

    private FirebaseUser user;
    private FirebaseDatabase database;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //-----------------------------------------------------------------------------------------------------------------------
    //onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //navigation drawer
        Drawer = findViewById(R.id.drawer_layout);

        //navigation view
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //retrieveing settings from firebase
        getSettings();
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //To navigate between fragments from the navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //home frag
            case R.id.homeMenu:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new MapsFragment()).commit();
                break;
            //settings frag
            case R.id.settingsMenu:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new SettingsFragment()).commit();
                break;
            //favourite landmarks frag
            case R.id.favouriteMenu:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new FavouriteLandmarkFragment()).commit();
                break;
            //logging out
            case R.id.logoutMenu:
                finish();
                break;
        }
        Drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //-----------------------------------------------------------------------------------------------------------------------
    //Method to pass the logged in user details to the fragment
    public FirebaseUser getUser() {
        return user;
    }


    //---------------------------------------------------------------------------------------------------------------
    //onClick method to get directions used by the listView
    public void getFavDirections(View view) {
        UserFavouriteLandmarks directionLandmark = (UserFavouriteLandmarks) view.getTag();
        //navigation to the maps fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                new MapsFragment(directionLandmark)).commit();
        navigationView.setCheckedItem(R.id.homeMenu);
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Method to load the settings from firebase
    public void getSettings() {
        //reference to that users userID
        database = FirebaseDatabase.getInstance("https://opsc7312-poe-8e798-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference mDatabase = database.getReference("UserSettings/" + user.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //gets the user settings
                UserSettings user = snapshot.getValue(UserSettings.class);

                //loading the retrieve settings
                loadSettings(user);
                openMapFrag();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Method to load the settings to preferences
    public void loadSettings(UserSettings user) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        String measureSystem;
        if (user.getIsMetric()) {
            measureSystem = "Metric";
        } else {
            measureSystem = "Imperial";
        }
        editor.putString("measureSystem", measureSystem);
        editor.putString("favouriteLandmark", user.getFavLandmarkType());
        //applying the changes
        editor.apply();
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //opening the map fragment first when the user is logged in
    public void openMapFrag() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new MapsFragment()).commit();
        navigationView.setCheckedItem(R.id.homeMenu);
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //onBackPressed, will close navigation drawer before it closes the activity
    @Override
    public void onBackPressed() {
        if (Drawer.isDrawerOpen(GravityCompat.START)) {
            Drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
//-------------------------------------------------End Of File----------------------------------------------------------