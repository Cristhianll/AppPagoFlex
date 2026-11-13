package com.example.pagoflex.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Emulador → 10.0.2.2 | Celular → IP de tu PC (ej. 192.168.1.15)
    private const val BASE_URL = "http://127.0.0.1:8080/"

    @Volatile
    var token: String? = null

    private val authInterceptor = Interceptor { chain ->
        val builder = chain.request().newBuilder()
        token?.let { builder.header("Authorization", "Bearer $it") }
        chain.proceed(builder.build())
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val usuarioApi: UsuarioApi by lazy { retrofit.create(UsuarioApi::class.java) }
    val deudaApi: DeudaApi by lazy { retrofit.create(DeudaApi::class.java) }
    val cuotaApi: CuotaApi by lazy { retrofit.create(CuotaApi::class.java) }
    val pagoApi: PagoApi by lazy { retrofit.create(PagoApi::class.java) }
    val tarjetaApi: TarjetaApi by lazy { retrofit.create(TarjetaApi::class.java) }
    val notificacionApi: NotificacionApi by lazy { retrofit.create(NotificacionApi::class.java) }
}