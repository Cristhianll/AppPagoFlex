package com.example.pagoflex.data.remote

import okhttp3.MultipartBody
import retrofit2.http.*

interface UsuarioApi {
    @POST("api/usuarios/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("api/usuarios")
    suspend fun registrar(@Body body: RegistroRequest): UsuarioResponse

    @GET("api/usuarios")
    suspend fun listar(@Query("buscar") buscar: String? = null): List<UsuarioResponse>

    @GET("api/usuarios/{id}")
    suspend fun obtener(@Path("id") id: Long): UsuarioResponse

    @PUT("api/usuarios/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body body: ActualizarUsuarioRequest): UsuarioResponse

    @PUT("api/usuarios/{id}/contrasena")
    suspend fun cambiarContrasena(@Path("id") id: Long, @Body body: CambiarContrasenaRequest)

    @POST("api/usuarios/recuperar")
    suspend fun recuperar(@Body body: RecuperarRequest)

    @Multipart
    @PUT("api/usuarios/{id}/foto")
    suspend fun subirFoto(@Path("id") id: Long, @Part foto: MultipartBody.Part)

    @DELETE("api/usuarios/{id}")
    suspend fun eliminar(@Path("id") id: Long)
}