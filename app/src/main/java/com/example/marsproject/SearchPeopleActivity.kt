package com.example.marsproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.example.marsproject.databinding.ActivitySearchPeopleBinding

class SearchPeopleActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchPeopleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchPeopleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 사용자 찾기 버튼 클릭 리스너
        binding.searchButton.setOnClickListener{
            // 추후 ar 기능으로 이동 작성
            Toast.makeText(applicationContext, "ar 기능 준비중", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            android.R.id.home -> { // 뒤로 가기 버튼 눌렀을 때
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}