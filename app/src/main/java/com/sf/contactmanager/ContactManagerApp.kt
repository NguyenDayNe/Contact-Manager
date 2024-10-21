package com.sf.contactmanager

import android.app.Application
import com.sf.contactmanager.database.ContactDatabase
import com.sf.contactmanager.repository.ContactRepository

class ContactManagerApp:Application() {
    lateinit var contactRepository: ContactRepository
    override fun onCreate() {
        super.onCreate()
        contactRepository = ContactRepository.Default(
            ContactDatabase.getDatabaseInstance(this).contactDao())
    }
}