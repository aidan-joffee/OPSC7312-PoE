package com.example.opsc7312task2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//-----------------------------------------------------------------------------------------------------------------------
//The login activity, where the user will gain access to the rest of the application
public class LoginActivity extends AppCompatActivity {

    TextView RegisterTxt;
    EditText Email, Password;
    Button LoginBtn;
    private FirebaseAuth mAuth;
    private static String TAG = "FIREBASE";


    //-----------------------------------------------------------------------------------------------------------------------
    //onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        //layout elements
        Email = (EditText) findViewById(R.id.edEmail);
        Password = (EditText) findViewById(R.id.edPassword);

        //-------------------------------------------------------------
        //register button
        RegisterTxt = (TextView) findViewById(R.id.tvRegister);
        //onclick
        RegisterTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        //-------------------------------------------------------------
        //login button
        LoginBtn = (Button) findViewById(R.id.LoginBtn);
        //onclick
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = Email.getText().toString();
                password = Password.getText().toString();
                if(!email.equals("")&&(!password.equals("")))
                {
                    loginUser(email, password);
                }
                else{
                    Toast.makeText(LoginActivity.this, "Make sure all fields contain values.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    //---------------------------------------------------------------------------------------------------------------
    ///Method to log the user in
    protected void loginUser(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            //move to main activity
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            //start the activity
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Invalid Credentials",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    //---------------------------------------------------------------------------------------------------------------
    //onStart to remove password, when logged out the email field still stay, password will be blank
    @Override
    protected void onStart() {
        super.onStart();
        Password.setText("");
    }
}
//-------------------------------------------------End Of File----------------------------------------------------------