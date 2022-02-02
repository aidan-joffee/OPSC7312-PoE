package Places.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResult {

    private List<Candidates> candidates;

    private String status;

    public void setCandidates(List<Candidates> candidates){
        this.candidates = candidates;
    }
    public List<Candidates> getCandidates(){
        return this.candidates;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }

    public class Location
    {
        private double lat;

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


    public class Northeast
    {
        private double lat;

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


    public class Southwest
    {
        private double lat;

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


    public class Viewport
    {
        private Northeast northeast;

        private Southwest southwest;

        public void setNortheast(Northeast northeast){
            this.northeast = northeast;
        }
        public Northeast getNortheast(){
            return this.northeast;
        }
        public void setSouthwest(Southwest southwest){
            this.southwest = southwest;
        }
        public Southwest getSouthwest(){
            return this.southwest;
        }
    }


    public class Geometry
    {
        private Location location;

        private Viewport viewport;

        public void setLocation(Location location){
            this.location = location;
        }
        public Location getLocation(){
            return this.location;
        }
        public void setViewport(Viewport viewport){
            this.viewport = viewport;
        }
        public Viewport getViewport(){
            return this.viewport;
        }
    }


    public class Candidates
    {
        private String formatted_address;

        private Geometry geometry;

        private String name;

        public void setFormatted_address(String formatted_address){
            this.formatted_address = formatted_address;
        }
        public String getFormatted_address(){
            return this.formatted_address;
        }
        public void setGeometry(Geometry geometry){
            this.geometry = geometry;
        }
        public Geometry getGeometry(){
            return this.geometry;
        }
        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
    }
}
