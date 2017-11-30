package com.georgeren.kotlin_weather.mvp

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.georgeren.kotlin_weather.R
import com.georgeren.kotlin_weather.mvp.models.Forecast
import com.georgeren.kotlin_weather.utils.WeatherToImage

/**
 * Created by georgeRen on 2017/11/30.
 * xx!!：确定xx不是null的时候才能这样写，否则报错
 */
class ForecastRecyclerAdapter(val context: Context, val forecastList: List<Forecast>?) :
        RecyclerView.Adapter<ForecastRecyclerAdapter.ViewHolder>() {

    private var mListener: (forecast: Forecast) -> Unit = {}

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindData(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recycler_forecast, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = forecastList?.size ?: 0

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView?.setOnClickListener(this)
        }

        val dayTextView = itemView?.findViewById(R.id.day_text_view) as TextView
        val weatherImageView = itemView?.findViewById(R.id.weather_image_view) as ImageView
        val temperatureTextView = itemView?.findViewById(R.id.temperature_text_view) as TextView

        fun bindData(position: Int) {
            val forecast = forecastList?.get(position)

            dayTextView.text = forecast?.day
            val high = forecast?.high?.toInt() ?: 0
            val low = forecast?.low?.toInt() ?: 0
            val formattedTemperatureText = String.format(context.getString(R.string.celcius_temperature), ((high + low) / 2).toString())
            temperatureTextView.text = formattedTemperatureText

            weatherImageView.setImageResource(WeatherToImage.getImageForCode(forecast?.code ?: "3200"))
        }

        override fun onClick(v: View?) {
            mListener(forecastList?.get(adapterPosition)!!)
        }
    }

    fun addActionListener(listener: (forecast: Forecast) -> Unit){
        mListener = listener
    }
}