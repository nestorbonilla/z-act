package me.nestorbonilla.zact.service

import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.model.ApiPutResponseModel
import retrofit2.Call
import retrofit2.http.*

interface ZactService {

    @GET("acts")
    fun getActList(): Call<List<ActModel>>

    @GET("acts/{actId}")
    fun getAct(@Path("actId") actId: Int): Call<ActModel>

    @POST("acts")
    fun addAct(@Body newAct: ActModel): Call<ActModel>

    @PUT("acts/{actId}")
    fun updateAct(@Path("actId") actId: String, @Body act: ActModel): Call<ApiPutResponseModel>
}