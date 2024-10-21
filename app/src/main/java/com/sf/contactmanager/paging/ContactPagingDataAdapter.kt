package com.sf.contactmanager.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sf.contactmanager.R
import com.sf.contactmanager.model.Contact

class ContactPagingDataAdapter (private val onItemClicked: OnItemClicked)
    :PagingDataAdapter<Contact,ContactPagingDataAdapter.MyViewHolder>(DIFF_UTIL) {

    companion object{
        val DIFF_UTIL = object :DiffUtil.ItemCallback<Contact>(){
            override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
    fun interface OnItemClicked{
        fun onClick(contact:Contact)
    }


    class MyViewHolder(private val view: View) : RecyclerView.ViewHolder(view){
        private val name: TextView = view.findViewById(R.id.name)
        private val phone: TextView = view.findViewById(R.id.phone)
        private val email: TextView = view.findViewById(R.id.email)

        fun bind(contact: Contact,onItemClicked: OnItemClicked){
            name.text = contact.name
            phone.text = contact.phone
            email.text = contact.email
            view.setOnClickListener {
                onItemClicked.onClick(contact)
            }
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it,onItemClicked) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =  LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_item,parent,false)
        return MyViewHolder(view)
    }
}