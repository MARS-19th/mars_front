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
            com.example.marsproject.R.id.action_ok -> { // 완료 버튼 눌렀을 때
                if(objective == "") {
                    Toast.makeText(baseContext, "하나를 선택해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    // api를 통해서 db에 저장
                    Thread {
                        // 유저 정보 저장
                        try {
                            val outputjson = JSONObject() //json 생성
                            outputjson.put("user_name", name) // 닉네임
                            outputjson.put("user_id", email) // 아이디
                            outputjson.put("choice_mark", objective) // 목표
                            outputjson.put("profile_local", "null") // 프로필 사진

                            val jsonObject =
                                Request().reqpost("http://dmumars.kro.kr/api/setuser", outputjson)
                            // jsonObject 변수에는 정상응답 json 객체가 저장되어있음

                            println(jsonObject.getString("results")) //results 데이터가 ture만 나오는 경우 굳이 처리 해줄 필요 없은
                            // getter는 자료형 별로 getint getJSONArray 이런것들이 있으니 결과 값에 따라 메소드를 변경해서 쓸것
                        } catch (e: UnknownServiceException) {
                            // API 사용법에 나와있는 모든 오류응답은 여기서 처리

                            println(e.message)
                            // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        // 유저 재화 저장
                        try {
                            val outputjson = JSONObject() //json 생성
                            outputjson.put("user_name", name) // 닉네임
                            outputjson.put("value", 0) // 재화

                            val jsonObject =
                                Request().reqpost("http://dmumars.kro.kr/api/setmoney", outputjson)
                            // jsonObject 변수에는 정상응답 json 객체가 저장되어있음

                            println(jsonObject.getString("results")) //results 데이터가 ture만 나오는 경우 굳이 처리 해줄 필요 없은
                            // getter는 자료형 별로 getint getJSONArray 이런것들이 있으니 결과 값에 따라 메소드를 변경해서 쓸것
                        } catch (e: UnknownServiceException) {
                            // API 사용법에 나와있는 모든 오류응답은 여기서 처리

                            println(e.message)
                            // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        // 유저 목숨 저장
                        try {
                            val outputjson = JSONObject() //json 생성
                            outputjson.put("user_name", name) // 닉네임
                            outputjson.put("value", 3) // 목숨

                            val jsonObject =
                                Request().reqpost("http://dmumars.kro.kr/api/setlife", outputjson)
                            // jsonObject 변수에는 정상응답 json 객체가 저장되어있음

                            println(jsonObject.getString("results")) //results 데이터가 ture만 나오는 경우 굳이 처리 해줄 필요 없은
                            // getter는 자료형 별로 getint getJSONArray 이런것들이 있으니 결과 값에 따라 메소드를 변경해서 쓸것
                        } catch (e: UnknownServiceException) {
                            // API 사용법에 나와있는 모든 오류응답은 여기서 처리

                            println(e.message)
                            // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        // 유저 레벨 저장
                        try {
                            val outputjson = JSONObject() //json 생성
                            outputjson.put("user_name", name) // 닉네임
                            outputjson.put("value", 1) // 레벨

                            val jsonObject =
                                Request().reqpost("http://dmumars.kro.kr/api/setlevel", outputjson)
                            // jsonObject 변수에는 정상응답 json 객체가 저장되어있음

                            println(jsonObject.getString("results")) //results 데이터가 ture만 나오는 경우 굳이 처리 해줄 필요 없은
                            // getter는 자료형 별로 getint getJSONArray 이런것들이 있으니 결과 값에 따라 메소드를 변경해서 쓸것
                        } catch (e: UnknownServiceException) {
                            // API 사용법에 나와있는 모든 오류응답은 여기서 처리

                            println(e.message)
                            // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        // 유저 칭호 저장
                        try {
                            val outputjson = JSONObject() //json 생성
                            outputjson.put("user_name", name) // 닉네임
                            outputjson.put("value", "새싹") // 칭호

                            val jsonObject =
                                Request().reqpost("http://dmumars.kro.kr/api/setusertitle", outputjson)
                            // jsonObject 변수에는 정상응답 json 객체가 저장되어있음

                            println(jsonObject.getString("results")) //results 데이터가 ture만 나오는 경우 굳이 처리 해줄 필요 없은
                            // getter는 자료형 별로 getint getJSONArray 이런것들이 있으니 결과 값에 따라 메소드를 변경해서 쓸것
                        } catch (e: UnknownServiceException) {
                            // API 사용법에 나와있는 모든 오류응답은 여기서 처리

                            println(e.message)
                            // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        // 유저 아바타 저장
                        try {
                            val outputjson = JSONObject() //json 생성
                            outputjson.put("user_name", name) // 닉네임
                            outputjson.put("look", face) // 표정
                            outputjson.put("color", appearance) // 색상

                            val jsonObject =
                                Request().reqpost("http://dmumars.kro.kr/api/setuseravatar", outputjson)
                            // jsonObject 변수에는 정상응답 json 객체가 저장되어있음

                            println(jsonObject.getString("results")) //results 데이터가 ture만 나오는 경우 굳이 처리 해줄 필요 없은
                            // getter는 자료형 별로 getint getJSONArray 이런것들이 있으니 결과 값에 따라 메소드를 변경해서 쓸것
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