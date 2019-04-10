package `in`.krishkam.activity

import `in`.krishkam.R
import `in`.krishkam.base.BaseActivity
import `in`.krishkam.base.BaseApplication
import `in`.krishkam.dataprefence.DataManager
import `in`.krishkam.networkUtils.ApiRequestClient
import `in`.krishkam.pojo.AllPostFeed.Post
import `in`.krishkam.pojo.ResponseFromServerSikayat
import `in`.krishkam.pojo.ResponseFromServerWhatsApp
import `in`.krishkam.pojo.listBlog.ResponseFromServerListBlog
import `in`.krishkam.utils.TimeUtils
import `in`.krishkam.utils.UtilityFiles
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.RadioGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_lekh_read_more.*
import kotlinx.android.synthetic.main.radiobutton_dialog.*

class HomeLekhReadMoreActivity : BaseActivity() {

    var post: Post?=null
    private var updateLike: Int? = null
    private var mCompositeDisposable_Like: CompositeDisposable? = null
    private var mCompositeDisposable_WhatsApp: CompositeDisposable? = null
    private var mCompositeDisposable_SiKayat: CompositeDisposable? = null
    private var mCompositeDisposable_Facebook: CompositeDisposable? = null
    private lateinit var dataManager: DataManager
    private var post_id_whatsApp: String? = null
    var iscallForCommentUpdate:Boolean= false
    private var callbackManager: CallbackManager? = null
    var shareDialog: ShareDialog? = null
    var like: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.activity_lekh_read_more)
        dataManager = BaseApplication.baseApplicationInstance.getdatamanger()
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.title = getString(R.string.lekh)
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.white))
        post =  intent.getSerializableExtra("youtubeVideoId") as Post
        val server_time_timestamp = TimeUtils.getServerTimeStamp(post!!.created)

        val relativeTime = TimeUtils.getTimeAgo(server_time_timestamp)
        tv_time.text = relativeTime

        callbackManager = CallbackManager.Factory.create()



        Glide.with(this).load(post!!.image).into(iv_lekh_view)

        tv_headline.text = post!!.title
        tv_content.text = Html.fromHtml(post!!.content)


        // click on linar like
        tv_total_like.text = post!!.no_like
        if (post!!.Isliked.equals("0")) {
            iv_like_first.visibility = View.VISIBLE
            iv_like_second.visibility = View.INVISIBLE
        } else {
            iv_like_second.visibility = View.VISIBLE
            iv_like_first.visibility = View.INVISIBLE
        }
        updateLike = post!!.no_like.toInt()

        linear_like.setOnClickListener {



            if (post!!.Isliked.equals("1")) {
                updateLike = post!!.no_like.toInt()
                if (iv_like_second.visibility == View.VISIBLE) {
                    iv_like_second.visibility = View.INVISIBLE
                    iv_like_first.visibility = View.VISIBLE
                    like = updateLike!! - 1
                    val Totallike: String = like.toString()
                    tv_total_like.text = Totallike
                    getAlltotalLike(post!!, "0")
                    return@setOnClickListener
                }
                if (iv_like_first.visibility == View.VISIBLE) {
                    iv_like_first.visibility = View.INVISIBLE
                    iv_like_second.visibility = View.VISIBLE
                    val like1: Int = like!! + 1
                    val Totallike: String = like1.toString()
                    tv_total_like.text = Totallike
                    getAlltotalLike(post!!, "1")
                    return@setOnClickListener

                }
            }
            if (post!!.Isliked.equals("0")) {
                updateLike = post!!.no_like.toInt()
                if (iv_like_first.visibility == View.VISIBLE) {
                    iv_like_second.visibility = View.VISIBLE
                    iv_like_first.visibility = View.INVISIBLE
                    like = updateLike!! + 1
                    val Totallike: String = like.toString()
                    tv_total_like.text = Totallike
                    getAlltotalLike(post!!, "1")
                    return@setOnClickListener
                } else {
                    iv_like_second.visibility = View.INVISIBLE
                    iv_like_first.visibility = View.VISIBLE
                    //val updateLike  =post.no_like.toInt()-1
                    //  val like: Int = updateLike
                    //  val Totallike: String = like.toString()
                    val like1: Int = like!! - 1
                    val Totallike: String = like1.toString()
                    tv_total_like.text = updateLike.toString()
                    getAlltotalLike(post!!, "0")
                    return@setOnClickListener
                }
            }





        }

        //whatsapp share
        tv_total_whattsapp.text = post!!.no_share
        linear_whatsapp.setOnClickListener {

            tv_total_whattsapp.text = (post!!.no_share.toInt()+1).toString()
            post_id_whatsApp = post!!.blog_id
            val appplaystore:String= "https://play.google.com/store/apps/details?id=in.krishkam"
            Glide.with(this).asBitmap()
                    .load(post!!.image)
                    .into(object : SimpleTarget<Bitmap>(100, 100) {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val sendIntent = Intent()
                            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            sendIntent.action = Intent.ACTION_SEND
                            sendIntent.type = "image/*"
                            sendIntent.putExtra(Intent.EXTRA_STREAM, UtilityFiles.getLocalBitmapUri(resource, applicationContext))
                            sendIntent.putExtra(Intent.EXTRA_TEXT, post!!.title + "\n"+"देखें कृषकं ऍप पर, अभी फ्री डाउनलोड करे"+
                                    "\n"+appplaystore)
                            sendIntent.setPackage("com.whatsapp")

                            try {
                                startActivityForResult(sendIntent, 1);
                            } catch (ex: android.content.ActivityNotFoundException) {
                                showSnackBar("Whatsapp have not been installed.")

                            }
                        }


                    })

        }

        //comment
        tv_total_cooment.text = post!!.no_comment
        linear_comment.setOnClickListener {

            iscallForCommentUpdate= true
            val intent = Intent(this, CommentsActivity::class.java)
            intent.putExtra("post_id", post!!.blog_id)
            startActivity(intent)



        }

