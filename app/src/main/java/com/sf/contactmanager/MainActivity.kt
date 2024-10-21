package com.sf.contactmanager

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sf.contactmanager.model.Contact
import com.sf.contactmanager.model.ContactNonID
import com.sf.contactmanager.model.toContact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var viewModel: AppViewModel
    private lateinit var nav:NavController
    private lateinit var itemAddContact:MenuItem
    private lateinit var itemPickJson:MenuItem
    private lateinit var progressBar: ProgressBar
    private lateinit var pickJson: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        progressBar = findViewById(R.id.home_progress_bar)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        viewModel = ViewModelProvider(this,AppViewModel.Factory).get(AppViewModel::class.java)

        pickJson = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                readJson(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        nav = Navigation.findNavController(this@MainActivity,R.id.fragmentContainerView)
        lifecycleScope.launch {
            viewModel.contacts.collectLatest { contacts->
                withContext(Dispatchers.Main){
                    nav.currentDestination?.let {destination->
                        if (contacts.isEmpty() && destination.id == R.id.home_Fragment){
                            nav.navigate(R.id.blankFragment)
                        }
                        if (contacts.isNotEmpty() && destination.id == R.id.blankFragment)
                            nav.navigate(R.id.home_Fragment)
                        toolbar.subtitle = viewModel.contacts.value.size.let {
                            it.toString() + if (it==0) " contact" else " contacts"
                        }
                    }
                }

            }

        }

        lifecycleScope.launch {
            viewModel.progressBarVisible.collectLatest {
                progressBar.visibility = if (it) View.VISIBLE else View.INVISIBLE
            }
        }
        lifecycleScope.launch {
            viewModel.contacts.collectLatest{
                viewModel.invalidatePaging()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar,menu)
        menu?.let {
            itemAddContact = menu.findItem(R.id.addContact)
            itemPickJson = menu.findItem(R.id.pick_json)
        }
        nav.addOnDestinationChangedListener{navigation,destination,_ ->
            if (destination.id == R.id.add2) itemAddContact.setVisible(false)
            else if (!itemAddContact.isVisible) itemAddContact.setVisible(true)

            if (destination.id == R.id.blankFragment && viewModel.contacts.value.isNotEmpty())
                navigation.navigate(R.id.home_Fragment)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.exit-> finishAndRemoveTask()
            R.id.addContact -> nav.navigate(R.id.add2)
            R.id.deleteAllContact -> viewModel.deleteAllContact()
            R.id.pick_json -> pickJson.launch("application/json")

        }

        return super.onOptionsItemSelected(item)
    }

    private fun readJson(uri: Uri){
        val contentResolver = applicationContext.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        var jsonString :String?=null
        inputStream?.use {
            jsonString = it.bufferedReader().use {reader->
                reader.readText()
            }
        }
        jsonString?.let { insertContactFromJson(it) }
    }

    private fun insertContactFromJson(jsonString:String){
        val gson = Gson()
        val userListType = object: TypeToken<List<ContactNonID>>(){}.type
        val resultContacts:List<ContactNonID> = gson.fromJson(jsonString,userListType)
        val contacts:List<ContactNonID> = resultContacts.distinct()
        val submitContacts = mutableListOf<Contact>()

        var progressStatus:ProgressStatus?=null
        val successCount =AtomicInteger(0)
        val failedCount = AtomicInteger(0)
        val callback:(ProgressStatus,Int)->Unit = { status, count->
            progressStatus = status
            if (status is ProgressStatus.Success) successCount.addAndGet(count)
            if (status is ProgressStatus.Failed) failedCount.addAndGet(count)
        }

        viewModel.setProgressBarVisible(true)
        nav.currentDestination?.let {
            if (it.id != R.id.home_Fragment) nav.navigate(R.id.home_Fragment)
        }
        viewModel.setHomeViewVisible(false)
        viewModel.setActionUpdateContacts(false)
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                contacts.forEach { contactNonID->
                    val contact = contactNonID.toContact()
                    if (!viewModel.checkContactExist(contact)){
                        submitContacts.add(contact)
                    }
                }
                viewModel.insertContacts(submitContacts,callback)

                withContext(Dispatchers.Main){
                    viewModel.setHomeViewVisible(true)
                    viewModel.setActionUpdateContacts(true)
                    viewModel.setProgressBarVisible(false)
                    when (progressStatus){
                        null-> Toast.makeText(this@MainActivity,"Nothing to add", Toast.LENGTH_SHORT).show()
                        else ->{
                            var text = ""
                            val failed = failedCount.get()
                            val success = successCount.get()
                            if (failed!=0 && success!=0){
                                text = "Success $success added, Failed $failed"
                            }else if (failedCount.get()==0){
                                text = "Success $success added"
                            }else {
                                text = "Failed"
                                if (progressStatus is ProgressStatus.Failed){
                                    text =
                                        "Failed ${(progressStatus as ProgressStatus.Failed).e.message}"
                                }
                            }

                            Toast.makeText(this@MainActivity,
                                text, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        }



    }

}
