package com.example.pagoflex.data.remote

import retrofit2.http.*

interface PagoApi {
    @POST("api/pagos")
    suspend fun registrar(@Body body: PagoRequest): PagoResponse

    @GET("api/pagos")
    suspend fun listar(@Query("usuarioId") usuarioId: Long): List<PagoResponse>

    @GET("api/pagos/deuda/{deudaId}")
    suspend fun porDeuda(@Path("deudaId") deudaId: Long): List<PagoResponse>

    @GET("api/pagos/{id}")
    suspend fun obtener(@Path("id") id: Long): PagoResponse
}