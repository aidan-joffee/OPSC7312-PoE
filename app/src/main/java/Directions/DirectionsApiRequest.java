package Directions;

import Directions.Model.DirectionResults;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DirectionsApiRequest {
    @GET("/maps/api/directions/json")
    Call<DirectionResults> getJson(@Query("origin") String origin,
                                   @Query("destination") String destination,
                                   @Query("units") String units,
                                   @Query("key") String key);
}
