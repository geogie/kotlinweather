package com.georgeren.kotlin_weather.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.georgeren.kotlin_weather.R
import com.georgeren.kotlin_weather.mvp.models.Forecast
import com.georgeren.kotlin_weather.utils.WeatherToImage

/**
 * Created by georgeRen on 2017/11/30.
 * companion object：单例，静态内部类，类加载的时候初始化的
 */
class ForecastDialogFragment : DialogFragment() {
    private var mWeatherImageView: ImageView? = null
    private var mHighTemperatureTextView: TextView? = null
    private var mLowTemperatureTextView: TextView? = null
    private var mTextTemperatureTextView: TextView? = null
    private var mDayTextView: TextView? = null
    private var mCloseImageView: ImageView? = null

    companion object {
        private val ARGS_FORECAST = "args_forecast"

        fun getInstance(forecast: Forecast): ForecastDialogFragment {
            val bundle = Bundle()
            bundle.putSerializable(ARGS_FORECAST, forecast)

            val fragment = ForecastDialogFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        val view = inflater?.inflate(R.layout.dialog_forecast_info, container, false)

        mWeatherImageView = view?.findViewById(R.id.weather_image_view) as ImageView
        mHighTemperatureTextView = view?.findViewById(R.id.high_temperature_text_view) as TextView
        mLowTemperatureTextView = view?.findViewById(R.id.low_temperature_text_view) as TextView
        mTextTemperatureTextView = view?.findViewById(R.id.weather_condition_text_view) as TextView
        mDayTextView = view?.findViewById(R.id.day_text_view) as TextView
        mCloseImageView = view?.findViewById(R.id.close_image_view) as ImageView

        initViews()

        return view
    }

    override fun onResume() {
        super.onResume()
        dialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun initViews() {
        val forecast = arguments.getSerializable(ARGS_FORECAST) as Forecast
        mWeatherImageView?.setImageResource(WeatherToImage.getImageForCode(forecast.code))
        mHighTemperatureTextView?.text = forecast.high
        mLowTemperatureTextView?.text = forecast.low
        mTextTemperatureTextView?.text = forecast.text
        mDayTextView?.text = forecast.day

        mCloseImageView?.setOnClickListener { view ->
            dismiss()
        }
    }

}