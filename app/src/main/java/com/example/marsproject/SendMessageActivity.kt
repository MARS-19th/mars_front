package com.example.marsproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.example.marsproject.databinding.ActivitySendMessageBinding

class SendMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySendMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar) // 툴바 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize) // 뒤로가기 버튼 이미지 설정
        supportActionBar?.title = "친구프로필" // 타이틀 지정

        // 이전 액티비티에서 전달된 데이터 추출
        val nickname = intent.getStringExtra("nickname")
        val title = intent.getStringExtra("title")
        val profileImageUrl = intent.getStringExtra("profileImageUrl")

        // 추출한 데이터를 사용하여 화면에 표시하거나 원하는 작업 수행
        binding.friendName.text = nickname
        binding.friendtitle.text = title

        // Glide 또는 이미지 로딩 라이브러리를 사용하여 프로필 이미지 표시
        Glide.with(this)
            .load(profileImageUrl)
            .into(binding.friendimage)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> { // 뒤로 가기 버튼 눌렀을 때
                finish() // 액티비티 종료
            }
        }
        return super.onOptionsItemSelected(item)
    }
}