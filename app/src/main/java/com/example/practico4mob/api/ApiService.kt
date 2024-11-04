package com.example.practico4mob.api

import com.example.practico4mob.models.Contact
import com.example.practico4mob.models.Email
import com.example.practico4mob.models.Phone
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface ApiService {
    @GET("personas")
    suspend fun getContacts(): Response<List<Contact>>
    @POST("personas")
    suspend fun addContact(@Body contact: Contact): Response<Contact>
    @DELETE("personas/{id}")
    suspend fun deleteContact(@Path("id") contactId: Int): Response<Unit>
    @POST("phones")
    suspend fun addPhone(@Body phone: Phone): Response<Phone> // Cambiado a recibir un objeto Phone
    @POST("emails")
    suspend fun addEmail(@Body email: Email): Response<Email>

    @Multipart
    @POST("api/personas/{id}/profile-picture")
    fun uploadProfilePicture(
        @Path("id") personaId: Int,
        @Part image: MultipartBody.Part
    ): Call<Void>

}
