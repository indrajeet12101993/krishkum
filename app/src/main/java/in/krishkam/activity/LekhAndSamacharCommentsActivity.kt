package `in`.krishkam.activity

import `in`.krishkam.R

import `in`.krishkam.adapter.CustomAdapterForCommentSamachar
import `in`.krishkam.base.BaseActivity
import `in`.krishkam.base.BaseApplication
import `in`.krishkam.callback.InterfaceCommentForNewsSelectListner

import `in`.krishkam.dataprefence.DataManager
import `in`.krishkam.networkUtils.ApiRequestClient
import `in`.krishkam.pojo.allCommentListForNews.CommentDetail
import `in`.krishkam.pojo.allCommentListForNews.ResponseFromServerAllCommentList

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_comments.*


class LekhAndSamacharCommentsActivity : BaseActivity(), InterfaceCommentForNewsSelectListner {
    override fun postDelete(commentDetail: CommentDetail, s: String) {
        getdeleteCooment(commentDetail.news_id, commentDetail.commentid)
    }

    private var mCompositeDisposable: CompositeDisposable? = null
    private var mCompositeDisposable_post_comment: CompositeDisposable? = null
    private var mCompositeDisposable_post_comment_Delete: CompositeDisposable? = null
    private var mUserAllCommentList: MutableList<CommentDetail>? = null
    private var mCustomAdapterForComment: CustomAdapterForCommentSamachar? = null
    private var post_id: String? = null


    private lateinit var dataManager: DataManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        dataManager = BaseApplication.baseApplicationInstance.getdatamanger()
        mUserAllCommentList = ArrayList<CommentDetail>()
        post_id = intent.getStringExtra("post_id")
        rv_comment_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        //creating our adapter
        mCustomAdapterForComment = CustomAdapterForCommentSamachar(mUserAllCommentList!!, this, dataManager.getUserId())

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

    }

    private fun getAllCooment(post_id: String?) {

        showDialogLoading()
        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable?.add(ApiRequestClient.createREtrofitInstance()
                .getToatalCommentForNews(post_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }


    // handle sucess response of api call
    private fun handleResponse(responseFromServerAllCommentList: ResponseFromServerAllCommentList) {
        hideDialogLoading()

        //  mUserAllCommentList?.clear()
        if (mUserAllCommentList?.size!!.equals("0")) {

        } else {
            editText_comment.requestFocus()
            mUserAllCommentList?.addAll(responseFromServerAllCommentList.comment_detail)
            mCustomAdapterForComment?.notifyDataSetChanged()
            rv_comment_recyclerview.scrollToPosition(responseFromServerAllCommentList.comment_detail.size - 1)
            mCompositeDisposable?.clear()

        }


    }


    // handle failure response of api call
    private fun handleError(error: Throwable) {
        mCompositeDisposable?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }

    private fun getPostCooment(comment: String) {


        mCompositeDisposable_post_comment = CompositeDisposable()
        mCompositeDisposable_post_comment?.add(ApiRequestClient.createREtrofitInstance()
                .postCommentForSmachar(post_id, dataManager.getUserId(), comment)
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

    private fun getdeleteCooment(post_id: String, commentid: String) {
        showDialogLoading()

        mCompositeDisposable_post_comment_Delete = CompositeDisposable()
        mCompositeDisposable_post_comment_Delete?.add(ApiRequestClient.createREtrofitInstance()
                .getDeleteCommentForNews(commentid, post_id)
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


    }


    // handle failure response of api call
    private fun handleError_deltee_comment(error: Throwable) {


        mCompositeDisposable_post_comment_Delete?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }
}
