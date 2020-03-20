package technerd.com.googlemaps;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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



       /* rDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance() ;
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(Register.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        rDoB.setText( i2 + "/"+ (i1+1)+"/"+i);
                    }
                },mYear,mMonth,mDay);
                datePickerDialog.setTitle("Select Date of Birth (DoB)");
                datePickerDialog.show();


            }
        });*/

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final  String email = rMail.getText().toString().trim();
                final String password = rPassword.getText().toString().trim();
                /*final String fName = etFname.getText().toString().trim();
                final String lName = etlName.getText().toString().trim();
                final String rcPassword = rCPassword.getText().toString().trim();*/
                final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


                if (rMail.getText().toString().isEmpty()) {
                    Toast.makeText(Register.this, "Enter your email", Toast.LENGTH_SHORT).show();

                }
               /* else if (etFname.getText().toString().isEmpty()) {
                    Toast.makeText(Register.this, "Enter your First Name", Toast.LENGTH_SHORT).show();

                }
                else if (etlName.getText().toString().isEmpty())  {
                    Toast.makeText(Register.this, "Enter your Last Name", Toast.LENGTH_SHORT).show();

                }
                else if (rCPassword.getText().toString().isEmpty())  {
                    Toast.makeText(Register.this, "Reconfirm Your Password", Toast.LENGTH_SHORT).show();

                }*/
                else if (rPassword.getText().toString().isEmpty())  {
                    Toast.makeText(Register.this, "Enter your Password", Toast.LENGTH_SHORT).show();

                }
               /* else if (rPhone.getText().toString().isEmpty())  {
                    Toast.makeText(Register.this, "Enter your phone", Toast.LENGTH_SHORT).show();

                }
                else if (rDoB.getText().toString().isEmpty())  {
                    Toast.makeText(Register.this, "Pick Your Date of Birth", Toast.LENGTH_SHORT).show();

                }*/

               /* rPassword.getText().toString().equals(rCPassword);*/
                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email,password/*,fName,lName,rcPassword*/).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            if (password.length()<8){
                                rPassword.setError("Password not Fit");
                            }
                            else if (!email.matches(emailPattern)){
                                rMail.setError("Invalid Email");

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
