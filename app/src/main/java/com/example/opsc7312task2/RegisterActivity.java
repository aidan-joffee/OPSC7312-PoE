package com.example.opsc7312task2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import User.UserSettings;

//-----------------------------------------------------------------------------------------------------------------------
//The register activity, where the user will register an account
public class RegisterActivity extends AppCompatActivity {

    private EditText EmailTxt, PasswordTxt;
    private Button RegisterBtn;
    //firebase
    private static String TAG = "FIREBASE";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    //-----------------------------------------------------------------------------------------------------------------------
    //onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //firebase references
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://opsc7312-poe-8e798-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        EmailTxt = (EditText) findViewById(R.id.edEmail);
        PasswordTxt = (EditText) findViewById(R.id.edPassword);

        //Button to register user
        RegisterBtn = (Button) findViewById(R.id.btRegister);
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = EmailTxt.getText().toString();
                password = PasswordTxt.getText().toString();
                if (!email.equals("") && (!password.equals(""))) {
                    registerUser(email, password);
                } else {
                    Toast.makeText(RegisterActivity.this, "Make sure all fields contain values.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //---------------------------------------------------------------------------------------------------------------
    //Method to register user to firebase
    protected void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "Account Registered at: " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            //if success, create the default settings
                            createDefaultSettings();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    //---------------------------------------------------------------------------------------------------------------
    //Method to publish the default settings to firebase for the user, which they can change later
    public void createDefaultSettings(){
        //setting the default settings
        UserSettings defaultUser = new UserSettings();
        defaultUser.setMetric(true);
        defaultUser.setFavLandmarkType("Statues");
        //adding to firebase
        mDatabase.child("UserSettings").child(user.getUid()).setValue(defaultUser)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        //success
                    } else {
                        //failure
                    }
                }
            });
    }
    //--
}
//-------------------------------------------------End Of File----------------------------------------------------------