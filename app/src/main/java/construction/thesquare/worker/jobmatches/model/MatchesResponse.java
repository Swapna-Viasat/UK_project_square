package construction.thesquare.worker.jobmatches.model;

import java.util.List;

/**
 * Created by Evgheni on 11/1/2016.
 */

public class MatchesResponse {
    public List<Job> response;
    public Pagination pagination;
    class Pagination {
        public int count;
        public Links links;
        class Links {
            public String previous;
            public String next;
        }
    }
}
