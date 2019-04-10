package `in`.krishkam.activity

import `in`.krishkam.R


import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import `in`.krishkam.base.BaseActivity
import `in`.krishkam.utils.Validation
import android.content.Intent



class MainActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // click listner of otp
        btn_otp_prapt.setOnClickListener {

            val phone_number: String = et_phone_number.text.toString()

            if (Validation.isEmptyField(phone_number)) {
                et_phone_number.error = getString(R.string.emptynumber)

                return@setOnClickListener


            }
            if (!Validation.isValidPhoneNumber(phone_number)) {
                et_phone_number.error = "कृपया वैद्य  फोन नंबर इनपुट करे जैसे १० डिजिट नंबर!"
                return@setOnClickListener

            }
            //  hideKeyboard(et_phone_number)

            if (isNetworkAvailable()) {
                val intent = Intent(this, OtpVerificationActivity::class.java)
                intent.putExtra("Username", phone_number)
                startActivity(intent)
                finish()
            } else {
                showSnackBar("\n" + "कोई इंटरनेट कनेक्शन नहीं!")
            }


        }
    }


}
