package `in`.krishkam.Fragment


import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import `in`.krishkam.R
import android.view.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_trending_fragemnt.view.*
import `in`.krishkam.base.BaseFragment
import `in`.krishkam.callback.InterfaceSearchSuggestions
import `in`.krishkam.networkUtils.ApiRequestClient
import `in`.krishkam.pojo.hashTagTrending.Post
import `in`.krishkam.pojo.hashTagTrending.ResponseFromServerHashTag
import `in`.krishkam.pojo.searchsuggestion.ResponseFromServerSearchSuggestions
import `in`.krishkam.pojo.searchsuggestion.Result
import android.widget.PopupWindow
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.content_user_feed.*
import `in`.krishkam.activity.UserFeedActivity
import `in`.krishkam.adapter.*
import `in`.krishkam.base.BaseApplication
import `in`.krishkam.callback.InterfaceCitySelectListner
import `in`.krishkam.callback.InterfaceStateSelectListner
import `in`.krishkam.dataprefence.DataManager
import `in`.krishkam.pojo.ServerResponseFromUplaodImage
import `in`.krishkam.pojo.UserEditShowInitial.ResponseFromServerInitialEditUser
import `in`.krishkam.pojo.city.ResponseFromServerCity
import `in`.krishkam.pojo.state.ResponseFromServerStateList
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.support.v7.widget.*
import android.widget.LinearLayout.HORIZONTAL
import kotlinx.android.synthetic.main.city_list.*
import kotlinx.android.synthetic.main.profile_dialog.view.*
import kotlinx.android.synthetic.main.rating_dialog.view.*
import kotlinx.android.synthetic.main.sujaho_dialog.view.*


class TrendingFragemnt : BaseFragment(), InterfaceSearchSuggestions, InterfaceStateSelectListner, InterfaceCitySelectListner {


