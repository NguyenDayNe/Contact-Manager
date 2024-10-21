package com.sf.contactmanager.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sf.contactmanager.R
import com.sf.contactmanager.model.Contact

class MyAdapter(private val onItemClicked: OnItemClicked)
    : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private var contacts = mutableListOf<Contact>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateContacts(contacts: List<Contact>){
        this.contacts = contacts.toMutableList()
        notifyDataSetChanged()
    }

    class MyViewHolder(private val view: View): RecyclerView.ViewHolder(view){
        private val name:TextView = view.findViewById(R.id.name)
        private val phone:TextView = view.findViewById(R.id.phone)
        private val email:TextView = view.findViewById(R.id.email)

        fun bind(contact: Contact,onItemClicked: OnItemClicked){
            name.text = contact.name
            phone.text = contact.phone
            email.text = contact.email
            view.setOnClickListener {
                onItemClicked.onClick(contact)
            }
        }

    }
    fun interface OnItemClicked{
        fun onClick(contact:Contact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
           .inflate(R.layout.contact_item,parent,false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(contacts[position],onItemClicked)
    }
}