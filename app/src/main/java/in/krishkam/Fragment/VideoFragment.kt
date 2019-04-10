package `in`.krishkam.Fragment


import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.LinearLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_video.view.*
import `in`.krishkam.R
import `in`.krishkam.activity.YoutubePlayerActivity
import `in`.krishkam.adapter.CustomVideoListFragmentAdpter
import `in`.krishkam.base.BaseApplication
import `in`.krishkam.base.BaseFragment
import `in`.krishkam.callback.InterfaceAllfeedPostSelectlistner
import `in`.krishkam.dataprefence.DataManager
import `in`.krishkam.networkUtils.ApiRequestClient
import `in`.krishkam.pojo.AllPostFeed.Post
import `in`.krishkam.pojo.AllPostFeed.ResponseFromServerAllfeed
import java.io.Serializable
import `in`.krishkam.activity.CommentsActivity
import `in`.krishkam.callback.IFragmentManager
import `in`.krishkam.pojo.ResponseFromServerWhatsApp
import android.widget.RadioButton
import android.widget.RadioGroup
import com.facebook.FacebookSdk
import kotlinx.android.synthetic.main.radiobutton_dialog.*
import`in`.krishkam.pojo.ResponseFromServerSikayat
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.facebook.CallbackManager
import com.facebook.share.widget.ShareButton
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.facebook.FacebookCallback
import`in`.krishkam.callback.InterFaceAllfeedVideoSelectListner
import `in`.krishkam.pojo.ResponseFromSerVerAddPostList
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.widget.SwipeRefreshLayout


class VideoFragment : BaseFragment(), InterFaceAllfeedVideoSelectListner, IFragmentManager, SwipeRefreshLayout.OnRefreshListener {
    override fun onRefresh() {
        getAllUserFeedForSwipe()

    }

    override fun userPostSelectReadMore(post: Post) {

    }

    override fun getSupportFragmentManager(): FragmentManager {
        return fragmentManager!!
    }

    override fun getSupportFragment(): Fragment {
        return this

    }

    override fun userPostSelectSavedPost(post: Post, position: Int,type:String) {
        postToAddSavedFeed(post.user_id,post.post_id,type)

    }

