package construction.thesquare.shared.data;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import construction.thesquare.BuildConfig;
import construction.thesquare.FlavorSettings;
import construction.thesquare.MainApplication;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.utils.TextTools;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpRestServiceConsumer {

    private static BaseApiInterface baseService;
    private static Retrofit retrofitInstance;
    private static final int TIMEOUT = 60;

    public static BaseApiInterface getBaseApiClient() {
        return getBaseApiInterface(!(TextUtils.isEmpty(
                SharedPreferencesManager.getInstance(MainApplication.getAppContext()).getToken())));

    }

    private static BaseApiInterface getBaseApiInterface(boolean setInterceptor) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FlavorSettings.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(getHttpClient(setInterceptor))
                .build();
        retrofitInstance = retrofit;
        if (!setInterceptor) {
            baseService = null;
            return retrofit.create(BaseApiInterface.class);
        } else {
            baseService = retrofit.create(BaseApiInterface.class);
            return baseService;
        }
    }

    private static OkHttpClient getHttpClient(boolean setInterceptor) {
        OkHttpClient okHttpClient;
        if (setInterceptor)
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT * 1000, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT * 1000, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT * 1000, TimeUnit.SECONDS)
                    .addInterceptor(getInterceptor())
                    .addInterceptor(getLoggingInterceptor())
                    .build();
        else {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT * 1000, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT * 1000, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT * 1000, TimeUnit.SECONDS)
                    .addInterceptor(getLoggingInterceptor())
                    .build();
        }
        return okHttpClient;
    }

    private static Interceptor getInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                TextTools.log("Interceptor Token: ",
                        SharedPreferencesManager.getInstance(MainApplication.getAppContext()).getToken());
                Request original = chain.request();
                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "Token " +
                                SharedPreferencesManager
                                        .getInstance(MainApplication.getAppContext()).getToken())
                        .header("Accept", "application/json")
                        .header("HTTP_USER_AGENT", "Android")
                        .method(original.method(), original.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
    }

    private static Interceptor getLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG)
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    public static Retrofit getRetrofitInstance() {
        return retrofitInstance;
    }
}