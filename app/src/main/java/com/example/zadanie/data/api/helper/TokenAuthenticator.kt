package com.example.zadanie.data.api.helper

import android.content.Context
import com.example.zadanie.data.api.RestApi
import com.example.zadanie.data.api.UserRefreshRequest
import com.example.zadanie.helpers.PreferenceData
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Route


class TokenAuthenticator(val context: Context) : Authenticator {
    override fun authenticate(route: Route?, response: okhttp3.Response): Request? {
        synchronized(this) {
            if (response.request().header("mobv-auth")
                    ?.compareTo("accept") == 0 && response.code() == 401
            ) {
                val userItem = PreferenceData.getInstance().getUserItem(context)

                if (userItem == null) {
                    PreferenceData.getInstance().clearData(context)
                    return null
                }

                val tokenResponse = RestApi.create(context).userRefresh(
                    UserRefreshRequest(
                        userItem.refresh
                    )
                ).execute()

                if (tokenResponse.isSuccessful) {
                    tokenResponse.body()?.let {
                        PreferenceData.getInstance().putUserItem(context, it)
                        return response.request().newBuilder()
                            .header("authorization", "Bearer ${it.access}")
                            .build()
                    }
                }

                PreferenceData.getInstance().clearData(context)
                return null


            }
        }
        return null
    }
}