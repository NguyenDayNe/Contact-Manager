package com.sf.contactmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sf.contactmanager.model.Contact
import com.sf.contactmanager.paging.ContactPagingSource
import com.sf.contactmanager.repository.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(private val contactRepository: ContactRepository):ViewModel() {
    private val _shouldUpdateContacts = MutableStateFlow(true)
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    init {
        viewModelScope.launch {
            contactRepository.getAllContact().collect{contacts->
                if (_shouldUpdateContacts.value) _contacts.value = contacts
            }

        }
    }
    val contacts = _contacts.asStateFlow()

    private val _homeInvisible = MutableStateFlow(true)
    val homeInvisible = _homeInvisible.asStateFlow()

    private val _pageFlow = MutableStateFlow(createPager())
    val pager = _pageFlow.asStateFlow()

    private val _progressBarVisible = MutableStateFlow(false)
    val progressBarVisible = _progressBarVisible.asStateFlow()

    fun setHomeViewVisible(visible:Boolean){
        _homeInvisible.value = visible
    }
    fun setProgressBarVisible(visible: Boolean){
        _progressBarVisible.value = visible
    }
    fun setActionUpdateContacts(shouldUpdate: Boolean){
        _shouldUpdateContacts.value = shouldUpdate
    }

    private fun createPager():Pager<Int,Contact>{
        return Pager(
            PagingConfig(
                20,
                20,
                true,
                20,
                20*999
            )
        ){ ContactPagingSource(_contacts.value)}
    }

    fun invalidatePaging(){
        _pageFlow.value = createPager()
    }

    fun insertContact(contact: Contact,callback:(ProgressStatus)->Unit = {}){
        viewModelScope.launch {
            try {
                contactRepository.insertContact(contact)
                callback(ProgressStatus.Success)
            }catch (e:Exception){
                callback(ProgressStatus.Failed(e))
            }
        }
    }

    suspend fun insertContacts(contacts: List<Contact>,callback:(ProgressStatus,Int)->Unit = { _: ProgressStatus, _: Int -> }){
        contacts.chunked(1000).forEach {
            try {
                contactRepository.insertContacts(it)
                callback(ProgressStatus.Success,it.size)
            }catch (e:Exception){
                callback(ProgressStatus.Failed(e),it.size)
            }
        }
    }

    fun deleteContact(contact: Contact,callback:(ProgressStatus)->Unit = {}){

        viewModelScope.launch {
            try {
                contactRepository.deleteContact(contact)
                callback(ProgressStatus.Success)
            }catch (e:Exception){
                callback(ProgressStatus.Failed(e))
            }
        }
    }

    fun deleteContactById(contactId: Long,callback:(ProgressStatus)->Unit = {}){
        viewModelScope.launch {
            try {
                contactRepository.deleteContactById(contactId)
                callback(ProgressStatus.Success)
            }catch (e:Exception){
                callback(ProgressStatus.Failed(e))
            }
        }
    }
    fun deleteAllContact(callback:(ProgressStatus)->Unit = {}){
        viewModelScope.launch {
            try {
                contactRepository.deleteAllContact()
                callback(ProgressStatus.Success)
            }catch (e:Exception){
                callback(ProgressStatus.Failed(e))
            }
        }
    }

    fun checkContactExist(contact: Contact):Boolean
        =_contacts.value.find {it.email== contact.email && it.phone == contact.phone }!=null

    companion object{
        val Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as ContactManagerApp
                AppViewModel(application.contactRepository)
            }
        }
    }
}
sealed class ProgressStatus{
    data object Success : ProgressStatus()
    data class Failed(val e:Exception):ProgressStatus()
}