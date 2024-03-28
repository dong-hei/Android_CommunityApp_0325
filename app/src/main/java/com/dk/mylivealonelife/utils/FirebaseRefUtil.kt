package com.dk.mylivealonelife.utils

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseRefUtil {

    companion object{

        private val db = Firebase.database

        val category1 = db.getReference("content")
        val category2 = db.getReference("content2")
        val bookmarkRef = db.getReference("bookmark_list")


    }
}