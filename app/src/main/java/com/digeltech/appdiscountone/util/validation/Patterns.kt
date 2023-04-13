package com.digeltech.appdiscountone.util.validation

import java.util.regex.Matcher
import java.util.regex.Pattern

const val EMAIL_PATTERN =
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+"

const val PASSWORD_MIN = 8

//const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!])(?=\\S+\$).{$PASSWORD_MIN,}\$"
const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+\$).{$PASSWORD_MIN,}\$"

fun isValidEmail(email: String): Boolean {
    val pattern: Pattern
    val matcher: Matcher
    pattern = Pattern.compile(EMAIL_PATTERN)
    matcher = pattern.matcher(email)
    return matcher.matches()
}

fun isValidPassword(password: String): Boolean {
    val pattern: Pattern
    val matcher: Matcher
    pattern = Pattern.compile(PASSWORD_PATTERN)
    matcher = pattern.matcher(password)
    return matcher.matches()
}