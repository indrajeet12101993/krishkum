package `in`.krishkam.Fragment


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
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
import kotlinx.android.synthetic.main.app_bar_user_feed.*

import kotlinx.android.synthetic.main.fragment_video.view.*
import kotlinx.android.synthetic.main.radiobutton_dialog.*
import `in`.krishkam.R
import `in`.krishkam.activity.*
import `in`.krishkam.adapter.CustomAdapterForCity
import `in`.krishkam.adapter.CustomAdapterForHomeReacycler
import `in`.krishkam.adapter.CustomAdapterForState

import `in`.krishkam.base.BaseApplication
import `in`.krishkam.base.BaseFragment
import `in`.krishkam.callback.IFragmentManager
import `in`.krishkam.callback.InterFaceAllfeedVideoSelectListner
import `in`.krishkam.callback.InterfaceCitySelectListner
import `in`.krishkam.callback.InterfaceStateSelectListner
import `in`.krishkam.dataprefence.DataManager
import `in`.krishkam.holder.VideoBinder
import `in`.krishkam.networkUtils.ApiRequestClient
import `in`.krishkam.pojo.AllPostFeed.Post
import `in`.krishkam.pojo.AllPostFeed.ResponseFromServerAllfeed
import `in`.krishkam.pojo.ResponseFromSerVerAddPostList
import `in`.krishkam.pojo.ResponseFromServerSikayat
import `in`.krishkam.pojo.ResponseFromServerWhatsApp
import `in`.krishkam.pojo.ServerResponseFromUplaodImage
import `in`.krishkam.pojo.UserEditShowInitial.ResponseFromServerInitialEditUser
import `in`.krishkam.pojo.city.ResponseFromServerCity
import `in`.krishkam.pojo.listBlog.ResponseFromServerListBlog
import `in`.krishkam.pojo.listnews.Result
import `in`.krishkam.pojo.listnews.ServerFromResponseListNews
import `in`.krishkam.pojo.state.ResponseFromServerStateList
import `in`.krishkam.utils.UtilityFiles.getLocalBitmapUri
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.city_list.*
import kotlinx.android.synthetic.main.profile_dialog.view.*
import kotlinx.android.synthetic.main.sujaho_dialog.view.*
import java.io.Serializable


class HomeFragment : BaseFragment(), InterFaceAllfeedVideoSelectListner, IFragmentManager, SwipeRefreshLayout.OnRefreshListener, InterfaceStateSelectListner, InterfaceCitySelectListner {
    // item click when state is selected
    override fun onItemClickState(result: `in`.krishkam.pojo.state.Result) {


        dialog_State.dismiss()
        state_id = result.id
        dialogView!!.et_state.setText(result.name)
        dialogView!!.et_state.requestFocus()

    }

    // item click when city is selected
    override fun onItemClickState(result: `in`.krishkam.pojo.city.Result) {

        dialog_city.dismiss()
        city_name = result.name
        city_id = result.id
        dialogView!!.et_district.setText(result.name)
        dialogView!!.et_district.requestFocus()


    }
    override fun onRefresh() {
        if (dataForHitServer.equals("yes")) {

            getAllUserFeedForSwipe(getArgument)
        } else {
            getAllUserFeedForSwipe("")
        }
    }

    override fun userPostSelectReadMore(post: Post) {

        if (post.post_type.equals("2")) {
            val intent = Intent(activity, HomeNewsMoreReadActivity::class.java)
            intent.putExtra("samachardata", post as Serializable)
            startActivity(intent)


        }

        if (post.post_type.equals("3")) {
            val intent = Intent(activity, HomeLekhReadMoreActivity::class.java)
            intent.putExtra("youtubeVideoId", post as Serializable)
            startActivity(intent)


        }

    }

    override fun getSupportFragmentManager(): FragmentManager {
        return fragmentManager!!
    }

    override fun getSupportFragment(): Fragment {
        return this

    }

    override fun userPostSelectSavedPost(post: Post, position: Int, type: String) {

        postToAddSavedFeed(post.user_id, post.post_id, type)

    }

