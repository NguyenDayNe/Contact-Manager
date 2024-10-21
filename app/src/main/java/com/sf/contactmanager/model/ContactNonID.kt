package com.sf.contactmanager.model

data class ContactNonID(
    val name:String,
    val email:String,
    val phone:String
){
    constructor() :this("","","")
}

fun ContactNonID.toContact() = Contact(name = name,phone = phone, email= email)