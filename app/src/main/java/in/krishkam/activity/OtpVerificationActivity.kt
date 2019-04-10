package `in`.krishkam.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.LocalBroadcastManager
import android.widget.ProgressBar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_otp_verification.*
import `in`.krishkam.R
import `in`.krishkam.base.BaseActivity
import `in`.krishkam.base.BaseApplication
import `in`.krishkam.braodcast.SMSBroadcastReceiver
import `in`.krishkam.constants.AppDataConstantsValue
import `in`.krishkam.dataprefence.DataManager
import `in`.krishkam.networkUtils.ApiRequestClient
import `in`.krishkam.pojo.ResponseFromSerevrPhoneNumber
import `in`.krishkam.pojo.otp.ResponseFromServerOtpVerify
import com.google.android.youtube.player.internal.d
import android.content.DialogInterface
import android.support.v7.app.AlertDialog

import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever


class OtpVerificationActivity : BaseActivity() {
    private var mCompositeDisposable: CompositeDisposable? = null
    private var mCompositeDisposable_verifyOtp: CompositeDisposable? = null
    lateinit var countDowntimer: CountDownTimer
    var response_code_user: String? = null
    lateinit var dataManager: DataManager
    private val smsBroadcastReceiver by lazy { SMSBroadcastReceiver() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)
        dataManager = BaseApplication.baseApplicationInstance.getdatamanger()
        response_code_user = ""
        val profileName = intent.getStringExtra("Username")
        val stringBuffer: StringBuffer = StringBuffer();
        stringBuffer.append(profileName)
        stringBuffer.append(" ")
        stringBuffer.append("को भेजे गए एसएमएस को स्वचालित रूप से ढूंढने का इंतजार कर रहा है !")
        otp_description.text = stringBuffer.toString()
        verify_text.text = profileName
        // api call for otp and profile verify

        if (isNetworkAvailable()) {
            startLoading(profileName)
        } else {
            showSnackBar("\n" +
                    "कोई इंटरनेट कनेक्शन नहीं!")
        }

        // click when user edit number
        wrong_number.setOnClickListener {
            launchActivity<MainActivity>()
            finish()
        }

        // click when resend button is happened
        btn_resend_sms.setOnClickListener {
            startLoading(profileName)
            stopCountDownTimer()

        }
        // click for submit
        btn_submit.setOnClickListener {
            if (lineField.text.toString() != null) {
                verifyOtp(lineField.text.toString())
            } else {
                lineField.error = "कृपया ओटीपी  इनपुट करें"
            }

        }

        val client = SmsRetriever.getClient(this)
        val retriever = client.startSmsRetriever()
        retriever.addOnSuccessListener {

            val listener = object : SMSBroadcastReceiver.Listener {
                override fun onSMSReceived(otp: String) {

                    val otpsplit = otp.split(":")
                    val otpsplit1 = otpsplit[1]
                    lineField.setText(otpsplit1)
                    stopCountDownTimer()
                    verifyOtp(lineField.text.toString())

                }


                override fun onTimeOut() {



                }
            }
            smsBroadcastReceiver.injectListener(listener)
           registerReceiver(smsBroadcastReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))
        }
        retriever.addOnFailureListener {
            //Problem to start listener
        }

        startCountDownTimer(120000, 1000)
    }


    //check number for otp
    private fun startLoading(phonenumber: String) {


        showDialogLoading()

        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable?.add(ApiRequestClient.createREtrofitInstance().postServerUserPhoneNumber(phonenumber)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }

    // handle sucess response of api call
    private fun handleResponse(responseFromSerevrPhoneNumber: ResponseFromSerevrPhoneNumber) {
        hideDialogLoading()
        countDowntimer.start()
        response_code_user = responseFromSerevrPhoneNumber.response_code
        mCompositeDisposable?.clear()


    }


    // handle failure response of api call
    private fun handleError(error: Throwable) {
        hideDialogLoading()

        showSnackBar(error.localizedMessage)
        mCompositeDisposable?.clear()

    }



    private fun verifyOtp(otp: String) {
        progressBar_cyclic.visibility = ProgressBar.VISIBLE
        mCompositeDisposable_verifyOtp = CompositeDisposable()

        mCompositeDisposable_verifyOtp?.add(ApiRequestClient.createREtrofitInstance()
                .postServerUserOtpVerify(otp)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseOtp, this::handleErrorOtp))


    }

    // handle sucess response of api call
    private fun handleResponseOtp(responseFromServerOtpVerify: ResponseFromServerOtpVerify) {

        progressBar_cyclic.visibility = ProgressBar.INVISIBLE




        saveUserId(responseFromServerOtpVerify.result.get(0).id)
        //save user loogin mode to true
        saveUserMobileNumber(responseFromServerOtpVerify.result.get(0).mobile)
        dataManager.setLoggedIn(true)


        if (responseFromServerOtpVerify.result.get(0).name.isNullOrEmpty() ||
                responseFromServerOtpVerify.result.get(0).state.isNullOrEmpty() ||
                responseFromServerOtpVerify.result.get(0).city.isNullOrEmpty() ||
                responseFromServerOtpVerify.result.get(0).village.isNullOrEmpty()){
            launchActivity<RegistrationActivity>()
            finish()

        }
        else{
            launchActivity<UserFeedActivity>()
            finish()
        }

        mCompositeDisposable_verifyOtp?.clear()


    }

    private fun saveUserMobileNumber(mobile: String) {
        dataManager.saveUserMobile(mobile)

    }

    private fun saveUserName(name: String) {
        dataManager.saveUserName(name)

    }

    // save userid which is come from otp response verify
    private fun saveUserId(id: String) {

        dataManager.saveUserId(id)
    }


    // handle failure response of api call
    private fun handleErrorOtp(error: Throwable) {
        progressBar_cyclic.visibility = ProgressBar.INVISIBLE
        showSnackBar(error.localizedMessage)
        mCompositeDisposable_verifyOtp?.clear()

    }





    override fun onDestroy() {
        super.onDestroy()
         unregisterReceiver(smsBroadcastReceiver)
    }


    // start counttimer for 60 seconds
    fun startCountDownTimer(duration: Long, interval: Long) {
        countDowntimer = object : CountDownTimer(duration, interval) {
            override fun onFinish() {
                tv_count_timer.text = "00:00";
            }

            override fun onTick(millisUntilFinished: Long) {
                val time: Int = millisUntilFinished.toInt()
                val seconds: Int = time / 1000 % 60;
                val minutes: Int = (time / (1000 * 60)) % 60;
                tv_count_timer.text = minutes.toString() + " min: " + seconds.toString() + " sec"


            }
        }

    }

    // stop countdown timer
    fun stopCountDownTimer() {
        if (countDowntimer != null)
            countDowntimer.cancel()
        tv_count_timer.text = "00:00"

    }


}




