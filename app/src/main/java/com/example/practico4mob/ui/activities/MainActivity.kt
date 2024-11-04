package com.example.practico4mob.ui.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practico4mob.ui.viewmodels.ContactoViewModel
import com.example.practico4mob.databinding.ActivityMainBinding
import com.example.practico4mob.models.Contact
import com.example.practico4mob.ui.adapters.ContactAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ContactoViewModel
    private lateinit var adapter: ContactAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ContactoViewModel::class.java)
        adapter = ContactAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Configuración del clic en un contacto para mostrar el diálogo de confirmación
        adapter.setOnItemClickListener(object : ContactAdapter.OnItemClickListener {
            override fun onItemClick(contact: Contact) {
                mostrarDialogoEliminacion(this@MainActivity, contact)
            }
        })

        // Observador para actualizar la lista de contactos
        viewModel.contactos.observe(this) { contactos ->
            adapter.setContacts(contactos)
        }

        // Botón para agregar un nuevo contacto
        binding.btnNuevoContacto.setOnClickListener {
            val intent = Intent(this, FormularioContactoActivity::class.java)
            startActivity(intent)
        }

        viewModel.fetchContacts()
    }

    // Función para mostrar el diálogo de confirmación de eliminación
    private fun mostrarDialogoEliminacion(context: Context, contact: Contact) {
        AlertDialog.Builder(context)
            .setTitle("Eliminar Contacto")
            .setMessage("¿Estás seguro de que deseas eliminar este contacto?")
            .setPositiveButton("Eliminar") { _, _ ->
                // Llama al ViewModel para eliminar el contacto y muestra una confirmación
                viewModel.deleteContact(contact.id)
                Toast.makeText(context, "Contacto eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
