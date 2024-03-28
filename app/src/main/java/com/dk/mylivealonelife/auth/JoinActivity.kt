package com.dk.mylivealonelife.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.dk.mylivealonelife.MainActivity
import com.dk.mylivealonelife.R
import com.dk.mylivealonelife.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var binding : ActivityJoinBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = DataBindingUtil.setContentView(this, R.layout.activity_join)

        binding.joinBtn.setOnClickListener{

            var isGoToJoin = true

            val email = binding.emailArea.text.toString()
            val password1 = binding.passwordArea1.text.toString()
            val password2 = binding.passwordArea2.text.toString()

            // null 확인
            if (email.isEmpty()) {
            Toast.makeText(this, "회원가입 실패, 이메일을 입력해주세요", Toast.LENGTH_LONG).show()
                isGoToJoin = false

            }
            
            if (password1.isEmpty()) {
            Toast.makeText(this, "회원가입 실패, 비밀번호를 입력해주세요", Toast.LENGTH_LONG).show()
                isGoToJoin = false

            }
            
            if (password2.isEmpty()) {
            Toast.makeText(this, "회원가입 실패, 비밀번호 확인을 입력해주세요", Toast.LENGTH_LONG).show()
                isGoToJoin = false

            }

            if (!password1.equals(password2)) {
            Toast.makeText(this, "회원가입 실패, 비밀번호가 동일하지않습니다", Toast.LENGTH_LONG).show()
                isGoToJoin = false

            }

            if (password1.length < 6) {
            Toast.makeText(this, "회원가입 실패, 최소 비밀번호는 6자리 이상입니다.", Toast.LENGTH_LONG).show()
                isGoToJoin = false

            }

            if (isGoToJoin) {
                auth.createUserWithEmailAndPassword(email, password1)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    // 기존에 있는 activity가 사라짐 
                    startActivity(intent)
                } else {

                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_LONG).show()

                             }
                       }
               }

        }

    }
}