package `in`.krishkam.activity


import android.os.Bundle
import android.os.Handler

import `in`.krishkam.R
import `in`.krishkam.base.BaseActivity
import `in`.krishkam.base.BaseApplication
import `in`.krishkam.constants.AppConstants.SPLASH_DELAY
import `in`.krishkam.dataprefence.DataManager
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.google.firebase.iid.FirebaseInstanceId


class SplashActivity : BaseActivity() {
    private var mDelayHandler: Handler? = null
    lateinit var dataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        dataManager = BaseApplication.baseApplicationInstance.getdatamanger()
        mDelayHandler = Handler()
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
    }
    private val mRunnable: Runnable = Runnable {
        if (!isFinishing) {

            if(!dataManager.getLoggedIn()){

                launchActivity<MainActivity>()
                finish()
                return@Runnable

            }
            else{

                launchActivity<UserFeedActivity>()
                finish()
            }









        }
    }

    override fun onDestroy() {
        mDelayHandler?.removeCallbacks(mRunnable)
        super.onDestroy()
    }

    override fun onBackPressed() {
        mDelayHandler?.removeCallbacks(mRunnable)
        super.onBackPressed()
    }
}
