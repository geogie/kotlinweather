package com.georgeren.kotlin_weather.net

import com.georgeren.kotlin_weather.consts.Constants
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * Created by georgeRen on 2017/11/30.
 * object：声明一个单例
 * ::class.java：获取 Java Class
 * ::class：获取Kotlin的 KClass
 */
object NetworkService {
    val retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    fun getMetaWeatherApi() = retrofit.create(MetaWeatherApi::class.java)
}