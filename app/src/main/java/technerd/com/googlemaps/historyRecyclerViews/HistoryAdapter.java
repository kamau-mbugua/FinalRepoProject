package technerd.com.googlemaps.historyRecyclerViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import technerd.com.googlemaps.R;
import technerd.com.googlemaps.historyRecyclerViews.HistoryObject;
import technerd.com.googlemaps.historyRecyclerViews.HistoryViewHolders;

public class HistoryAdapter extends RecyclerView.Adapter<technerd.com.googlemaps.historyRecyclerViews.HistoryViewHolders> {

    private List<technerd.com.googlemaps.historyRecyclerViews.HistoryObject> itemList;
    private Context context;

    public HistoryAdapter(List<HistoryObject> itemList, Context context)
    {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public technerd.com.googlemaps.historyRecyclerViews.HistoryViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.
                LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        technerd.com.googlemaps.historyRecyclerViews.HistoryViewHolders rcv = new technerd.com.googlemaps.historyRecyclerViews.HistoryViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(HistoryViewHolders holder, int position) {

        holder.rideId.setText(itemList.get(position).getRideId());

        if(itemList.get(position).getTime()!=null){
            holder.time.setText(itemList.get(position).getTime());
        }
        if(itemList.get(position).getDestination()!=null){
            holder.destination.setText(itemList.get(position).getDestination());
        }
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}
