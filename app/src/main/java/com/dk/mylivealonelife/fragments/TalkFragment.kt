package com.dk.mylivealonelife.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.dk.mylivealonelife.R
import com.dk.mylivealonelife.board.BoardInsideActivity
import com.dk.mylivealonelife.board.BoardListVAdapter
import com.dk.mylivealonelife.board.BoardModel
import com.dk.mylivealonelife.board.BoardWriteActivity
import com.dk.mylivealonelife.contentList.ContentModel
import com.dk.mylivealonelife.databinding.FragmentHomeBinding
import com.dk.mylivealonelife.databinding.FragmentTalkBinding
import com.dk.mylivealonelife.utils.FirebaseRefUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class TalkFragment : Fragment() {

    private lateinit var binding : FragmentTalkBinding

    private val boardDataList = mutableListOf<BoardModel>()
    private val boardKeyList = mutableListOf<String>()
    private val TAG = TalkFragment::class.java.simpleName

    private lateinit var  boardRVAdapter : BoardListVAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_talk, container, false)

        binding.writeBtn.setOnClickListener {
            val intent = Intent(context, BoardWriteActivity::class.java)
            startActivity(intent)
        }

        boardRVAdapter = BoardListVAdapter(boardDataList)
        binding.boardListView.adapter = boardRVAdapter

        binding.boardListView.setOnItemClickListener{ parent, view , position, id ->

        // talk 컨텐츠 상세 내용 확인 하는 방법 1 : listview에 있는 데이터를 다 다른 액티비티로 전달해줘서 만들기
//            val intent = Intent(context, BoardInsideActivity::class.java)
//            intent.putExtra("title",boardDataList[position].title)
//            intent.putExtra("content",boardDataList[position].content)
//            intent.putExtra("time",boardDataList[position].time)
//            startActivity(intent)

        // talk 컨텐츠 상세 내용 확인 하는 방법 2 : Firebase에 있는 board에 대한 id 기반으로 다시 데이터를 받아오는 방법
            val intent = Intent(context, BoardInsideActivity::class.java)
            intent.putExtra("key", boardKeyList[position])
            startActivity(intent)

        }

        binding.tipTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_talkFragment_to_tipFragment2)
        }

        binding.storeTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_talkFragment_to_storeFragment)
        }

        binding.bookmarkTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_talkFragment_to_bookmarkFragment)
        }

        binding.homeTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_talkFragment_to_homeFragment)
        }

        getFirebaseBoardData()

        return binding.root
    }

    private fun getFirebaseBoardData(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                boardDataList.clear()

                for (dm in dataSnapshot.children) {

                    Log.d(TAG, dm.toString())
                    val item = dm.getValue(BoardModel::class.java)
                    boardDataList.add(item!!)
                    boardKeyList.add(dm.key.toString())
                }
                boardKeyList.reverse()
                boardDataList.reverse() // 가장 최신 글을 앞에 오게끔
                boardRVAdapter.notifyDataSetChanged()
                Log.d(TAG, boardDataList.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {

                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRefUtil.boardRef.addValueEventListener(postListener)
    }
}