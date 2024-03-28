package com.dk.mylivealonelife.contentList

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dk.mylivealonelife.R
import com.dk.mylivealonelife.utils.FirebaseAuthUtil
import com.dk.mylivealonelife.utils.FirebaseRefUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ContentListActivity : AppCompatActivity() {

    lateinit var myRef: DatabaseReference

    val bookmarkList = mutableListOf<String>()

    lateinit var rvAdapter: ContentRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_list)

        val items = ArrayList<ContentModel>()
        val itemKeyList = ArrayList<String>()

        rvAdapter = ContentRVAdapter(baseContext, items, itemKeyList, bookmarkList)

        val database = Firebase.database
        val category = intent.getStringExtra("category") // 받은 카테고리 val 값

        if (category == "category1") {
            myRef = database.getReference("contents")
        } else if (category == "category2") {
            myRef = database.getReference("contents2")
        }

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dm in dataSnapshot.children) {
                    Log.d("ContentListActivity", dm.toString())
//                    Log.d("ContentListActivity", dm.key.toString()) 북마크 클릭할시 키값이 뜨는지 확인
                    val item = dm.getValue(ContentModel::class.java)
                    items.add(item!!)
                    itemKeyList.add(dm.key.toString())
                }
                rvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        myRef.addValueEventListener(postListener)


        val rv: RecyclerView = findViewById(R.id.rv)

        rv.adapter = rvAdapter

        rv.layoutManager = GridLayoutManager(this, 2)

        getBookmarkData()

//        사진 클릭시 이동되는 코드
//        rvAdapter.itemClick = object : ContentRVAdapter.ItemClick{
//            override fun onClick(view: View, position: Int) {
////                Toast.makeText(baseContext, items[position].title, Toast.LENGTH_SHORT).show()
//
//                val intent = Intent(this@ContentListActivity, ContentShowActivity::class.java)
//                intent.putExtra("url", items[position].webUrl) // 클릭한 값이 넘어가게끔
//                startActivity(intent)
//            }
//        }

    }

    private fun getBookmarkData() {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                bookmarkList.clear()

                for (dm in dataSnapshot.children) {
                    bookmarkList.add(dm.key.toString())
//                    Log.d("getBookmarkData", dm.toString())
//                    Log.d("getBookmarkData", dm.key.toString())
                }
                rvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRefUtil.bookmarkRef.child(FirebaseAuthUtil.getUid()).addValueEventListener(postListener)

    }
}
