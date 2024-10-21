package com.sf.contactmanager.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.sf.contactmanager.AppViewModel
import com.sf.contactmanager.ProgressStatus
import com.sf.contactmanager.R
import com.sf.contactmanager.model.Contact
import kotlinx.coroutines.launch


class AddContactFragment : Fragment() {
    private lateinit var phoneInput:TextView
    private lateinit var nameInput:TextView
    private lateinit var emailInput:TextView
    private lateinit var addButton : Button
    private val viewModel by activityViewModels<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        nameInput = view.findViewById(R.id.inputName)
        phoneInput = view.findViewById(R.id.inputPhone)
        emailInput = view.findViewById(R.id.inputEmail)
        addButton = view.findViewById(R.id.bAdd)

        addButton.setOnClickListener {
            val name = nameInput.text.toString()
            val phone = phoneInput.text.toString()
            val email = emailInput.text.toString()

            val callback:(ProgressStatus)->Unit = {
                lifecycleScope.launch {
                    when(it){
                        is ProgressStatus.Success -> {
                            Toast.makeText(requireActivity(),"Success",Toast.LENGTH_SHORT).show()
                        }
                        is ProgressStatus.Failed ->{
                            Toast.makeText(requireActivity(),"Failed e: ${it.e.message}",Toast.LENGTH_SHORT).show()
                            Log.e("error",it.e.message,it.e)
                        }
                    }
                }
            }

            if (name.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty()){
                val contact = createContact(name,phone,email)
                if (!viewModel.checkContactExist(contact)) {
                    viewModel.insertContact(contact,callback)
                } else Toast.makeText(
                    requireActivity(),
                    "Contact is exist",
                    Toast.LENGTH_SHORT).show()
            }

        }




        return view
    }

    private fun createContact(name:String,phone:String,email:String):Contact{
        return Contact(name =name,phone =phone,email =email)
    }




}