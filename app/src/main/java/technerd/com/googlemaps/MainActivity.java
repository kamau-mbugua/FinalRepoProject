package technerd.com.googlemaps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class MainActivity extends AppCompatActivity {
    Button bCustomer, bRider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("HOME");
//        getSupportActionBar().setSubtitle("");

        bCustomer = findViewById(R.id.btnCustomer);
        bRider = findViewById(R.id.btnRider);

        bCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intCustomer = new Intent(MainActivity.this, CustomerLoginActivity.class);
                startActivity(intCustomer);
                finish();
                return;

            }
        });

        bRider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intRider = new Intent(MainActivity.this, DriverLoginActivity.class);
                startActivity(intRider);
                finish();
                return;

            }
        });
    }
}

/*

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this,MapsActivity.class));
    }
}
*/
