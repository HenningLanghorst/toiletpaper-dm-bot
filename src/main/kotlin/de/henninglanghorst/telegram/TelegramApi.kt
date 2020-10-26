package de.henninglanghorst.telegram

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TelegramApi {

    @GET("getUpdates")
    fun getUpdates(@Query("offset") offset: Int? = null, @Query("limit") limit: Int? = null): Call<UpdateResponse>

    @POST("sendMessage")
    fun sendMessage(@Body sendMessageRequest: SendMessageRequest): Call<SendMessageResponse>

    companion object {
        private val log = LoggerFactory.getLogger(TelegramApi::class.java)
        val instance: TelegramApi by lazy {
            val token = System.getProperty("token")
            Retrofit.Builder()
                .client(OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val method = chain.request().method()
                        val sanitizedUrl = chain.request().url().toString().replace(token, "****")
                        log.info("$method $sanitizedUrl")
                        val response = chain.proceed(chain.request())
                        log.info("Response code: ${response.code()}")
                        response
                    }
                    .build())
                .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()))
                .baseUrl("https://api.telegram.org/bot$token/")
                .build()
                .create(TelegramApi::class.java)
        }
    }

}