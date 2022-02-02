package Places.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlacesResult {

    @SerializedName("html_attributions")
    private List<String> html_attributions;

    @SerializedName("results")
    private List<Results> results;

    @SerializedName("status")
    private String status;

    public void setHtml_attributions(List<String> html_attributions){
        this.html_attributions = html_attributions;
    }
    public List<String> getHtml_attributions(){
        return this.html_attributions;
    }
    public void setResults(List<Results> results){
        this.results = results;
    }
    public List<Results> getResults(){
        return this.results;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }

    public class Location
    {
        @SerializedName("lat")
        private double lat;

        @SerializedName("lng")
        private double lng;

        public void setLat(double lat){
            this.lat = lat;
        }
        public double getLat(){
            return this.lat;
        }
        public void setLng(double lng){
            this.lng = lng;
        }
        public double getLng(){
            return this.lng;
        }
    }


    public class Geometry
    {
        @SerializedName("location")
        private Location location;

        public void setLocation(Location location){
            this.location = location;
        }
        public Location getLocation(){
            return this.location;
        }

    }

    public class Photos
    {
        private int height;

        private String photo_reference;

        private int width;

        public void setHeight(int height){
            this.height = height;
        }
        public int getHeight(){
            return this.height;
        }
        public void setPhoto_reference(String photo_reference){
            this.photo_reference = photo_reference;
        }
        public String getPhoto_reference(){
            return this.photo_reference;
        }
        public void setWidth(int width){
            this.width = width;
        }
        public int getWidth(){
            return this.width;
        }
    }

    public class Plus_code
    {
        private String compound_code;

        private String global_code;

        public void setCompound_code(String compound_code){
            this.compound_code = compound_code;
        }
        public String getCompound_code(){
            return this.compound_code;
        }
        public void setGlobal_code(String global_code){
            this.global_code = global_code;
        }
        public String getGlobal_code(){
            return this.global_code;
        }
    }


    public class Results
    {
        private String business_status;

        @SerializedName("geometry")
        private Geometry geometry;

        private String icon;

        @SerializedName("name")
        private String name;

        private String place_id;

        @SerializedName("photos")
        private List<Photos> photos;

        private Plus_code plus_code;

        @SerializedName("reference")
        private String reference;

        private String scope;

        private List<String> types;

        @SerializedName("vicinity")
        private String vicinity;

        public void setBusiness_status(String business_status){
            this.business_status = business_status;
        }
        public String getBusiness_status(){
            return this.business_status;
        }
        public void setGeometry(Geometry geometry){
            this.geometry = geometry;
        }
        public Geometry getGeometry(){
            return this.geometry;
        }
        public void setIcon(String icon){
            this.icon = icon;
        }
        public String getIcon(){
            return this.icon;
        }
        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
        public void setPlace_id(String place_id){
            this.place_id = place_id;
        }
        public String getPlace_id(){
            return this.place_id;
        }
        public void setPlus_code(Plus_code plus_code){
            this.plus_code = plus_code;
        }
        public Plus_code getPlus_code(){
            return this.plus_code;
        }
        public void setReference(String reference){
            this.reference = reference;
        }
        public String getReference(){
            return this.reference;
        }
        public void setScope(String scope){
            this.scope = scope;
        }
        public String getScope(){
            return this.scope;
        }
        public void setTypes(List<String> types){
            this.types = types;
        }
        public List<String> getTypes(){
            return this.types;
        }
        public void setVicinity(String vicinity){
            this.vicinity = vicinity;
        }
        public String getVicinity(){
            return this.vicinity;
        }
        public List<Photos> getPhotos(){ return this.photos;}
        public void setPhotos(List<Photos> photos){this.photos = photos;}
    }
}
