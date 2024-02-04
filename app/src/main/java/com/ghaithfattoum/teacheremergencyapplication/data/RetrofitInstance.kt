package com.ghaithfattoum.teacheremergencyapplication.data

import com.ghaithfattoum.teacheremergencyapplication.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(EmergencyApi::class.java)
        }
    }
}
