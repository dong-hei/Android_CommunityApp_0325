package com.dk.mylivealonelife.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.dk.mylivealonelife.R
import com.dk.mylivealonelife.comment.CommentLVAdapter
import com.dk.mylivealonelife.comment.CommentModel
import com.dk.mylivealonelife.databinding.ActivityBoardInsideBinding
import com.dk.mylivealonelife.utils.FirebaseAuthUtil
import com.dk.mylivealonelife.utils.FirebaseRefUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BoardInsideActivity : AppCompatActivity() {

    private val TAG = BoardInsideActivity::class.java.simpleName

    private lateinit var binding : ActivityBoardInsideBinding

    private lateinit var key:String

    private val commentDataList = mutableListOf<CommentModel>()

    private lateinit var commentAdapter : CommentLVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_inside)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_inside)

        binding.boardSettingIcon.setOnClickListener {
            showDialog()

        }

        commentAdapter = CommentLVAdapter(commentDataList)
        binding.commentLV.adapter = commentAdapter



        // talk 컨텐츠 상세 내용 확인 하는 방법 1
//        val title = intent.getStringExtra("title").toString()
//        val content = intent.getStringExtra("content").toString()
//        val time = intent.getStringExtra("time").toString()
//
//        binding.titleArea.text = title
//        binding.contentArea.text = content
//        binding.timeArea.text = time
//
//        Log.d(TAG, title)
//        Log.d(TAG, content)
//        Log.d(TAG, time)

        // 두번쨰 방법
        key = intent.getStringExtra("key").toString()
        getBoardData(key)
        getImgData(key)

        binding.commentBtn.setOnClickListener {
            insertComment(key)
        }
        getCommentData(key)
    }

    fun getCommentData(key : String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                commentDataList.clear()

                for (dm in dataSnapshot.children) {

                    Log.d(TAG, dm.toString())
                    val item = dm.getValue(CommentModel::class.java)
                    commentDataList.add(item!!)
                }
//                boardKeyList.reverse()
//                boardDataList.reverse() // 가장 최신 글을 앞에 오게끔
                commentAdapter.notifyDataSetChanged()
                Log.d(TAG, commentDataList.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {

                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRefUtil.commentRef.child(key).addValueEventListener(postListener)
    }

    /**
     * comment 계층
     * comment
     *  ㄴ Boardkey
     *    ㄴ CommentKey
     *      ㄴ CommentData
     *
     */
    fun insertComment(key : String){
        FirebaseRefUtil.commentRef
            .child(key)
            .push()
            .setValue(CommentModel(binding.commentArea.text.toString()
                      ,FirebaseAuthUtil.getTime()) )

        Toast.makeText(this, "댓글이 입력되었습니다.", Toast.LENGTH_SHORT ).show()
        binding.commentArea.setText("")

    }

    private fun showDialog(){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
                                                 .setView(mDialogView)
                                                 .setTitle("게시글 수정/삭제")
        val alertDialog = mBuilder.show()
        alertDialog.findViewById<Button>(R.id.editBtn)?.setOnClickListener{
            Toast.makeText(this, "수정 버튼을 눌렀습니다.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, BoardEditActivity::class.java)
            intent.putExtra("key", key)
            startActivity(intent)
        }

        alertDialog.findViewById<Button>(R.id.removeBtn)?.setOnClickListener{
            FirebaseRefUtil.boardRef.child(key).removeValue()
            Toast.makeText(this, "삭제가 완료 되었습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getImgData(key: String){
        val storageReference = Firebase.storage.reference.child(key + ".png")

        val imageViewFromFB = binding.getImgArea

        storageReference.downloadUrl.addOnCompleteListener({task ->
            if(task.isSuccessful){
                Glide.with(this)
                    .load(task.result)
                    .into(imageViewFromFB)
            } else{
                binding.getImgArea.isVisible = false
        } })
    }

    private fun getBoardData(key: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try {
                    val dm = dataSnapshot.getValue(BoardModel::class.java)
                    //                Log.d(TAG, dataSnapshot.toString())
//                                Log.d(TAG, dm!!.title)

                    binding.titleArea.text = dm!!.title
                    binding.contentArea.text = dm!!.content
                    binding.timeArea.text = dm!!.time

                    val myUid = FirebaseAuthUtil.getUid()
                    val writerUid = dm.uid

                    if (myUid.equals(writerUid)) {
                        Toast.makeText(baseContext, "내가 쓴 글입니다.", Toast.LENGTH_SHORT).show()
                        binding.boardSettingIcon.isVisible = true

                    } else {
                        Toast.makeText(baseContext, "내가 쓴 글이 아닙니다.", Toast.LENGTH_SHORT).show()
                    }

                } catch (e : Exception) {
                    Log.d(TAG, "삭제 완료")
                    finish() // 해당 액티비티가 닫히도록 하는것이다.
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRefUtil.boardRef.child(key).addValueEventListener(postListener)
    }
}