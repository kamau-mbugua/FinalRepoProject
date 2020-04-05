

package technerd.com.googlemaps;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class DriverLoginActivity extends AppCompatActivity {
    EditText mEmailRider, mPasswordRider;
    TextView mRiderRegister, mForgetPassword;
    Button btnRiderLogin;
    ProgressBar mProgress;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Rider Login");
        getSupportActionBar().setSubtitle("Kindly Input Correct Details");

        mAuth=FirebaseAuth.getInstance();

        mEmailRider = findViewById(R.id.etEmailRider);
        mProgress = findViewById(R.id.progressBar);
        mPasswordRider = findViewById(R.id.etPasswordRider);

        mRiderRegister = findViewById(R.id.tvRiderRegister);
        mForgetPassword = findViewById(R.id.tvForgetPassword);

        btnRiderLogin = findViewById(R.id.btnLoginRider);

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null){
                    Intent intent = new Intent(getApplicationContext(), DriverMapActivity.class);
                    startActivity( intent);
                    finish();
                    return;
                }
            }
        };

        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmailRider.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(DriverLoginActivity.this, "Enter Your Registered Email to Reset the Password", Toast.LENGTH_LONG).show();
                }
                mProgress.setVisibility(View.VISIBLE);

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(DriverLoginActivity.this, "Password Reset Successfully, Check Your Email!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(DriverLoginActivity.this, "Failed to Reset Password, Try Again!", Toast.LENGTH_SHORT).show();
                        }
                        mProgress.setVisibility(View.GONE);
                    }
                });


            }
        });

        btnRiderLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmailRider.getText().toString().isEmpty() && mPasswordRider.getText().toString().isEmpty()){
                    Toast.makeText(DriverLoginActivity.this, "Email and Password Required", Toast.LENGTH_SHORT).show();
                }
                else if (mEmailRider.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter Your Registered Email", Toast.LENGTH_SHORT).show();

                }
                else if (mPasswordRider .getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter Your Password", Toast.LENGTH_SHORT).show();

                }

                else {
                    final String email = mEmailRider.getText().toString().trim();
                    final String password =mPasswordRider.getText().toString().trim();
                    final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                       if (!task.isSuccessful()){
                         //  Toast.makeText(DriverLoginActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
                       if (email.matches(emailPattern)){
                           mEmailRider.setError("Invalid Email!");
                       }
                       else if (password.length()>8){
                           mPasswordRider.setError("The password is Short");

                       }
                       else {
                           Toast.makeText(DriverLoginActivity.this, "Login Failed, Try Again!!", Toast.LENGTH_SHORT).show();
                       }

                       }
                       else {
                           Intent customerLogin = new Intent(DriverLoginActivity.this, DriverMapActivity.class);
                           startActivity(customerLogin);
                           finish();
                           return;

                       }
                            mProgress.setVisibility(View.GONE);
                        }
                    });


                }

            }
        });
        mRiderRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent customerLogin = new Intent(DriverLoginActivity.this, Register.class);
                startActivity(customerLogin);
                finish();
                return;


            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}





/*
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class DriverLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);
    }
}
*/
