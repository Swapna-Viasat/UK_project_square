package construction.thesquare.employer.createjob.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by gherg on 1/18/17.
 */

public class GsonConfig {

    public static Gson buildDefault() {
        return new GsonBuilder().setLenient().create();
    }

}
