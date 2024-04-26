package volovyk.guerrillamail.ui.util

import java.util.regex.Pattern

interface EmailValidator {
    fun isValidEmailAddress(email: String): Boolean
}

class EmailValidatorImpl : EmailValidator {

    private val pattern = Pattern.compile("^.+@.+\\..+$")

    override fun isValidEmailAddress(email: String): Boolean {
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
}