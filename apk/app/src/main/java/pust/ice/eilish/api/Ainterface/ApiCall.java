package pust.ice.eilish.api.Ainterface;





import okhttp3.ResponseBody;
import pust.ice.eilish.api.Ainterface.Objects.CheckObject;
import pust.ice.eilish.api.Ainterface.Objects.FetchObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiCall {
    @GET
    Call<ResponseBody> getimage(@Url String url);

    @GET("api/android?check")
    Call<CheckObject> performCheck();

    @GET("api/android")
    Call<FetchObject> getupdate(@Query("data") String data, @Query("noti") String noti, @Query("info") String info);

}
