package construction.thesquare.shared.data.model;

/**
 * Created by juanmaggi on 21/9/16.
 */
public class SMSSent {

    private String message;
    private boolean sms_sended;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSms_sended() {
        return sms_sended;
    }

    public void setSms_sended(boolean sms_sended) {
        this.sms_sended = sms_sended;
    }
}
