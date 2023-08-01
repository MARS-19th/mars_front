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
    private lateinit var email: String
    private lateinit var profile: String
    private lateinit var name: String
    private lateinit var animal: String
    private lateinit var face: String
    private lateinit var appearance: String
    private lateinit var category: String
    private var objective: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingDetailObjectiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(com.example.marsproject.R.drawable.icon_left_resize)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 액티비티 이동하면서 넘어온 값 받아오기
        email = intent.getStringExtra("email").toString()
        profile = intent.getStringExtra("profile").toString()
        name = intent.getStringExtra("name").toString()
        animal = intent.getStringExtra("animal").toString()
        face = intent.getStringExtra("face").toString()
        appearance = intent.getStringExtra("appearance").toString()
        category = intent.getStringExtra("category").toString()

        if(category == "공부") {
            val clkListener = View.OnClickListener { p0 ->
                when(p0?.id) {
                    com.example.marsproject.R.id.objectiveButton -> {
                        binding.objectiveButton.setBackgroundResource(com.example.marsproject.R.drawable.button_clicked)
                        objective = "프로그래밍"
                    }
                }
            }
            binding.objectiveButton.setOnClickListener(clkListener)
        } else {
            binding.objectiveButton.text = "등산"
            binding.objectiveButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, com.example.marsproject.R.drawable.climb_resize)
            val clkListener = View.OnClickListener { p0 ->
                when(p0?.id) {
                    com.example.marsproject.R.id.objectiveButton -> {
                        binding.objectiveButton.setBackgroundResource(com.example.marsproject.R.drawable.button_clicked)
                        objective = "등산"
                    }
                }
            }
            binding.objectiveButton.setOnClickListener(clkListener)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        binding.toolbar.inflateMenu(com.example.marsproject.R.menu.toolbar_menu2)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.home -> { // 뒤로 가기 버튼 눌렀을 때
                finish()
            }
            com.example.marsproject.R.id.action_ok -> { // 완료 버튼 눌렀을 때s
                if(objective == "") {
                    Toast.makeText(baseContext, "하나를 선택해주세요.", Toast.LENGTH_SHORT).show()
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
                            avatarjson.put("look", face) // 표정
                            avatarjson.put("color", appearance) // 색상

                            val jsonAvatar =
                                Request().reqpost("http://dmumars.kro.kr/api/setuseravatar", avatarjson)

                            // 프로필 json 생성
                            val profilejson = JSONObject()

                            profilejson.put("user_name", name)
                            if(profile == "null") {
                                profilejson.put("file", JSONObject.NULL)
                            } else {
                                profilejson.put("file", profile)
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