package com.dk.mylivealonelife.utils

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseRefUtil {

    companion object{

        private val db = Firebase.database

        val category1 = db.getReference("contents")
        val category2 = db.getReference("contents2")
        val category3 = db.getReference("contents3")
        val category4 = db.getReference("contents4")
        val category5 = db.getReference("contents5")
        val category6 = db.getReference("contents6")
        val category7 = db.getReference("contents7")
        val category8 = db.getReference("contents8")
        val bookmarkRef = db.getReference("bookmark_list")

        val boardRef = db.getReference("board")

        val commentRef = db.getReference("comment")


    }
}