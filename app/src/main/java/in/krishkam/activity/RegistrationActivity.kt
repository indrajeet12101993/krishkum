package `in`.krishkam.activity


import android.app.Dialog
import android.content.DialogInterface
import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ListView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_registration.*
import `in`.krishkam.R
import `in`.krishkam.base.BaseActivity
import `in`.krishkam.base.BaseApplication
import `in`.krishkam.constants.AppDataConstantsValue
import `in`.krishkam.dataprefence.DataManager
import `in`.krishkam.networkUtils.ApiRequestClient
import `in`.krishkam.pojo.ResponseFromServerForUserRegistartion
import `in`.krishkam.pojo.state.ResponseFromServerStateList
import `in`.krishkam.utils.Validation

import android.widget.ArrayAdapter
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.city_list.*
import kotlinx.android.synthetic.main.fragment_video.view.*
import `in`.krishkam.pojo.state.Result

import `in`.krishkam.adapter.CustomAdapterForState
import`in`.krishkam.callback.InterfaceStateSelectListner
import android.support.v7.widget.DividerItemDecoration
import `in`.krishkam.adapter.CustomAdapterForCity
import `in`.krishkam.callback.InterfaceCitySelectListner
import `in`.krishkam.pojo.city.ResponseFromServerCity


class RegistrationActivity : BaseActivity(), InterfaceStateSelectListner, InterfaceCitySelectListner {


    private lateinit var dataManager: DataManager
    private var mCompositeDisposable: CompositeDisposable? = null
    private var mCompositeDisposable_city: CompositeDisposable? = null
    private var mCompositeDisposable_state: CompositeDisposable? = null
    private lateinit var dialog_city: Dialog
    private lateinit var dialog_State: Dialog

    private var mAndroidStateList: MutableList<Result>? = null
    private var mAndroidCityList: MutableList<`in`.krishkam.pojo.city.Result>? = null
    private var mStateAdapter: CustomAdapterForState? = null
    private var mCityAdapter: CustomAdapterForCity? = null
    private lateinit var state_id: String
    private lateinit var city_name: String
    private lateinit var city_id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        dataManager = BaseApplication.baseApplicationInstance.getdatamanger()
        state_id=""
        city_id=""

        // initialaiae sttae list dialog
        initState()

        // execute api of state
        getState()

        // show state dialog
        et_state.setOnClickListener {
            showSTATE()
        }
        // show city dialog
        et_district.setOnClickListener {
            initCity()
        }

        // click event on next button
        btn_aage_badhe.setOnClickListener {

            val name: String = et_name.text.toString()
            val state: String = et_state.text.toString()
            val district: String = et_district.text.toString()
            val village: String = et_village.text.toString()
            if (Validation.isEmptyField(name)) {
                et_name.error = getString(R.string.emptynumber)
                et_name.requestFocus()
                return@setOnClickListener
            }
            if (Validation.isEmptyField(state)) {
                et_state.error = getString(R.string.emptystate)
                et_state.requestFocus()
                return@setOnClickListener
            }
            if (Validation.isEmptyField(district)) {
                et_district.error = getString(R.string.emptydistrict)
                et_district.requestFocus()
                return@setOnClickListener
            }
            if (Validation.isEmptyField(village)) {
                et_village.error = getString(R.string.emptyvillage)
                et_village.requestFocus()
                return@setOnClickListener
            }

            startRegistration(name, state, district, village)
        }
        btn_baad_me_kare.setOnClickListener {
            launchActivity<UserPhotoUploadActivity>()
            finish()
        }
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
        if(state_id!=null){

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
        et_district.text= null
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

    // api call for user registration
    private fun startRegistration(name: String, state: String, district: String, village: String) {

        showDialogLoading()
        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable?.add(ApiRequestClient.createREtrofitInstance()
                .postServerUserDetailsWithCity(dataManager.getUserId(), name, state_id, city_id, village)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }


    // handle sucess response of api call
    private fun handleResponse(responseFromServerForUserRegistartion: ResponseFromServerForUserRegistartion) {
        hideDialogLoading()


        if (responseFromServerForUserRegistartion.response_code.equals("0")) {
            setUserToTrue()


        }
        if (responseFromServerForUserRegistartion.response_code.equals("1")) {
            showSnackBar(responseFromServerForUserRegistartion.response_message)
        }

        mCompositeDisposable?.clear()

    }


    private fun setUserToTrue() {

          dataManager.setRegistartion(true)
        dataManager.saveUserName(et_name.text.toString())
        launchActivity<UserPhotoUploadActivity>()
        finish()
    }


    // handle failure response of api call
    private fun handleError(error: Throwable) {
        hideDialogLoading()

        showSnackBar(error.localizedMessage)
        mCompositeDisposable?.clear()

    }

}
