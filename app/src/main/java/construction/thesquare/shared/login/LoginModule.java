package construction.thesquare.shared.login;

import javax.inject.Singleton;

import construction.thesquare.shared.login.model.AccountValidator;
import construction.thesquare.shared.login.presenter.LoginPresenter;
import dagger.Module;
import dagger.Provides;

/**
 * Created by gherg on 3/29/17.
 */

@Module
public class LoginModule {

    @Provides
    @Singleton
    LoginPresenter provideLoginPresenter(AccountValidator accountValidator) {
        return new LoginPresenter(accountValidator);
    }

    @Provides
    @Singleton
    AccountValidator provideAccountValidator() {
        return new AccountValidator();
    }
}
