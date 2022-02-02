package Places;

import Places.Model.PlacesResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesApiRequest {
    @GET("/maps/api/place/nearbysearch/json?sensor=true")
    Call<PlacesResult> getPlaces(@Query("keyword") String keyword,
                                       @Query("location") String location,
                                       @Query("radius") int radius,
                                       @Query("key") String key);
}
