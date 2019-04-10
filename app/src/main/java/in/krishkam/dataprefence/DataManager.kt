package `in`.krishkam.dataprefence

class DataManager(internal var mSharedPrefsHelper: SharedPrefsHelper) {

    fun clear() {
        mSharedPrefsHelper.clear()
    }

    fun saveEmailId(email: String) {
        mSharedPrefsHelper.email=email
    }

    fun setLoggedIn(isLogin:Boolean) {
        mSharedPrefsHelper.loggedInMode =isLogin
    }

    fun getLoggedIn():Boolean {
        return mSharedPrefsHelper.loggedInMode
    }

    fun setRegistartion(loggedInMoe:Boolean) {
        mSharedPrefsHelper.loggedInModeRegistartion =loggedInMoe
    }

    fun getRegistartion():Boolean {
        return mSharedPrefsHelper.loggedInModeRegistartion
    }

    fun setPhoto(isLogin:Boolean) {
        mSharedPrefsHelper.loggedInModePhoto =isLogin
    }

    fun getPhoto():Boolean {
        return mSharedPrefsHelper.loggedInModePhoto
    }

    fun saveUserId(userId:String){
        mSharedPrefsHelper.appUserId=userId

    }
    fun getUserId():String?{
        return mSharedPrefsHelper.appUserId
    }
    fun saveUserName(saveUserName:String){
        mSharedPrefsHelper.appUserNAME=saveUserName

    }
    fun getUserName():String?{
        return mSharedPrefsHelper.appUserNAME
    }

    fun saveUserMobile(saveUserMobile:String){
        mSharedPrefsHelper.appUserMobile=saveUserMobile

    }
    fun getUserMobile():String?{
        return mSharedPrefsHelper.appUserMobile
    }

    fun saveUserProfilePic(saveUserproflePic:String){
        mSharedPrefsHelper.appUserPHOTO=saveUserproflePic

    }
    fun getUserProfilePic():String?{
        return mSharedPrefsHelper.appUserPHOTO
    }

    fun saveFireBaseToken(token:String){
        mSharedPrefsHelper.appUserId=token

    }
    fun getFirebaseToken():String?{

        return mSharedPrefsHelper.appUserId
    }
}