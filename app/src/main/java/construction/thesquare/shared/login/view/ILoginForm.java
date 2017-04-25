package construction.thesquare.shared.login.view;

import construction.thesquare.shared.login.model.InputValidator;

/**
 * Created by gherg on 3/28/17.
 */

public interface ILoginForm {
    void clearAllErrors();
    void showEmailError(InputValidator.ErrorType errorType);
    void showPasswordError(InputValidator.ErrorType errorType);
    void processValidInput();
}
