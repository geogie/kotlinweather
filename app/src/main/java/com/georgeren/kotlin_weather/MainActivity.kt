package com.georgeren.kotlin_weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView
import com.georgeren.kotlin_weather.dialog.ForecastDialogFragment
import com.georgeren.kotlin_weather.mvp.ForecastRecyclerAdapter
import com.georgeren.kotlin_weather.mvp.HomeContract
import com.georgeren.kotlin_weather.mvp.HomePresenter
import com.georgeren.kotlin_weather.mvp.models.WeatherData
import com.georgeren.kotlin_weather.utils.WeatherToImage

class MainActivity : AppCompatActivity(), HomeContract.View {

    private val RC_ENABLE_LOCATION = 1
    private val RC_LOCATION_PERMISSION = 2
    private val TAG_FORECAST_DIALOG = "forecast_dialog"
    var mPresenter: HomeContract.Presenter? = null
    var mLocationManager: LocationManager? = null
    var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    var mLocation: Location? = null

    var mLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            mSwipeRefreshLayout?.isRefreshing = true
            mPresenter?.refresh(location?.latitude ?: 0.0, location?.longitude ?: 0.0)

            if (location?.latitude != null && location.latitude != 0.0 && location.longitude != 0.0) {
                mLocation = location
                mLocationManager?.removeUpdates(this)
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }
    }

    override fun onDataFetched(weatherData: WeatherData?) {
        mSwipeRefreshLayout?.isRefreshing = false
        updateUI(weatherData)
    }

    override fun onStoredDataFetched(weatherData: WeatherData?) {
        updateUI(weatherData)
    }

    override fun onError() {
        mSwipeRefreshLayout?.isRefreshing = false
        // 显示snackbar，不消失显示，除非手动取消
        val coordinatorLayout = findViewById(R.id.coordinator_layout) as CoordinatorLayout
        val retrySnackBar = Snackbar.make(coordinatorLayout, "无法获取天气信息。", Snackbar.LENGTH_INDEFINITE)
        retrySnackBar.setAction("Retry") { v ->
            mPresenter?.refresh(mLocation?.latitude ?: 0.0, mLocation?.longitude ?: 0.0)
            mSwipeRefreshLayout?.isRefreshing = true
            retrySnackBar.dismiss()
        }
        retrySnackBar.setActionTextColor(ContextCompat.getColor(this, R.color.md_white_1000))
        retrySnackBar.show()
    }

    override fun getContext() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        mPresenter = HomePresenter()
        mPresenter?.subscribe(this)

        initViews()

        if (checkAndAskForLocationPermissions()) {
            checkGpsEnableAndPrompt()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            RC_LOCATION_PERMISSION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkGpsEnableAndPrompt()
                } else {
                    checkAndAskForLocationPermissions()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_ENABLE_LOCATION -> {
                checkGpsEnableAndPrompt()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.unSubscribe()
        mLocationManager?.removeUpdates(mLocationListener)
    }

    private fun initViews() {
        mSwipeRefreshLayout?.setOnRefreshListener {
            if (mLocation != null) {
                mPresenter?.refresh(mLocation?.latitude ?: 0.0, mLocation?.longitude ?: 0.0)
            } else {
                mSwipeRefreshLayout?.isRefreshing = false
            }
        }
    }

    /**
     * 检查是否有 gps 权限：有 获取位置，无 提示用户去允许
     */
    private fun checkGpsEnableAndPrompt() {
        // gps 是否有权限
        val isLocationEnable = mLocationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isLocationEnable) {
            // 显示提示窗口 去开启gps
            AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("GPS 不能用")
                    .setMessage("此应用需要GPS权限来获取天气信息.请打开GPS？")
                    .setPositiveButton(android.R.string.ok, {
                        // 跳转到手机设置界面去打开gps权限
                        dialog, which ->
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivityForResult(intent, RC_ENABLE_LOCATION)
                        dialog.dismiss()
                    })
                    .setNegativeButton(android.R.string.cancel, { dialog, which ->
                        dialog.dismiss()
                    })
        } else {
            requestLocationUpdates()
        }
    }

    /**
     * 更新位置信息
     */
    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val provider = LocationManager.NETWORK_PROVIDER
        // 添加位置监听 请求更新位置信息
        mLocationManager?.requestLocationUpdates(provider, 0, 0.0f, mLocationListener)

        val location = mLocationManager?.getLastKnownLocation(provider)
        mLocationListener.onLocationChanged(location)
    }

    private fun checkAndAskForLocationPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), RC_LOCATION_PERMISSION)
                return false
            }
        }
        return true
    }

    /**
     * 根据网络数据来更新UI
     */
    private fun updateUI(weatherData: WeatherData?) {
        val temperatureTextView = findViewById(R.id.temperature_text_view) as TextView
        val windSpeedTextView = findViewById(R.id.wind_speed_text_view) as TextView
        val humidityTextView = findViewById(R.id.humidity_text_view) as TextView
        val weatherImageView = findViewById(R.id.weather_image_view) as ImageView
        val weatherConditionTextView = findViewById(R.id.weather_condition_text_view) as TextView
        val cityNameTextView = findViewById(R.id.city_name_text_view) as TextView

        val formattedTemperatureText = String.format(getString(R.string.celcius_temperature), weatherData?.query?.results?.channel?.item?.condition?.temp ?: "")

        // 设置风速、湿度
        temperatureTextView.text = formattedTemperatureText
        windSpeedTextView.text = "${weatherData?.query?.results?.channel?.wind?.speed ?: ""} km/h"
        humidityTextView.text = "${weatherData?.query?.results?.channel?.atmosphere?.humidity ?: ""} %"

        val weatherCode = weatherData?.query?.results?.channel?.item?.condition?.code ?: "3200"
        weatherImageView.setImageResource(WeatherToImage.getImageForCode(weatherCode))
        weatherConditionTextView.text = weatherData?.query?.results?.channel?.item?.condition?.text ?: ""


        // 设置城市名字
        val city = weatherData?.query?.results?.channel?.location?.city ?: ""
        val country = weatherData?.query?.results?.channel?.location?.country ?: ""
        val region = weatherData?.query?.results?.channel?.location?.region ?: ""
        cityNameTextView.text = "${city.trim()}, ${region.trim()}, ${country.trim()}"

        // 设置天气 recyclerView
        val forecastRecyclerView = findViewById(R.id.forecast_recycler_view) as RecyclerView
        val forecastRecyclerAdapter = ForecastRecyclerAdapter(this, weatherData?.query?.results?.channel?.item?.forecast?.asList())
        forecastRecyclerAdapter.addActionListener { forecast ->
            val forecastDialog = ForecastDialogFragment.getInstance(forecast)
            forecastDialog.show(supportFragmentManager, TAG_FORECAST_DIALOG)
        }
        forecastRecyclerView.adapter = forecastRecyclerAdapter
        forecastRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }
}
