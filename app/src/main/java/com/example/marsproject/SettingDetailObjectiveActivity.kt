package com.example.marsproject

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.example.marsproject.databinding.ActivitySettingDetailObjectiveBinding
import org.json.JSONObject
import java.net.UnknownServiceException

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
        supportActionBar?.setDisplayShowTitleEnabled(false) // 앱 타이틀 비활성화

        // 액티비티 이동하면서 넘어온 값 받아오기
        email = intent.getStringExtra("email").toString() // 이메일
        profile = intent.getStringExtra("profile").toString() // 프로필
        name = intent.getStringExtra("name").toString() // 닉네임
        animal = intent.getStringExtra("animal").toString() // 동물 종류
        face = intent.getStringExtra("face").toString() // 표정
        appearance = intent.getStringExtra("appearance").toString() // 외형
        category = intent.getStringExtra("category").toString() // 카테고리

        // 카테고리 값에 따라 버튼 텍스트, 이미지, 리스너 변경
        if(category == "공부") {
            // 버튼 클릭 시 백그라운드 변경해주는 리스너
            val clkListener = View.OnClickListener { p0 ->
                when(p0?.id) {
                    com.example.marsproject.R.id.objectiveButton1 -> {
                        binding.objectiveButton1.setBackgroundResource(com.example.marsproject.R.drawable.objective_button_clicked) // 버튼 배경 변경
                        objective = "프로그래밍" // 값 저장
                    }
                }
            }
            // 클릭 리스너 지정
            binding.objectiveButton1.setOnClickListener(clkListener)
        } else {
            binding.objectiveButton1.text = "등산" // 버튼 텍스트 변경
            binding.objectiveButton1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, com.example.marsproject.R.drawable.climb_resize) // 등산 이미지로 변경
            // 버튼 클릭 시 백그라운드 변경해주는 리스너
            val clkListener = View.OnClickListener { p0 ->
                when(p0?.id) {
                    com.example.marsproject.R.id.objectiveButton1 -> {
                        binding.objectiveButton1.setBackgroundResource(com.example.marsproject.R.drawable.objective_button_clicked) // 버튼 배경 변경
                        objective = "등산" // 값 저장
                    }
                }
            }
            // 클릭 리스너 지정
            binding.objectiveButton1.setOnClickListener(clkListener)
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
            com.example.marsproject.R.id.action_ok -> { // 다음 버튼 눌렀을 때
                // 상세 목표를 선택하지 않았을 때
                if(objective == "") {
                    Toast.makeText(baseContext, "하나를 선택해주세요.", Toast.LENGTH_SHORT).show() // 토스트 메시지 출력
                } else {
                    // api를 통해서 db에 저장
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

                            val jsonAvatar =
                                Request().reqpost("http://dmumars.kro.kr/api/setuseravatar", avatarjson)

                            // 프로필 json 생성
                            val profilejson = JSONObject()

                            profilejson.put("user_name", name) // 닉네임
                            // 프로필이 비어있을 때
                            if(profile == "null") {
                                profilejson.put("file", JSONObject.NULL) // NULL
                            } else {
                                profilejson.put("file", profile) // 프로필
                            }

                            Request().fileupload("http://korseok.kro.kr/api/uploadprofile", profilejson)
                        } catch (e: UnknownServiceException) {
                            // API 사용법에 나와있는 모든 오류응답은 여기서 처리

                            println(e.message)
                            // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.start()

                    // 완료 결과 보내기
                    val intentO = Intent()
                    setResult(RESULT_OK, intentO)
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}