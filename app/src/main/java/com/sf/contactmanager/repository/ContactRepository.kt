package com.sf.contactmanager.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sf.contactmanager.dao.ContactDao
import com.sf.contactmanager.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    fun getAllContact(): Flow<List<Contact>>
    suspend fun insertContact(contact: Contact)
    suspend fun deleteContact(contact: Contact)
    suspend fun deleteContactById(contactId:Long)
    suspend fun deleteAllContact()
    suspend fun insertContacts(contact:List<Contact>)
    class Default(private val contactDao: ContactDao):ContactRepository{
        override fun getAllContact(): Flow<List<Contact>> =
            contactDao.getAllContact()

        override suspend fun insertContact(contact: Contact) =
            contactDao.insertContact(contact)

        override suspend fun deleteContact(contact: Contact) =
            contactDao.deleteContact(contact)

        override suspend fun deleteContactById(contactId: Long) {
            contactDao.deleteContactById(contactId)
        }

        override suspend fun deleteAllContact() {
            contactDao.deleteAllUser()
        }

        override suspend fun insertContacts(contact: List<Contact>) {
            contactDao.insertContacts(contact)
        }
    }

}
