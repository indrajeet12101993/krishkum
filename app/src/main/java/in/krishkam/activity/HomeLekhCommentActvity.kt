package `in`.krishkam.activity

import `in`.krishkam.R
import `in`.krishkam.adapter.CustomAdapterForComment
import `in`.krishkam.base.BaseActivity
import `in`.krishkam.base.BaseApplication
import `in`.krishkam.callback.InterfaceCommentSelectListner
import `in`.krishkam.dataprefence.DataManager
import `in`.krishkam.networkUtils.ApiRequestClient
import `in`.krishkam.pojo.AllCommentList.CommentDetail
import `in`.krishkam.pojo.AllCommentList.ResponseFromServerAllCommentList
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_comments.*

class HomeLekhCommentActvity : BaseActivity(), InterfaceCommentSelectListner {

    override fun postDelete(commentDetail: CommentDetail, s: String) {
        getdeleteCooment(commentDetail.post_id, commentDetail.commentid)
        commentid1=commentDetail.commentid
    }

    private var mCompositeDisposable: CompositeDisposable? = null
    private var mCompositeDisposable_post_comment: CompositeDisposable? = null
    private var mCompositeDisposable_post_comment_Delete: CompositeDisposable? = null
    private var mUserAllCommentList: MutableList<CommentDetail>? = null
    private var mCustomAdapterForComment: CustomAdapterForComment? = null
    private var post_id: String? = null
    private var news_id: String? = null
    private var commentid1: String? = null
    private lateinit var dataManager: DataManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        dataManager = BaseApplication.baseApplicationInstance.getdatamanger()
        mUserAllCommentList = ArrayList<CommentDetail>()
        post_id = intent.getStringExtra("post_id")
        news_id = intent.getStringExtra("news_id")
        rv_comment_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        //creating our adapter
        mCustomAdapterForComment = CustomAdapterForComment(mUserAllCommentList!!, this, dataManager.getUserId())

        //now adding the adapter to recyclerview
        rv_comment_recyclerview.adapter = mCustomAdapterForComment
        getAllCooment(post_id)

