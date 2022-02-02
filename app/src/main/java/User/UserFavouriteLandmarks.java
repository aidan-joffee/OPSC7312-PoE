package User;

//class to store the favourite landmarks of the user
public class UserFavouriteLandmarks {
    private String title;
    private double latitude;
    private double longitude;

    public UserFavouriteLandmarks(){}

    public UserFavouriteLandmarks(String title, double latitude, double longitude){

        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //gets
    public String getTitle(){
        return title;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
