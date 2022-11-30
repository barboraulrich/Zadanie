package com.example.zadanie.data.api.helper

import android.content.Context
import com.example.zadanie.helpers.Config
import com.example.zadanie.helpers.PreferenceData
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(val context: Context) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        synchronized(this) {
            val request = chain.request()
                .newBuilder()
                .addHeader("User-Agent", "Mobv-Android/1.0.0")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")

            if (chain.request().header("mobv-auth")?.compareTo("accept") == 0) {
                request.addHeader(
                    "Authorization",
                    "Bearer ${PreferenceData.getInstance().getUserItem(context)?.access}"
                )

            }
            PreferenceData.getInstance().getUserItem(context)?.uid?.let {
                request.addHeader(
                    "x-user",
                    it
                )
            }
            request.addHeader("x-apikey", Config.API_KEY)

            val response = chain.proceed(request.build())
            return response
        }
    }

}