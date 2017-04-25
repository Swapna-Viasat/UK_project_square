package construction.thesquare.shared.login.presenter;

import org.junit.Before;
import org.junit.Test;

import construction.thesquare.shared.data.model.LoginUser;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.login.controller.LoginController;
import construction.thesquare.shared.login.model.AccountValidator;
import retrofit2.Callback;
import retrofit2.Response;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by gherg on 3/30/17.
 */

public class LoginPresenterTest {

    private LoginPresenter loginPresenter;
    private TestLoginController testLoginController;

    @Before
    public void setup() throws Exception {
        testLoginController = new TestLoginController();
        loginPresenter = new LoginPresenter(new TestAccountValidator());
        loginPresenter.register(testLoginController);
    }

    @Test
    public void onLoginButtonClick_shouldShowSuccessOnValidInput() throws Exception {
        loginPresenter.onLoginButtonClick("valid_username", "valid_password");
        assertThat(testLoginController.success).isTrue();
    }

    @Test
    public void onLoginButtonClick_shouldShowErrorOnInvalidInput() throws Exception {
        loginPresenter.onLoginButtonClick("invalid_username", "invalid_username");
        assertThat(testLoginController.error).isTrue();
    }

    private class TestLoginController implements LoginController {

        boolean progress;
        boolean error;
        boolean success;

        @Override
        public void showProgress(boolean show) {
            progress = show;
        }

        @Override
        public void showRetrofitError(Throwable t) {
            error = true;
        }

        @Override
        public void showError(Response<ResponseObject<LoginUser>> response) {
            error = true;
        }

        @Override
        public void showSuccess(Response<ResponseObject<LoginUser>> response) {
            success = true;
        }
    }

    private class TestAccountValidator extends AccountValidator {

        boolean validated;

        @Override
        public void validate(String username,
                             String password,
                             Callback callback) {
            if ("valid_username".equals(username) &&
                    "valid_password".equals(password)) {
                // callback.onResponse(null, null);
                validated = true;
            } else {
                // callback.onResponse(null, null);
                validated = false;
            }
        }
    }
}
