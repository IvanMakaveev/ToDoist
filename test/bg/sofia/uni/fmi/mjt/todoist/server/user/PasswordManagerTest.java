package bg.sofia.uni.fmi.mjt.todoist.server.user;

import bg.sofia.uni.fmi.mjt.todoist.server.exception.PasswordFormatException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PasswordManagerTest {

    @Test
    void testValidateCorrectPassword() {
        assertDoesNotThrow(() -> PasswordManager.validate("testPass123"),
            "The password validation should work with correct data");
    }

    @Test
    void testValidateNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> PasswordManager.validate(null),
            "The password validation should throw with null password");
    }

    @Test
    void testValidateTooShortPasswordThrows() {
        assertThrows(PasswordFormatException.class, () -> PasswordManager.validate("1tT"),
            "The password validation should throw with too short password");
    }

    @Test
    void testValidateTooLongPasswordThrows() {
        assertThrows(PasswordFormatException.class,
            () -> PasswordManager.validate("1tESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTEST"),
            "The password validation should throw with too long password");
    }

    @Test
    void testValidateNoLowercasePasswordThrows() {
        assertThrows(PasswordFormatException.class,
            () -> PasswordManager.validate("1TESTPASSWORDUPPER"),
            "The password validation should throw for password with no lowercase");
    }

    @Test
    void testValidateNoUppercasePasswordThrows() {
        assertThrows(PasswordFormatException.class,
            () -> PasswordManager.validate("1testpasswordlower"),
            "The password validation should throw for password with no uppercase");
    }

    @Test
    void testValidateNoDigitPasswordThrows() {
        assertThrows(PasswordFormatException.class,
            () -> PasswordManager.validate("noDigitsTestPassword"),
            "The password validation should throw for password with no uppercase");
    }

    @Test
    void testHashPasswordHashesCorrectly() {
        String expected = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";

        String result = PasswordManager.hashPassword("abc");

        assertEquals(expected, result, "The password hash should match");
    }

    @Test
    void testHashPasswordNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> PasswordManager.hashPassword(null),
            "The password hashing should throw with null password");
    }
}
