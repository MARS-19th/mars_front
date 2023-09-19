package com.example.marsproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.marsproject.databinding.ActivityStoreBinding

class StoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize)
        supportActionBar?.setDisplayShowTitleEnabled(false)

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