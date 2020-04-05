package technerd.com.googlemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import technerd.com.googlemaps.historyRecyclerViews.HistoryAdapter;
import technerd.com.googlemaps.historyRecyclerViews.HistoryObject;

public class HistoryActivity extends AppCompatActivity {

    private  String customerOdDriver , userId;

    private RecyclerView mHistoryRecyclerView;
    private  RecyclerView.Adapter mHistoryAdapter;
    private  RecyclerView.LayoutManager mHistorylayoutManager;

    private TextView mBalance;
    private  Double balance = 0.0;
    private Button mPayout;
    private EditText mPayoutEmail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mBalance = findViewById(R.id.balance);
        mHistoryRecyclerView = findViewById(R.id.historyRecyclerView);
        mHistoryRecyclerView.setNestedScrollingEnabled(false);
        mHistoryRecyclerView.setHasFixedSize(true);

        mHistorylayoutManager =new LinearLayoutManager(HistoryActivity.this);
        mHistoryRecyclerView.setLayoutManager(mHistorylayoutManager);
        mHistoryAdapter = new HistoryAdapter(getDataSetHistory(), HistoryActivity.this);
        mHistoryRecyclerView.setAdapter(mHistoryAdapter);

        customerOdDriver = getIntent().getExtras().getString("customerOrDriver");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUserHistoryIds();

        if (customerOdDriver.equals("Drivers")){
            mBalance.setVisibility(View.VISIBLE);

        }
        mPayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payoutRequest();
            }
        });




    }

    private void getUserHistoryIds(){
        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(customerOdDriver).child(userId).child("history");
        userHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot history : dataSnapshot.getChildren()){
                        FetchRideInformation(history.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private  void FetchRideInformation(String rideKey){
        DatabaseReference historyDatabase = FirebaseDatabase.getInstance().getReference().child("history").child(rideKey);
        historyDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String rideId = dataSnapshot.getKey();
                    String destination= "";
                    Long timestamp = 0L;
                    String distance = "";
                    Double ridePrice = 0.0;

                    if (dataSnapshot.child("timestamp").getValue()!=null){
                        timestamp = Long.valueOf(dataSnapshot.child("timestamp").getValue().toString());
                    }

                    if (dataSnapshot.child("destination").getValue()!=null){
                        destination = String.valueOf(dataSnapshot.child("destination").getValue().toString());
                    }
                    if (dataSnapshot.child("customerPaid").getValue() != null && dataSnapshot.child("driverPaidOut").getValue()==null){
                        if (dataSnapshot.child("distance").getValue() != null){
                            ridePrice = Double.valueOf(dataSnapshot.child("price").getValue().toString());
                            balance += ridePrice;
                            mBalance.setText("Balance:"+ String.valueOf(balance)+ "KSH");
                        }
                    }
                    HistoryObject obj = new HistoryObject(rideId,getDate(timestamp),destination);
                    resultsHistory.add(obj);
                    mHistoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getDate(Long timestamp){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(timestamp*1000);
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String date = df.format("MM-dd-yyyy hh:mm",cal).toString();

        return date;
    }


    private ArrayList resultsHistory = new ArrayList<HistoryObject>();
    private ArrayList<HistoryObject> getDataSetHistory(){return  resultsHistory;}

    private  void payoutRequest(){

    }




}
