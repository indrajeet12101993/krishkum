package `in`.krishkam.utils

import android.text.TextUtils
import java.util.regex.Pattern

object Validation {


    fun isEmptyField(name: String): Boolean {

        return TextUtils.isEmpty(name)
    }

    fun isValidPhoneNumber(number: String): Boolean {

        val testPattern = Pattern.compile("^[1-9][0-9]{9}")
        val teststring = testPattern.matcher(number)

        if (teststring.matches()) {
            return true
        }

        return false
    }
}