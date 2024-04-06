package com.dk.mylivealonelife.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.dk.mylivealonelife.R
import com.dk.mylivealonelife.databinding.ActivityBoardEditBinding
import com.dk.mylivealonelife.fragments.TalkFragment
import com.dk.mylivealonelife.utils.FirebaseAuthUtil
import com.dk.mylivealonelife.utils.FirebaseRefUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BoardEditActivity : AppCompatActivity() {

    private lateinit var key:String

    private val TAG = BoardEditActivity::class.java.simpleName

    private lateinit var binding: ActivityBoardEditBinding

    private lateinit var writerUid : String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_edit)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_edit)

        key = intent.getStringExtra("key").toString()
        getBoardData(key)
        getImgData(key)

        binding.editBtn.setOnClickListener{

            editBoardData(key)

            val intent = Intent(this, TalkFragment::class.java)
            startActivity(intent)
        }
    }

    private fun editBoardData(key: String) {

        FirebaseRefUtil.boardRef
            .child(key)
            .setValue(
                BoardModel(binding.titleArea.text.toString(),
                                                    binding.contentArea.text.toString(),
                                                    writerUid,
                                                    FirebaseAuthUtil.getTime())
            )

        Toast.makeText(this, "수정에 성공하였습니다.", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun getImgData(key: String){
        val storageReference = Firebase.storage.reference.child(key + ".png")

        val imageViewFromFB = binding.imageArea

        storageReference.downloadUrl.addOnCompleteListener({task ->
            if(task.isSuccessful){
                Glide.with(this)
                    .load(task.result)
                    .into(imageViewFromFB)
            } else{


            } })
    }

    private fun getBoardData(key: String) {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try {
                    val dm = dataSnapshot.getValue(BoardModel::class.java)

                    binding.titleArea.setText(dm!!.title)
                    binding.contentArea.setText(dm!!.content)
                    writerUid = dm.uid

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