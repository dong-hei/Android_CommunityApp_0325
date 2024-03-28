package com.dk.mylivealonelife.fragments

import BookmarkRVAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dk.mylivealonelife.R
import com.dk.mylivealonelife.contentList.ContentModel
import com.dk.mylivealonelife.databinding.FragmentBookmarkBinding
import com.dk.mylivealonelife.utils.FirebaseAuthUtil
import com.dk.mylivealonelife.utils.FirebaseRefUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BookmarkFragment : Fragment() {

    private lateinit var binding : FragmentBookmarkBinding

    private val TAG = BookmarkFragment::class.java.simpleName

    val bookmarkIdList = mutableListOf<String>()
    val items = ArrayList<ContentModel>()
    val itemKeyList = ArrayList<String>()

    lateinit var rvAdapter : BookmarkRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookmark, container, false)

        // 2. 사용자가 북마크한 정보를 다 가져옴!
        getBookmarkData()



        rvAdapter = BookmarkRVAdapter(requireContext(), items, itemKeyList, bookmarkIdList)

        val rv : RecyclerView = binding.bookmarkRV
        rv.adapter = rvAdapter

        rv.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.homeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_homeFragment)
        }

        binding.tipTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_tipFragment)
        }

        binding.talkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_talkFragment)
        }

        binding.storeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_storeFragment)
        }

        return binding.root
    }

    private fun getCategoryData(){
    val postListener = object : ValueEventListener {
    override fun onDataChange(dataSnapshot: DataSnapshot) {
        for (dm in dataSnapshot.children) {
            Log.d(TAG , dm.toString())
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
    FirebaseRefUtil.category1.addValueEventListener(postListener)
    FirebaseRefUtil.category2.addValueEventListener(postListener)

   }

    private fun getBookmarkData(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {

                    Log.e(TAG, dataModel.toString())
                    bookmarkIdList.add(dataModel.key.toString())

                }

                // 1. 전체 카테고리에 있는 컨텐츠 데이터들을 다 가져옴!
                getCategoryData()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRefUtil.bookmarkRef.child(FirebaseAuthUtil.getUid()).addValueEventListener(postListener)

    }



}