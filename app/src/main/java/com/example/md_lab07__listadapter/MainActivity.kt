package com.example.md_lab07__listadapter

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Timber.plant(Timber.DebugTree())
        val client = OkHttpClient()
        val request = Request.Builder().url("https://drive.google.com/u/0/uc?id=1-KO-9GA3NzSgIc1dkAsNm8Dqw0fuPxcR&export=download").build()

        val rView: RecyclerView = findViewById(R.id.rView)
        rView.layoutManager = LinearLayoutManager(this)
        val adapter = ContactAdapter(emptyList())
        rView.adapter = adapter

        val searchButton: Button = findViewById(R.id.btn_search)
        val editTextField: EditText = findViewById(R.id.et_search)
        var contactsList = emptyList<Contact>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body
                val json = responseBody?.string()
                contactsList = Gson().fromJson(json, Array<Contact>::class.java).toList()

                withContext(Dispatchers.Main) {
                    adapter.updateContacts(contactsList)
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        searchButton.setOnClickListener {
            val searchText = editTextField.text.toString()
            val filteredContacts = contactsList.filter { contact ->
                contact.name.contains(searchText, true) || contact.phone.contains(searchText)
            }
            adapter.updateContacts(filteredContacts)
        }
    }
}