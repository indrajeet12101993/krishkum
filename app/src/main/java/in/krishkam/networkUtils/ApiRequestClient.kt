package `in`.krishkam.networkUtils

import com.google.gson.GsonBuilder
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import `in`.krishkam.BuildConfig
import `in`.krishkam.constants.AppConstants
import java.util.concurrent.TimeUnit



object ApiRequestClient {
    fun createREtrofitInstance(): ApiRequestEndPoint {
        val rxAdapter= RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
        val builder  = OkHttpClient().newBuilder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor())


        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(rxAdapter)
                .addConverterFactory(GsonConverterFactory.create())
                .client(builder.build())
                .baseUrl(AppConstants.BASE_URL)
                .build()

        return retrofit.create(ApiRequestEndPoint::class.java);
    }

    fun loggingInterceptor() = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }
}