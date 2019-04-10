package `in`.krishkam.base

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import `in`.krishkam.R
import `in`.krishkam.utils.UtiliyMethods

abstract class BaseFragment:Fragment() {
    lateinit var dialog: ProgressDialog


    fun  launchActivity( T : Activity) {

        val intent = Intent(activity, T::class.java)
        startActivity(intent)


    }

    fun showDialogLoading() {

        dialog = ProgressDialog(activity)
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
    protected fun hideKeyboard(view: View) {
        UtiliyMethods.hideKeyboard(activity!!, view)
    }



    fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(activity!!.findViewById<View>(android.R.id.content),
                message, Snackbar.LENGTH_SHORT)
        val sbView = snackbar.view
        val textView = sbView
                .findViewById(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
        snackbar.show()
    }

    fun  showAlertDialog( title:String,messageError: String){

        val builder = AlertDialog.Builder(activity!!)

        // Set the alert dialog title
        builder.setTitle(title)

        // Display a message on alert dialog
        builder.setMessage(messageError)

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton(android.R.string.ok){dialog, which ->
            dialog.dismiss()
        }
        builder.show()


    }
}