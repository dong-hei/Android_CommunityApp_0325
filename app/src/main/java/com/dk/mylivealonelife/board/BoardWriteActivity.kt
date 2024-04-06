package com.dk.mylivealonelife.board

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.dk.mylivealonelife.R
import com.dk.mylivealonelife.contentList.BookmarkModel
import com.dk.mylivealonelife.databinding.ActivityBoardWriteBinding
import com.dk.mylivealonelife.utils.FirebaseAuthUtil
import com.dk.mylivealonelife.utils.FirebaseRefUtil
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class BoardWriteActivity : AppCompatActivity() {

    private val TAG = BoardWriteActivity::class.java.simpleName

    private lateinit var binding : ActivityBoardWriteBinding

    private var isImgUpload = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_write)

        binding.writeBtn.setOnClickListener{

            val title = binding.titleArea.text.toString()
            val content = binding.contentArea.text.toString()
            val uid = FirebaseAuthUtil.getUid()
            val time = FirebaseAuthUtil.getTime()
            Log.d(TAG, title)
            Log.d(TAG, content)

//            파이어베이스 store에 이미지를 저장하고 싶다
//            게시글을 클릭했을 때, 게시글에 대한 정보를 받아와야 한다. 이미지 이름을 key 값으로 해서 이미지를 찾기 쉽게 함
            val key = FirebaseRefUtil.boardRef.push().key.toString()

            // board
            // ㄴ key
            //   ㄴ boardModel(title, content, uid, time)

             FirebaseRefUtil.boardRef
                 .child(key)
                 .setValue(BoardModel(title, content, uid, time))

            Toast.makeText(this, "게시글 입력 완료", Toast.LENGTH_SHORT).show()

            if (isImgUpload == true) {
                imageUpload(key)
            }

            finish()
        }

        binding.imageArea.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)
            isImgUpload = true

        }
    }

    private fun imageUpload(key: String){

        val storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child(key + ".png")

        val imageView = binding.imageArea
        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == 100){
            binding.imageArea.setImageURI(data?.data)
        }
    }
}