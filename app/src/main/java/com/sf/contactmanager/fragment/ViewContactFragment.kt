package com.sf.contactmanager.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.sf.contactmanager.AppViewModel
import com.sf.contactmanager.ProgressStatus
import com.sf.contactmanager.R
import com.sf.contactmanager.model.Contact

class ViewContactFragment : Fragment() {
    private val viewModel by activityViewModels<AppViewModel>()
    private lateinit var contact :Contact

    private lateinit var name:TextView
    private lateinit var phone:TextView
    private lateinit var email:TextView
    private lateinit var deleteButton: Button
    private lateinit var modifyButton: Button



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val args  by navArgs<ViewContactFragmentArgs>()
        contact = args.contact

        val view = inflater.inflate(R.layout.fragment_view_contact, container, false)
        name = view.findViewById(R.id.vName)
        phone = view.findViewById(R.id.vPhone)
        email = view.findViewById(R.id.vEmail)

        deleteButton = view.findViewById(R.id.deleteBtn)
        modifyButton = view.findViewById(R.id.modifyBtn)

        name.text = contact.name
        phone.text = contact.phone
        email.text = contact.email
        val callback :(ProgressStatus)->Unit = {
            var text = ""
            text = when (it){
                is ProgressStatus.Success -> "Success"
                else -> "Failed"
            }
            Toast.makeText(requireActivity(),text,Toast.LENGTH_SHORT).show()
            viewModel.setProgressBarVisible(false)
            Navigation.findNavController(view).popBackStack()
        }

        deleteButton.setOnClickListener {
            viewModel.setProgressBarVisible(true)
            viewModel.deleteContactById(contact.id,callback)
        }
        modifyButton.setOnClickListener {

        }


        return view
    }

}