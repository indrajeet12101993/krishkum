package `in`.krishkam.braodcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import `in`.krishkam.constants.AppConstants
import android.support.v4.content.LocalBroadcastManager


class SmsReceiver : BroadcastReceiver() {
    private val TAG = SmsReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {

        val bundle = intent.extras
        try {
            if (bundle != null) {
                val pdusObj = bundle.get("pdus") as Array<Any>
                for (aPdusObj in pdusObj) {
                    val currentMessage = SmsMessage.createFromPdu(aPdusObj as ByteArray)
                    val senderAddress = currentMessage.displayOriginatingAddress
                    val message = currentMessage.displayMessageBody

                    Log.e(TAG, "Received SMS: $message, Sender: $senderAddress")

                    // if the SMS is not from our gateway, ignore the message
                    if (!senderAddress.toLowerCase().contains(AppConstants.SMS_ORIGIN.toLowerCase())) {
                        return
                    }

                    // verification code from sms
                    val verificationCode = getVerificationCode(message)

                    Log.e(TAG, "OTP received: " + verificationCode!!)

                    val myIntent = Intent("otp")
                    myIntent.putExtra("message", verificationCode)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(myIntent)


                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: " + e.message)
        }

    }

    /**
     * Getting the OTP from sms message body
     * ':' is the separator of OTP from the message
     *
     * @param message
     * @return
     */
    private fun getVerificationCode(message: String): String? {
        var code: String? = null
        val index = message.indexOf(AppConstants.OTP_DELIMITER)

        if (index != -1) {
            val start = index + 1
            val length = 6
            code = message.substring(start, start + length)
            return code
        }

        return code
    }
}