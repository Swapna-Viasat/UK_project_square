package construction.thesquare.worker.signup.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class CSCSCardWorker implements Serializable {

    public int id;
    @SerializedName("verification_status")
    public int verificationStatus;
    public String surname;
    @SerializedName("birth_date")
    public String birthDate;
    @SerializedName("registration_number")
    public String registrationNumber;
    @SerializedName("insurance_number")
    public String insuranceNumber;
    @SerializedName("card_picture")
    public String cardPicture;
    @SerializedName("expiry_date")
    public String expiryDate;
    @SerializedName("cscs_records")
    public HashMap<String, List<CscsRecord>> cscsRecords;

    public class CscsRecord implements Serializable {
        public String id;
        public String name;
        public Category category;

        public class Category implements Serializable {
            public int id;
            public String name;
        }
    }
}
