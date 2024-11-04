package com.example.practico4mob.ui.activities
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.practico4mob.R
import com.example.practico4mob.databinding.ActivityFormularioContactoBinding
import com.example.practico4mob.models.Contact
import com.example.practico4mob.models.Email
import com.example.practico4mob.models.Phone
import com.example.practico4mob.ui.viewmodels.ContactoViewModel

class FormularioContactoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormularioContactoBinding
    private val viewModel: ContactoViewModel by viewModels()
    private var imageUri: Uri? = null
    private val phoneFields = mutableListOf<Pair<EditText, Spinner>>()
    private val emailFields = mutableListOf<Pair<EditText, Spinner>>()
    private val REQUEST_IMAGE_PICK = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormularioContactoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinners()
        setupAddFieldsButtons()
        setupImagePicker()

        binding.btnGuardar.setOnClickListener {
            guardarContacto()
        }
    }

    private fun setupSpinners() {
        val etiquetasTelefono = listOf("Casa", "Trabajo", "Celular")
        val etiquetasEmail = listOf("Personal", "Trabajo", "Universidad")

        val adapterTelefono = ArrayAdapter(this, android.R.layout.simple_spinner_item, etiquetasTelefono)
        val adapterEmail = ArrayAdapter(this, android.R.layout.simple_spinner_item, etiquetasEmail)


        adapterTelefono.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterEmail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

    }

    private fun setupAddFieldsButtons() {
        binding.btnAddPhone.setOnClickListener { addPhoneField() }
        binding.btnAddEmail.setOnClickListener { addEmailField() }
    }

    private fun addPhoneField() {
        val phoneLayout = layoutInflater.inflate(R.layout.phone_email_item, null)
        val phoneEditText = phoneLayout.findViewById<EditText>(R.id.etItem)
        val phoneSpinner = phoneLayout.findViewById<Spinner>(R.id.spinnerItem)
        binding.phoneContainer.addView(phoneLayout)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Casa", "Trabajo", "Celular", "Personalizada"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        phoneSpinner.adapter = adapter

        phoneFields.add(phoneEditText to phoneSpinner)
    }

    private fun addEmailField() {
        val emailLayout = layoutInflater.inflate(R.layout.phone_email_item, null)
        val emailEditText = emailLayout.findViewById<EditText>(R.id.etItem)
        val emailSpinner = emailLayout.findViewById<Spinner>(R.id.spinnerItem)
        binding.emailContainer.addView(emailLayout)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Personal", "Trabajo", "Universidad", "Personalizada"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        emailSpinner.adapter = adapter

        emailFields.add(emailEditText to emailSpinner)
    }

    private fun setupImagePicker() {
        binding.profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            binding.profileImage.setImageURI(imageUri)
        }
    }

    private fun guardarContacto() {
        val nombre = binding.etNombre.text.toString()
        val apellido = binding.etApellido.text.toString()
        val compania = binding.etCompania.text.toString()
        val direccion = binding.etDireccion.text.toString()
        val ciudad = binding.etCiudad.text.toString()
        val estado = binding.etEstado.text.toString()

        val listaTelefonos = phoneFields.map {
            Phone(0, 0, it.second.selectedItem.toString(), it.first.text.toString())
        }
        val listaEmails = emailFields.map {
            Email(0, 0, it.second.selectedItem.toString(), it.first.text.toString())
        }

        val contact = Contact(
            id = 0,
            name = nombre,
            last_name = apellido,
            company = compania,
            address = direccion,
            city = ciudad,
            state = estado,
            profile_picture = imageUri.toString(),
            phones = listaTelefonos,
            emails = listaEmails
        )

        viewModel.agregarContacto(contact).observe(this) { success ->
            if (success) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}
