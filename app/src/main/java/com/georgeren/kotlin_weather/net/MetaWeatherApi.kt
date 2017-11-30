package com.georgeren.kotlin_weather.net

import com.georgeren.kotlin_weather.mvp.models.WeatherData
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Created by georgeRen on 2017/11/30.
 */
interface MetaWeatherApi{
    @GET("weatherdata")
    fun getLocationDetails(@Header("X-Mashape-Key") key: String, @Header("Accept") type: String,
                           @Query("lat") lat: Double, @Query("lng") lng: Double): Observable<WeatherData>
}