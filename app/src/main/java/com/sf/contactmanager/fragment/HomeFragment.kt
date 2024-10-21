package com.sf.contactmanager.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sf.contactmanager.AppViewModel
import com.sf.contactmanager.R
import com.sf.contactmanager.paging.ContactPagingDataAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactPagingDataAdapter
    private val viewModel by activityViewModels<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        val onItemClicked = ContactPagingDataAdapter.OnItemClicked{
            Navigation.findNavController(view)
                .navigate(HomeFragmentDirections.actionHomeFragmentToViewContactFragment(it))
        }
        adapter = ContactPagingDataAdapter(onItemClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            viewModel.pager.collectLatest { pager->
                pager.flow.collectLatest {
                    adapter.submitData(it)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.homeInvisible.collectLatest{
                view.visibility = if(it) View.VISIBLE else View.INVISIBLE
            }
        }

    }
}