    override fun userPostSelectfacebook(post: Post, position: Int) {


        if (post.post_type.equals("0")) {
            post_id_whatsApp = post.post_id
            post_id_Position = position
            shareFaceBook(post.title)
        }
        if (post.post_type.equals("1")) {
            post_id_whatsApp = post.post_id
            post_id_Position = position

            shareFaceBook(post.headline)
        }
        if (post.post_type.equals("2")) {
            iscallForNewsCount_facebook = true
            post_id_whatsApp = post.post_id
            post_id_Position = position
            id_Count_whatsApp = post.news_id
            shareFaceBook(post.title)
        }
        if (post.post_type.equals("3")) {
            iscallForLekhCount_facebook = true
            post_id_whatsApp = post.post_id
            post_id_Position = position
            id_Count_whatsApp = post.blog_id
            shareFaceBook(post.title)
        }


    }

    fun shareFaceBook(title:String) {

        val appplaystore: String = "https://play.google.com/store/apps/details?id=in.krishkam"
        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
            shareDialog?.registerCallback(callbackManager, callback)

            val linkContent = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(appplaystore))
                    .setQuote(title)
                    .build()
            shareDialog!!.show(linkContent)

        }

    }


    override fun userPostSelectSikayat(post: Post) {
        showRadioButtonDialog(post)
    }

    override fun userPostSelectComment(post: Post, position: Int) {
        iscallForCommentUpdate = true

        if (post.post_type.equals("0")) {
            val intent = Intent(activity, CommentsActivity::class.java)
            intent.putExtra("post_id", post.post_id)
            startActivity(intent)



        }

        if (post.post_type.equals("1")) {
            val intent = Intent(activity, CommentsActivity::class.java)
            intent.putExtra("post_id", post.post_id)
            startActivity(intent)



        }

        if (post.post_type.equals("2")) {
            val intent = Intent(activity, HomeSamcharCommentActivity::class.java)
            intent.putExtra("post_id", post.post_id)
            intent.putExtra("news_id", post.news_id)
            startActivity(intent)


        }

        if (post.post_type.equals("3")) {
            val intent = Intent(activity, HomeLekhCommentActvity::class.java)
            intent.putExtra("post_id", post.post_id)
            intent.putExtra("news_id", post.blog_id)
            startActivity(intent)


        }

    }


    override fun userPostSelectWhatsApp(post: Post, position: Int) {

        val appplaystore: String = "https://play.google.com/store/apps/details?id=in.krishkam"

        if (post.post_type.equals("0")) {
            post_id_whatsApp = post.post_id
            post_id_Position = position

            if (post.post_image.isNullOrEmpty()) {
                val sendIntent = Intent()
                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        post.headline + "\n" + "देखें कृषकं ऍप पर, अभी फ्री डाउनलोड करे" +
                                "\n" + appplaystore)
                sendIntent.type = "text/plain"
                sendIntent.setPackage("com.whatsapp")

                try {
                    startActivityForResult(sendIntent, 1);
                } catch (ex: android.content.ActivityNotFoundException) {
                    showSnackBar("Whatsapp have not been installed.")

                }
            } else {
                Glide.with(activity!!).asBitmap()
                        .load(post.post_image)
                        .into(object : SimpleTarget<Bitmap>(100, 100) {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                val sendIntent = Intent()
                                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                sendIntent.action = Intent.ACTION_SEND
                                sendIntent.type = "image/*"
                                sendIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource, activity!!))
                                sendIntent.putExtra(Intent.EXTRA_TEXT, post.headline + "\n" + "देखें कृषकं ऍप पर, अभी फ्री डाउनलोड करे" +
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

        } else if (post.post_type.equals("1")) {
            post_id_whatsApp = post.post_id
            post_id_Position = position
            val sendIntent = Intent()
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, post.headline + "\n" + "देखें कृषकं ऍप पर, अभी फ्री डाउनलोड करे" +
                    "\n" + appplaystore)
            sendIntent.type = "text/plain"
            sendIntent.setPackage("com.whatsapp")

            try {
                startActivityForResult(sendIntent, 1);
            } catch (ex: android.content.ActivityNotFoundException) {
                showSnackBar("Whatsapp have not been installed.")

            }
        } else if (post.post_type.equals("2")) {
            iscallForNewsCount = true
            post_id_whatsApp = post.post_id
            post_id_Position = position
            id_Count_whatsApp = post.news_id
            Glide.with(activity!!).asBitmap()
                    .load(post.news_image)
                    .into(object : SimpleTarget<Bitmap>(100, 100) {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val sendIntent = Intent()
                            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            sendIntent.action = Intent.ACTION_SEND
                            sendIntent.type = "image/*"
                            sendIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource, activity!!))
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
        } else if (post.post_type.equals("3")) {
            iscallForLekhCount = true
            post_id_whatsApp = post.post_id
            post_id_Position = position
            id_Count_whatsApp = post.blog_id
            Glide.with(activity!!).asBitmap()
                    .load(post.image)
                    .into(object : SimpleTarget<Bitmap>(100, 100) {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val sendIntent = Intent()
                            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            sendIntent.action = Intent.ACTION_SEND
                            sendIntent.type = "image/*"
                            sendIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource, activity!!))
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


    }


    override fun userPostSelectPostLike(post: Post, like: String) {
        if (post.post_type.equals("2")) {
            noOfLike = like
            id_Like = post.news_id
            getAlltotalLike_ForNews(post, like)
        } else if (post.post_type.equals("3")) {
            noOfLike = like
            id_Like = post.blog_id
            getAlltotalLike_ForLekh(post, like)
        } else {
            getAlltotalLike(post, like)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)



        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {


                getTotalWhatsAppLike()

            }
        } else {

        }
    }

    private val callback = object : FacebookCallback<Sharer.Result> {


        override fun onSuccess(result: Sharer.Result) {
            //   Toast.makeText(activity, "success", Toast.LENGTH_SHORT).show()

            getTotalFacebookLike()
        }

        override fun onCancel() {
//
            //Toast.makeText(activity, "cancel", Toast.LENGTH_SHORT).show()

        }

        override fun onError(error: FacebookException) {
            // Toast.makeText(activity, "cancelExpec", Toast.LENGTH_SHORT).show()


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

        if(iscallForNewsCount_facebook){
            getTotalFacebookLikeForSamachar()

        }
        if(iscallForLekhCount_facebook){
            getTotalFacebookLikeForLekh()

        }
        mCompositeDisposable_Facebook?.clear()


    }


    // handle failure response of api call
    private fun handleError_facebook(error: Throwable) {
        mCompositeDisposable_Facebook?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }

    private fun getTotalFacebookLikeForLekh() {

        //  showDialogLoading()
        mCompositeDisposable_Facebook = CompositeDisposable()
        mCompositeDisposable_Facebook?.add(ApiRequestClient.createREtrofitInstance()
                .getsharecountWhatsAppForlekh(id_Count_whatsApp, "fb")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_facebook_Lekh, this::handleError_facebook_Lekh))
    }


    // handle sucess response of api call
    private fun handleResponse_facebook_Lekh(responseFromServerWhatsApp: ResponseFromServerWhatsApp) {


        mCompositeDisposable_Facebook?.clear()


    }


    // handle failure response of api call
    private fun handleError_facebook_Lekh(error: Throwable) {


        showSnackBar(error.localizedMessage)
        mCompositeDisposable_Facebook?.clear()

    }

    private fun getTotalFacebookLikeForSamachar() {

        //  showDialogLoading()
        mCompositeDisposable_Facebook = CompositeDisposable()
        mCompositeDisposable_Facebook?.add(ApiRequestClient.createREtrofitInstance()
                .getsharecountWhatsAppForSamachar(id_Count_whatsApp, "fb")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_facebook_for_smaachar, this::handleError_facebook_For_smachar))
    }


    // handle sucess response of api call
    private fun handleResponse_facebook_for_smaachar(responseFromServerWhatsApp: ResponseFromServerWhatsApp) {


        mCompositeDisposable_Facebook?.clear()


    }


    // handle failure response of api call
    private fun handleError_facebook_For_smachar(error: Throwable) {

        showSnackBar(error.localizedMessage)
        mCompositeDisposable_Facebook?.clear()

    }


    private fun postToAddSavedFeed(user_id: String, post_id: String, type: String) {

        // showDialogLoading()
        mCompositeDisposable_Saved_Feed = CompositeDisposable()
        mCompositeDisposable_Saved_Feed?.add(ApiRequestClient.createREtrofitInstance()
                .postSavedPostFeed(dataManager.getUserId(), post_id, type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_feed, this::handleError_svedFedd))
    }


    // handle sucess response of api call
    private fun handleResponse_feed(responseFromSerVerAddPostList: ResponseFromSerVerAddPostList) {

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
    private var mALlFeedVideoAdapter: CustomAdapterForHomeReacycler? = null
    private var post_id_whatsApp: String? = null
    private var post_id_Position: Int? = null
    private var id_Like: String? = null
    private var id_Count_whatsApp: String? = null
    private var noOfLike: String? = null
    private var callbackManager: CallbackManager? = null
    var fb_share_button: ShareButton? = null
    var shareDialog: ShareDialog? = null
    var iscallForCommentUpdate: Boolean = false
    var iscallForNewsCount: Boolean = false
    var iscallForLekhCount: Boolean = false
    var iscallForNewsCount_facebook: Boolean = false
    var iscallForLekhCount_facebook: Boolean = false
    var getArgument: String? = null
    var dataForHitServer: String? = null
    lateinit var tv_no_saved_post: TextView
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private lateinit var sujao: String
    var dialogView: View? = null
    private var mAndroidStateList: MutableList<`in`.krishkam.pojo.state.Result>? = null
    private lateinit var dialog_city: Dialog
    private lateinit var dialog_State: Dialog
    private var mCompositeDisposable_city: CompositeDisposable? = null
    private var mCompositeDisposable_state: CompositeDisposable? = null
    private var mStateAdapter: CustomAdapterForState? = null
    private var mCityAdapter: CustomAdapterForCity? = null
    private lateinit var city_name: String
    private var mCompositeDisposable_Update_Profile: CompositeDisposable? = null
    private lateinit var state_id: String
    private lateinit var city_id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        FacebookSdk.sdkInitialize(activity!!.applicationContext)
        val view: View = inflater.inflate(R.layout.fragment_video, container, false)
        dataManager = BaseApplication.baseApplicationInstance.getdatamanger()
        mUserAllFeedList = ArrayList<Post>()
        //crating an arraylist to store users using the data class user

        view.rv_user_feed_list.layoutManager = LinearLayoutManager(activity!!, LinearLayout.VERTICAL, false)
        tv_no_saved_post  =view.tv_no_saved_post
        //creating our adapter
        mALlFeedVideoAdapter = CustomAdapterForHomeReacycler(mUserAllFeedList!!, this, this)

        //now adding the adapter to recyclerview
        view.rv_user_feed_list.adapter = mALlFeedVideoAdapter

        //Get Argument that passed from activity in "data" key value
        getArgument = arguments!!.getString("data")
        dataForHitServer = arguments!!.getString("dataForHitServer")
        activity!!.toolbar.title = getArgument
        swipeRefreshLayout = view.swipe_refresh_layout
        swipeRefreshLayout!!.setOnRefreshListener(this)

        if (dataForHitServer.equals("yes")) {

            getAllUserFeed(getArgument)
        } else {
            getAllUserFeed("")
        }


        fb_share_button = view.fb_share_button
        fb_share_button?.fragment = this
        callbackManager = CallbackManager.Factory.create()
        shareDialog = ShareDialog(this)

        return view
    }

    override fun onResume() {
        super.onResume()
        if (iscallForCommentUpdate) {
            getAllUserFeed("")
        }

    }

    // api call for user registration
    private fun getAllUserFeed(argument: String?) {

        showDialogLoading()
        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable?.add(ApiRequestClient.createREtrofitInstance()
                .getUserAllHomeFeed(dataManager.getUserId(), argument)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }


    // handle sucess response of api call
    private fun handleResponse(responseFromServerAllfeed: ResponseFromServerAllfeed) {
        hideDialogLoading()
        if(responseFromServerAllfeed.post.size==0){

            tv_no_saved_post.text=" कोई  पोस्ट नहीं!"
            tv_no_saved_post.visibility=View.VISIBLE
        }else{
            mUserAllFeedList?.clear()
            mUserAllFeedList?.addAll(responseFromServerAllfeed.post)
            mALlFeedVideoAdapter?.notifyDataSetChanged()


        }
        mCompositeDisposable?.clear()



    }


    // handle failure response of api call
    private fun handleError(error: Throwable) {
        mCompositeDisposable?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }
    // api call for user registration
    private fun getAllUserFeedForSwipe(argument: String?) {

        swipeRefreshLayout!!.isRefreshing = true
        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable?.add(ApiRequestClient.createREtrofitInstance()
                .getUserAllHomeFeed(dataManager.getUserId(), argument)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseForSwipe, this::handleErrorForSwipe))
    }


    // handle sucess response of api call
    private fun handleResponseForSwipe(responseFromServerAllfeed: ResponseFromServerAllfeed) {
        swipeRefreshLayout!!.isRefreshing = false

        if(responseFromServerAllfeed.post.size==0){

            tv_no_saved_post.text=" कोई  पोस्ट नहीं!"
            tv_no_saved_post.visibility=View.VISIBLE
        }else{
            mUserAllFeedList?.clear()
            mUserAllFeedList?.addAll(responseFromServerAllfeed.post)
            mALlFeedVideoAdapter?.notifyDataSetChanged()


        }
        mCompositeDisposable?.clear()



    }


    // handle failure response of api call
    private fun handleErrorForSwipe(error: Throwable) {
        mCompositeDisposable?.clear()
        swipeRefreshLayout!!.isRefreshing = false

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


        if (iscallForNewsCount) {
            getTotalWhatsAppLikeForSamchar()


        }
        if (iscallForLekhCount) {
            getAlltotalLikeForLekh()

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
    private fun getTotalWhatsAppLikeForSamchar() {

        //  showDialogLoading()
        mCompositeDisposable_WhatsApp = CompositeDisposable()

        mCompositeDisposable_WhatsApp?.add(ApiRequestClient.createREtrofitInstance()
                .getsharecountWhatsAppForSamachar(id_Count_whatsApp, "wp")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_whatsAppForSamachar, this::handleError_whatsApp_ForSmachar))
    }


    // handle sucess response of api call
    private fun handleResponse_whatsAppForSamachar(responseFromServerWhatsApp: ResponseFromServerWhatsApp) {


        mCompositeDisposable_WhatsApp?.clear()


    }


    // handle failure response of api call
    private fun handleError_whatsApp_ForSmachar(error: Throwable) {
        mCompositeDisposable_WhatsApp?.clear()

        showSnackBar(error.localizedMessage)


    }

    // api call for user registration
    private fun getAlltotalLikeForLekh() {

        //  showDialogLoading()
        //  showDialogLoading()
        mCompositeDisposable_WhatsApp = CompositeDisposable()

        mCompositeDisposable_WhatsApp?.add(ApiRequestClient.createREtrofitInstance()
                .getsharecountWhatsAppForlekh(id_Count_whatsApp, "wp")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_Like_Lekh, this::handleError_like_Lekh))
    }


    // handle sucess response of api call
    private fun handleResponse_Like_Lekh(responseFromServerAllfeed: ResponseFromServerWhatsApp) {

        mCompositeDisposable_WhatsApp?.clear()


    }


    // handle failure response of api call
    private fun handleError_like_Lekh(error: Throwable) {


        showSnackBar(error.localizedMessage)
        mCompositeDisposable_WhatsApp?.clear()


    }


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
        mCompositeDisposable_Like?.clear()


    }


    // handle failure response of api call
    private fun handleError_like(error: Throwable) {


        showSnackBar(error.localizedMessage)
        mCompositeDisposable_Like?.clear()


    }

    private fun getAlltotalLike_ForNews(post: Post, like: String) {

        //  showDialogLoading()
        mCompositeDisposable_Like = CompositeDisposable()
        mCompositeDisposable_Like?.add(ApiRequestClient.createREtrofitInstance()
                .getTotalLike(dataManager.getUserId(), post.post_id, like)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_LikeForNews, this::handleError_likeForNews))
    }


    // handle sucess response of api call
    private fun handleResponse_LikeForNews(responseFromServerAllfeed: ResponseFromServerAllfeed) {
        getAlltotalLike_ForNews1()

        mCompositeDisposable_Like?.clear()


    }


    // handle failure response of api call
    private fun handleError_likeForNews(error: Throwable) {
        showSnackBar(error.localizedMessage)
        mCompositeDisposable_Like?.clear()


    }

    private fun getAlltotalLike_ForNews1() {

        //  showDialogLoading()
        mCompositeDisposable_Like = CompositeDisposable()
        mCompositeDisposable_Like?.add(ApiRequestClient.createREtrofitInstance()
                .getTotalLikeForSamchar(dataManager.getUserId(), id_Like, noOfLike)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_LikeForNews1, this::handleError_likeForNews1))
    }


    // handle sucess response of api call
    private fun handleResponse_LikeForNews1(responseFromServerAllfeed: ServerFromResponseListNews) {


        mCompositeDisposable_Like?.clear()


    }


    // handle failure response of api call
    private fun handleError_likeForNews1(error: Throwable) {
        showSnackBar(error.localizedMessage)
        mCompositeDisposable_Like?.clear()


    }

    private fun getAlltotalLike_ForLekh(post: Post, like: String) {

        //  showDialogLoading()
        mCompositeDisposable_Like = CompositeDisposable()
        mCompositeDisposable_Like?.add(ApiRequestClient.createREtrofitInstance()
                .getTotalLike(dataManager.getUserId(), post.post_id, like)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_LikeForLekh, this::handleError_likeForLekh))
    }


    // handle sucess response of api call
    private fun handleResponse_LikeForLekh(responseFromServerAllfeed: ResponseFromServerAllfeed) {
        getAlltotalLike_ForLekh1()

        mCompositeDisposable_Like?.clear()


    }


    // handle failure response of api call
    private fun handleError_likeForLekh(error: Throwable) {
        showSnackBar(error.localizedMessage)
        mCompositeDisposable_Like?.clear()


    }

    private fun getAlltotalLike_ForLekh1() {

        //  showDialogLoading()
        mCompositeDisposable_Like = CompositeDisposable()
        mCompositeDisposable_Like?.add(ApiRequestClient.createREtrofitInstance()
                .getTotalLikeForLekh(dataManager.getUserId(), id_Like, noOfLike)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_LikeForLekh1, this::handleError_likeForLekh1))
    }


    // handle sucess response of api call
    private fun handleResponse_LikeForLekh1(responseFromServerAllfeed: ResponseFromServerListBlog) {


        mCompositeDisposable_Like?.clear()


    }


    // handle failure response of api call
    private fun handleError_likeForLekh1(error: Throwable) {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_search) {

            val homeFragment = SearchAgianFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.place_holder_for_fragment, homeFragment)
            transaction?.commit()
        }
        if (id == R.id.action_sujhao) {
            sujhaoDialog()

        }


        return super.onOptionsItemSelected(item)
    }

    fun sujhaoDialog() {

        val popDialog = AlertDialog.Builder(activity!!)
        val inflater: LayoutInflater = this.getLayoutInflater()
        val dialogView: View = inflater.inflate(R.layout.sujaho_dialog, null)
        popDialog.setView(dialogView);
        popDialog.setTitle("हमें सुझाव  दें !")

        popDialog.create()

        // Set a positive button and its click listener on alert dialog
        popDialog.setPositiveButton(android.R.string.ok) { dialog, which ->

            sujao = dialogView.et_sujaho.text.toString()
            if (sujao.isEmpty()) {
                dialogView.et_sujaho.setError("हमें सुझाव  दें!")
                dialogView.et_sujaho.requestFocus()

            } else {
                getInitalEditProfile()

            }


        }



        popDialog.setNegativeButton("Cancel") { dialog, which ->

        }

        popDialog.show()


    }

    // api call for user registration
    private fun getInitalEditProfile() {

        showDialogLoading()
        mCompositeDisposable = CompositeDisposable()
        mCompositeDisposable?.add(ApiRequestClient.createREtrofitInstance()
                .getUserInitialEditData(dataManager.getUserId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsesujhao, this::handleErrorsujhao))
    }


    // handle sucess response of api call
    private fun handleResponsesujhao(response: ResponseFromServerInitialEditUser) {
        hideDialogLoading()

        if (response.user_detail.size == 0) {

            ShowRatingDialog()
        }
        else{
            uploadingUserProfileUpdate(response.user_detail.get(0).name,response.user_detail.get(0).state, response.user_detail.get(0).city, response.user_detail.get(0).village)
        }
        mCompositeDisposable?.clear()


    }




    // handle failure response of api call
    private fun handleErrorsujhao(error: Throwable) {
        mCompositeDisposable?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }

    fun ShowRatingDialog() {

        val popDialog = AlertDialog.Builder(activity!!).create()
        val inflater: LayoutInflater = this.getLayoutInflater()
        dialogView = inflater.inflate(R.layout.profile_dialog, null)
        popDialog.setView(dialogView)
        popDialog.show()
        // show state dialog
        dialogView!!.et_state.setOnClickListener {

            // initialaiae sttae list dialog
            initState()
            // execute api of state
            getState()


        }
        // show city dialog
        dialogView!!.et_district.setOnClickListener {
            initCity()
        }
        dialogView!!.btn_profile_edit_kare.setOnClickListener {


            val name: String = dialogView!!.et_name.text.toString()
            val state: String = dialogView!!.et_state.text.toString()
            val district: String = dialogView!!.et_district.text.toString()
            val village: String = dialogView!!.et_village.text.toString()

            if (name.isNullOrEmpty() || state.isNullOrEmpty() || district.isNullOrEmpty() || village.isNullOrEmpty()) {
                showSnackBar("खली जगह भदे !")
            } else {
                popDialog.dismiss()
                uploadingUserProfileUpdate(name, state_id, city_id, village)
            }


        }


    }


    private fun initState() {
        mAndroidStateList = ArrayList<`in`.krishkam.pojo.state.Result>()

        dialog_State = Dialog(activity!!)
        dialog_State.setContentView(R.layout.city_list)
        dialog_State.setCanceledOnTouchOutside(false)
        dialog_State.setCancelable(true)
        dialog_State.setTitle(" स्टेट का चयन करें!")
        dialog_State.recycler_view.layoutManager = LinearLayoutManager(activity!!, LinearLayout.VERTICAL, false)
        val itemDecor = DividerItemDecoration(activity!!, LinearLayout.HORIZONTAL)
        dialog_State.recycler_view.addItemDecoration(itemDecor)
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

    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(activity!!).create()
        alertDialog.setTitle("Network Problem")
        alertDialog.setMessage("Reload!")
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    //  finish()
                })
        alertDialog.show()
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

    // show state list dialog
    private fun showSTATE() {


        dialogView!!.et_district.text = null
        dialog_State.show()
        mStateAdapter?.notifyDataSetChanged()

    }

    private fun initCity() {
        dialog_city = Dialog(activity!!)
        dialog_city.setContentView(R.layout.city_list)
        dialog_city.setCanceledOnTouchOutside(false)
        dialog_city.setCancelable(true)
        dialog_city.setTitle(" जिला का चयन करें!")
        dialog_city.recycler_view.layoutManager = LinearLayoutManager(activity!!, LinearLayout.VERTICAL, false)
        val itemDecor = DividerItemDecoration(activity!!, DividerItemDecoration.HORIZONTAL)
        dialog_city.recycler_view.addItemDecoration(itemDecor)
        if (state_id.isEmpty()) {
            val alertDialog = AlertDialog.Builder(activity!!).create()
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

        } else {

            getCity(state_id)
        }

    }

    private fun uploadingUserProfileUpdate(name: String, state: String, district: String, village: String) {


        showDialogLoading()


        mCompositeDisposable_Update_Profile = CompositeDisposable()

        mCompositeDisposable_Update_Profile?.add(ApiRequestClient.createREtrofitInstance()
                .userSujhao(dataManager.getUserId(), name, state, district, sujao,village)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUpdateProfile, this::handleErrorUpdateProfile))
    }


    // handle sucess response of api call
    private fun handleResponseUpdateProfile(response: ServerResponseFromUplaodImage) {
        hideDialogLoading()

        val dialogBuilder = AlertDialog.Builder(activity!!)

        // set message of alert dialog
        dialogBuilder.setMessage("आपके सुझाव  के लिए धन्यवाद !")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id ->

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


}
