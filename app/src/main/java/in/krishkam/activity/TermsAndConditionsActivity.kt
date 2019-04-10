package `in`.krishkam.activity

import `in`.krishkam.R
import `in`.krishkam.base.BaseActivity
import `in`.krishkam.networkUtils.ApiRequestClient
import `in`.krishkam.pojo.termsandconditions.ResponseFromServerTermsAndConditions
import android.os.Bundle
import android.support.v4.content.ContextCompat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_terms_and_conditions.*

class TermsAndConditionsActivity : BaseActivity() {
    private var mCompositeDisposable: CompositeDisposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_conditions)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.title = getString(R.string.lekh)
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.white))
        getTermsAndConditions()
    }

    private fun getTermsAndConditions() {

        showDialogLoading()
        mCompositeDisposable = CompositeDisposable()

        mCompositeDisposable?.add(ApiRequestClient.createREtrofitInstance()
                .getTerms()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }


    // handle sucess response of api call
    private fun handleResponse(response: ResponseFromServerTermsAndConditions) {
        hideDialogLoading()

        if(response.result!=null){

            tv_terms.text = response.result.get(0).content
        }





    }


    // handle failure response of api call
    private fun handleError(error: Throwable) {
        mCompositeDisposable?.clear()
        hideDialogLoading()
        showSnackBar(error.localizedMessage)


    }
}
