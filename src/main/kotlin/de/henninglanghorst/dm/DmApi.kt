package de.henninglanghorst.dm

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.slf4j.LoggerFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
interface DmApi {

    @GET("https://products.dm.de/store-availability/DE/availability")
    fun storeAvailabilty(
        @Query("dans") articleNumbers: String,
        @Query("storeNumbers") storeNumbers: String
    ): Call<StoreAvailablityResponse>

    @GET("https://store-data-service.services.dmtech.com/stores/bbox/{gpsRectangle}")
    fun lookupStores(@Path("gpsRectangle") gpsRectangle: String): Call<StoreLookupResponse>

    companion object {
        private val log = LoggerFactory.getLogger(DmApi::class.java)

        val instance: DmApi by lazy {
            Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()))
                .client(OkHttpClient.Builder().addInterceptor(loggingInterceptor).build())
                .baseUrl("https://products.dm.de/")
                .build()
                .create(DmApi::class.java)
        }

        private val loggingInterceptor
            get(): (Interceptor.Chain) -> Response = { chain ->
                log.info("${chain.request().method()} ${chain.request().url()}")
                val response = chain.proceed(chain.request())
                log.info("Response code: ${response.code()}")
                response
            }

    }
}