    override fun userPostSelectfacebook(post: Post, position: Int) {
        post_id_whatsApp = post.post_id
        post_id_Position = position
        val appplaystore:String= "https://play.google.com/store/apps/details?id=in.krishkam"


        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
            shareDialog?.registerCallback(callbackManager, callback)

            val linkContent = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(appplaystore))
                    .setQuote(post.headline)
                    .build()
            shareDialog!!.show(linkContent)

        }


    }


    override fun userPostSelectSikayat(post: Post) {
        showRadioButtonDialog(post)
    }

    override fun userPostSelectComment(post: Post, position: Int) {
        iscallForCommentUpdate= true
        val intent = Intent(activity, CommentsActivity::class.java)
        intent.putExtra("post_id", post.post_id)
        startActivity(intent)

    }


    override fun userPostSelectWhatsApp(post: Post, position: Int) {
        post_id_whatsApp = post.post_id
        post_id_Position = position
        val appplaystore:String= "https://play.google.com/store/apps/details?id=in.krishkam"
        val sendIntent = Intent()
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, post.headline + "\n"+"देखें कृषकं ऍप पर, अभी फ्री डाउनलोड करे"+
                "\n"+appplaystore)
        sendIntent.type = "text/plain"
        sendIntent.setPackage("com.whatsapp")

        try {
            startActivityForResult(sendIntent, 1);
        } catch (ex: android.content.ActivityNotFoundException) {
            showSnackBar("Whatsapp have not been installed.")

        }


    }

    override fun userPostSelectPostLike(post: Post, like: String) {
        getAlltotalLike(post, like)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)



        if (resultCode == RESULT_OK) {
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

           // Toast.makeText(activity, "success", Toast.LENGTH_SHORT).show()

            getTotalFacebookLike()
        }

        override fun onCancel() {
//
          //  Toast.makeText(activity, "cancel", Toast.LENGTH_SHORT).show()

        }

        override fun onError(error: FacebookException) {
          //  Toast.makeText(activity, "cancel", Toast.LENGTH_SHORT).show()


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
        mUserAllFeedList!!.forEach {
//            if (it.post_id.equals(item)) {
//                val post: Post = Post(it.post_id, it.user_id, it.headline, it.content,
//                        it.post_image, it.hashtag_id, it.no_like, it.no_comment,
//                        it.created, result, it.link, it.post_type, it.username, it.uimage, it.Isliked, it.IsSavedpost
//                        , it.no_share,it.source,it.is_reported,it.news_id,it.title,it.news_content,
//                        it.news_image,it.url,it.blog_id,it.image,it.image1,it.image2,it.image3)
//                mUserAllFeedList!!.set(post_id_Position!!, post)
//                mUserAllFeedList!!.set(post_id_Position!!, post)
//                mALlFeedVideoAdapter?.notifyItemChanged(post_id_Position!!)
            //}
        }
        mCompositeDisposable_Facebook?.clear()


    }


    // handle failure response of api call
    private fun handleError_facebook(error: Throwable) {
        mCompositeDisposable_Facebook?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }

    private fun postToAddSavedFeed(user_id: String, post_id: String, type: String) {

        // showDialogLoading()
        mCompositeDisposable_Saved_Feed = CompositeDisposable()
        mCompositeDisposable_Saved_Feed?.add(ApiRequestClient.createREtrofitInstance()
                .postSavedPostFeed(dataManager.getUserId(),post_id,type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_feed, this::handleError_svedFedd))
    }


    // handle sucess response of api call
    private fun handleResponse_feed(responseFromSerVerAddPostList:ResponseFromSerVerAddPostList) {

        mCompositeDisposable_Saved_Feed?.clear()


    }


    // handle failure response of api call
    private fun handleError_svedFedd(error: Throwable) {
        hideDialogLoading()
        mCompositeDisposable_Saved_Feed?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }
    private var mCompositeDisposable: CompositeDisposable? = null
    private var mCompositeDisposable_Like: CompositeDisposable? = null
    private var mCompositeDisposable_WhatsApp: CompositeDisposable? = null
    private var mCompositeDisposable_SiKayat: CompositeDisposable? = null
    private var mCompositeDisposable_Facebook: CompositeDisposable? = null
    private var mCompositeDisposable_Saved_Feed: CompositeDisposable? = null
    private lateinit var dataManager: DataManager
    private var mUserAllFeedList: MutableList<Post>? = null
    private var mALlFeedVideoAdapter: CustomVideoListFragmentAdpter? = null
    private var post_id_whatsApp: String? = null
    private var post_id_Position: Int? = null
    private var callbackManager: CallbackManager? = null
    var fb_share_button: ShareButton? = null
    var shareDialog: ShareDialog? = null
    var iscallForCommentUpdate:Boolean= false
    private var post_id_ForRefresh: String? = "0"
    private var swipeRefreshLayout: SwipeRefreshLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance=true

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_video, container, false)
        dataManager = BaseApplication.baseApplicationInstance.getdatamanger()
        mUserAllFeedList = ArrayList<Post>()
        //crating an arraylist to store users using the data class user
        view.rv_user_feed_list.layoutManager = LinearLayoutManager(activity!!, LinearLayout.VERTICAL, false)

        //creating our adapter
        mALlFeedVideoAdapter = CustomVideoListFragmentAdpter(mUserAllFeedList!!, this,this)

        //now adding the adapter to recyclerview
        view.rv_user_feed_list.adapter = mALlFeedVideoAdapter
        getAllUserFeed()
        fb_share_button = view.fb_share_button
        fb_share_button?.fragment = this
        FacebookSdk.sdkInitialize(activity!!.applicationContext)
        callbackManager = CallbackManager.Factory.create()
        shareDialog = ShareDialog(this)
        shareDialog?.registerCallback(callbackManager, callback)
        swipeRefreshLayout=view.swipe_refresh_layout
        swipeRefreshLayout!!.setOnRefreshListener(this)
        return view
    }

    override fun onResume() {
        super.onResume()
        if(iscallForCommentUpdate){
            getAllUserFeed()
        }

    }

    // api call for user registration
    private fun getAllUserFeed() {

        showDialogLoading()
        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable?.add(ApiRequestClient.createREtrofitInstance()
                .getUserAllPostFeed(dataManager.getUserId(),post_id_ForRefresh)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }


    // handle sucess response of api call
    private fun handleResponse(responseFromServerAllfeed: ResponseFromServerAllfeed) {
        hideDialogLoading()
        mUserAllFeedList?.clear()
        mUserAllFeedList?.addAll(responseFromServerAllfeed.post)
        mALlFeedVideoAdapter?.notifyDataSetChanged()
        mCompositeDisposable?.clear()


    }


    // handle failure response of api call
    private fun handleError(error: Throwable) {
        mCompositeDisposable?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }
    // api call for user registration
    private fun getAllUserFeedForSwipe() {

        //showDialogLoading()
        swipeRefreshLayout!!.isRefreshing = true
        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable?.add(ApiRequestClient.createREtrofitInstance()
                .getUserAllPostFeed(dataManager.getUserId(),post_id_ForRefresh)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseForSwipe, this::handleErrorForSwipe))
    }


    // handle sucess response of api call
    private fun handleResponseForSwipe(responseFromServerAllfeed: ResponseFromServerAllfeed) {
        swipeRefreshLayout!!.isRefreshing = false
       // hideDialogLoading()
        mUserAllFeedList?.clear()
        mUserAllFeedList?.addAll(responseFromServerAllfeed.post)
        mALlFeedVideoAdapter?.notifyDataSetChanged()
        mCompositeDisposable?.clear()


    }


    // handle failure response of api call
    private fun handleErrorForSwipe(error: Throwable) {
        swipeRefreshLayout!!.isRefreshing = false
        mCompositeDisposable?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }

    // api call for user registration
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
        mUserAllFeedList!!.forEach {
            if (it.post_id.equals(item)) {
//                val post: Post = Post(it.post_id, it.user_id, it.headline, it.content,
//                        it.post_image, it.hashtag_id, it.no_like, it.no_comment,
//                        it.created, it.no_fb_share, it.link, it.post_type, it.username, it.uimage, it.Isliked, it.IsSavedpost
//                        , result,it.source,it.is_reported,it.news_id,it.title,it.news_content,
//                        it.news_image,it.url,it.blog_id,it.image,it.image1,it.image2,it.image3)
//
//                mUserAllFeedList!!.set(post_id_Position!!, post)
//                mALlFeedVideoAdapter?.notifyItemChanged(post_id_Position!!)
            }
        }




        mCompositeDisposable_WhatsApp?.clear()


    }


    // handle failure response of api call
    private fun handleError_whatsApp(error: Throwable) {
        mCompositeDisposable_WhatsApp?.clear()
        hideDialogLoading()
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

        hideDialogLoading()
        showSnackBar(error.localizedMessage)
        mCompositeDisposable_Like?.clear()


    }

    override fun userPostSelectVideoId(post: Post) {
        val intent = Intent(activity, YoutubePlayerActivity::class.java)
        intent.putExtra("youtubeVideoId", post as Serializable)
        startActivity(intent)
    }


    private fun showRadioButtonDialog(post: Post) {

        // custom dialog
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.setContentView(R.layout.radiobutton_dialog)
        dialog.setTitle("मै इस पोस्ट का विरोध करता हूँ ,क्यूकि ये पोस्ट...")

        dialog.show()
        activity?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
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
}



