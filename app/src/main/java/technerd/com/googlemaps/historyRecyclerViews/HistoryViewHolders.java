package technerd.com.googlemaps.historyRecyclerViews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import technerd.com.googlemaps.HistorySingleActivity;
import technerd.com.googlemaps.R;

public class HistoryViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView rideId;
    public TextView  destination;
    public TextView time;
     public HistoryViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        rideId = itemView.findViewById(R.id.rideId);
         destination = itemView.findViewById(R.id.destination);
        time= itemView.findViewById(R.id.time);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), HistorySingleActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("rideId", rideId.getText().toString());
        intent.putExtras(bundle);
        view.getContext().startActivity(intent);

    }
}
