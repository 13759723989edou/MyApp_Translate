package com.androidstudio_2024_vision.myapp

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
// 定义API接口
interface BaiduTranslateService {
    @FormUrlEncoded
    @POST("translate")
    fun translate(
        @Field("q") q: String,
        @Field("from") from: String,
        @Field("to") to: String,
        @Field("appid") appid: String,
        @Field("salt") salt: String,
        @Field("sign") sign: String   //表示它会把你传入的 appid（sign） 变量的值以 "appid（sign）" 作为参数名发送到服务器
    ): Call<TranslateResult>
}