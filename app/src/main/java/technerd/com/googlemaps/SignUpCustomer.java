package technerd.com.googlemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpCustomer extends AppCompatActivity {
    private EditText rMail, rPassword ;

    private Button btnRegister;

    private TextView tvLogin;
    private DatePickerDialog datePickerDialog;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener firebaseAuthlistener;

    CheckBox terms;
    TextView termsNconditions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_customer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Customer Register");
        getSupportActionBar().setSubtitle("Kindly Input Correct Details");

        mAuth = FirebaseAuth.getInstance();


        progressBar= findViewById(R.id.progressBar);
//        etFname = findViewById(R.id.etNameRegister) ;
//        etlName = findViewById(R.id.etName2Register);
        rMail = findViewById(R.id.etCEmailRegister);
        rPassword = findViewById(R.id.etCPasswordRegister);
//        rCPassword = findViewById(R.id.etConfirmPasswordRegister);
//        rDoB = findViewById(R.id.etDOBRegister);
//        rPhone = findViewById(R.id.etPhoneRegister);
        btnRegister = findViewById(R.id.btnCRegister);
        tvLogin = findViewById(R.id.tvCAlreadyRegisterd);

        terms = findViewById(R.id.checkboxTermsandConditions);
        termsNconditions = findViewById(R.id.termsNcodition);

        termsNconditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SignUpCustomer.this)
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
                                Toast.makeText(SignUpCustomer.this, "KINDLY ACCEPT TERMS AND CONDITIONS", Toast.LENGTH_SHORT).show();
                                terms.setChecked(false);
                            }
                        }).setMessage("Available Soon")
                        .show();

            }
        });



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


                /*final String fName = etFname.getText().toString().trim();
                final String lName = etlName.getText().toString().trim();
                final String rcPassword = rCPassword.getText().toString().trim();*/



                if (rMail.getText().toString().isEmpty() && rPassword.getText().toString().isEmpty()){

                    Toast.makeText(SignUpCustomer.this, "Email and Password Required to Proceed with Registration of iBodaa", Toast.LENGTH_LONG).show();

                }


                else if (rMail.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpCustomer.this, "Enter your email", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(SignUpCustomer.this, "Enter your Password", Toast.LENGTH_SHORT).show();

                }


               /* else if (rPhone.getText().toString().isEmpty())  {
                    Toast.makeText(Register.this, "Enter your phone", Toast.LENGTH_SHORT).show();

                }
                else if (rDoB.getText().toString().isEmpty())  {
                    Toast.makeText(Register.this, "Pick Your Date of Birth", Toast.LENGTH_SHORT).show();

                }*/

                /* rPassword.getText().toString().equals(rCPassword);*/
              //  progressBar.setVisibility(View.VISIBLE);

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
                                    Toast.makeText(SignUpCustomer.this, "Register Failed,Try Again!", Toast.LENGTH_SHORT).show();
                                }

                            }
                            else {
                                Intent registerActivity = new Intent(SignUpCustomer.this, MainActivity.class);
                                startActivity(registerActivity);
                                finish();
                                return;

                            }

                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

          /*      mAuth.createUserWithEmailAndPassword(email,password*//*,fName,lName,rcPassword*//*).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        *//*if (rMail.getText().toString().isEmpty() && rPassword.getText().toString().isEmpty()){

                            Toast.makeText(SignUpCustomer.this, "Email and Password Required to Proceed with Registration of iBodaa", Toast.LENGTH_LONG).show();

                        }
                        else if (rMail.getText().toString().isEmpty()) {
                            Toast.makeText(SignUpCustomer.this, "Enter your email", Toast.LENGTH_SHORT).show();

                        }
                        else if (rPassword.getText().toString().isEmpty())  {
                            Toast.makeText(SignUpCustomer.this, "Enter your Password", Toast.LENGTH_SHORT).show();

                        }
                         else *//*if (!task.isSuccessful()){
                            if (password.length()<8){
                                rPassword.setError("Password not Fit");
                            }
                            else if (!email.matches(emailPattern)){
                                rMail.setError("Invalid Email");

                            }
                            else{
                                Toast.makeText(SignUpCustomer.this, "Register Failed,Try Again!", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else {
                            Intent registerActivity = new Intent(SignUpCustomer.this, MainActivity.class);
                            startActivity(registerActivity);
                            finish();
                            return;

                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });
*/


            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(SignUpCustomer.this, CustomerLoginActivity.class);
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
}
