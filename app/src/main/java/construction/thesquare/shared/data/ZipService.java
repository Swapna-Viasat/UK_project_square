package construction.thesquare.shared.data;

import construction.thesquare.shared.data.model.ZipResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by gherg on 12/5/2016.
 */

public interface ZipService {

    @GET("{pk}")
    Call<ZipResponse> verify(@Path("pk") String code, @Query("api-key") String key);
}
