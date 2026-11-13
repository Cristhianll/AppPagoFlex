package com.example.pagoflex.data.remote

import retrofit2.http.*

interface DeudaApi {
    @POST("api/deudas")
    suspend fun crear(@Body body: DeudaRequest): DeudaResponse

    /** Filtros: estado, desde, hasta, montoMin, montoMax */
    @GET("api/deudas")
    suspend fun listar(
        @Query("usuarioId") usuarioId: Long,
        @Query("estado") estado: String? = null,
        @Query("desde") desde: String? = null,
        @Query("hasta") hasta: String? = null,
        @Query("montoMin") montoMin: Double? = null,
        @Query("montoMax") montoMax: Double? = null
    ): List<DeudaResponse>

    @GET("api/deudas/{id}")
    suspend fun obtener(@Path("id") id: Long): DeudaResponse

    @PUT("api/deudas/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body body: DeudaRequest): DeudaResponse

    @PUT("api/deudas/{id}/pagar")
    suspend fun marcarPagada(@Path("id") id: Long): DeudaResponse

    @DELETE("api/deudas/{id}")
    suspend fun eliminar(@Path("id") id: Long)
}