//        // shikayat
//        tv_shikayat.setOnClickListener {
//
//            showRadioButtonDialog(post!!)
//        }


        //facebook
        tv_total_facebook.text = post!!.no_fb_share
        linear_facebook.setOnClickListener {
            tv_total_facebook.text = (post!!.no_fb_share.toInt()+1).toString()
            shareDialog = ShareDialog(this)
            val appplaystore:String= "https://play.google.com/store/apps/details?id=in.krishkam"
            post_id_whatsApp = post!!.blog_id

            if (ShareDialog.canShow(ShareLinkContent::class.java)) {
                shareDialog?.registerCallback(callbackManager, callback)

                val linkContent = ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(appplaystore))
                        .build()
                shareDialog!!.show(linkContent)

            }



        }
    }
    private val callback = object : FacebookCallback<Sharer.Result> {


        override fun onSuccess(result: Sharer.Result) {

            // Toast.makeText(this@lekhReadMoreActivity, "success", Toast.LENGTH_SHORT).show()

            getTotalFacebookLike()
        }

        override fun onCancel() {

            //   Toast.makeText(this@lekhReadMoreActivity, "cancel", Toast.LENGTH_SHORT).show()

        }

        override fun onError(error: FacebookException) {
            // Toast.makeText(this@lekhReadMoreActivity, "error", Toast.LENGTH_SHORT).show()


        }
    }

    private fun getTotalFacebookLike() {

        //  showDialogLoading()
        mCompositeDisposable_Facebook = CompositeDisposable()
        mCompositeDisposable_Facebook?.add(ApiRequestClient.createREtrofitInstance()
                .getsharecountWhatsAppForlekh(post_id_whatsApp, "fb")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_facebook, this::handleError_facebook))
    }


    // handle sucess response of api call
    private fun handleResponse_facebook(responseFromServerWhatsApp: ResponseFromServerWhatsApp) {


        val result = responseFromServerWhatsApp.result
        tv_total_facebook.text = result
        mCompositeDisposable_Facebook?.clear()


    }


    // handle failure response of api call
    private fun handleError_facebook(error: Throwable) {
        mCompositeDisposable_Facebook?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }


    private fun showRadioButtonDialog(post: Post) {

        // custom dialog
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.setContentView(R.layout.radiobutton_dialog)
        dialog.setTitle("मै इस पोस्ट का विरोध करता हूँ ,क्यूकि ये पोस्ट...")

        dialog.show()
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT)

        dialog.radioGroup.setOnCheckedChangeListener(
                RadioGroup.OnCheckedChangeListener { group, checkedId ->
                    if (checkedId == R.id.anaykuch) {


                        dialog.et_anay_kuch.visibility = View.VISIBLE
                    }
                })
        dialog.radhkare.setOnClickListener {
            dialog.dismiss()
        }
        dialog.buttonthhikhai.setOnClickListener {
            val id: Int = dialog.radioGroup.checkedRadioButtonId
            if (id != -1) {

                val radio: RadioButton = dialog.findViewById(id)
                if (radio.text.equals("अन्य कुछ")) {
                    val sikayat = dialog.et_anay_kuch.text.toString()
                    postSikayat(post.blog_id, sikayat)
                    dialog.dismiss()
                } else {
                    postSikayat(post.blog_id, radio.text.toString())
                    dialog.dismiss()
                }
            } else {
                showSnackBar("Nothing selected!")
            }

        }


    }
    // api call for user registration
    private fun postSikayat(post_id: String, sikayat: String) {

        showDialogLoading()
        mCompositeDisposable_SiKayat = CompositeDisposable()
        mCompositeDisposable_SiKayat?.add(ApiRequestClient.createREtrofitInstance()
                .postSikayat(post_id, sikayat)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_Sikayat, this::handleError_Sikayat))
    }


    // handle sucess response of api call
    private fun handleResponse_Sikayat(responseFromServerSikayat: ResponseFromServerSikayat) {
        hideDialogLoading()
        mCompositeDisposable_SiKayat?.clear()


    }


    // handle failure response of api call
    private fun handleError_Sikayat(error: Throwable) {
        mCompositeDisposable_SiKayat?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }


    private fun getAlltotalLike(post: Post, like: String) {


        //  showDialogLoading()
        mCompositeDisposable_Like = CompositeDisposable()

        mCompositeDisposable_Like?.add(ApiRequestClient.createREtrofitInstance()
                .getTotalLikeForLekh(dataManager.getUserId(), post.blog_id, like)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_Like, this::handleError_like))
    }


    // handle sucess response of api call
    private fun handleResponse_Like(responseFromServerAllfeed: ResponseFromServerListBlog) {

        mCompositeDisposable_Like?.clear()


    }


    // handle failure response of api call
    private fun handleError_like(error: Throwable) {


        showSnackBar(error.localizedMessage)
        mCompositeDisposable_Like?.clear()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)



        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                getTotalWhatsAppLike()
                // Toast.makeText(activity, "Got Callback yeppeee...:", Toast.LENGTH_SHORT).show()
            }
        } else {
            //Toast.makeText(activity, "cancel", Toast.LENGTH_SHORT).show()
        }
    }

    // api call for user registration
    private fun getTotalWhatsAppLike() {

        //  showDialogLoading()
        mCompositeDisposable_WhatsApp = CompositeDisposable()

        mCompositeDisposable_WhatsApp?.add(ApiRequestClient.createREtrofitInstance()
                .getsharecountWhatsAppForlekh(post_id_whatsApp, "wp")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_whatsApp, this::handleError_whatsApp))
    }


    // handle sucess response of api call
    private fun handleResponse_whatsApp(responseFromServerWhatsApp: ResponseFromServerWhatsApp) {

        val item = responseFromServerWhatsApp.post_id
        val result = responseFromServerWhatsApp.result

        tv_total_whattsapp.text = result



        mCompositeDisposable_WhatsApp?.clear()


    }


    // handle failure response of api call
    private fun handleError_whatsApp(error: Throwable) {
        mCompositeDisposable_WhatsApp?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {

            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
