package User;

//class to store the user settings
public class UserSettings {
    private boolean isMetric;
    private String favLandmarkType;

    //blank constructor for firebase
    public UserSettings(){}

    //gets and sets
    public boolean getIsMetric(){
        return isMetric;
    }

    public String getFavLandmarkType(){
        return favLandmarkType;
    }

    public void setMetric(boolean metric) {
        isMetric = metric;
    }

    public void setFavLandmarkType(String favLandmarkType) {
        this.favLandmarkType = favLandmarkType;
    }
}
