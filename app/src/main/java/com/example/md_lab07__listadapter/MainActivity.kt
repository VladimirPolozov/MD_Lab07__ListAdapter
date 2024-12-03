package com.example.md_lab07__listadapter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        val adapter = ContactAdapter { contact ->
            val dial = Intent(Intent.ACTION_DIAL)
            dial.data = Uri.parse("tel:${contact.phone}")
            startActivity(dial)
        }
        rView.adapter = adapter

        val editTextField: EditText = findViewById(R.id.et_search)
        var contactsList = emptyList<Contact>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body
                val json = responseBody?.string()
                contactsList = Gson().fromJson(json, Array<Contact>::class.java).toList()

                withContext(Dispatchers.Main) {
                    adapter.submitList(contactsList)
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        editTextField.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.submitList(filter(contactsList, s.toString()))
            }
        })
    }

    private fun filter(list: List<Contact>, query: String): List<Contact> {
        return if (query.isEmpty()) {
            list
        } else {
            list.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.phone.contains(query) ||
                        it.type.contains(query, ignoreCase = true)
            }
        }
    }
}