        ib_send.setOnClickListener {

            if (editText_comment.text.toString().isNullOrEmpty()) {
                editText_comment.error = "अपना कमेंट दे !"
            } else {
                addCommentToServer(editText_comment.text.toString())
            }


        }
    }

    private fun addCommentToServer(comment: String) {
        getPostCooment(comment)
      getPostCoomentForHomeSmachar(comment)
    }

    private fun getAllCooment(post_id: String?) {

        showDialogLoading()
        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable?.add(ApiRequestClient.createREtrofitInstance()
                .getToatalComment(post_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }


    // handle sucess response of api call
    private fun handleResponse(responseFromServerAllCommentList: ResponseFromServerAllCommentList) {
        hideDialogLoading()

          mUserAllCommentList?.clear()
        if (mUserAllCommentList?.size!!.equals("0")) {
            mCompositeDisposable?.clear()
        } else {
            editText_comment.requestFocus()
            mUserAllCommentList?.addAll(responseFromServerAllCommentList.comment_detail)
            mCustomAdapterForComment?.notifyDataSetChanged()
            rv_comment_recyclerview.scrollToPosition(responseFromServerAllCommentList.comment_detail.size - 1)

            mCompositeDisposable?.clear()

        }
        getAllCoomentForHmeSanachar()

    }


    // handle failure response of api call
    private fun handleError(error: Throwable) {
        mCompositeDisposable?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }

    private fun getAllCoomentForHmeSanachar() {

        showDialogLoading()
        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable?.add(ApiRequestClient.createREtrofitInstance()
                .getToatalCommentForLekh(news_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_home_samchar, this::handleError_home_snachar))
    }


    // handle sucess response of api call
    private fun handleResponse_home_samchar(responseFromServerAllCommentList: `in`.krishkam.pojo.allCommentListForNews.ResponseFromServerAllCommentList) {
        hideDialogLoading()


        mCompositeDisposable?.clear()

    }


    // handle failure response of api call
    private fun handleError_home_snachar(error: Throwable) {
        mCompositeDisposable?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }


    private fun getPostCooment(comment: String) {


        mCompositeDisposable_post_comment = CompositeDisposable()
        mCompositeDisposable_post_comment?.add(ApiRequestClient.createREtrofitInstance()
                .postComment(post_id, dataManager.getUserId(), comment)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_post_cooment, this::handleError_post_comment))
    }


    // handle sucess response of api call
    private fun handleResponse_post_cooment(responseFromServerAllCommentList: ResponseFromServerAllCommentList) {

        editText_comment.text.clear()
        hideKeyboard(editText_comment)
        mUserAllCommentList?.clear()
        mUserAllCommentList?.addAll(responseFromServerAllCommentList.comment_detail)
        mCustomAdapterForComment?.notifyDataSetChanged()
        rv_comment_recyclerview.scrollToPosition(responseFromServerAllCommentList.comment_detail.size - 1)
        mCompositeDisposable_post_comment?.clear()


    }


    // handle failure response of api call
    private fun handleError_post_comment(error: Throwable) {
        editText_comment.text.clear()
        hideKeyboard(editText_comment)
        mCompositeDisposable_post_comment?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }

    private fun getPostCoomentForHomeSmachar(comment: String) {


        mCompositeDisposable_post_comment = CompositeDisposable()
        mCompositeDisposable_post_comment?.add(ApiRequestClient.createREtrofitInstance()
                .postCommentForLekh(news_id, dataManager.getUserId(), comment)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_post_cooment_smachar, this::handleError_post_comment_smachar))
    }


    // handle sucess response of api call
    private fun handleResponse_post_cooment_smachar(responseFromServerAllCommentList: `in`.krishkam.pojo.allCommentListForNews.ResponseFromServerAllCommentList) {

        mCompositeDisposable_post_comment?.clear()


    }


    // handle failure response of api call
    private fun handleError_post_comment_smachar(error: Throwable) {

        mCompositeDisposable_post_comment?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }


    private fun getdeleteCooment(post_id: String, commentid: String) {
        showDialogLoading()

        mCompositeDisposable_post_comment_Delete = CompositeDisposable()
        mCompositeDisposable_post_comment_Delete?.add(ApiRequestClient.createREtrofitInstance()
                .getDeleteComment(commentid, post_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_delete_cooment, this::handleError_deltee_comment))
    }


    // handle sucess response of api call
    private fun handleResponse_delete_cooment(responseFromServerAllCommentList: ResponseFromServerAllCommentList) {

        hideDialogLoading()
        mUserAllCommentList?.clear()
        mUserAllCommentList?.addAll(responseFromServerAllCommentList.comment_detail)
        mCustomAdapterForComment?.notifyDataSetChanged()
        mCompositeDisposable_post_comment_Delete?.clear()
        getdeleteCoomentForLekh()


    }


    // handle failure response of api call
    private fun handleError_deltee_comment(error: Throwable) {


        mCompositeDisposable_post_comment_Delete?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }

    private fun getdeleteCoomentForLekh() {
        showDialogLoading()

        mCompositeDisposable_post_comment_Delete = CompositeDisposable()
        mCompositeDisposable_post_comment_Delete?.add(ApiRequestClient.createREtrofitInstance()
                .getDeleteCommentForLekh(commentid1, news_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_delete_cooment_for_lekh, this::handleError_deltee_comment_for_lekh))
    }


    // handle sucess response of api call
    private fun handleResponse_delete_cooment_for_lekh(responseFromServerAllCommentList: `in`.krishkam.pojo.allCommentListForNews.ResponseFromServerAllCommentList) {

        hideDialogLoading()

        mCompositeDisposable_post_comment_Delete?.clear()


    }


    // handle failure response of api call
    private fun handleError_deltee_comment_for_lekh(error: Throwable) {


        mCompositeDisposable_post_comment_Delete?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }
}
