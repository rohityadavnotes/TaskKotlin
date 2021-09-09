package com.task.data.remote

import com.google.gson.JsonObject
import com.task.model.BaseResponse
import com.task.model.Data
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET(RemoteConfiguration.EMPLOYEE)
    fun getEmployee(): Observable<Response<JsonObject>>

    @GET(RemoteConfiguration.EMPLOYEE_LIST)
    fun getEmployeeList(): Observable<Response<JsonObject>>

    @FormUrlEncoded
    @POST(RemoteConfiguration.GET_USERS)
    fun getPage(
        @Field("apiKey") key: String?,
        @Field("pageNumber") pageNumber: String?
    ): Observable<Response<JsonObject>>

    @Multipart
    @POST(RemoteConfiguration.SIGN_UP)
    fun signUp(
        @Part profilePic: MultipartBody.Part?,
        @Part("firstName") firstName: RequestBody?,
        @Part("lastName") lastName: RequestBody?,
        @Part("gender") gender: RequestBody?,
        @Part("countryCode") countryCode: RequestBody?,
        @Part("phoneNumber") phoneNumber: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("password") password: RequestBody?,
        @Part("fcmToken") fcmToken: RequestBody?
    ): Observable<Response<BaseResponse<Data>>>

    @FormUrlEncoded
    @POST(RemoteConfiguration.SIGN_IN)
    fun signIn(
        @Field("email") email: String?,
        @Field("password") password: String?,
        @Field("fcmToken") fcmToken: String?
    ): Observable<Response<BaseResponse<Data>>>

    @FormUrlEncoded
    @POST(RemoteConfiguration.SOCIAL_SIGN_IN)
    fun socialSignIn(
        @Field("provider") provider: String?,
        @Field("socialId") socialId: String?,
        @Field("picture") picture: String?,
        @Field("firstName") firstName: String?,
        @Field("lastName") lastName: String?,
        @Field("email") email: String?,
        @Field("fcmToken") fcmToken: String?
    ): Observable<Response<JsonObject>>
}