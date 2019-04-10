package `in`.krishkam.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_feed.*
import kotlinx.android.synthetic.main.app_bar_user_feed.*
import kotlinx.android.synthetic.main.content_user_feed.*
import kotlinx.android.synthetic.main.nav_header_user_feed.view.*
import `in`.krishkam.Fragment.*
import `in`.krishkam.R
import `in`.krishkam.base.BaseActivity
import `in`.krishkam.base.BaseApplication
import  `in`.krishkam.dataprefence.DataManager
import`in`.krishkam.networkUtils.ApiRequestClient
import `in`.krishkam.pojo.ServerResponseFromUplaodImage
import `in`.krishkam.pojo.UserEditShowInitial.ResponseFromServerInitialEditUser
import `in`.krishkam.pojo.UserEditShowInitial.UserDetail
import com.google.firebase.analytics.FirebaseAnalytics
import android.util.StatsLog.logEvent
import android.R.attr.name
import android.content.Intent
import android.net.Uri
import android.content.DialogInterface
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater


import kotlinx.android.synthetic.main.rating_dialog.view.*
import kotlinx.android.synthetic.main.sujaho_dialog.view.*


class UserFeedActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var dataManager: DataManager
    var responsefromserverForInitial: MutableList<UserDetail>? = null
    private var mCompositeDisposable: CompositeDisposable? = null
    lateinit var navigation1: BottomNavigationView
    var headerLayout: View? = null
    var refresh: Boolean = false
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private lateinit var sujao: String
    private var mCompositeDisposable_Update_Profile: CompositeDisposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_feed)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        dataManager = BaseApplication.baseApplicationInstance.getdatamanger()


        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        //naviagtion drawer
        nav_view.setNavigationItemSelectedListener(this)

        addHomeFragment()
        bindDataWithUi()
        getInitalEditProfile()
        // find header view in naviagtion drawer
        headerLayout = nav_view.getHeaderView(0) // 0-index header


        headerLayout?.setOnClickListener {
            refresh = true

            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            }

            replaceFragment(UserEditFragment(), getString(R.string.myprofile))


        }
        navigation1 = navigation
        //bottom naviagtion view
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, dataManager.getUserId())
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, dataManager.getUserMobile())
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

    }

    override fun onResume() {
        super.onResume()
        if (refresh) {
            getInitalEditProfile()
        }
    }

    private fun bindDataWithUi() {
        if (responsefromserverForInitial != null) {

            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.ic_person_black_24dp)
            requestOptions.error(R.drawable.ic_person_black_24dp)

            if (responsefromserverForInitial!!.get(0).name.isEmpty()) {
                headerLayout!!.tv_nav_user_name.text = "नाम अपडेट करे!"
            } else {
                headerLayout!!.tv_nav_user_name.text = responsefromserverForInitial!!.get(0).name
            }

            if (responsefromserverForInitial!!.get(0).state.isEmpty()) {
                headerLayout!!.tv_city.text = "अपडेट करे!"
            } else {
                headerLayout!!.tv_city.text = responsefromserverForInitial!!.get(0).statename
            }

            if (responsefromserverForInitial!!.get(0).city.isEmpty()) {
                headerLayout!!.tv_district.text = "अपडेट करे!"
            } else {
                headerLayout!!.tv_district.text = responsefromserverForInitial!!.get(0).cityname
            }




            headerLayout!!.tv_nav_user_mobile.text = responsefromserverForInitial!!.get(0).mobile
            Glide.with(this).load(responsefromserverForInitial!!.get(0).image).apply(requestOptions).into(headerLayout!!.iv_nav_profile)
            dataManager.saveUserName(responsefromserverForInitial!!.get(0).name)
            dataManager.saveUserMobile(responsefromserverForInitial!!.get(0).mobile)
            dataManager.saveUserProfilePic(responsefromserverForInitial!!.get(0).image)
        }
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


    private fun addHomeFragment() {
        //  toolbar.setTitle(getString(R.string.title_home))
        val homeFragment = HomeFragment()
        val data = Bundle()
        data.putString("data", getString(R.string.title_home))
        data.putString("dataForHitServer", "no")
        homeFragment.arguments = data
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.place_holder_for_fragment, homeFragment)
        //  transaction.addToBackStack(null)
        transaction.commit()
        toolbar.title = getString(R.string.title_home)
    }

    private fun replaceFragment(fragment: Fragment, string: String) {
        toolbar.title = string
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.place_holder_for_fragment, fragment)
        transaction.commit()
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {

                val homeFragment = HomeFragment()
                val data = Bundle()
                data.putString("data", getString(R.string.title_home))
                data.putString("dataForHitServer", "no")
                homeFragment.arguments = data
                replaceFragment(homeFragment, getString(R.string.title_home))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_video -> {

                replaceFragment(VideoFragment(), getString(R.string.video))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_smachar -> {
                replaceFragment(SamaCharFragment(), getString(R.string.samachar))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_lekh -> {

                replaceFragment(LekhFragment(), getString(R.string.lekh))
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)

        } else {
            if (supportFragmentManager.findFragmentById(R.id.place_holder_for_fragment) is HomeFragment) {
                super.onBackPressed()
            } else {
                navigation1.selectedItemId = R.id.navigation_home
                val homeFragment = HomeFragment()
                val data = Bundle()
                data.putString("data", getString(R.string.title_home))
                data.putString("dataForHitServer", "no")
                homeFragment.arguments = data
                replaceFragment(homeFragment, getString(R.string.title_home))
            }


        }


    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                //toolbar.setTitle(getString(R.string.app_name_title))
                val homeFragment = HomeFragment()
                val data = Bundle()
                data.putString("data", getString(R.string.title_home))
                data.putString("dataForHitServer", "no")
                homeFragment.arguments = data

                replaceFragment(homeFragment, getString(R.string.title_home))
            }

            R.id.nav_video -> {
                replaceFragment(VideoFragment(), getString(R.string.video))
            }
            R.id.nav_samachar -> {
                replaceFragment(SamaCharFragment(), getString(R.string.samachar))
            }
            R.id.nav_lekh -> {
                replaceFragment(LekhFragment(), getString(R.string.lekh))
            }
            R.id.nav_saved_post -> {
                replaceFragment(UserSavedFeedListFragemnt(), getString(R.string.savedpost))
            }
            R.id.nav_rating -> {

                ShowRatingDialog()



            }
            R.id.nav_search -> {
                replaceFragment(TrendingFragemnt(), getString(R.string.search))
            }
            R.id.nav_upyog -> {

                launchActivity<TermsAndConditionsActivity>()

            }
            R.id.nav_logout -> {

                dataManager.clear()
                endActivity<MainActivity>()
                finish()

            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun ShowRatingDialog() {

        val popDialog = AlertDialog.Builder(this)
        val inflater: LayoutInflater = this.getLayoutInflater()
        val dialogView: View = inflater.inflate(R.layout.rating_dialog, null)
        popDialog.setView(dialogView);
        popDialog.setIcon(android.R.drawable.btn_star_big_on)
        popDialog.setTitle("हमें रेटिंग दे !")

        popDialog.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->


            dialog.dismiss()
            var rating   =dialogView.rating_bar.progress
            if(rating>4){
                showDhnwad()



            }else{
                sujhaoDialog()
              //  showDhnwadfornot()
            }

        }).setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        popDialog.create()
        popDialog.show()


    }

    fun showDhnwad() {

        val popDialog = AlertDialog.Builder(this)
        popDialog.setTitle("हमे रेटिंग देने के लिए धन्यवाद! कृपया अप्प स्टोर पर भी 5 सितारा रेट करें!")
        popDialog.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->

            dialog.dismiss()



                val appPackageName = packageName // getPackageName() from Context or Activity object
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                } catch (anfe: android.content.ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                }




        }).setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        popDialog.create()
        popDialog.show()


    }

    fun sujhaoDialog() {

        val popDialog = AlertDialog.Builder(this)
        val inflater: LayoutInflater = this.getLayoutInflater()
        val dialogView: View = inflater.inflate(R.layout.sujaho_dialog, null)
        popDialog.setView(dialogView);
        popDialog.setTitle("सुझाव दे और क्या बेहतर  कर सकते है !")

        popDialog.create()

        // Set a positive button and its click listener on alert dialog
        popDialog.setPositiveButton(android.R.string.ok) { dialog, which ->

            sujao = dialogView.et_sujaho.text.toString()
            if (sujao.isEmpty()) {
                dialogView.et_sujaho.setError("सुझाव दे और क्या बेहतर  कर सकते है !!")
                dialogView.et_sujaho.requestFocus()

            } else {

                uploadingUserProfileUpdate()

            }


        }



        popDialog.setNegativeButton("Cancel") { dialog, which ->

        }

        popDialog.show()


    }

    private fun uploadingUserProfileUpdate() {


        showDialogLoading()
        mCompositeDisposable_Update_Profile = CompositeDisposable()
        mCompositeDisposable_Update_Profile?.add(ApiRequestClient.createREtrofitInstance()
                .userSujhao(dataManager.getUserId(), "", "", "", sujao,"")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUpdateProfile, this::handleErrorUpdateProfile))
    }


    // handle sucess response of api call
    private fun handleResponseUpdateProfile(response: ServerResponseFromUplaodImage) {
        hideDialogLoading()

        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder.setMessage("आपके सुझाव  के लिए धन्यवाद !")

                .setCancelable(false)

                .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id ->

                })
        val alert = dialogBuilder.create()
        alert.show()
        mCompositeDisposable_Update_Profile?.clear()

    }

    // handle failure response of api call
    private fun handleErrorUpdateProfile(error: Throwable) {
        hideDialogLoading()
        showSnackBar(error.localizedMessage)
        mCompositeDisposable_Update_Profile?.clear()

    }
    fun showDhnwadfornot() {

        val popDialog = AlertDialog.Builder(this)


        popDialog.setTitle("हमें रेटिंग देने के लिए धयानवाद !")
        popDialog.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->


            dialog.dismiss()


        }).setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        popDialog.create()
        popDialog.show()


    }
}
