package de.henninglanghorst.telegram

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.henninglanghorst.ConfigProperties.token
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.slf4j.LoggerFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TelegramApi {

    @POST("sendMessage")
    fun sendMessage(@Body sendMessageRequest: SendMessageRequest): Call<SendMessageResponse>

    @GET("setWebhook")
    fun setWebhook(
            @Query("url") url: String,
            @Query("max_connections") maConnections: Int? = null
    ): Call<WebhookResponse>

    @GET("deleteWebhook")
    fun deleteWebhook(): Call<WebhookResponse>

    companion object {
        private val log = LoggerFactory.getLogger(TelegramApi::class.java)
        val instance: TelegramApi by lazy {
            Retrofit.Builder()
                    .client(
                            OkHttpClient.Builder()
                                    .addInterceptor(loggingInterceptor(token))
                                    .build()
                    )
                    .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()))
                    .baseUrl("https://api.telegram.org/bot$token/")
                    .build()
                    .create(TelegramApi::class.java)
        }

        private fun loggingInterceptor(token: String): (Interceptor.Chain) -> Response = { chain ->
            val method = chain.request().method()
            val sanitizedUrl = chain.request().url().toString().replace(token, "****")
            log.debug("$method $sanitizedUrl")
            val response = chain.proceed(chain.request())
            log.debug("Response code: ${response.code()}")
            response
        }
    }

}