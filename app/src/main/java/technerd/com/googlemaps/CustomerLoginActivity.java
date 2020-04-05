
package technerd.com.googlemaps;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CustomerLoginActivity extends AppCompatActivity {
        private FirebaseAuth mAuth;
        private  FirebaseAuth.AuthStateListener firebaseAuthListener;

        private EditText mEmailCustomer, mPasswordCustomer;
        private TextView mCustomerRegister, mForgetPassword;
        private Button btnCustomerLogin;
        ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Customer Login");
        getSupportActionBar().setSubtitle("Kindly Input Correct Details");

        mAuth =FirebaseAuth.getInstance();

        mEmailCustomer = findViewById(R.id.etEmail);
        mPasswordCustomer = findViewById(R.id.etPassword);

        mCustomerRegister = findViewById(R.id.tvCustomerRegister);
        mForgetPassword = findViewById(R.id.tvForgetPassword);

        btnCustomerLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null){
                    Intent intent = new Intent(CustomerLoginActivity.this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmailCustomer.getText().toString().trim();
                final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }
               /* else if (email.matches(emailPattern)){

                    mEmailCustomer.setError("Invalid Email Address");
                }*/

                progressBar.setVisibility(View.VISIBLE);

                mAuth.sendPasswordResetEmail(email)

                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText(CustomerLoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_LONG).show();
                                }
                            /*    else if (email.matches(emailPattern)){

                                    mEmailCustomer.setError("Invalid Email Address");
                                }*/ else {
                                    if (email.isEmpty()){
//                                    Toast.makeText(CustomerLoginActivity.this, "Inpu", Toast.LENGTH_SHORT).show();
                                    }
                                    /*else if (email.matches(emailPattern)){

                                        mEmailCustomer.setError("Invalid Email Address");
                                    }*/
                                    else {
                                        Toast.makeText(getApplicationContext(),"Email not Registered",Toast.LENGTH_LONG).show();
                                    }
                                }

                                progressBar.setVisibility(View.GONE);
                            }
                        });

//                requestResetPassword();

            }
        });

        btnCustomerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mEmailCustomer.getText().toString().isEmpty() && mPasswordCustomer.getText().toString().isEmpty()){
                    Toast.makeText(CustomerLoginActivity.this, "Email and Password Required", Toast.LENGTH_SHORT).show();
                }
                else if (mEmailCustomer.getText().toString().isEmpty()){
                    Toast.makeText(CustomerLoginActivity.this, "Enter Your Registered Email", Toast.LENGTH_SHORT).show();

                }
                else if (mPasswordCustomer .getText().toString().isEmpty()){
                    Toast.makeText(CustomerLoginActivity.this, "Enter Your Password", Toast.LENGTH_SHORT).show();

                }
                else {
                    final  String email = mEmailCustomer.getText().toString().trim();
                    final  String password = mPasswordCustomer.getText().toString().trim();
                    final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                   /* mEmailCustomer.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (email.matches(emailPattern) && editable.length()>0){

                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Email incompatible",Toast.LENGTH_LONG).show();
                            }

                        }
                    });*/
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(CustomerLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){

                                if (password.length()<8){
                                    mPasswordCustomer.setError("Password not Fit, At least 8 Characters Long");
                                }
                              else if (!email.matches(emailPattern)){
                                  mEmailCustomer.setError("Email Format Does not Exist");
                                }
                                else {
                                    Toast.makeText(CustomerLoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                               }
                            else {
                                String user_id = mAuth.getCurrentUser().getProviderId();
                                DatabaseReference current_user_db= FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(user_id);
                                current_user_db.setValue(true);
                            }
                        }
                    });
                }



                /*Intent customerLogin = new Intent(CustomerLoginActivity.this, MapsActivity.class);
                startActivity(customerLogin);
                finish();
                return;*/

            }
        });

        mCustomerRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent customerLogin = new Intent(CustomerLoginActivity.this, SignUpCustomer.class);
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

    /*public void requestResetPassword(){

    }*/

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
