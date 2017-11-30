package com.georgeren.kotlin_weather.mvp

import android.content.Context
import com.georgeren.kotlin_weather.consts.Constants
import com.georgeren.kotlin_weather.consts.Secrets
import com.georgeren.kotlin_weather.mvp.models.WeatherData
import com.georgeren.kotlin_weather.net.NetworkService
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * Created by georgeRen on 2017/11/30.
 */
class HomePresenter : HomeContract.Presenter {

    var mView: HomeContract.View? = null

    override fun subscribe(view: HomeContract.View) {
        mView = view

        val storeWeather = getFileFromStorage(mView?.getContext())
        if (storeWeather != null) {
            mView?.onStoredDataFetched(storeWeather)
        }
    }

    override fun unSubscribe() {
        mView = null
    }

    override fun refresh(lat: Double, long: Double) {
        NetworkService.getMetaWeatherApi()
                .getLocationDetails(Secrets.API_KEY, Constants.TYPE_TEXT_PLAIN, lat, long)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ weatherData ->
                    mView?.onDataFetched(weatherData)
                    storeFileToExternalStorage(weatherData, mView?.getContext())
                }, { error ->
                    mView?.onError()
                })
    }

    private fun storeFileToExternalStorage(weatherData: WeatherData, context: Context?) {
        val gson = Gson()
        val weatherJson = gson.toJson(weatherData)

        val weatherFile = File(mView?.getContext()?.filesDir, Constants.WEATHER_FILE_NAME)
        if (weatherFile.exists()) weatherFile.delete()
        weatherFile.createNewFile()

        val outputStream = mView?.getContext()?.openFileOutput(Constants.WEATHER_FILE_NAME, Context.MODE_PRIVATE)
        outputStream?.write(weatherJson.toByteArray())
        outputStream?.close()
    }

    /**
     * 从文件里获取天气信息
     */
    private fun getFileFromStorage(context: Context?): WeatherData? {
        try {
            val weatherFile = File(context?.filesDir, Constants.WEATHER_FILE_NAME)
            val weatherJson = weatherFile.readText()
            val gson = Gson()
            val weatherData = gson.fromJson(weatherJson, WeatherData::class.java)
            return weatherData
        } catch (e: Exception) {
            return null
        }
    }

}