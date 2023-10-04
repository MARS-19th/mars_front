package com.example.marsproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.example.marsproject.databinding.ActivityChangeTitleBinding

class ChangeTitleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeTitleBinding
    private lateinit var fViews: List<View> // "f1"부터 "f6"까지
    private lateinit var bViews: List<View> // "b1"부터 "b6"까지
    private var selectedView: View? = null // 선택된 뷰를 추적하는 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeTitleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize)
        supportActionBar?.title = "내 칭호"

        // f 초기화
        fViews = listOf(
            findViewById(R.id.f1),
            findViewById(R.id.f2),
            findViewById(R.id.f3),
            findViewById(R.id.f4),
            findViewById(R.id.f5),
            findViewById(R.id.f6)
        )

        // b 초기화
        bViews = listOf(
            findViewById(R.id.b1),
            findViewById(R.id.b2),
            findViewById(R.id.b3),
            findViewById(R.id.b4),
            findViewById(R.id.b5),
            findViewById(R.id.b6)
        )

        // f
        fViews.forEach { fView ->
            fView.setOnClickListener {
                toggleBackground(fView)
            }
        }

        // b
        bViews.forEach { bView ->
            bView.setOnClickListener {
                toggleBackground(bView)
            }
        }
    }

    private fun toggleBackground(view: View) {
        if (selectedView != null) {
            // 이전에 선택된 뷰가 있으면 배경을 복원
            selectedView?.setBackgroundResource(R.drawable.act_btn)
        }

        if (selectedView == view) {
            // 같은 뷰를 다시 클릭하면 선택 해제
            selectedView = null
        } else {
            // 다른 뷰를 클릭하면 배경 변경
            view.setBackgroundResource(R.drawable.act_btn_click)
            selectedView = view
        }
    }

    // 옵션 메뉴 클릭 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
