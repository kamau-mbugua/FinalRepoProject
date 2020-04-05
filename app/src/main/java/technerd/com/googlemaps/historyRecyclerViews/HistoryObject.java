package technerd.com.googlemaps.historyRecyclerViews;

public class HistoryObject {
    private  String  rideId;
    private String destination;
    private String time;

    public HistoryObject(String rideId, String destination, String time) {
        this.rideId = rideId;
        this.destination = destination;
        this.time = time;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
