package com.example.HOMEIOT

import android.util.Log

import com.example.HOMEIOT.data.weather.CurrentWeather
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// http://api.openweathermap.org/data/2.5/weather
// ?q=Seoul&APPID=0333a09d025f1976cbb5177a5f6c9b9a&lang=kr

interface OpenWeatherApi{
    @GET("/data/2.5/weather?APPID=0333a09d025f1976cbb5177a5f6c9b9a")
    fun getWeather(
        @Query("q") city:String,
        @Query("lang") lang:String="kr",
        @Query("units") units:String="metric"
    ):Call<CurrentWeather>
}

object OpenWeather{
    private val retrofit=Retrofit.Builder()
        .baseUrl("http://api.openweathermap.org")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service= retrofit.create(OpenWeatherApi::class.java)
    fun getweather(
        city: String,
        lang: String="kr",
        units: String="metric",
        callback: (CurrentWeather)->Unit
    ) {
        service.getWeather(city, lang, units)
            .enqueue(object : Callback<CurrentWeather> {
                override fun onResponse(
                    call: Call<CurrentWeather>,
                    response: Response<CurrentWeather>
                ) {
                    if(response.isSuccessful){
                        val image=response.body()
                        callback(image!!)
                    }
                }

                override fun onFailure(call: Call<CurrentWeather>, t: Throwable) {
                    Log.e("---",t.toString())
                }
            })
    }
}