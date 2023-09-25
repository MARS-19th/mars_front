package com.example.marsproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.marsproject.databinding.ActivityChangeTitleBinding

class ChangeTitleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeTitleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeTitleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar) // 툴바 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize) // 뒤로가기 버튼 이미지 설정
        supportActionBar?.title = "내 칭호" // 타이틀 지정

    }

    // 옵션 메뉴 클릭 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> { // 뒤로 가기 버튼 눌렀을 때
                finish() // 액티비티 종료
            }
        }
        return super.onOptionsItemSelected(item)
    }
}