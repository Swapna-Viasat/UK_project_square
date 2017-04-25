package construction.thesquare.shared.data;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gherg on 12/5/2016.
 */

public class ZipCodeVerifier {

    static ZipCodeVerifier zipCodeVerifier;
    ZipService zipService;

    public static final String BASE_URL = "https://api.getAddress.io/v2/uk/";
    // public static final String API_KEY = "qPOZI4RghEW6c9E6ZweUHg6070";
    // public static final String API_KEY = "JljVDrsq4k-SkVQTFWbvDw6907";

    // the following is a paid API key from getAddress api valid jan9-feb9:
    // 2000 api requests per day then 1000 more with a 5 second delay
    public static final String API_KEY = "qPOZI4RghEW6c9E6ZweUHg6070";
    public static final String BAD_REQUEST = "Bad Request";

    public static ZipCodeVerifier getInstance() {
        if (zipCodeVerifier == null) {
            zipCodeVerifier = new ZipCodeVerifier();
        }
        return zipCodeVerifier;
    }

    public ZipCodeVerifier() {
        Retrofit retrofit = getRetrofit();
        zipService = retrofit.create(ZipService.class);
    }

    private Retrofit getRetrofit() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(30, TimeUnit.SECONDS);
        client.addInterceptor(loggingInterceptor);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(client.build())
                .build();
        return retrofit;
    }

    public ZipService api() {
        return zipService;
    }
}
