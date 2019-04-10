package `in`.krishkam.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.HORIZONTAL
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_edit_profile.*
import kotlinx.android.synthetic.main.city_list.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import `in`.krishkam.BuildConfig
import `in`.krishkam.R
import `in`.krishkam.adapter.CustomAdapterForCity
import `in`.krishkam.adapter.CustomAdapterForState
import `in`.krishkam.base.BaseActivity
import `in`.krishkam.base.BaseApplication
import `in`.krishkam.callback.InterfaceCitySelectListner
import `in`.krishkam.callback.InterfaceStateSelectListner
import `in`.krishkam.constants.AppConstants
import `in`.krishkam.constants.AppConstants.REQUEST_PICK_PHOTO
import `in`.krishkam.dataprefence.DataManager
import `in`.krishkam.networkUtils.ApiRequestClient
import `in`.krishkam.pojo.ServerResponseFromUplaodImage
import `in`.krishkam.pojo.UserEditShowInitial.ResponseFromServerInitialEditUser
import `in`.krishkam.pojo.UserEditShowInitial.UserDetail
import `in`.krishkam.pojo.city.ResponseFromServerCity
import `in`.krishkam.pojo.state.ResponseFromServerStateList
import  `in`.krishkam.pojo.state.Result
import `in`.krishkam.utils.UtilityFiles.saveBitmapToFile
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger

class UserEditProfileActivity :BaseActivity() , InterfaceStateSelectListner, InterfaceCitySelectListner {


    private var mCompositeDisposable: CompositeDisposable? = null
    private var mCompositeDisposable_update_Photo: CompositeDisposable? = null
    private var mCompositeDisposable_remove_Photo: CompositeDisposable? = null
    private lateinit var dataManager: DataManager
    var responsefromserverForInitial: MutableList<UserDetail>?=null
    private var mCompositeDisposable_city: CompositeDisposable? = null
    private var mCompositeDisposable_state: CompositeDisposable? = null
    private var mCompositeDisposable_Update_Profile: CompositeDisposable? = null
    private lateinit var dialog_city: Dialog
    private lateinit var dialog_State: Dialog

    private var mAndroidStateList: MutableList<Result>? = null
    private var mAndroidCityList: MutableList<`in`.krishkam.pojo.city.Result>? = null
    private var mStateAdapter: CustomAdapterForState? = null
    private var mCityAdapter: CustomAdapterForCity? = null
    private lateinit var state_id: String
    private lateinit var city_id: String

    private lateinit var city_name: String

    private var mImageFileLocation = ""
    private var fileUri: Uri? = null

