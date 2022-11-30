package com.example.zadanie.data.api

import android.content.Context
import com.example.zadanie.data.api.helper.AuthInterceptor
import com.example.zadanie.data.api.helper.TokenAuthenticator
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface RestApi {

    @GET("https://overpass-api.de/api/interpreter?")
    suspend fun barDetail(@Query("data") data: String): Response<BarDetailResponse>

    @GET("https://overpass-api.de/api/interpreter?")
    suspend fun barNearby(@Query("data") data: String): Response<BarDetailResponse>

    @POST("user/create.php")
    suspend fun userCreate(@Body user: UserCreateRequest): Response<UserResponse>

    @POST("user/login.php")
    suspend fun userLogin(@Body user: UserLoginRequest): Response<UserResponse>

//    @POST("user/refresh.php")
//    suspend fun userRefresh(@Body user: UserRefreshRequest) : Response<UserResponse>

    @POST("user/refresh.php")
    fun userRefresh(@Body user: UserRefreshRequest) : Call<UserResponse>

    @GET("bar/list.php")
    @Headers("mobv-auth: accept")
    suspend fun barList() : Response<List<BarListResponse>>

    @POST("bar/message.php")
    @Headers("mobv-auth: accept")
    suspend fun barMessage(@Body bar: BarMessageRequest) : Response<Any>

    @POST("/contact/message.php")
    @Headers("mobv-auth: accept")
    suspend fun addFriend(@Body friend: AddFriendRequest) : Response<ResponseBody>


    companion object{
        const val BASE_URL = "https://zadanie.mpage.sk/"

        fun create(context: Context): RestApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .authenticator(TokenAuthenticator(context))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(RestApi::class.java)
        }
    }

    @GET("contact/list.php")
    @Headers("mobv-auth: accept")
    suspend fun friendsList() : Response<List<FriendsListResponse>>


}