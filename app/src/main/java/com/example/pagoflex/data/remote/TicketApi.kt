package com.example.pagoflex.data.remote

import retrofit2.http.*

data class TicketRequest(
    val usuarioId: Long,
    val titulo: String,
    val descripcion: String
)

data class TicketResponse(
    val id: Long,
    val usuarioId: Long,
    val titulo: String,
    val descripcion: String,
    val estado: String,
    val resuelto: Boolean?,
    val fechaCreacion: String?,
    val fechaCierre: String?
)

data class MensajeRequest(
    val autorId: Long,
    val rolAutor: String,
    val mensaje: String
)

data class MensajeResponse(
    val id: Long,
    val ticketId: Long,
    val autorId: Long,
    val rolAutor: String,
    val mensaje: String,
    val fecha: String?
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

    @PUT("api/tickets/{id}/cerrar")
    suspend fun cerrar(@Path("id") id: Long, @Body body: CerrarTicketRequest): TicketResponse
}