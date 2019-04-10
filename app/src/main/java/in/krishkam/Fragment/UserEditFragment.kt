package `in`.krishkam.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_user_edit.*
import kotlinx.android.synthetic.main.fragment_user_edit.view.*


import `in`.krishkam.R
import `in`.krishkam.activity.UserEditProfileActivity
import`in`.krishkam.base.BaseApplication
import `in`.krishkam.base.BaseFragment
import `in`.krishkam.dataprefence.DataManager
import `in`.krishkam.networkUtils.ApiRequestClient
import `in`.krishkam.pojo.UserEditShowInitial.ResponseFromServerInitialEditUser
import `in`.krishkam.pojo.UserEditShowInitial.UserDetail


class UserEditFragment : BaseFragment(){
    lateinit var dataManager: DataManager
    var responsefromserverForInitial: MutableList<UserDetail>? = null
    private var mCompositeDisposable: CompositeDisposable? = null
    lateinit var tv_nav_user_name:TextView
    lateinit var tv_district:TextView
    lateinit var tv_city:TextView
    lateinit var iv_nav_profile:ImageView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_user_edit, container, false)
        dataManager = BaseApplication.baseApplicationInstance.getdatamanger()

        tv_nav_user_name  =view.tv_nav_user_name
        tv_district  =view.tv_district
        tv_city  =view.tv_city
        iv_nav_profile=  view.iv_nav_profile
        if (dataManager.getUserMobile() != null)
            view.tv_nav_user_mobile.text = dataManager.getUserMobile()

        getInitalEditProfile()

        // click on edit button
        view.bt_edit_profile.setOnClickListener {

        launchActivity(UserEditProfileActivity())
           // activity!!.finish()
        }

         replaceFragment(UserMyPostFragment())
         view.my_post.setTextColor(ContextCompat.getColor(context!!, R.color.top_view))
        view.my_saved_post.setTextColor(ContextCompat.getColor(context!!, R.color.et_line))

        view.my_post.setOnClickListener{

            replaceFragment(UserMyPostFragment())
            view.my_post.setTextColor(ContextCompat.getColor(context!!, R.color.top_view))
            view.my_saved_post.setTextColor(ContextCompat.getColor(context!!, R.color.et_line))
        }
        view.my_saved_post.setOnClickListener {

            replaceFragment(UserSavedFeedListFragemnt())
            view.my_post.setTextColor(ContextCompat.getColor(context!!, R.color.et_line))
            view.my_saved_post.setTextColor(ContextCompat.getColor(context!!, R.color.top_view))
        }
        return view
    }

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

        responsefromserverForInitial = response.user_detail
        mCompositeDisposable?.clear()
        bindDataWithUi()


    }


    // handle failure response of api call
    private fun handleError(error: Throwable) {
        mCompositeDisposable?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }

    private fun replaceFragment(fragment: Fragment) {

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.place_holder_for_fragment_edit, fragment)
        //  transaction.addToBackStack(null)
        transaction?.commit()
    }

    private fun bindDataWithUi() {
        if (responsefromserverForInitial != null) {

            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.ic_person_black_24dp)
            requestOptions.error(R.drawable.ic_person_black_24dp)

            if (responsefromserverForInitial!!.get(0).name.isEmpty()) {
                tv_nav_user_name.text = "नाम अपडेट करे!"
            } else {
                tv_nav_user_name.text = responsefromserverForInitial!!.get(0).name
            }

            if (responsefromserverForInitial!!.get(0).state.isEmpty()) {
                tv_city.text = "अपडेट करे!"
            } else {
                tv_city.text = responsefromserverForInitial!!.get(0).statename
            }

            if (responsefromserverForInitial!!.get(0).city.isEmpty()) {
                tv_district.text = "अपडेट करे!"
            } else {
                tv_district.text = responsefromserverForInitial!!.get(0).cityname
            }
            Glide.with(this).load(responsefromserverForInitial!!.get(0).image).apply(requestOptions).into(iv_nav_profile)


        }
    }



}
