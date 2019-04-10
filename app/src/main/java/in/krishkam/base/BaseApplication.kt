package `in`.krishkam.base

import android.app.Application
import `in`.krishkam.dataprefence.DataManager
import`in`.krishkam.dataprefence.SharedPrefsHelper
import android.content.pm.PackageManager
import com.google.android.youtube.player.internal.d
import android.content.pm.PackageInfo
import android.util.Base64
import android.util.Log
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class BaseApplication:Application() {

    companion object {
        lateinit var baseApplicationInstance: BaseApplication
            private set
    }
    lateinit var dataManager: DataManager


    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        baseApplicationInstance= this
        val sharedPrefsHelper = SharedPrefsHelper(applicationContext)
        dataManager = DataManager(sharedPrefsHelper)
        Log.i("AppApplication","appSignatures = ${AppSignatureHelper(this).getAppSignatures()}")
        printHashKey()

    }

    fun getdatamanger(): DataManager {

        return dataManager
    }

    fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(
                    "in.krishkam", PackageManager.GET_SIGNATURES)

            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }

    }

}