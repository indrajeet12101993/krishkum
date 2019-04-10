package `in`.krishkam.activity


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_user_photo_upload.*
import `in`.krishkam.R
import `in`.krishkam.base.BaseActivity
import `in`.krishkam.base.BaseApplication

import android.content.DialogInterface

import android.os.Build
import `in`.krishkam.dataprefence.DataManager
import android.widget.Toast

import com.karumi.dexter.PermissionToken
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionRequest

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.content.FileProvider
import android.util.Log
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import `in`.krishkam.BuildConfig
import `in`.krishkam.constants.AppConstants.CAMERA_PIC_REQUEST
import `in`.krishkam.constants.AppConstants.IMAGE_DIRECTORY_NAME
import `in`.krishkam.constants.AppConstants.MEDIA_TYPE_IMAGE
import `in`.krishkam.constants.AppConstants.REQUEST_PICK_PHOTO
import `in`.krishkam.constants.AppConstants.REQUEST_TAKE_PHOTO
import `in`.krishkam.networkUtils.ApiRequestClient
import `in`.krishkam.pojo.ServerResponseFromUplaodImage
import `in`.krishkam.utils.UtilityFiles
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger


class UserPhotoUploadActivity : BaseActivity() {

    lateinit var dataManager: DataManager
    private var mCompositeDisposable_remove_Photo: CompositeDisposable? = null

    private val mMediaUri: Uri? = null

    private var fileUri: Uri? = null

    private var mediaPath: String? = null

    private val TAG = UserPhotoUploadActivity::class.java.simpleName

    private var mImageFileLocation = ""

