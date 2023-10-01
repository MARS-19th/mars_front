package com.example.marsproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        supportActionBar?.title = "친구" // 타이틀 지정
    }
}