package construction.thesquare.worker.jobmatches.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Evgheni on 11/2/2016.
 */

public class Mappings implements Serializable {
    public Response response;
    public class Response {
        public List<Role> roles;
        public Job job;
        public class Role {
            public int id;
            public String name;
            public String description;
            public String image;
        }
        public class Job {
            public List<Status> status;
            @SerializedName("budget_type") public List<Budget> budgetType;
            public class Status {
                public int id;
                public String name;
            }
            public class Budget {
                public int id;
                public String name;
            }
        }
    }
}
