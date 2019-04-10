package `in`.krishkam.base

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import `in`.krishkam.R
import `in`.krishkam.utils.UtiliyMethods
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


abstract class BaseActivity : AppCompatActivity() {
    lateinit var dialog: ProgressDialog


    protected inline fun <reified T : Activity> Activity.launchActivity() {
        val intent = Intent(this, T::class.java)
        startActivity(intent)


    }

    protected inline fun <reified T : Activity> Activity.endActivity() {
        val intent = Intent(this, T::class.java)
        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;

        startActivity(intent)


    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun hideKeyboard(view: View) {
        UtiliyMethods.hideKeyboard(this, view)
    }

    fun showSnackBar(message: String) {


        showAlertDialog(message)
//        val snackbar = Snackbar.make(findViewById<View>(android.R.id.content),
//                message, Snackbar.LENGTH_SHORT)
//        val sbView = snackbar.view
//        val textView = sbView
//                .findViewById(android.support.design.R.id.snackbar_text) as TextView
//        textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
//        snackbar.show()
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    fun showDialogLoading() {

        dialog = ProgressDialog(this)
        dialog.setMessage("Please wait...")
        dialog.setTitle("Loading...")
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.isIndeterminate = true
        if(!dialog.isShowing){
            dialog.show()
        }

    }

    fun hideDialogLoading() {


        if (dialog != null && dialog.isShowing)
            dialog.cancel()


    }

    fun  showAlertDialog(messageError: String){

        val builder = AlertDialog.Builder(this)

        // Set the alert dialog title
        builder.setTitle("कुछ त्रुटि है !")

        // Display a message on alert dialog
        builder.setMessage(messageError)

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton(android.R.string.ok){dialog, which ->
           dialog.dismiss()
        }
        builder.show()


    }
    fun showKeyboard(yourEditText: EditText, activity: Activity) {
        try {
            val input = activity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            input.showSoftInput(yourEditText, InputMethodManager.SHOW_IMPLICIT)
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }


}