    private var postPath: String? = null
    private var mCompositeDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_photo_upload)
        dataManager = BaseApplication.baseApplicationInstance.getdatamanger()
        if (dataManager.getUserName() != null) {
            tv_name.text = dataManager.getUserName()
        }


        btn_aage_badhe.isEnabled = false
        profile_image.isEnabled = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            requestStoragePermission()

        } else {
            btn_aage_badhe.isEnabled = true
            profile_image.isEnabled = true
        }
        // event on prifile pic upload
        profile_image.setOnClickListener {
            MaterialDialog(this)
                    .title(R.string.uploadImages)

                    .listItems(R.array.itemIds)
                    { dialog, which, text ->
                        when (which) {
                            0 -> {
                                val galleryIntent = Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                                startActivityForResult(galleryIntent, REQUEST_PICK_PHOTO)
                            }
                            1 -> captureImage()
                            2 -> removeImage()

                        }
                    }
                    .show()
        }
        // click event on next button
        btn_aage_badhe.setOnClickListener {

            if (dataManager.getRegistartion()) {

                uploadFile()
            } else {

                showDialogForRegistationComplete()
            }
        }


        btn_baad_me_kare.setOnClickListener {
            launchActivity<UserFeedActivity>()
            finish()
        }
    }

    private fun removeImage() {
        profile_image.setImageResource(R.drawable.ic_launcher_background)
        removimgUserPhoto()
    }


    fun showDialogForRegistationComplete() {
        val builder: AlertDialog.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
        } else {
            builder = AlertDialog.Builder(this)
        }
        builder.setTitle("कोई प्रोफाइल अपडेट नहीं")
                .setMessage("अपनी प्रोफ़ाइल अद्यतित करें")
                .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                    launchActivity<RegistrationActivity>()
                    finish()
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_PICK_PHOTO) {
                if (data != null) {
                    // Get the Image from data
                    val selectedImage = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

                    val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
                    assert(cursor != null)
                    cursor!!.moveToFirst()

                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    mediaPath = cursor.getString(columnIndex)
                    // Set the Image in ImageView for Previewing the Media
                    profile_image.setImageBitmap(BitmapFactory.decodeFile(mediaPath))
                    Glide.with(this).load(mediaPath).into(profile_image)
                    cursor.close()


                    postPath = mediaPath
                }


            } else if (requestCode == CAMERA_PIC_REQUEST) {
                if (Build.VERSION.SDK_INT > 21) {

                    Glide.with(this).load(mImageFileLocation).into(profile_image)
                    postPath = mImageFileLocation

                } else {
                    Glide.with(this).load(fileUri).into(profile_image)
                    postPath = fileUri!!.path

                }

            }

        } else if (resultCode != Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Sorry, there was an error!", Toast.LENGTH_LONG).show()
        }
    }

    private fun captureImage() {
        if (Build.VERSION.SDK_INT > 21) { //use this if Lollipop_Mr1 (API 22) or above
            val callCameraApplicationIntent = Intent()
            callCameraApplicationIntent.action = MediaStore.ACTION_IMAGE_CAPTURE

            // We give some instruction to the intent to save the image
            var photoFile: File? = null

            try {
                // If the createImageFile will be successful, the photo file will have the address of the file
                photoFile = createImageFile()
                // Here we call the function that will try to catch the exception made by the throw function
            } catch (e: IOException) {
                Logger.getAnonymousLogger().info("Exception error in generating the file")
                e.printStackTrace()
            }

            // Here we add an extra file to the intent to put the address on to. For this purpose we use the FileProvider, declared in the AndroidManifest.
            val outputUri = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".provider", photoFile!!)
            callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)


            // The following is a new line with a trying attempt
            callCameraApplicationIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

            Logger.getAnonymousLogger().info("Calling the camera App by intent")

            // The following strings calls the camera app and wait for his file in return.
            startActivityForResult(callCameraApplicationIntent, CAMERA_PIC_REQUEST)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE)

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)

            // start the image capture Intent
            startActivityForResult(intent, CAMERA_PIC_REQUEST)
        }


    }

    @Throws(IOException::class)
    internal fun createImageFile(): File {
        Logger.getAnonymousLogger().info("Generating the image - method started")

        // Here we create a "non-collision file name", alternatively said, "an unique filename" using the "timeStamp" functionality
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmSS").format(Date())
        val imageFileName = "IMAGE_" + timeStamp
        // Here we specify the environment location and the exact path where we want to save the so-created file
        val storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/photo_saving_app")
        Logger.getAnonymousLogger().info("Storage directory set")

        // Then we create the storage directory if does not exists
        if (!storageDirectory.exists()) storageDirectory.mkdir()

        // Here we create the file using a prefix, a suffix and a directory
        val image = File(storageDirectory, imageFileName + ".jpg")
        // File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);

        // Here the location is saved into the string mImageFileLocation
        Logger.getAnonymousLogger().info("File name and path set")

        mImageFileLocation = image.absolutePath
        // fileUri = Uri.parse(mImageFileLocation);
        // The file is returned to the previous intent across the camera application
        return image
    }

    fun getOutputMediaFileUri(type: Int): Uri {
        return Uri.fromFile(getOutputMediaFile(type))
    }

    private fun getOutputMediaFile(type: Int): File? {

        // External sdcard location
        val mediaStorageDir = File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory")
                return null
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(Date())
        val mediaFile: File
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = File(mediaStorageDir.path + File.separator
                    + "IMG_" + ".jpg")
        } else {
            return null
        }

        return mediaFile
    }


    fun requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            btn_aage_badhe.isEnabled = true
                            profile_image.isEnabled = true
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).withErrorListener { Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT).show() }
                .onSameThread()
                .check()
    }

    // Uploading Image/Video
    private fun uploadFile() {
        if (postPath == null || postPath == "") {
            Toast.makeText(this, "please select an image ", Toast.LENGTH_LONG).show()
            return
        } else {
            uploadingUserPhoto()


        }
    }


    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()

    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }


    // api call for user registration
    private fun uploadingUserPhoto() {

        showDialogLoading()
      //  val file = File(postPath!!)
        val file = UtilityFiles.saveBitmapToFile(File(postPath!!))

        val reqFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", file!!.name, reqFile)
        val id: RequestBody = RequestBody.create(MediaType.parse("text/plain"), dataManager.getUserId())

        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable?.add(ApiRequestClient.createREtrofitInstance()
                .uploadImage(body, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }


    // handle sucess response of api call
    private fun handleResponse(response: ServerResponseFromUplaodImage) {
        hideDialogLoading()


        if (response.response_code.equals("0")) {

            if (response.response_message != null)

                dataManager.saveUserProfilePic(response.response_message)
               setUserToTrue()


        }
        if (response.response_code.equals("1")) {
            showSnackBar(response.response_message)
        }

        mCompositeDisposable?.clear()

    }


    private fun setUserToTrue() {

        dataManager.setPhoto(true)
        launchActivity<UserFeedActivity>()
        finish()
    }


    // handle failure response of api call
    private fun handleError(error: Throwable) {
        hideDialogLoading()

        showSnackBar(error.localizedMessage)
        mCompositeDisposable?.clear()

    }
    // api call for user registration
    private fun removimgUserPhoto() {

        showDialogLoading()




        mCompositeDisposable_remove_Photo = CompositeDisposable()

        mCompositeDisposable_remove_Photo?.add(ApiRequestClient.createREtrofitInstance()
                .removeImage(dataManager.getUserId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseRemove_Photo, this::handleError_Remove_photo))
    }


    // handle sucess response of api call
    private fun handleResponseRemove_Photo(response: ServerResponseFromUplaodImage) {
        hideDialogLoading()

        if(response.response_code.equals("0")){

            val alertDialog = android.support.v7.app.AlertDialog.Builder(this).create()
            alertDialog.setTitle("सफलता")
            alertDialog.setMessage("अपका का प्रोफाइल पीक हट  गया!")
            alertDialog.setCanceledOnTouchOutside(true)
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "OK",
                    DialogInterface.OnClickListener {
                        dialog, which ->
                        launchActivity<UserFeedActivity>()
                        finish()})
            alertDialog.show()


        }



        mCompositeDisposable_remove_Photo?.clear()

    }





    // handle failure response of api call
    private fun handleError_Remove_photo(error: Throwable) {

        hideDialogLoading()

        showSnackBar(error.localizedMessage)
        mCompositeDisposable_remove_Photo?.clear()

    }


}
