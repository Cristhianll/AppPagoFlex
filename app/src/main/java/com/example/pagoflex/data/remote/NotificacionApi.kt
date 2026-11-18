package com.example.pagoflex.data.remote

import retrofit2.http.*

data class NotificacionResponse(
    val id: Long,
    val usuarioId: Long,
    val titulo: String,
    val mensaje: String,
    val tipo: String?,
    val leida: Boolean,
    val fecha: String?,
    val referenciaId: Long?
)

data class ContarResponse(val noLeidas: Long)

interface NotificacionApi {
    @GET("api/notificaciones")
    suspend fun listar(
        @Query("usuarioId") usuarioId: Long,
        @Query("soloNoLeidas") soloNoLeidas: Boolean? = null
    ): List<NotificacionResponse>

    @GET("api/notificaciones/contar")
    suspend fun contar(@Query("usuarioId") usuarioId: Long): ContarResponse

    @PUT("api/notificaciones/{id}/leer")
    suspend fun marcarLeida(@Path("id") id: Long): NotificacionResponse

    @PUT("api/notificaciones/leer-todas")
    suspend fun marcarTodas(@Query("usuarioId") usuarioId: Long)

    @POST("api/notificaciones/vencimiento")
    suspend fun notificarVencimiento(
        @Query("usuarioId") usuarioId: Long,
        @Query("referenciaId") referenciaId: Long,
        @Query("concepto") concepto: String,
        @Query("fechaVenc") fechaVenc: String
    ): NotificacionResponse
}