package construction.thesquare.shared.login.model;

import android.content.Context;

import java.util.HashMap;

import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.LoginUser;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.utils.Constants;
import retrofit2.Callback;

/**
 * Created by gherg on 3/28/17.
 */

public class AccountValidator {

    public void validate(String email,
                             String password,
                             Callback<ResponseObject<LoginUser>> callback) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("platform", Constants.PLATFORM_ANDROID);
        HttpRestServiceConsumer.getBaseApiClient()
                .loginUser(body)
                .enqueue(callback);
    }
}
