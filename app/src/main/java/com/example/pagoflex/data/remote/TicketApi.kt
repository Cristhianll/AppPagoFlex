package com.example.pagoflex.data.remote

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

data class TicketRequest(
    val titulo: String,
    val descripcion: String,
    val deudaId: Long? = null,
    val referencia: String? = null
)

data class TicketResponse(
    val id: Long,
    val usuarioId: Long,
    val usuarioCorreo: String? = null,
    val titulo: String,
    val descripcion: String,
    val deudaId: Long? = null,
    val referencia: String? = null,
    val estado: String,
    val resuelto: Boolean? = null,
    val fechaCreacion: String? = null,
    val fechaCierre: String? = null
)

data class MensajeRequest(val mensaje: String)

data class MensajeResponse(
    val id: Long,
    val ticketId: Long,
    val autorId: Long,
    val rolAutor: String,
    val mensaje: String,
    val fecha: String? = null
)

data class AdjuntoResponse(
    val id: Long,
    val ticketId: Long,
    val nombreOriginal: String? = null,
    val tipoContenido: String? = null,
    val tamano: Long = 0,
    val url: String? = null,
    val fecha: String? = null
)

data class CerrarTicketRequest(val resuelto: Boolean)

interface TicketApi {
    @POST("api/tickets")
    suspend fun crear(@Body body: TicketRequest): TicketResponse

    @GET("api/tickets")
    suspend fun listar(
        @Query("usuarioId") usuarioId: Long? = null,
        @Query("estado") estado: String? = null
    ): List<TicketResponse>

    @GET("api/tickets/{id}")
    suspend fun obtener(@Path("id") id: Long): TicketResponse

    @POST("api/tickets/{id}/mensajes")
    suspend fun enviarMensaje(@Path("id") id: Long, @Body body: MensajeRequest): MensajeResponse

    @GET("api/tickets/{id}/mensajes")
    suspend fun mensajes(@Path("id") id: Long): List<MensajeResponse>

    @Multipart
    @POST("api/tickets/{id}/adjuntos")
    suspend fun subirAdjunto(@Path("id") id: Long, @Part archivo: MultipartBody.Part): AdjuntoResponse

    @GET("api/tickets/{id}/adjuntos")
    suspend fun adjuntos(@Path("id") id: Long): List<AdjuntoResponse>

    @Streaming
    @GET("api/tickets/{id}/adjuntos/{adjuntoId}")
    suspend fun descargarAdjunto(
        @Path("id") id: Long,
        @Path("adjuntoId") adjuntoId: Long
    ): ResponseBody

    @PUT("api/tickets/{id}/cerrar")
    suspend fun cerrar(@Path("id") id: Long, @Body body: CerrarTicketRequest): TicketResponse
}
