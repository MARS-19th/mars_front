package com.example.marsproject

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.example.marsproject.databinding.ActivityStoreBinding

class StoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar) // 툴바 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize) // 뒤로가기 버튼 이미지 설정
        supportActionBar?.title = "상점" // 타이틀 지정

        // 카테고리 클릭 리스너 설정
        categoryClickListener()
    }

    // 카테고리 클릭 리스너 설정
    private fun categoryClickListener(){
        // 클릭 시 해당 뷰 밑줄 색상 변경 리스너
        val clklistener = View.OnClickListener {
            when(it.id) {
                R.id.headImage -> {
                    binding.headView.setBackgroundColor(Color.parseColor("#FF9C46")) // 머리 밑줄 색상 변경
                    binding.bodyView.setBackgroundColor(Color.parseColor("#DDDDDD")) // 몸통 밑줄 색상 변경
                    binding.etcView.setBackgroundColor(Color.parseColor("#DDDDDD")) // 기타 밑줄 색상 변경
                }
                R.id.bodyImage -> {
                    binding.headView.setBackgroundColor(Color.parseColor("#DDDDDD")) // 머리 밑줄 색상 변경
                    binding.bodyView.setBackgroundColor(Color.parseColor("#FF9C46")) // 몸통 밑줄 색상 변경
                    binding.etcView.setBackgroundColor(Color.parseColor("#DDDDDD")) // 기타 밑줄 색상 변경
                }
                R.id.etcImage -> {
                    binding.headView.setBackgroundColor(Color.parseColor("#DDDDDD")) // 머리 밑줄 색상 변경
                    binding.bodyView.setBackgroundColor(Color.parseColor("#DDDDDD")) // 몸통 밑줄 색상 변경
                    binding.etcView.setBackgroundColor(Color.parseColor("#FF9C46")) // 기타 밑줄 색상 변경
                }
            }
        }

        // 클릭 리스너 지정
        binding.headImage.setOnClickListener(clklistener)
        binding.bodyImage.setOnClickListener(clklistener)
        binding.etcImage.setOnClickListener(clklistener)
    }

    // 옵션 메뉴 클릭 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            android.R.id.home -> { // 뒤로 가기 버튼 눌렀을 때
                finish() // 액티비티 종료
            }
        }
        return super.onOptionsItemSelected(item)
    }
}