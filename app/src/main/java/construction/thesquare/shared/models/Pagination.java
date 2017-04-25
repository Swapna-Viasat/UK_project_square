package construction.thesquare.shared.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gherg on 4/24/17.
 */

public class Pagination {

    public int count;
    @SerializedName("page_count") public int pageCount;
    public Links links;

    public static class Links {
        public String previous;
        public String next;
    }

}
