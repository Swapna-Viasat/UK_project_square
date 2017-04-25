package construction.thesquare.shared.login.controller;

import construction.thesquare.shared.data.model.LoginUser;
import construction.thesquare.shared.data.model.ResponseObject;
import retrofit2.Response;

/**
 * Created by gherg on 3/28/17.
 */

public interface LoginController {
    void showProgress(boolean show);
    void showRetrofitError(Throwable throwable);
    void showError(Response<ResponseObject<LoginUser>> response);
    void showSuccess(Response<ResponseObject<LoginUser>> response);
}
