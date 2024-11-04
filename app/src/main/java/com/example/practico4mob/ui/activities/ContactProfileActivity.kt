package com.example.practico4mob.ui.activities

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.practico4mob.ui.viewmodels.ContactoViewModel
import com.example.practico4mob.R
import com.example.practico4mob.models.Contact

class ContactProfileActivity : AppCompatActivity() {
    private lateinit var viewModel: ContactoViewModel
    private lateinit var contact: Contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_profile)

        val contactId = intent.getIntExtra("CONTACT_ID", -1)
        viewModel = ViewModelProvider(this).get(ContactoViewModel::class.java)

        viewModel.contactos.observe(this) { contactos ->
            contact = contactos.find { it.id == contactId } ?: return@observe
            displayContactDetails(contact)
        }

        viewModel.fetchContacts()
    }

    private fun displayContactDetails(contact: Contact) {
        findViewById<TextView>(R.id.textViewName).text = "${contact.name} ${contact.last_name}"
        findViewById<TextView>(R.id.textViewCompany).text = contact.company
        findViewById<TextView>(R.id.textViewAddress).text = "${contact.address}, ${contact.city}, ${contact.state}"

        val imageViewProfile = findViewById<ImageView>(R.id.imageViewProfile)
        Glide.with(this)
            .load(contact.profile_picture)
            .into(imageViewProfile)

        val phoneContainer = findViewById<LinearLayout>(R.id.phoneContainer)
        val emailContainer = findViewById<LinearLayout>(R.id.emailContainer)

        contact.phones.forEach { phone ->
            val phoneView = TextView(this)
            phoneView.text = "${phone.label}: ${phone.number}"
            phoneContainer.addView(phoneView)
        }

        contact.emails.forEach { email ->
            val emailView = TextView(this)
            emailView.text = "${email.label}: ${email.email}"
            emailContainer.addView(emailView)
        }
    }
}
