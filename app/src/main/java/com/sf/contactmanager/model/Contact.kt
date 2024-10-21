package com.sf.contactmanager.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize



@Parcelize
@Entity(tableName = "contact_db")
data class Contact (
    @PrimaryKey(autoGenerate = true) val id:Long = 0,
    val name:String,
    val phone:String,
    val email:String
):Parcelable
