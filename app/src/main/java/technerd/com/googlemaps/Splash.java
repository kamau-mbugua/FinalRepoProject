package technerd.com.googlemaps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {
    /*Duration of Wait*/
    private  final int SPLASH_DISPLAY_LENGTH = 3000;

//    getActionBar().hide();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        getActionBar().hide();
        getSupportActionBar().hide();
        /*New Handler to start the Main Activity and
        * closes this splash
        * after some time*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
