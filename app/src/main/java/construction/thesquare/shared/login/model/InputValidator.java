package construction.thesquare.shared.login.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gherg on 3/28/17.
 */

public class InputValidator {

    /**
     * Input validation response type.
     */
    public class Response {

        private final boolean success;
        private final ErrorType errorType;

        private Response(boolean success,
                         ErrorType errorType) {
            this.success = success;
            this.errorType = errorType;
        }

        public boolean isSuccess() {
            return success;
        }

        public ErrorType getErrorType() {
            return errorType;
        }
    }

    /**
     * Input validation error codes.
     */
    public enum ErrorType {
        NULL,
        EMPTY,
        INVALID
    }

    /**
     * Verifying password input.
     *
     * @param password String to be validated as potential password.
     * @return
     */
    public Response validatePassword(String password) {
        if (password == null) {
            return new Response(false, ErrorType.NULL);
        }
        if (password.equals("")) {
            return new Response(false, ErrorType.EMPTY);
        }
        if (password.length() < 8) {
            return new Response(false, ErrorType.INVALID);
        }
        return new Response(true, null);
    }

    /**
     * Verifying email input.
     *
     * @param email String to be validated as potential email.
     * @return
     */
    public Response validateEmail(String email) {
        if (email == null) {
            return new Response(false, ErrorType.NULL);
        }
        if (email.equals("")) {
            return new Response(false, ErrorType.EMPTY);
        }
        if (!isValidEmailAddress(email)) {
            return new Response(false, ErrorType.INVALID);
        }
        return new Response(true, null);
    }

    private boolean isValidEmailAddress(String email) {
        String emailPattern =
"^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