    private var mediaPath: String? = null
    private var postPath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_edit_profile)
        dataManager = BaseApplication.baseApplicationInstance.getdatamanger()
        state_id=""
        city_id=""
        getInitalEditProfile()
        bindDataWithUi()




        // show state dialog
        et_state.setOnClickListener {

            // initialaiae sttae list dialog
             initState()
            // execute api of state
             getState()


        }
        // show city dialog
        et_district.setOnClickListener {
           initCity()
        }

        // event on prifile pic upload
        profile_image.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                requestStoragePermission()

            }
            else{
                showDialogForImageCapture()
            }


        }

        // uploading profile
        btn_profile_edit_kare.setOnClickListener {
            val name: String = et_name.text.toString()
            val state: String = et_state.text.toString()
            val district: String = et_district.text.toString()
            val village: String = et_village.text.toString()

            if(name.isNullOrEmpty()||state.isNullOrEmpty()||district.isNullOrEmpty()||village.isNullOrEmpty()){
                showSnackBar("खली जगह भदे !")
            }
            else{
                uploadingUserProfileUpdate(name,state_id,city_id,village)
            }




        }



    }

    // api call for user registration
    private fun uploadingUserProfileUpdate(name: String, state: String, district: String, village: String) {



        showDialogLoading()
//        val file = File(postPath!!)
//        val reqFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
//        val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.getName(), reqFile)
//        val id: RequestBody = RequestBody.create(MediaType.parse("text/plain"), dataManager.getUserId())
//        val name1: RequestBody = RequestBody.create(MediaType.parse("text/plain"),name )
//        val state1: RequestBody = RequestBody.create(MediaType.parse("text/plain"), state)
//        val city1: RequestBody = RequestBody.create(MediaType.parse("text/plain"), district)
//        val village1: RequestBody = RequestBody.create(MediaType.parse("text/plain"), village)

        mCompositeDisposable_Update_Profile = CompositeDisposable()

        mCompositeDisposable_Update_Profile?.add(ApiRequestClient.createREtrofitInstance()
                .uploadUpadteUserProfile(dataManager.getUserId(),name,state,district,village)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUpdateProfile, this::handleErrorUpdateProfile))
    }


    // handle sucess response of api call
    private fun handleResponseUpdateProfile(response: ServerResponseFromUplaodImage) {
        hideDialogLoading()

        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder.setMessage("आपका प्रोफाइल अपडेट हो गया !")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                    dialog, id ->
                    launchActivity<UserFeedActivity>()
                    finish()
                })
                // negative button text and action


        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box

        // show alert dialog
        alert.show()


        mCompositeDisposable_Update_Profile?.clear()

    }

    // handle failure response of api call
    private fun handleErrorUpdateProfile(error: Throwable) {
        hideDialogLoading()

        showSnackBar(error.localizedMessage)
        mCompositeDisposable_Update_Profile?.clear()

    }

    fun showDialogForImageCapture(){

        MaterialDialog(this)
                .title(R.string.uploadImages)

                .listItems(R.array.itemIds)
                { dialog, which, text ->
                    when (which) {
                        0 -> {
                            val galleryIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            galleryIntent.type = "image/jpeg"
                            startActivityForResult(galleryIntent, REQUEST_PICK_PHOTO)
                        }
                        1 -> captureImage()
                        2 -> removeImage()

                    }
                }
                .show()
    }

    private fun removeImage() {

        profile_image.setImageResource(R.drawable.ic_person_black_24dp)
        removimgUserPhoto()

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



            // The following strings calls the camera app and wait for his file in return.
            startActivityForResult(callCameraApplicationIntent, AppConstants.CAMERA_PIC_REQUEST)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            fileUri = getOutputMediaFileUri(AppConstants.MEDIA_TYPE_IMAGE)

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)

            // start the image capture Intent
            startActivityForResult(intent, AppConstants.CAMERA_PIC_REQUEST)
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
                AppConstants.IMAGE_DIRECTORY_NAME)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                return null
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(Date())
        val mediaFile: File
        if (type == AppConstants.MEDIA_TYPE_IMAGE) {
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
                            showDialogForImageCapture()
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
    private fun showSettingsDialog() {
        val builder = android.app.AlertDialog.Builder(this)
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



    private fun bindDataWithUi() {
        if(responsefromserverForInitial!=null){

            state_id=responsefromserverForInitial!!.get(0).stateid
            city_id=responsefromserverForInitial!!.get(0).cityid
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.ic_person_black_24dp)
            requestOptions.error(R.drawable.ic_person_black_24dp)

            Glide.with(this).load(responsefromserverForInitial!!.get(0).image).apply(requestOptions).into(profile_image)
            et_name.setText(responsefromserverForInitial!!.get(0).name)
            et_state.setText(responsefromserverForInitial!!.get(0).statename)
            et_district.setText(responsefromserverForInitial!!.get(0).cityname)
            et_village.setText(responsefromserverForInitial!!.get(0).village)
        }
    }

    // api call for user registration
    private fun getInitalEditProfile() {

        showDialogLoading()
        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable?.add(ApiRequestClient.createREtrofitInstance()
                .getUserInitialEditData(dataManager.getUserId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }


    // handle sucess response of api call
    private fun handleResponse(response: ResponseFromServerInitialEditUser) {
        hideDialogLoading()

        responsefromserverForInitial= response.user_detail
        mCompositeDisposable?.clear()
        bindDataWithUi()


    }


    // handle failure response of api call
    private fun handleError(error: Throwable) {
        mCompositeDisposable?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }
    // initiliaze city dialog
    private fun initCity() {
        dialog_city = Dialog(this)
        dialog_city.setContentView(R.layout.city_list)
        dialog_city.setCanceledOnTouchOutside(false)
        dialog_city.setCancelable(true)
        dialog_city.setTitle(" जिला का चयन करें!")
        dialog_city.recycler_view.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        val itemDecor = DividerItemDecoration(this, HORIZONTAL)
        dialog_city.recycler_view.addItemDecoration(itemDecor)
        if(state_id.isEmpty()){
            val alertDialog = AlertDialog.Builder(this).create()
           // alertDialog.setTitle("स्टेट का चयन करें")
            alertDialog.setMessage("स्टेट का चयन करें!")
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                        initState()
                        // execute api of state
                        getState()


                    })
            alertDialog.show()

        }
        else{

            getCity(state_id)
        }

    }

    // initilizae state dialog
    private fun initState() {
        mAndroidStateList = ArrayList<Result>()

        dialog_State = Dialog(this);
        dialog_State.setContentView(R.layout.city_list)
        dialog_State.setCanceledOnTouchOutside(false)
        dialog_State.setCancelable(true)
        dialog_State.setTitle(" स्टेट का चयन करें!")
        dialog_State.recycler_view.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        val itemDecor = DividerItemDecoration(this, HORIZONTAL)
        dialog_State.recycler_view.addItemDecoration(itemDecor)
    }


    // item click when state is selected
    override fun onItemClickState(result: Result) {


        dialog_State.dismiss()
        state_id = result.id
        et_state.setText(result.name)
        et_state.requestFocus()

    }

    // item click when city is selected
    override fun onItemClickState(result: `in`.krishkam.pojo.city.Result) {

        dialog_city.dismiss()
        city_name= result.name
        city_id=result.id
        et_district.setText(result.name)
        et_district.requestFocus()


    }


    // show state list dialog
    private fun showSTATE() {


        et_district.text=null
        dialog_State.show()
        mStateAdapter?.notifyDataSetChanged()

    }

    // api call for user registration
    private fun getState() {

        showDialogLoading()
        mCompositeDisposable_state = CompositeDisposable()

        mCompositeDisposable_state?.add(ApiRequestClient.createREtrofitInstance()
                .getStateList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_State, this::handleError_State))
    }


    // handle sucess response of api call
    private fun handleResponse_State(responseFromServerStateList: ResponseFromServerStateList) {
        hideDialogLoading()


        if (responseFromServerStateList.result != null) {
            mStateAdapter = CustomAdapterForState(mAndroidStateList!!, this)
            dialog_State.recycler_view.adapter = mStateAdapter
            mAndroidStateList?.addAll(responseFromServerStateList.result)
            showSTATE()

        }
        mCompositeDisposable_state?.clear()


    }


    // handle failure response of api call
    private fun handleError_State(error: Throwable) {
        mCompositeDisposable_state?.clear()
        hideDialogLoading()

        showAlertDialog()


    }

    // api call for user registration
    private fun getCity(stateid: String) {


        showDialogLoading()
        mCompositeDisposable_city = CompositeDisposable()

        mCompositeDisposable_city?.add(ApiRequestClient.createREtrofitInstance()
                .getCityList(stateid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_city, this::handleError_City))
    }


    // handle sucess response of api call
    private fun handleResponse_city(responseFromServerCity: ResponseFromServerCity) {
        hideDialogLoading()


        if (responseFromServerCity.result != null) {
            mCityAdapter = CustomAdapterForCity(responseFromServerCity.result, this)
            dialog_city.recycler_view.adapter = mCityAdapter
            dialog_city.show()





        }
        mCompositeDisposable_state?.clear()


    }


    // handle failure response of api call
    private fun handleError_City(error: Throwable) {
        mCompositeDisposable_state?.clear()
        hideDialogLoading()

        showAlertDialog()


    }


    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("Network Problem")
        alertDialog.setMessage("Reload!")
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    finish()
                })
        alertDialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if ( requestCode == REQUEST_PICK_PHOTO) {
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
                    uploadingUserPhoto()


                }


            } else if (requestCode == AppConstants.CAMERA_PIC_REQUEST) {
                if (Build.VERSION.SDK_INT > 21) {

                    Glide.with(this).load(mImageFileLocation).into(profile_image)
                    postPath = mImageFileLocation
                    uploadingUserPhoto()
                } else {
                    Glide.with(this).load(fileUri).into(profile_image)

                    postPath = fileUri!!.path
                    uploadingUserPhoto()
                }

            }

        } else if (resultCode != Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Sorry, there was an error!", Toast.LENGTH_LONG).show()
        }
    }

    // api call for user registration
    private fun uploadingUserPhoto() {

        showDialogLoading()

       //val file = File(postPath!!)
        val file = saveBitmapToFile(File(postPath!!))



        val reqFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", file!!.name.toString(), reqFile)
        val id: RequestBody = RequestBody.create(MediaType.parse("text/plain"), dataManager.getUserId().toString())

        mCompositeDisposable_update_Photo = CompositeDisposable()

        mCompositeDisposable_update_Photo?.add(ApiRequestClient.createREtrofitInstance()
                .uploadImage(body, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUser_Photo, this::handleError_user_photo))
    }


    // handle sucess response of api call
    private fun handleResponseUser_Photo(response: ServerResponseFromUplaodImage) {
        hideDialogLoading()

       if(response.response_code.equals("0")){

           val alertDialog = AlertDialog.Builder(this).create()
           alertDialog.setTitle("सफलता")
           alertDialog.setMessage("अपका का प्रोफाइल पीक अपडेट हो गया!")
           alertDialog.setCanceledOnTouchOutside(true)
           alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                   DialogInterface.OnClickListener {
                       dialog, which ->
                       launchActivity<UserFeedActivity>()
                   finish()})
           alertDialog.show()


       }



        mCompositeDisposable_update_Photo?.clear()

    }





    // handle failure response of api call
    private fun handleError_user_photo(error: Throwable) {

        hideDialogLoading()

        showSnackBar(error.localizedMessage)
        mCompositeDisposable_update_Photo?.clear()

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

            val alertDialog = AlertDialog.Builder(this).create()
            alertDialog.setTitle("सफलता")
            alertDialog.setMessage("अपका का प्रोफाइल पीक हट  गया!")
            alertDialog.setCanceledOnTouchOutside(true)
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
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
