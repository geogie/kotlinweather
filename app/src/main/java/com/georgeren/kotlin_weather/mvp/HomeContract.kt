package com.georgeren.kotlin_weather.mvp

import android.content.Context
import com.georgeren.kotlin_weather.mvp.models.WeatherData

/**
 * Created by georgeRen on 2017/11/29.
 */
interface HomeContract {
    interface View {
        fun onDataFetched(weatherData: WeatherData?)

        fun onStoredDataFetched(weatherData: WeatherData?)

        fun onError()

        fun getContext(): Context
    }

    interface Presenter {
        fun subscribe(view: HomeContract.View)

        fun unSubscribe()

        fun refresh(lat: Double, long: Double)
    }
}