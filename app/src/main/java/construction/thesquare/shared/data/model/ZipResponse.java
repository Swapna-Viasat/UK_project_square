package construction.thesquare.shared.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by juanmaggi on 6/12/16.
 */

public class ZipResponse {

    @SerializedName("Message") public String message;
    @SerializedName("Latitude") public double lat;
    @SerializedName("Longitude") public double lang;
    @SerializedName("Addresses") public List<String> addresses;

}
