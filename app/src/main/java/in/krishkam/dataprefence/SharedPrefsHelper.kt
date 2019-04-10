package `in`.krishkam.dataprefence

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import `in`.krishkam.constants.AppConstants.PREFILENAME
import `in`.krishkam.constants.AppConstants.PREF_KEY_EMAIL
import `in`.krishkam.constants.AppConstants.PREF_KEY_IS_LOGGED_IN
import `in`.krishkam.constants.AppConstants.PREF_KEY_IS_UPLOADPHOTO
import `in`.krishkam.constants.AppConstants.PREF_KEY_IS_USER_ID
import `in`.krishkam.constants.AppConstants.PREF_KEY_IS_USER_MOBILE
import `in`.krishkam.constants.AppConstants.PREF_KEY_IS_USER_NAME
import `in`.krishkam.constants.AppConstants.PREF_KEY_IS_USER_PROFILE_PHOTO
import `in`.krishkam.constants.AppConstants.PREF_KEY_IS_USER_REGISTARION

class SharedPrefsHelper(context: Context) {
    internal var mSharedPreferences: SharedPreferences

    init {
        mSharedPreferences = context.getSharedPreferences(PREFILENAME, MODE_PRIVATE)
    }

    var email: String?
        get() = mSharedPreferences.getString(PREF_KEY_EMAIL, null)
    //using first method
    // set(email) =  mSharedPreferences.edit().putString(EMAIL, email).apply()

        set(email) = with(mSharedPreferences.edit()) {
            putString(PREF_KEY_EMAIL, email)
            apply()
        }

    var loggedInMode: Boolean
        get() = mSharedPreferences.getBoolean(PREF_KEY_IS_LOGGED_IN, false)
        set(loggedInMode) = with(mSharedPreferences.edit()) {
            putBoolean(PREF_KEY_IS_LOGGED_IN, loggedInMode)
            apply()
        }
    var loggedInModeRegistartion: Boolean
        get() = mSharedPreferences.getBoolean(PREF_KEY_IS_USER_REGISTARION, false)
        set(loggedInModeRegistartion) = with(mSharedPreferences.edit()) {
            putBoolean(PREF_KEY_IS_USER_REGISTARION, loggedInModeRegistartion)
            apply()
        }

    var appUserId: String?
        get() = mSharedPreferences.getString(PREF_KEY_IS_USER_ID, null)
        set(appUserId) = with(mSharedPreferences.edit()) {
            putString(PREF_KEY_IS_USER_ID, appUserId)

            apply()
        }

    var appUserNAME: String?
        get() = mSharedPreferences.getString(PREF_KEY_IS_USER_NAME, null)
        set(appUserNAME) = with(mSharedPreferences.edit()) {
            putString(PREF_KEY_IS_USER_NAME, appUserNAME)
            apply()
        }
    var loggedInModePhoto: Boolean
        get() = mSharedPreferences.getBoolean(PREF_KEY_IS_UPLOADPHOTO, false)
        set(loggedInModePhoto) = with(mSharedPreferences.edit()) {
            putBoolean(PREF_KEY_IS_UPLOADPHOTO, loggedInModePhoto)
            apply()
        }

    var appUserMobile: String?
        get() = mSharedPreferences.getString(PREF_KEY_IS_USER_MOBILE, null)
        set(appUserMobile) = with(mSharedPreferences.edit()) {
            putString(PREF_KEY_IS_USER_MOBILE, appUserMobile)
            apply()
        }

    var appUserPHOTO: String?
        get() = mSharedPreferences.getString(PREF_KEY_IS_USER_PROFILE_PHOTO, null)
        set(appUserPHOTO) = with(mSharedPreferences.edit()) {
            putString(PREF_KEY_IS_USER_PROFILE_PHOTO, appUserPHOTO)
            apply()
        }




    fun clear() {

        with(mSharedPreferences.edit()) {
            clear()
            apply()
        }


    }

}