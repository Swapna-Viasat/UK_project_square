package construction.thesquare.shared.login.view;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import construction.thesquare.R;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by gherg on 3/30/17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", sdk = 23)
public class LoginFormTest {

    private LoginForm loginForm = new LoginForm(RuntimeEnvironment.application);

    @Test
    public void shouldNotBeNull() throws Exception {
        assertThat(loginForm).isNotNull();
    }

    @Test
    public void onClickSignInButton_shouldNotInvokeListenerForInvalidInput() throws Exception {
        EditText emailView = (EditText) loginForm.findViewById(R.id.email);
        EditText passwordView = (EditText) loginForm.findViewById(R.id.password);
        TextView signInButton = (TextView) loginForm.findViewById(R.id.login);
        TestListener testListener = new TestListener();

        emailView.setText("bad_email");
        passwordView.setText("2short");
        loginForm.setOnClickListener(testListener);
        signInButton.performClick();
        assertThat(testListener.click).isFalse();
    }

    @Test
    public void onClickSignInButton_shouldInvokeListenerForValidInput() throws Exception {
        EditText emailView = (EditText) loginForm.findViewById(R.id.email);
        EditText passwordView = (EditText) loginForm.findViewById(R.id.password);
        TextView signInButton = (TextView) loginForm.findViewById(R.id.login);
        TestListener testListener = new TestListener();

        emailView.setText("gherg001@gmail.com");
        passwordView.setText("longEnoughPassword");
        loginForm.setOnClickListener(testListener);
        signInButton.performClick();
        assertThat(testListener.click).isTrue();
    }

    private class TestListener implements View.OnClickListener {

        private boolean click;

        @Override
        public void onClick(View view) {
            click = true;
        }
    }
}
