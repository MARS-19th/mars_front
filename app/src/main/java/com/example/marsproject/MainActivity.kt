package com.example.marsproject

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.example.marsproject.databinding.ActivityMainBinding
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.Constants
import com.kakao.sdk.user.UserApiClient
import org.json.JSONObject
import java.net.UnknownServiceException


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var launcher: ActivityResultLauncher<Intent>? = null
    private var skill: String = ""
    private var login: String = "ok"

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 로그인 정보 불러오기
        val pref = getSharedPreferences("userLogin", 0)
        val savedID = pref.getString("id", "").toString()
        val savedPW = pref.getString("pw", "").toString()

        val CheckThread = Thread {
            if (savedID == "" && savedPW == "") {
                // 로그인 페이지로 가도록
                login = "login"
            } else {
                try {
                    val outputjson = JSONObject() //json 생성
                    outputjson.put("id", savedID) // 아이디
                    outputjson.put("passwd", savedPW) // 비밀번호

                    val jsonObject =
                        Request().reqpost("http://dmumars.kro.kr/api/login", outputjson)
                    // jsonObject 변수에는 정상응답 json 객체가 저장되어있음

                    // getter는 자료형 별로 getint getJSONArray 이런것들이 있으니 결과 값에 따라 메소드를 변경해서 쓸것
                } catch (e: UnknownServiceException) {
                    // API 사용법에 나와있는 모든 오류응답은 여기서 처리

                    if (e.message == "is_new") {
                        // 설정 페이지로 가도록
                        login = "is_new"
                    }
                    println(e.message)
                    // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        CheckThread.start()
        CheckThread.join()

        if (login == "login") {
            val contract = ActivityResultContracts.StartActivityForResult()
            val callback = object : ActivityResultCallback<ActivityResult> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result?.resultCode == RESULT_OK) { // 로그인을 완료 했을 때
                        val LoginThread = Thread {
                            var id = ""
                            var pw = "qwer1234"
                            UserApiClient.instance.me { user, error ->
                                if (error != null) {
                                    Log.e(Constants.TAG, "사용자 정보 요청 실패 $error")
                                } else if (user != null) {
                                    Log.d(Constants.TAG, "사용자 정보 요청 성공 : $user")
                                    id = user.id.toString()
                                }
                            }
                            // 회원가입 & 로그인
                            try {
                                val outputjson = JSONObject() //json 생성
                                outputjson.put("id", id) // 아이디
                                outputjson.put("passwd", pw) // 비밀번호

                                val jsonObject =
                                    Request().reqpost("http://dmumars.kro.kr/api/setperson", outputjson)
                                // jsonObject 변수에는 정상응답 json 객체가 저장되어있음

                                // getter는 자료형 별로 getint getJSONArray 이런것들이 있으니 결과 값에 따라 메소드를 변경해서 쓸것
                            } catch (e: UnknownServiceException) {
                                // API 사용법에 나와있는 모든 오류응답은 여기서 처리

                                if (e.message == "ER_DUP_ENTRY") { /* 중복오류 발생 예외처리구문 */
                                }
                                println(e.message)
                                // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            // 로그인 정보 저장
                            // 추후에 로그인 페이지에 사용
                            saveLogin(id, pw)
                        }
                        LoginThread.start()
                        LoginThread.join()
                        changeFragment(MainHomeFragment())
                    }
                }
            }
            launcher = registerForActivityResult(contract, callback)
            // 로그인 액티비티 시작
            val intentL = Intent(this, LoginActivity::class.java)
            launcher?.launch(intentL)
        }
        if (login == "is_new") {
            val contract = ActivityResultContracts.StartActivityForResult()
            val callback = object : ActivityResultCallback<ActivityResult> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result?.resultCode == RESULT_OK) { // 닉네임, 아바타, 목표 설정이 끝났을 때
                        // 닉네임 정보 저장하기
                        val NameThread = Thread {
                            try {
                                val outputjson = JSONObject() //json 생성
                                outputjson.put("id", savedID) // 아이디
                                outputjson.put("passwd", savedPW) // 비밀번호

                                val jsonObject =
                                    Request().reqpost("http://dmumars.kro.kr/api/login", outputjson)
                                // jsonObject 변수에는 정상응답 json 객체가 저장되어있음

                                saveName(jsonObject.getString("user_name"))
                                // getter는 자료형 별로 getint getJSONArray 이런것들이 있으니 결과 값에 따라 메소드를 변경해서 쓸것
                            } catch (e: UnknownServiceException) {
                                // API 사용법에 나와있는 모든 오류응답은 여기서 처리

                                println(e.message)
                                // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        NameThread.start()
                        NameThread.join()
                        changeFragment(MainHomeFragment())
                    }
                }
            }
            launcher = registerForActivityResult(contract, callback)
            // 닉네임 설정 액티비티 시작
            val intentN = Intent(this, SettingNameActivity::class.java)
            intentN.putExtra("email", savedID)
            launcher?.launch(intentN)
        }

        val menuBottomNavigation = binding.menuBottomNavigation

        // 하단 탭 클릭 리스너 설정
        menuBottomNavigation.setOnItemSelectedListener { item ->
            changeFragment(
                when (item.itemId) {
                    R.id.menu_home -> { // 홈 탭 클릭
                        menuBottomNavigation.itemIconTintList =
                            ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        menuBottomNavigation.itemTextColor =
                            ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        MainHomeFragment()
                    }

                    R.id.menu_objective -> { // 목표 탭 클릭
                        menuBottomNavigation.itemIconTintList =
                            ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        menuBottomNavigation.itemTextColor =
                            ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        MainObjectiveFragment()
                    }

                    else -> { // 마이 탭 클릭
                        menuBottomNavigation.itemIconTintList =
                            ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        menuBottomNavigation.itemTextColor =
                            ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        MainMypageFragment()
                    }
                }
            )
            true
        }

        // 초기 화면 홈으로 설정
        menuBottomNavigation.selectedItemId = R.id.menu_home
    }

    // 프래그먼트 변경 함수
    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.menu_frame_layout, fragment)
            .commit()
    }

    // 클릭 시 프래그먼트 변경 함수
    @SuppressLint("ResourceType")
    fun clickchangeFragment(index: Int) {
        when(index) {
            1 -> { // 홈 프래그먼트에서 사용
                supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
                val menuBottomNavigation = binding.menuBottomNavigation
                menuBottomNavigation.selectedItemId = R.id.menu_objective
                menuBottomNavigation.itemIconTintList = ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                menuBottomNavigation.itemTextColor = ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.menu_frame_layout, MainObjectiveFragment())
                    .commit()
            }
            2 -> { // 목표 프래그먼트에서 사용
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.menu_frame_layout, MainDetailStudyFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    // 로그인 정보를 저장하는 함수
    fun saveLogin(loginID: String, loginPW: String) {
        val pref = getSharedPreferences("userLogin", MODE_PRIVATE) //shared key 설정
        val edit = pref.edit() // 수정모드
        edit.putString("id", loginID) // 값 넣기
        edit.putString("pw", loginPW) // 값 넣기
        edit.apply() // 적용하기
    }

    // 닉네임 정보를 저장하는 함수
    fun saveName(userName: String) {
        val pref = getSharedPreferences("userName", MODE_PRIVATE) //shared key 설정
        val edit = pref.edit() // 수정모드
        edit.putString("name", userName) // 값 넣기
        edit.apply() // 적용하기
    }

    // 닉네임 정보를 가져오는 함수
    fun getName(): String {
        val pref = getSharedPreferences("userName", 0)
        return pref.getString("name", "").toString()
    }

    // 목표에서 선택한 이름을 저장하는 함수
    fun setSkill(name: String) {
        skill = name
    }

    // 상세 목표에서 선택한 이름을 가져오는 함수
    fun getSkill(): String {
        return skill
    }
}