package pust.ice.eilish.api;



import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {
    private static Retrofit rf;
    public static String Token;

    public static String URL="http://e-ilish.org/";
//    public static String URL="http://192.168.25.100:8000/";

    public static Retrofit getInstance()
    {
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(interceptor)
//                .build();
        if(rf==null)
        {
            rf=new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
//                    .client(client)
                    .build();
            return rf;
        }
        return rf;
    }


    public static <S> S createService(Class<S> serviceClass) {
        getInstance();
        return rf.create(serviceClass);
    }
}
