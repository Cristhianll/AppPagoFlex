package com.example.pagoflex.data.remote

import retrofit2.http.*

interface TarjetaApi {
    @POST("api/tarjetas")
    suspend fun agregar(@Body body: TarjetaRequest): TarjetaResponse

    @GET("api/tarjetas")
    suspend fun listar(@Query("usuarioId") usuarioId: Long): List<TarjetaResponse>

    @PUT("api/tarjetas/{id}/principal")
    suspend fun marcarPrincipal(@Path("id") id: Long): TarjetaResponse

    @DELETE("api/tarjetas/{id}")
    suspend fun eliminar(@Path("id") id: Long)
}