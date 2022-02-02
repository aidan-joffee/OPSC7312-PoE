package Places;

import Places.Model.SearchResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchApiRequest {
    @GET("/maps/api/place/findplacefromtext/json?")
    Call<SearchResult> getPlace(@Query("input") String input,
                                @Query("inputtype") String inputtype,
                                @Query(value = "locationbias", encoded = true) String locationbias,
                                @Query(value = "fields", encoded = true) String fields,
                                @Query("key") String key);
}
