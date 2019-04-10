package `in`.krishkam.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search_agian.view.*

import `in`.krishkam.R
import `in`.krishkam.adapter.CustomSearchAgainAdapter
import `in`.krishkam.base.BaseFragment
import `in`.krishkam.callback.InterfaceSearchAgainSuggestions
import `in`.krishkam.networkUtils.ApiRequestClient
import `in`.krishkam.pojo.searchsuggestion.ResponseFromServerSearchSuggestions
import `in`.krishkam.pojo.searchsuggestion.Result
import android.text.Editable

import android.text.TextWatcher
import android.widget.EditText

import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.content_user_feed.*

import `in`.krishkam.activity.UserFeedActivity
import `in`.krishkam.base.BaseApplication
import `in`.krishkam.dataprefence.DataManager
import `in`.krishkam.pojo.AllPostFeed.ResponseFromServerAllfeed


class SearchAgianFragment : BaseFragment(), InterfaceSearchAgainSuggestions {
    override fun searchname(name: String) {
        editText.setText(name)
        editText.isEnabled = false
        replaceFragmentForHome(name)
    }

    var searchname: String? = null
    lateinit var editText: EditText
    private var mUserSearchSuggestion: MutableList<Result>? = null
    private var mSuggestionSearchAdapter: CustomSearchAgainAdapter? = null
    private var mCompositeDisposable: CompositeDisposable? = null
    private var mCompositeDisposable_search_valiadtion: CompositeDisposable? = null
    private lateinit var dataManager: DataManager
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_search_agian, container, false)
        dataManager = BaseApplication.baseApplicationInstance.getdatamanger()
        editText = view.search
        if (arguments?.getString("searchname").isNullOrEmpty()) {
        } else {
            searchname = arguments!!.getString("searchname")
            editText.setText(searchname)
        }
        view.recycler_view_search.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        mUserSearchSuggestion = ArrayList()
        mSuggestionSearchAdapter = CustomSearchAgainAdapter(mUserSearchSuggestion!!, this)
        view.recycler_view_search.adapter = mSuggestionSearchAdapter

        view.btn_search.setOnClickListener {

            if(editText.text.toString().isNullOrEmpty()){
                showAlertDialog("यह उपलब्ध नहीं है !","पुनः सर्च करे !")
                hideKeyboard(view.btn_search)
            }
            else{
                getAllUserFeed(editText.text.toString())
                hideKeyboard(view.btn_search)

            }



        }

        view.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
                if (!mUserSearchSuggestion!!.isEmpty()) {
                    mUserSearchSuggestion!!.clear()
                    mSuggestionSearchAdapter!!.notifyDataSetChanged()
                    getSearchSuggestions(view.search.text.toString())
                } else {
                    getSearchSuggestions(view.search.text.toString())
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
        (activity as UserFeedActivity).navigation.visibility = View.INVISIBLE
    }

    private fun getSearchSuggestions(query: String) {


        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable?.add(ApiRequestClient.createREtrofitInstance()
                .getSearchSuggestions(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }


    // handle sucess response of api call
    private fun handleResponse(response: ResponseFromServerSearchSuggestions) {

        if (response.response_code.equals("1")) {
            // showSnackBar(response.response_message)
        } else {

            mUserSearchSuggestion?.addAll(response.result)
            mSuggestionSearchAdapter?.notifyDataSetChanged()
            mCompositeDisposable?.clear()
        }


    }

    // handle failure response of api call
    private fun handleError(error: Throwable) {
        mCompositeDisposable?.clear()

        showSnackBar(error.localizedMessage)


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onStop() {
        (activity as UserFeedActivity).navigation.visibility = View.VISIBLE
        (activity as AppCompatActivity).supportActionBar!!.show()
        super.onStop()

    }

    private fun getAllUserFeed(argument: String?) {

        showDialogLoading()
        mCompositeDisposable_search_valiadtion = CompositeDisposable()

        mCompositeDisposable_search_valiadtion?.add(ApiRequestClient.createREtrofitInstance()
                .getUserAllHomeFeed(dataManager.getUserId(),argument )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse_search_validation, this::handleError_search_valiadtion))
    }


    // handle sucess response of api call
    private fun handleResponse_search_validation(responseFromServerAllfeed: ResponseFromServerAllfeed) {
        hideDialogLoading()


        if(responseFromServerAllfeed.post.isEmpty()){

            showAlertDialog("यह उपलब्ध नहीं है !","पुनः सर्च करे !")
        }
        else{
            replaceFragmentForHome(editText.text.toString())

        }
        mCompositeDisposable_search_valiadtion?.clear()


    }


    // handle failure response of api call
    private fun handleError_search_valiadtion(error: Throwable) {

        mCompositeDisposable_search_valiadtion?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)

    }

  fun  replaceFragmentForHome(stringsearch: String){
      val homeFragment = HomeFragment()
      val data = Bundle()
      data.putString("data", stringsearch)
      data.putString("dataForHitServer", "yes")
      homeFragment.arguments = data
      val transaction = activity?.supportFragmentManager?.beginTransaction()
      transaction?.replace(R.id.place_holder_for_fragment, homeFragment)
      transaction?.commit()

  }
}
