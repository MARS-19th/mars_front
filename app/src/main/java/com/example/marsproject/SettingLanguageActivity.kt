package com.example.marsproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.marsproject.databinding.ActivitySettingLanguageBinding
import org.json.JSONObject
import java.net.UnknownServiceException

class SettingLanguageActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingLanguageBinding
    var launcher: ActivityResultLauncher<Intent>? = null
    private lateinit var email: String // 이메일
    private lateinit var name: String // 닉네임
    private lateinit var animal: String // 동물 종류
    private lateinit var face: String // 표정
    private lateinit var appearance: String // 외형
    private lateinit var category: String // 카테고리
    private lateinit var objective: String // 상세 목표
    private var language: String = "" // 언어

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar) // 툴바 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize) // 뒤로가기 버튼 이미지 설정
        supportActionBar?.title = "언어 선택" // 타이틀 지정

        // 액티비티 이동하면서 넘어온 값 받아오기
        email = intent.getStringExtra("email").toString() // 이메일
        name = intent.getStringExtra("name").toString() // 닉네임
        animal = intent.getStringExtra("animal").toString() // 동물 종류
        face = intent.getStringExtra("face").toString() // 표정
        appearance = intent.getStringExtra("appearance").toString() // 외형
        category = intent.getStringExtra("category").toString() // 카테고리
        objective = intent.getStringExtra("objective").toString() // 상세 목표

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

        // 언어 버튼 클릭 리스너 설정
        buttonClick()

        binding.skipButton.setOnClickListener{
            // DB에 데이터 저장하는 쓰레드 실행
            Thread {
                try {
                    // 유저 정보 json 생성
                    val userjson = JSONObject()
                    userjson.put("user_name", name) // 닉네임
                    userjson.put("user_id", email) // 아이디
                    userjson.put("choice_mark", objective) // 목표
                    userjson.put("profile_local", JSONObject.NULL) // 프로필 사진

                    val jsonUser =
                        Request().reqpost("http://dmumars.kro.kr/api/setuser", userjson)

                    // 재화 json 생성
                    val moneyjson = JSONObject()
                    moneyjson.put("user_name", name) // 닉네임
                    moneyjson.put("value", 0) // 재화

                    val jsonMoney =
                        Request().reqpost("http://dmumars.kro.kr/api/setmoney", moneyjson)

                    // 목숨 json 생성
                    val lifejson = JSONObject()
                    lifejson.put("user_name", name) // 닉네임
                    lifejson.put("value", 3) // 목숨

                    val jsonLife =
                        Request().reqpost("http://dmumars.kro.kr/api/setlife", lifejson)

                    // 레벨 json 생성
                    val leveljson = JSONObject()
                    leveljson.put("user_name", name) // 닉네임
                    leveljson.put("value", 1) // 레벨

                    val jsonLevel =
                        Request().reqpost("http://dmumars.kro.kr/api/setlevel", leveljson)

                    // 칭호 json 생성
                    val titlejson = JSONObject()
                    titlejson.put("user_name", name) // 닉네임
                    titlejson.put("value", "새싹") // 칭호

                    val jsonTitle =
                        Request().reqpost("http://dmumars.kro.kr/api/setusertitle", titlejson)

                    // 아바타 json 생성
                    val avatarjson = JSONObject()
                    avatarjson.put("user_name", name) // 닉네임
                    avatarjson.put("type", animal) // 아바타 타입(cat, monkey)
                    avatarjson.put("look", face) // 표정
                    avatarjson.put("color", appearance) // 색상

                    val jsonAvatar = Request().reqpost("http://dmumars.kro.kr/api/setuseravatar", avatarjson)

                    // 임시로 일일 목표 json 생성
                    val dailyjson1 = JSONObject() // JSON 생성
                    dailyjson1.put("user_name", name) // 닉네임
                    dailyjson1.put("mark_list", "html 1일차 강의 듣기") // 목표 내용

                    // 목표 추가하기
                    val jsonDaily1 = Request().reqpost("http://dmumars.kro.kr/api/setuserdatemark", dailyjson1)

                    val dailyjson2 = JSONObject() // JSON 생성
                    dailyjson2.put("user_name", name) // 닉네임
                    dailyjson2.put("mark_list", "html 2일차 강의 듣기") // 목표 내용

                    // 목표 추가하기
                    val jsonDaily2 = Request().reqpost("http://dmumars.kro.kr/api/setuserdatemark", dailyjson2)
                } catch (e: UnknownServiceException) {
                    // API 사용법에 나와있는 모든 오류응답은 여기서 처리

                    println(e.message)
                    // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()

            // 완료 결과 보내기
            val intentD = Intent()
            setResult(RESULT_OK, intentD)
            finish()
        }
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

    // 언어 버튼 클릭 리스너 설정 함수
    private fun buttonClick() {
        // 클릭 시 해당 언어 문제 풀기로 이동
        val clklistener = View.OnClickListener {
            when(it.id) {
                R.id.htmlButton -> language = "html"
                R.id.pythonButton -> language = "python"
                R.id.cssButton -> language = "css"
                R.id.javaButton -> language = "java"
            }

            // 인텐트 생성 후 액티비티 생성
            val intentT = Intent(this, LanguageTestActivity::class.java) // 언어 테스트 페이지로 설정
            intentT.putExtra("email", email) // 이메일
            intentT.putExtra("name", name) // 닉네임
            intentT.putExtra("animal", animal) // 동물 종류
            intentT.putExtra("face", face) // 표정
            intentT.putExtra("appearance", appearance) // 외형
            intentT.putExtra("category", category) // 카테고리
            intentT.putExtra("objective", objective) // 상세 목표
            intentT.putExtra("language", language) // 언어
            launcher?.launch(intentT) // 액티비티 생성
        }
        binding.htmlButton.setOnClickListener(clklistener)
        binding.pythonButton.setOnClickListener(clklistener)
        binding.cssButton.setOnClickListener(clklistener)
        binding.javaButton.setOnClickListener(clklistener)
    }
}