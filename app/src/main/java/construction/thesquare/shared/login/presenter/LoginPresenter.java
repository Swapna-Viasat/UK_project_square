package construction.thesquare.shared.login.presenter;

import construction.thesquare.shared.data.model.LoginUser;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.login.controller.LoginController;
import construction.thesquare.shared.login.model.AccountValidator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 3/29/17.
 */

public class LoginPresenter {

    private LoginController loginController;
    private AccountValidator accountValidator;

    public LoginPresenter(AccountValidator accountValidator) {
        this.accountValidator = accountValidator;
    }

    public LoginPresenter register(LoginController loginController) {
        this.loginController = loginController;
        if (null != accountValidator) {
            //
        }
        return this;
    }

    public void onLoginButtonClick(String email, String password) {
        loginController.showProgress(true);
        accountValidator.validate(email, password,
                new Callback<ResponseObject<LoginUser>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<LoginUser>> call,
                                           Response<ResponseObject<LoginUser>> response) {
                        //
                        loginController.showProgress(false);
                        if (response.isSuccessful()) {
                            loginController.showSuccess(response);
                        } else {
                            loginController.showError(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<LoginUser>> call, Throwable t) {
                        //
                        loginController.showProgress(false);
                        loginController.showRetrofitError(t);
                    }
        });
    }
}
