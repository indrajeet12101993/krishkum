package `in`.krishkam.activity


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.share.Sharer
import com.facebook.share.model.ShareHashtag
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareButton
import com.facebook.share.widget.ShareDialog
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_youtube_player.*
import kotlinx.android.synthetic.main.radiobutton_dialog.*
import `in`.krishkam.R
import `in`.krishkam.base.BaseActivity
import `in`.krishkam.base.BaseApplication
import `in`.krishkam.dataprefence.DataManager
import `in`.krishkam.networkUtils.ApiRequestClient
import `in`.krishkam.pojo.AllPostFeed.Post
import `in`.krishkam.pojo.AllPostFeed.ResponseFromServerAllfeed
import `in`.krishkam.pojo.ResponseFromServerSikayat
import `in`.krishkam.pojo.ResponseFromServerWhatsApp


class YoutubePlayerActivity : YouTubeBaseActivity(){

    private var mCompositeDisposable: CompositeDisposable? = null
    private var mCompositeDisposable_Like: CompositeDisposable? = null
    private var mCompositeDisposable_WhatsApp: CompositeDisposable? = null
    private var mCompositeDisposable_SiKayat: CompositeDisposable? = null
    private var mCompositeDisposable_Facebook: CompositeDisposable? = null
    private lateinit var dataManager: DataManager
    val id: String = "AIzaSyAeygVuWu26XAOibEKmW-GTiSlRLT3vDTA"
    var video: String? = null
    var post: Post?=null
    lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener
    private var updateLike: Int? = null
    private var post_id_whatsApp: String? = null

    private var callbackManager: CallbackManager? = null
    var fb_share_button: ShareButton? = null

    var shareDialog: ShareDialog? = null

    // lateinit var youtubePlayerView : YouTubePlayerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_youtube_player)
        dataManager = BaseApplication.baseApplicationInstance.getdatamanger()
        post =  intent.getSerializableExtra("youtubeVideoId") as Post
        fb_share_button = fb_share_button1

        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()
        shareDialog = ShareDialog(this)
        shareDialog?.registerCallback(callbackManager, callback)

        video= post?.link.toString()
        relative_layout.setOnClickListener {

            if(tv_tile.visibility==View.VISIBLE &&  tv_bottom.visibility==View.VISIBLE)
            {
                tv_tile.visibility= View.INVISIBLE
                tv_bottom.visibility= View.INVISIBLE
            }
            else{

                tv_tile.visibility= View.VISIBLE
                tv_bottom.visibility= View.VISIBLE
                tv_tile.text=post?.headline
                tv_bottom.text=post?.content
            }


        }

        initial()

        youtube_player.initialize(id, youtubePlayerInit)
        // like functionality
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

