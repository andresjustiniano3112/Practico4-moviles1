package com.example.practico4mob.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practico4mob.models.Contact
import com.example.practico4mob.models.Email
import com.example.practico4mob.models.Phone
import com.example.practico4mob.repositories.RetrofitInstance
import kotlinx.coroutines.launch

class ContactoViewModel : ViewModel() {
    private val _contactos = MutableLiveData<List<Contact>>().apply { value = listOf() }
    val contactos: LiveData<List<Contact>> get() = _contactos

    private val apiService = RetrofitInstance.api

    fun fetchContacts() {
        viewModelScope.launch {
            try {
                val response = apiService.getContacts()
                if (response.isSuccessful) {
                    response.body()?.let { contacts ->
                        _contactos.value = contacts
                        Log.d("ContactoViewModel", "Contactos obtenidos: $contacts")
                    } ?: Log.e("ContactoViewModel", "Error: la respuesta de contactos es nula")
                } else {
                    Log.e("ContactoViewModel", "Error en la obtención de contactos: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ContactoViewModel", "Error en la obtención de contactos", e)
            }
        }
    }

    fun deleteContact(contactId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteContact(contactId)
                if (response.isSuccessful) {
                    Log.d("ContactoViewModel", "Contacto eliminado exitosamente")
                    fetchContacts() // Refresca la lista de contactos
                } else {
                    Log.e("ContactoViewModel", "Error al eliminar contacto: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ContactoViewModel", "Excepción al eliminar contacto", e)
            }
        }
    }

    fun agregarContacto(contact: Contact): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        viewModelScope.launch {
            try {
                val response = apiService.addContact(contact)
                if (response.isSuccessful) {
                    response.body()?.let { newContact ->
                        val newContactId = newContact.id
                        Log.d("ContactoViewModel", "Nuevo ID del contacto: $newContactId")

                        // Agregar teléfonos y emails usando el ID del nuevo contacto
                        agregarTelefonosYEmails(newContactId, contact.phones, contact.emails)
                        result.value = true
                        fetchContacts() // Refresca la lista si es necesario
                    } ?: run {
                        Log.e("ContactoViewModel", "Error: la respuesta al crear contacto es nula")
                        result.value = false
                    }
                } else {
                    Log.e("ContactoViewModel", "Error al crear contacto: ${response.errorBody()?.string()}")
                    result.value = false
                }
            } catch (e: Exception) {
                Log.e("ContactoViewModel", "Fallo al crear contacto", e)
                result.value = false
            }
        }

        return result
    }

    private fun agregarTelefonosYEmails(contactId: Int, phones: List<Phone>, emails: List<Email>) {
        viewModelScope.launch {
            phones.forEach { phone ->
                // Intercambia number y label
                val phoneRequest = Phone(id = 0, number = phone.label, persona_id = contactId, label = phone.number) // label como number
                try {
                    val response = apiService.addPhone(phoneRequest) // Enviar el objeto completo
                    if (response.isSuccessful) {
                        Log.d("ContactoViewModel", "Teléfono añadido con éxito: ${phone.label}")
                    } else {
                        Log.e("ContactoViewModel", "Error al agregar teléfono: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("ContactoViewModel", "Fallo al agregar teléfono", e)
                }
            }

            emails.forEach { email ->
                // Intercambia email y label
                val emailRequest = Email(id = 0, email = email.label, persona_id = contactId, label = email.email) // label como email
                try {
                    val response = apiService.addEmail(emailRequest) // Enviar el objeto completo
                    if (response.isSuccessful) {
                        Log.d("ContactoViewModel", "Email añadido con éxito: ${email.label}")
                    } else {
                        Log.e("ContactoViewModel", "Error al agregar email: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("ContactoViewModel", "Fallo al agregar email", e)
                }
            }
        }
    }
}
