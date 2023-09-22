package com.example.marsproject

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.marsproject.databinding.ActivitySettingObjectiveBinding


class SettingObjectiveActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingObjectiveBinding
    var launcher: ActivityResultLauncher<Intent>? = null
    private lateinit var email: String // 이메일
    private lateinit var profile: String // 프로필
    private lateinit var name: String // 닉네임
    private lateinit var animal: String // 동물 종류
    private lateinit var face: String // 표정
    private lateinit var appearance: String // 외형
    private var category: String = "" // 카테고리 (공부, 운동)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingObjectiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar) // 툴바 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(com.example.marsproject.R.drawable.icon_left_resize) // 뒤로가기 버튼 이미지 설정
        supportActionBar?.setDisplayShowTitleEnabled(false) // 앱 타이틀 비활성화

        // 액티비티 이동하면서 넘어온 값 받아오기
        email = intent.getStringExtra("email").toString() // 이메일
        profile = intent.getStringExtra("profile").toString() // 프로필
        name = intent.getStringExtra("name").toString() // 닉네임
        animal = intent.getStringExtra("animal").toString() // 동물 종류
        face = intent.getStringExtra("face").toString() // 표정
        appearance = intent.getStringExtra("appearance").toString() // 외형

        val contract = ActivityResultContracts.StartActivityForResult()
        val callback = object: ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult?) {
                if(result?.resultCode == RESULT_OK) {
                    // 완료 결과 보내기
                    val intentA = Intent()
                    setResult(RESULT_OK, intentA)
                    finish()
                }
            }
        }
        launcher = registerForActivityResult(contract, callback)

        // 버튼 클릭 시 백그라운드 변경해주는 리스너
        val clkListener = View.OnClickListener { p0 ->
            when(p0?.id) {
                // 공부 버튼 클릭 시
                com.example.marsproject.R.id.studyButton -> {
                    binding.studyButton.setBackgroundResource(com.example.marsproject.R.drawable.button_clicked) // 공부 버튼 배경 변경
                    binding.exerciseButton.setBackgroundResource(com.example.marsproject.R.drawable.button_background) // 운동 버튼 배경 변경
                    category = "공부" // 값 저장
                }
                // 운동 버튼 클릭 시
                com.example.marsproject.R.id.exerciseButton -> {
                    binding.studyButton.setBackgroundResource(com.example.marsproject.R.drawable.button_background) // 공부 버튼 배경 변경
                    binding.exerciseButton.setBackgroundResource(com.example.marsproject.R.drawable.button_clicked) // 운동 버튼 배경 변경
                    category = "운동" // 값 저장
                }
            }
        }
        // 클릭 리스너 지정
        binding.studyButton.setOnClickListener(clkListener)
        binding.exerciseButton.setOnClickListener(clkListener)
    }

    // 툴바에 옵션 메뉴 생성
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(com.example.marsproject.R.menu.toolbar_menu1, menu) // 다음 버튼 생성
        return true
    }

    // 옵션 메뉴 클릭 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.home -> { // 뒤로 가기 버튼 눌렀을 때
                finish() // 액티비티 종료
            }
            com.example.marsproject.R.id.action_next -> { // 다음 버튼 눌렀을 때
                // 카테고리를 선택하지 않았을 때
                if(category == "") {
                    Toast.makeText(baseContext, "하나를 선택해주세요.", Toast.LENGTH_SHORT).show() // 토스트 메시지 출력
                } else {
                    // 인텐트 생성 후 액티비티 생성
                    val intentD = Intent(this, SettingDetailObjectiveActivity::class.java) // 상세 목표 설정 페이지로 설정
                    intentD.putExtra("email", email) // 이메일
                    intentD.putExtra("profile", profile) // 프로필
                    intentD.putExtra("name", name) // 닉네임
                    intentD.putExtra("animal", animal) // 동물 종류
                    intentD.putExtra("face", face) // 표정
                    intentD.putExtra("appearance", appearance) // 외형
                    intentD.putExtra("category", category) // 카테고리
                    launcher?.launch(intentD) // 액티비티 생성
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}