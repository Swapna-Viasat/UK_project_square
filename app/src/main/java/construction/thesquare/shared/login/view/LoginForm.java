package construction.thesquare.shared.login.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import construction.thesquare.R;
import construction.thesquare.shared.login.model.InputValidator;
import construction.thesquare.shared.login.presenter.LoginFormPresenter;

/**
 * Created by gherg on 3/28/17.
 */

public class LoginForm extends LinearLayout
        implements ILoginForm {

    private EditText mEmailView;
    private EditText mPasswordView;
    private LoginFormPresenter loginFormPresenter;

    public LoginForm(Context context) {
        super(context);
        init();
    }

    public LoginForm(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoginForm(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ((LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.form_login, this, true);

        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        TextView loginButton = (TextView) findViewById(R.id.login);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        });

        loginFormPresenter = new LoginFormPresenter(this);
    }

    private void validateInput() {
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        loginFormPresenter.onLoginButtonClick(email, password);
    }

    public String getEmail() {
        return mEmailView.getText().toString();
    }

    public String getPassword() {
        return mPasswordView.getText().toString();
    }

    public void displayLoginError() {
        mPasswordView.setError("This password is incorrect");
        mPasswordView.requestFocus();
    }

    /**
     * Implementing the ILoginForm interface.
     */
    @Override
    public void clearAllErrors() {
        mEmailView.setError(null);
        mPasswordView.setError(null);
    }

    @Override
    public void showEmailError(InputValidator.ErrorType errorType) {
        switch (errorType) {
            case EMPTY:
                mEmailView.setError("This field is required");
                break;
            case INVALID:
                mEmailView.setError("This email address is invalid");
                break;
        }
        mEmailView.requestFocus();
    }

    @Override
    public void showPasswordError(InputValidator.ErrorType errorType) {
        switch (errorType) {
            case EMPTY:
                mPasswordView.setError("This field is required");
                break;
            case INVALID:
                mPasswordView.setError("This password is too short");
                break;
        }
        mPasswordView.requestFocus();
    }

    /**
     *  callOnClick is an inherited method from View which
     *  according to documentation
     *  "Directly call[s] any attached OnClickListener."
     *  in our case it calls the "loginButton"s on click listener
     *  we set inside init()
     */
    @Override
    public void processValidInput() {
        callOnClick();
    }
}
