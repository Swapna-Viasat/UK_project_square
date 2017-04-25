package construction.thesquare.shared.login.presenter;

import construction.thesquare.shared.login.model.InputValidator;
import construction.thesquare.shared.login.view.ILoginForm;

/**
 * Created by gherg on 3/28/17.
 */

public class LoginFormPresenter {

    private final ILoginForm loginForm;

    public LoginFormPresenter(ILoginForm loginForm) {
        this.loginForm = loginForm;
    }

    public void onLoginButtonClick(String email,
                                   String password) {
        loginForm.clearAllErrors();

        final InputValidator inputValidator = new InputValidator();

        final InputValidator.Response passwordResponse =
                inputValidator.validatePassword(password);
        if (!passwordResponse.isSuccess()) {
            loginForm.showPasswordError(passwordResponse.getErrorType());
            return;
        }

        final InputValidator.Response emailResponse =
                inputValidator.validateEmail(email);
        if (!emailResponse.isSuccess()) {
            loginForm.showEmailError(emailResponse.getErrorType());
            return;
        }

        loginForm.processValidInput();
    }
}
