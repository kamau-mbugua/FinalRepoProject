package technerd.com.googlemaps;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;


public class Register extends AppCompatActivity {

  private   EditText /*etFname, etlName,*/ rMail, rPassword /*rCPassword, rDoB, rPhone*/;

   private  Button btnRegister;

   private  TextView tvLogin;
   private  DatePickerDialog datePickerDialog;

   private  FirebaseAuth mAuth;
   private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener firebaseAuthlistener;

    CheckBox terms;
    TextView termsNconditions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setSubtitle("Kindly Input Correct Details");

        mAuth = FirebaseAuth.getInstance();


        progressBar= findViewById(R.id.progressBar);
//        etFname = findViewById(R.id.etNameRegister) ;
//        etlName = findViewById(R.id.etName2Register);
        rMail = findViewById(R.id.etEmailRegister);
        rPassword = findViewById(R.id.etPasswordRegister);
//        rCPassword = findViewById(R.id.etConfirmPasswordRegister);
//        rDoB = findViewById(R.id.etDOBRegister);
//        rPhone = findViewById(R.id.etPhoneRegister);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvAlreadyRegisterd);
        terms = findViewById(R.id.checkboxTermsandConditions);
        termsNconditions = findViewById(R.id.termsNcodition);

        termsNconditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(Register.this)
                        .setTitle("Terms and Conditions")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                terms.setChecked(true);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(Register.this, "KINDLY ACCEPT TERMS AND CONDITIONS", Toast.LENGTH_SHORT).show();
                                terms.setChecked(false);
                            }
                        }).setMessage("Available Soon")
                        .show();

            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  final  String email = rMail.getText().toString().trim();
                final String password = rPassword.getText().toString().trim();
                final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";*/





                if (rMail.getText().toString().isEmpty() && rPassword.getText().toString().isEmpty()){

                    Toast.makeText(Register.this, "Email and Password Required to Proceed with Registration of iBodaa", Toast.LENGTH_LONG).show();

                }


               else if (rMail.getText().toString().isEmpty()) {
                    Toast.makeText(Register.this, "Enter your email", Toast.LENGTH_SHORT).show();

                }

                else if (rPassword.getText().toString().isEmpty())  {
                    Toast.makeText(Register.this, "Enter your Password", Toast.LENGTH_SHORT).show();

                }
                else {
                    final  String email = rMail.getText().toString().trim();
                    final String password = rPassword.getText().toString().trim();
                    final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                    mAuth.createUserWithEmailAndPassword(email,password/*,fName,lName,rcPassword*/).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                if (email.matches(emailPattern)){
                                    rMail.setError("Invalid Email!");
                                }
                                else if (password.length()>8){
                                    rPassword.setError("The password is Short");

                                }
                                else{
                                    Toast.makeText(Register.this, "Register Failed,Try Again!", Toast.LENGTH_SHORT).show();
                                }

                            }
                            else {
                                Intent registerActivity = new Intent(Register.this, MainActivity.class);
                                startActivity(registerActivity);
                                finish();
                                return;

                            }

                            progressBar.setVisibility(View.GONE);
                        }
                    });



                }





            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(Register.this, DriverLoginActivity.class);
                startActivity(registerActivity);
                finish();
                return;

            }
        });

    }

    public  void onCheckBoxClick(View view){
        //Is the View now Checked
        boolean checked = ((CheckBox)view).isChecked();
        switch (view.getId()){
            case R.id.checkboxTermsandConditions:
                if (checked){
                    Toast.makeText(this, "ACCEPTED", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "YOU SHOULD ACCEPT TERMS AND CONDITIONS", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
    /*@Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }*/
}

/*
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
}
*/