            if (iv_like_first.visibility == View.VISIBLE) {
                iv_like_second.visibility = View.VISIBLE
                iv_like_first.visibility = View.INVISIBLE
                val like: Int = updateLike!! + 1
                val Totallike: String = like.toString()
                tv_total_like.text = Totallike
                getAlltotalLike(post!!, "1")
            } else {
                iv_like_second.visibility = View.INVISIBLE
                iv_like_first.visibility = View.VISIBLE

                tv_total_like.text = updateLike.toString()
                getAlltotalLike(post!!, "0")
            }


        }
        //whatsapp share
        tv_total_whattsapp.text = post!!.no_share
        linear_whatsapp.setOnClickListener {


            post_id_whatsApp = post!!.post_id

            val sendIntent = Intent()
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, post!!.headline + "\n"
                    + "https://www.youtube.com/watch?v=" + post!!.link + "\n" +
                    post!!.content)
            sendIntent.type = "text/plain"
            sendIntent.setPackage("com.whatsapp")

            try {
                startActivityForResult(sendIntent, 1);
            } catch (ex: android.content.ActivityNotFoundException) {
                showSnackBar("Whatsapp have not been installed.")

            }

        }
        //comment
        tv_total_cooment.text = post!!.no_comment
        linear_comment.setOnClickListener {

            val intent = Intent(this, CommentsActivity::class.java)
            intent.putExtra("post_id", post!!.post_id)
            startActivity(intent)

        }
        // shikayat
        tv_shikayat.setOnClickListener {

            showRadioButtonDialog(post!!)
        }

        //facebook
        tv_total_facebook.text = post!!.no_fb_share
        linear_facebook.setOnClickListener {
            post_id_whatsApp = post!!.post_id

            val content = ShareLinkContent.Builder()

                    .setContentUrl(Uri.parse("https://www.youtube.com/watch?v=" + post!!.link))
                    .setShareHashtag(ShareHashtag.Builder()
                            .setHashtag(post!!.hashtag_id)
                            .build())
                    .setQuote(post!!.headline)
                    .build()
            fb_share_button?.shareContent = content
            fb_share_button?.performClick()

        }

    }

    private val callback = object : FacebookCallback<Sharer.Result> {


        override fun onSuccess(result: Sharer.Result) {

            //Toast.makeText(activity, "success", Toast.LENGTH_SHORT).show()

            getTotalFacebookLike()
        }

        override fun onCancel() {
//
            //Toast.makeText(activity, "cancel", Toast.LENGTH_SHORT).show()

        }

        override fun onError(error: FacebookException) {
            // Toast.makeText(activity, "cancel", Toast.LENGTH_SHORT).show()


        }
    }
    private fun getTotalFacebookLike() {

        //  showDialogLoading()
        mCompositeDisposable_Facebook = CompositeDisposable()
        mCompositeDisposable_Facebook?.add(ApiRequestClient.createREtrofitInstance()
                .getsharecountWhatsApp(post_id_whatsApp, "fb")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_facebook, this::handleError_facebook))
    }


    // handle sucess response of api call
    private fun handleResponse_facebook(responseFromServerWhatsApp: ResponseFromServerWhatsApp) {

        val item = responseFromServerWhatsApp.post_id
        val result = responseFromServerWhatsApp.result
        tv_total_facebook.text = result
        mCompositeDisposable_Facebook?.clear()


    }


    // handle failure response of api call
    private fun handleError_facebook(error: Throwable) {
        mCompositeDisposable_Facebook?.clear()

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
                    postSikayat(post.post_id, sikayat)
                    dialog.dismiss()
                } else {
                    postSikayat(post.post_id, radio.text.toString())
                    dialog.dismiss()
                }
            } else {
                showSnackBar("Nothing selected!")
            }

        }


    }


    // api call for user registration
    private fun postSikayat(post_id: String, sikayat: String) {


        mCompositeDisposable_SiKayat = CompositeDisposable()
        mCompositeDisposable_SiKayat?.add(ApiRequestClient.createREtrofitInstance()
                .postSikayat(post_id, sikayat)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_Sikayat, this::handleError_Sikayat))
    }


    // handle sucess response of api call
    private fun handleResponse_Sikayat(responseFromServerSikayat: ResponseFromServerSikayat) {

        mCompositeDisposable_SiKayat?.clear()


    }


    // handle failure response of api call
    private fun handleError_Sikayat(error: Throwable) {
        mCompositeDisposable_SiKayat?.clear()

        showSnackBar(error.localizedMessage)


    }

    // api call for user registration
    private fun getAlltotalLike(post: Post, like: String) {

        //  showDialogLoading()
        mCompositeDisposable_Like = CompositeDisposable()

        mCompositeDisposable_Like?.add(ApiRequestClient.createREtrofitInstance()
                .getTotalLike(dataManager.getUserId(), post.post_id, like)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_Like, this::handleError_like))
    }


    // handle sucess response of api call
    private fun handleResponse_Like(responseFromServerAllfeed: ResponseFromServerAllfeed) {
        //   hideDialogLoading()
        //  mUserAllFeedList?.clear()
        // mUserAllFeedList?.addAll(responseFromServerAllfeed.post)

        //  mALlFeedVideoAdapter?.notifyDataSetChanged()
        mCompositeDisposable_Like?.clear()


    }


    // handle failure response of api call
    private fun handleError_like(error: Throwable) {


        showSnackBar(error.localizedMessage)
        mCompositeDisposable_Like?.clear()


    }


    private fun initial() {
        youtubePlayerInit = object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer?, p2: Boolean) {
                p1?.cueVideo(video)
             //   p1?.loadVideo(video)
                //  p1?.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);

            }

            override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {

            }

        }
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

    private fun getTotalWhatsAppLike() {

        //  showDialogLoading()
        mCompositeDisposable_WhatsApp = CompositeDisposable()

        mCompositeDisposable_WhatsApp?.add(ApiRequestClient.createREtrofitInstance()
                .getsharecountWhatsApp(post_id_whatsApp, "wp")
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

        showSnackBar(error.localizedMessage)


    }

    fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(findViewById<View>(android.R.id.content),
                message, Snackbar.LENGTH_SHORT)
        val sbView = snackbar.view
        val textView = sbView
                .findViewById(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        snackbar.show()
    }



}
