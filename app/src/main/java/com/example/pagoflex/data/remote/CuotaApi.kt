package com.example.pagoflex.data.remote

import retrofit2.http.*

data class GenerarCuotasRequest(
    val deudaId: Long,
    val usuarioId: Long,
    val cantidadCuotas: Int,
    val primerVencimiento: String // "2026-12-01"
)

data class CuotaResponse(
    val id: Long,
    val deudaId: Long,
    val usuarioId: Long,
    val numero: Int,
    val monto: Double,
    val vencimiento: String,
    val estado: String,
    val fechaPago: String?
)

interface CuotaApi {
    @POST("api/deudas/cuotas/generar")
    suspend fun generar(@Body body: GenerarCuotasRequest): List<CuotaResponse>

    @GET("api/deudas/{deudaId}/cuotas")
    suspend fun porDeuda(@Path("deudaId") deudaId: Long): List<CuotaResponse>

    @GET("api/deudas/cuotas")
    suspend fun porUsuario(
        @Query("usuarioId") usuarioId: Long,
        @Query("estado") estado: String? = null
    ): List<CuotaResponse>

    @PUT("api/deudas/cuotas/{id}/pagar")
    suspend fun pagar(@Path("id") id: Long): CuotaResponse

    @GET("api/deudas/{deudaId}/cuotas/siguiente")
    suspend fun siguiente(@Path("deudaId") deudaId: Long): CuotaResponse
}