    override fun searchname(name: String) {
        val homeFragment = HomeFragment()
        val data = Bundle()
        data.putString("data", name)
        data.putString("dataForHitServer", "yes")
        homeFragment.arguments = data
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.place_holder_for_fragment, homeFragment)
        transaction?.commit()
    }

    private var searchView: SearchView? = null
    private var queryTextListener: SearchView.OnQueryTextListener? = null
    private var mCompositeDisposable: CompositeDisposable? = null
    private var mCompositeDisposable_hashTag: CompositeDisposable? = null
    private var mCompositeDisposable_Trending: CompositeDisposable? = null
    private var mSuggestionSearchAdapter: CustomSuggestionSearchAdapter? = null
    private var mHashTagListAdapter: CustomAdapterForHashtagList? = null
    private var mTrendingListAdapter: CustomAdapterForTrending? = null
    private var mUserSearchSuggestion: MutableList<Result>? = null
    private var mHashTagList: MutableList<Post>? = null
    private var mTrendingList: MutableList<Post>? = null
    private lateinit var dataManager: DataManager
    var linear1: RecyclerView? = null
    private lateinit var state_id: String
    private lateinit var city_id: String
    private var mAndroidStateList: MutableList<`in`.krishkam.pojo.state.Result>? = null
    private lateinit var dialog_city: Dialog
    private lateinit var dialog_State: Dialog
    private var mCompositeDisposable_city: CompositeDisposable? = null
    private var mCompositeDisposable_state: CompositeDisposable? = null
    private var mStateAdapter: CustomAdapterForState? = null
    private var mCityAdapter: CustomAdapterForCity? = null
    private lateinit var city_name: String
    private var mCompositeDisposable_Update_Profile: CompositeDisposable? = null
    private lateinit var sujao: String

    var dialogView: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_trending_fragemnt, container, false)
        linear1 = view.recycler_view_hashtag
        dataManager = BaseApplication.baseApplicationInstance.getdatamanger()

        // trending list
        mTrendingList = ArrayList()
        view.recycler_view_trending.layoutManager = GridLayoutManager(activity, 3) as RecyclerView.LayoutManager?
        mTrendingListAdapter = CustomAdapterForTrending(mTrendingList!!, this)
        view.recycler_view_trending.adapter = mTrendingListAdapter

        // hashtaglist
        mHashTagList = ArrayList()
        view.recycler_view_hashtag.layoutManager = GridLayoutManager(activity, 3)
        mHashTagListAdapter = CustomAdapterForHashtagList(mHashTagList!!, this)
        view.recycler_view_hashtag.adapter = mHashTagListAdapter


        //suggestions
        view.recycler_view_search.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        mUserSearchSuggestion = ArrayList()
        mSuggestionSearchAdapter = CustomSuggestionSearchAdapter(mUserSearchSuggestion!!, this)
        view.recycler_view_search.adapter = mSuggestionSearchAdapter
        getHashtagList()
        state_id = ""
        city_id = ""

        return view


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_trending, menu)
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
        popDialog.setTitle("हमें सुझाव दे !")

         popDialog.create()

        // Set a positive button and its click listener on alert dialog
        popDialog.setPositiveButton(android.R.string.ok) { dialog, which ->

            sujao = dialogView.et_sujaho.text.toString()
            if (sujao.isEmpty()) {
                dialogView.et_sujaho.setError("हमें सुझाव दे !")
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
                .subscribe(this::handleResponse, this::handleError))
    }


    // handle sucess response of api call
    private fun handleResponse(response: ResponseFromServerInitialEditUser) {
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
    private fun handleError(error: Throwable) {
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


    private fun getHashtagList() {

        showDialogLoading()
        mCompositeDisposable_hashTag = CompositeDisposable()

        mCompositeDisposable_hashTag?.add(ApiRequestClient.createREtrofitInstance()
                .getHashTagList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_hash_Tag, this::handleError_hash_tag_eror))
    }


    // handle sucess response of api call
    private fun handleResponse_hash_Tag(response: ResponseFromServerHashTag) {

        if (response.response_message.equals("Nopost")) {
            hideDialogLoading()
            //showSnackBar(response.response_message)
            getTrendingList()
        } else {
            mHashTagList?.addAll(response.post)
            mHashTagListAdapter?.notifyDataSetChanged()
            mCompositeDisposable_hashTag?.clear()
            getTrendingList()
        }


    }

    // handle failure response of api call
    private fun handleError_hash_tag_eror(error: Throwable) {
        hideDialogLoading()

        showSnackBar(error.localizedMessage)
        mCompositeDisposable_hashTag?.clear()

    }

    private fun getTrendingList() {


        mCompositeDisposable_Trending = CompositeDisposable()
        mCompositeDisposable_Trending?.add(ApiRequestClient.createREtrofitInstance()
                .getTrendingList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_trending, this::handleError_trendng))
    }


    // handle sucess response of api call
    private fun handleResponse_trending(response: ResponseFromServerHashTag) {

        if (response.response_message.equals("Nocategory")) {
            hideDialogLoading()
            // showSnackBar(response.response_message)
            val homeFragment = SearchAgianFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.place_holder_for_fragment, homeFragment)
            transaction?.commit()
        } else {

            hideDialogLoading()
            mTrendingList?.addAll(response.post)
            mTrendingListAdapter?.notifyDataSetChanged()
            mCompositeDisposable_Trending?.clear()
        }


    }

    // handle failure response of api call
    private fun handleError_trendng(error: Throwable) {
        hideDialogLoading()

        showSnackBar(error.localizedMessage)
        mCompositeDisposable_Trending?.clear()

    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.show()
        (activity as UserFeedActivity).navigation.visibility = View.VISIBLE
    }

    override fun onStop() {

        super.onStop()

    }

    private fun initState() {
        mAndroidStateList = ArrayList<`in`.krishkam.pojo.state.Result>()

        dialog_State = Dialog(activity!!)
        dialog_State.setContentView(R.layout.city_list)
        dialog_State.setCanceledOnTouchOutside(false)
        dialog_State.setCancelable(true)
        dialog_State.setTitle(" स्टेट का चयन करें!")
        dialog_State.recycler_view.layoutManager = LinearLayoutManager(activity!!, LinearLayout.VERTICAL, false)
        val itemDecor = DividerItemDecoration(activity!!, HORIZONTAL)
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

    // initiliaze city dialog
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
