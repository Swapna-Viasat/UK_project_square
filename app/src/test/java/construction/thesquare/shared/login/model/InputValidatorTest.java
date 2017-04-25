package construction.thesquare.shared.login.model;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by gherg on 3/30/17.
 */

public class InputValidatorTest {

    private InputValidator inputValidator;

    @Before
    public void setup() {
        inputValidator = new InputValidator();
    }

    @Test
    public void shouldNotBeNull() throws Exception {
        assertThat(inputValidator).isNotNull();
    }

    /**
     * Email validation tests
     */
    @Test
    public void validateEmail_shouldReturnFalseForNullInput() throws Exception {
        assertThat(inputValidator.validateEmail(null).isSuccess()).isFalse();
    }

    @Test
    public void validateEmail_shouldReturnFalseForEmptyInput() throws Exception {
        assertThat(inputValidator.validateEmail("").isSuccess()).isFalse();
    }

    @Test
    public void validateEmail_shouldReturnFalseForInvalidInput() throws Exception {
        assertThat(inputValidator.validateEmail("abc").isSuccess()).isFalse();
    }

    @Test
    public void validateEmail_shouldReturnTrueForValidInput() throws Exception {
        assertThat(inputValidator.validateEmail("sample@gmail.com").isSuccess()).isTrue();
    }

    @Test
    public void validateEmail_shouldReturnErrorTypeNullForNullInput() throws Exception {
        assertThat(inputValidator.validateEmail(null).getErrorType())
                .isEqualTo(InputValidator.ErrorType.NULL);
    }

    @Test
    public void validateEmail_shouldReturnErrorTypeEmptyForEmptyInput() throws Exception {
        assertThat(inputValidator.validateEmail("").getErrorType())
                .isEqualTo(InputValidator.ErrorType.EMPTY);
    }

    @Test
    public void validateEmail_shouldReturnErrorTypeInvalidForInvalidInput() throws Exception {
        assertThat(inputValidator.validateEmail("abc").getErrorType())
                .isEqualTo(InputValidator.ErrorType.INVALID);
    }

    @Test
    public void validateEmail_shouldNotReturnErrorTypeForValidInput() throws Exception {
        assertThat(inputValidator.validateEmail("sample@gmail.com").getErrorType()).isNull();
    }

    /**
     * Password validation tests
     */
    @Test
    public void validatePassword_shouldReturnFalseForNullInput() throws Exception {
        assertThat(inputValidator.validatePassword(null).isSuccess()).isFalse();
    }

    @Test
    public void validatePassword_shouldReturnFalseForEmptyInput() throws Exception {
        assertThat(inputValidator.validatePassword("").isSuccess()).isFalse();
    }

    @Test
    public void validatePassword_shouldReturnFalseForTooShortInput() throws Exception {
        assertThat(inputValidator.validatePassword("1234567").isSuccess()).isFalse();
    }

    @Test
    public void validatePassword_shouldReturnTrueForValidInput() throws Exception {
        assertThat(inputValidator.validatePassword("12345678").isSuccess()).isTrue();
    }

    @Test
    public void validatePassword_shouldReturnErrorNullForNullInput() throws Exception {
        assertThat(inputValidator.validatePassword(null).getErrorType())
                .isEqualTo(InputValidator.ErrorType.NULL);
    }

    @Test
    public void validatePassword_shouldReturnErrorEmptyForEmptyInput() throws Exception {
        assertThat(inputValidator.validatePassword("").getErrorType())
                .isEqualTo(InputValidator.ErrorType.EMPTY);
    }

    @Test
    public void validatePassword_shouldReturnErrorInvalidForTooShortInput() throws Exception {
        assertThat(inputValidator.validatePassword("1234567").getErrorType())
                .isEqualTo(InputValidator.ErrorType.INVALID);
    }

    @Test
    public void validatePassword_shouldNotReturnErrorForValidInput() throws Exception {
        assertThat(inputValidator.validatePassword("12345678").getErrorType()).isNull();
    }
}
