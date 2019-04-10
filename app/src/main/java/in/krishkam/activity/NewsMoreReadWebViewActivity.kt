package`in`.krishkam.activity


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.*


import android.webkit.WebView
import `in`.krishkam.base.BaseActivity
import `in`.krishkam.pojo.listnews.Result
import android.webkit.WebViewClient
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.share.Sharer
import com.facebook.share.model.ShareHashtag
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareButton
import com.facebook.share.widget.ShareDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_news_more_read_web_view.*

import kotlinx.android.synthetic.main.radiobutton_dialog.*
import `in`.krishkam.Fragment.VideoFragment
import `in`.krishkam.R
import `in`.krishkam.adapter.CustomAdapterForSmacharFragment
import `in`.krishkam.base.BaseApplication
import `in`.krishkam.dataprefence.DataManager
import `in`.krishkam.networkUtils.ApiRequestClient
import `in`.krishkam.pojo.ResponseFromServerSikayat
import `in`.krishkam.pojo.ResponseFromServerWhatsApp
import `in`.krishkam.pojo.listBlog.ResponseFromServerListBlog
import `in`.krishkam.utils.UtilityFiles
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition


class NewsMoreReadWebViewActivity : BaseActivity() {

    private var mCompositeDisposable: CompositeDisposable? = null
    private var mCompositeDisposable_Like: CompositeDisposable? = null
    private var mCompositeDisposable_WhatsApp: CompositeDisposable? = null
    private var mCompositeDisposable_SiKayat: CompositeDisposable? = null
    private var mCompositeDisposable_Facebook: CompositeDisposable? = null
    private var mCompositeDisposable_Saved_Feed: CompositeDisposable? = null
    private lateinit var dataManager: DataManager
    private var mUserAllFeedList: MutableList<Result>? = null
    private var mALlFeedVideoAdapter: CustomAdapterForSmacharFragment? = null
    private var post_id_whatsApp: String? = null
    private var post_id_Position: Int? = null
    private var callbackManager: CallbackManager? = null
    var shareDialog: ShareDialog? = null
    var iscallForCommentUpdate: Boolean = false
    var post: Result? = null
    private var updateLike: Int? = null
    var like: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_more_read_web_view)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        dataManager = BaseApplication.baseApplicationInstance.getdatamanger()
        post = intent.getSerializableExtra("samachardata") as Result
        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()
        shareDialog = ShareDialog(this)
        shareDialog?.registerCallback(callbackManager, callback)
        fb_share_button!!.performClick()
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        val webViewClient = MyWebViewClient()
        webView.webViewClient = webViewClient
        webView!!.loadUrl(post!!.url)
        tv_source.text=post!!.source
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
                    userPostSelectPostLike(post!!, "0")
                    return@setOnClickListener
                }
                if (iv_like_first.visibility == View.VISIBLE) {
                    iv_like_first.visibility = View.INVISIBLE
                    iv_like_second.visibility = View.VISIBLE
                    val like1: Int = like!! + 1
                    val Totallike: String = like1.toString()
                    tv_total_like.text = Totallike
                    userPostSelectPostLike(post!!, "1")
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
                    userPostSelectPostLike(post!!, "1")
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
                    userPostSelectPostLike(post!!, "0")
                    return@setOnClickListener
                }
            }


        }
        //whatsapp share
        tv_total_whattsapp.text = post!!.wp_share
        linear_whatsapp.setOnClickListener {
            userPostSelectWhatsApp(post!!, 0)

        }
        //comment
        tv_total_cooment.text = post!!.no_comment
        linear_comment.setOnClickListener {

            userPostSelectComment(post!!, 0)

        }
        //facebook
        tv_total_facebook.text = post!!.fb_share
        linear_facebook.setOnClickListener {
            tv_total_facebook.text = (post!!.fb_share.toInt()+1).toString()
            userPostSelectfacebook(post!!, 0)
        }


    }


    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            progressBar.visibility = View.GONE
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && this.webView.canGoBack()) {
            this.webView.goBack()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    fun userPostSelectfacebook(post: Result, position: Int) {
        post_id_whatsApp = post.news_id
        post_id_Position = position
        val appplaystore: String = "https://play.google.com/store/apps/details?id=in.krishkam"


        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
            shareDialog?.registerCallback(callbackManager, callback)

            val linkContent = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(appplaystore))
                    .build()
            shareDialog!!.show(linkContent)

        }

    }


    fun userPostSelectSikayat(post: Result) {
        showRadioButtonDialog(post)
    }

    fun userPostSelectComment(post: Result, position: Int) {
        iscallForCommentUpdate = true
        val intent = Intent(this, CommentsActivity::class.java)
        intent.putExtra("post_id", post.news_id)
        startActivity(intent)

    }


    fun userPostSelectWhatsApp(post: Result, position: Int) {
        tv_total_whattsapp.text = (post!!.wp_share.toInt()+1).toString()
        post_id_whatsApp = post.news_id
        post_id_Position = position
        val appplaystore: String = "https://play.google.com/store/apps/details?id=in.krishkam"
        Glide.with(this).asBitmap()
                .load(post.news_image)
                .into(object : SimpleTarget<Bitmap>(100, 100) {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val sendIntent = Intent()
                        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        sendIntent.action = Intent.ACTION_SEND
                        sendIntent.type = "image/*"
                        sendIntent.putExtra(Intent.EXTRA_STREAM, UtilityFiles.getLocalBitmapUri(resource, applicationContext))
                        sendIntent.putExtra(Intent.EXTRA_TEXT, post.title + "\n" + "देखें कृषकं ऍप पर, अभी फ्री डाउनलोड करे" +
                                "\n" + appplaystore)
                        sendIntent.setPackage("com.whatsapp")

                        try {
                            startActivityForResult(sendIntent, 1);
                        } catch (ex: android.content.ActivityNotFoundException) {
                            showSnackBar("Whatsapp have not been installed.")

                        }
                    }


                })


    }

    fun userPostSelectPostLike(post: Result, like: String) {
        getAlltotalLike(post, like)
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

    private val callback = object : FacebookCallback<Sharer.Result> {


        override fun onSuccess(result: Sharer.Result) {


            getTotalFacebookLike()
        }

        override fun onCancel() {
//


        }

        override fun onError(error: FacebookException) {


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

        val item = responseFromServerWhatsApp.post_id
   //     val result = responseFromServerWhatsApp.result
//        mUserAllFeedList!!.forEach {
//            if (it.news_id.equals(item)) {
//
//
//                val post: Result = Result(it.news_id, it.hashtag_id, it.title, it.news_content,
//                        it.news_image, it.url, it.created,
//                        it.no_like, it.no_comment, result, it.wp_share, it.Isliked, it.source)
//
//                mUserAllFeedList!!.set(post_id_Position!!, post)
//                mALlFeedVideoAdapter?.notifyItemChanged(post_id_Position!!)
//            }
//        }
       // mCompositeDisposable_Facebook?.clear()
      //  val result = responseFromServerWhatsApp.result
     //   tv_total_facebook.text = result
        mCompositeDisposable_Facebook?.clear()


    }


    // handle failure response of api call
    private fun handleError_facebook(error: Throwable) {
        mCompositeDisposable_Facebook?.clear()
      //  hideDialogLoading()
        showSnackBar(error.localizedMessage)


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
//        val item = responseFromServerWhatsApp.post_id
//        val result = responseFromServerWhatsApp.result
//
//        tv_total_whattsapp.text = result



        mCompositeDisposable_WhatsApp?.clear()


    }


    // handle failure response of api call
    private fun handleError_whatsApp(error: Throwable) {
        mCompositeDisposable_WhatsApp?.clear()
     //   hideDialogLoading()
      //  showSnackBar(error.localizedMessage)


    }

    // api call for user registration
    private fun getAlltotalLike(post: Result, like: String) {

        //  showDialogLoading()
        mCompositeDisposable_Like = CompositeDisposable()

        mCompositeDisposable_Like?.add(ApiRequestClient.createREtrofitInstance()
                .getTotalLikeForLekh(dataManager.getUserId(), post.news_id, like)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_Like, this::handleError_like))
    }


    // handle sucess response of api call
    private fun handleResponse_Like(responseFromServerAllfeed: ResponseFromServerListBlog) {
        //   hideDialogLoading()
        //  mUserAllFeedList?.clear()
        // mUserAllFeedList?.addAll(responseFromServerAllfeed.post)

        //  mALlFeedVideoAdapter?.notifyDataSetChanged()
        mCompositeDisposable_Like?.clear()


    }


    // handle failure response of api call
    private fun handleError_like(error: Throwable) {

        hideDialogLoading()
        showSnackBar(error.localizedMessage)
        mCompositeDisposable_Like?.clear()


    }


    private fun showRadioButtonDialog(post: Result) {

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
                    postSikayat(post.news_id, sikayat)
                    dialog.dismiss()
                } else {
                    postSikayat(post.news_id, radio.text.toString())
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
