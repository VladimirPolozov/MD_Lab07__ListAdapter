package com.example.md_lab07__listadapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(private var contacts: List<Contact>) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textName)
        val phone: TextView = view.findViewById(R.id.textPhone)
        val type: TextView = view.findViewById(R.id.textType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rview_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = contacts[position].name
        holder.phone.text = contacts[position].phone
        holder.type.text = contacts[position].type
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateContacts(newContactsList: List<Contact>) {
        contacts = newContactsList
        notifyDataSetChanged()
    }
}