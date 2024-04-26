package volovyk.guerrillamail.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import volovyk.guerrillamail.ui.util.EmailValidator
import volovyk.guerrillamail.ui.util.EmailValidatorImpl

class EmailValidatorImplTest {

    @Test
    fun `valid email address`() {
        val emailValidator: EmailValidator = EmailValidatorImpl()
        val validEmail = "test@example.com"
        assertTrue(emailValidator.isValidEmailAddress(validEmail))
    }

    @Test
    fun `invalid email address missing @`() {
        val emailValidator: EmailValidator = EmailValidatorImpl()
        val invalidEmail = "testexample.com"
        assertFalse(emailValidator.isValidEmailAddress(invalidEmail))
    }

    @Test
    fun `invalid email address missing domain`() {
        val emailValidator: EmailValidator = EmailValidatorImpl()
        val invalidEmail = "test@"
        assertFalse(emailValidator.isValidEmailAddress(invalidEmail))
    }

    @Test
    fun `invalid email address missing username`() {
        val emailValidator: EmailValidator = EmailValidatorImpl()
        val invalidEmail = "@example.com"
        assertFalse(emailValidator.isValidEmailAddress(invalidEmail))
    }

    @Test
    fun `invalid email address with missing top-level domain`() {
        val emailValidator: EmailValidator = EmailValidatorImpl()
        val invalidEmail = "test@example."
        assertFalse(emailValidator.isValidEmailAddress(invalidEmail))
    }
}