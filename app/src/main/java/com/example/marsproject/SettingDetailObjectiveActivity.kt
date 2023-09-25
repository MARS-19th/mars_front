package com.example.marsproject

import android.R
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.marsproject.databinding.ActivitySettingDetailObjectiveBinding

class SettingDetailObjectiveActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingDetailObjectiveBinding
    var launcher: ActivityResultLauncher<Intent>? = null
    private lateinit var email: String // 이메일
    private lateinit var profile: String // 프로필
    private lateinit var name: String // 닉네임
    private lateinit var animal: String // 동물 종류
    private lateinit var face: String // 표정
    private lateinit var appearance: String // 외형
    private lateinit var category: String // 카테고리
    private var objective: String = "" // 상세 목표 (프로그래밍)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingDetailObjectiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar) // 툴바 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(com.example.marsproject.R.drawable.icon_left_resize) // 뒤로가기 버튼 이미지 설정
        supportActionBar?.title = "목표 선택" // 타이틀 지정

        // 액티비티 이동하면서 넘어온 값 받아오기
        email = intent.getStringExtra("email").toString() // 이메일
        profile = intent.getStringExtra("profile").toString() // 프로필
        name = intent.getStringExtra("name").toString() // 닉네임
        animal = intent.getStringExtra("animal").toString() // 동물 종류
        face = intent.getStringExtra("face").toString() // 표정
        appearance = intent.getStringExtra("appearance").toString() // 외형
        category = intent.getStringExtra("category").toString() // 카테고리

        val contract = ActivityResultContracts.StartActivityForResult()
        val callback = object: ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult?) {
                if(result?.resultCode == RESULT_OK) {
                    // 완료 결과 보내기
                    val intentO = Intent()
                    setResult(RESULT_OK, intentO)
                    finish()
                }
            }
        }
        launcher = registerForActivityResult(contract, callback)

        // 상세 목표 클릭 리스너 설정
        detailObjectiveButtonClickListener()

    }

    // 상세 목표 클릭 리스너 설정
    private fun detailObjectiveButtonClickListener() {
        // 카테고리 값에 따라 텍스트, 이미지, 리스너 변경
        if(category == "공부") {
            // 클릭 시 백그라운드 변경해주는 리스너
            val clkListener = View.OnClickListener { p0 ->
                when(p0?.id) {
                    com.example.marsproject.R.id.objectiveView1,
                    com.example.marsproject.R.id.objectiveImage1,
                    com.example.marsproject.R.id.objectiveText1 -> {
                        binding.objectiveView1.setBackgroundResource(com.example.marsproject.R.drawable.objective_clicked) // 배경 변경
                        binding.objectiveText1.setTextColor(Color.parseColor("#FF8F2F")) // 텍스트 색상 변경
                        objective = "프로그래밍" // 값 저장
                    }
                }
            }
            // 클릭 리스너 지정
            binding.objectiveView1.setOnClickListener(clkListener)
            binding.objectiveImage1.setOnClickListener(clkListener)
            binding.objectiveText1.setOnClickListener(clkListener)
        } else {
            binding.objectiveText1.text = "등산" // 버튼 텍스트 변경
            binding.objectiveImage1.setImageResource(com.example.marsproject.R.drawable.climb) // 등산 이미지로 변경
            // 버튼 클릭 시 백그라운드 변경해주는 리스너
            val clkListener = View.OnClickListener { p0 ->
                when(p0?.id) {
                    com.example.marsproject.R.id.objectiveView1,
                    com.example.marsproject.R.id.objectiveImage1,
                    com.example.marsproject.R.id.objectiveText1 -> {
                        binding.objectiveView1.setBackgroundResource(com.example.marsproject.R.drawable.objective_clicked) // 버튼 배경 변경
                        binding.objectiveText1.setTextColor(Color.parseColor("#FF8F2F")) // 버튼 텍스트 색상 변경
                        objective = "등산" // 값 저장
                    }
                }
            }
            // 클릭 리스너 지정
            binding.objectiveView1.setOnClickListener(clkListener)
            binding.objectiveImage1.setOnClickListener(clkListener)
            binding.objectiveText1.setOnClickListener(clkListener)
        }
    }

    // 툴바에 옵션 메뉴 생성
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        binding.toolbar.inflateMenu(com.example.marsproject.R.menu.toolbar_menu1) // 다음 버튼 생성
        return true
    }

    // 옵션 메뉴 클릭 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.home -> { // 뒤로 가기 버튼 눌렀을 때
                finish() // 액티비티 종료
            }
            com.example.marsproject.R.id.action_next -> { // 다음 버튼 눌렀을 때
                // 상세 목표를 선택하지 않았을 때
                if(objective == "") {
                    Toast.makeText(baseContext, "하나를 선택해주세요.", Toast.LENGTH_SHORT).show() // 토스트 메시지 출력
                } else {
                    // 인텐트 생성 후 액티비티 생성
                    val intentL = Intent(this, SettingLanguageActivity::class.java) // 언어 설정 페이지로 설정
                    intentL.putExtra("email", email) // 이메일
                    intentL.putExtra("profile", profile) // 프로필
                    intentL.putExtra("name", name) // 닉네임
                    intentL.putExtra("animal", animal) // 동물 종류
                    intentL.putExtra("face", face) // 표정
                    intentL.putExtra("appearance", appearance) // 외형
                    intentL.putExtra("category", category) // 카테고리
                    intentL.putExtra("objective", objective) // 상세 목표
                    launcher?.launch(intentL) // 액티비티 생성
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}