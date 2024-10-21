package com.sf.contactmanager.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.sf.contactmanager.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("""
        SELECT * FROM contact_db
        ORDER BY name
    """)
    fun getAllContact(): Flow<List<Contact>>

    @Insert
    suspend fun insertContact(contact: Contact)

    @Insert
    suspend fun insertListContact(contacts:List<Contact>)

    @Transaction
    suspend fun insertContacts(contacts: List<Contact>){
        insertListContact(contacts)
    }

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Query("""
        DELETE FROM contact_db WHERE id=:contactId
    """)
    suspend fun deleteContactById(contactId:Long)

    @Query("""
        DELETE FROM contact_db;
    """)
    suspend fun deleteAllUser()


}