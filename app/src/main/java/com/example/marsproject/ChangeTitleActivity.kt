package com.example.marsproject

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.marsproject.R
import com.example.marsproject.Request
import com.example.marsproject.databinding.ActivityChangeTitleBinding
import org.json.JSONObject
import java.net.UnknownServiceException

class ChangeTitleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeTitleBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var selectedViewId: Int = 0
    private var text: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeTitleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("selected_view", Context.MODE_PRIVATE)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize)
        supportActionBar?.title = "내 칭호"

        // view 클릭 시 선택 기록하기 위한 변수
        var selectedTitle: String? = null

        // 프론트 뷰들
        val frontViews =
            arrayOf(binding.f1, binding.f2, binding.f3, binding.f4, binding.f5, binding.f6)
        val frontTextViews =
            arrayOf(binding.tF1, binding.tF2, binding.tF3, binding.tF4, binding.tF5, binding.tF6)

        // 백엔드 뷰들
        val backViews =
            arrayOf(binding.b1, binding.b2, binding.b3, binding.b4, binding.b5, binding.b6)
        val backTextViews =
            arrayOf(binding.tB1, binding.tB2, binding.tB3, binding.tB4, binding.tB5, binding.tB6)

        // view에 클릭 리스너 설정 함수
        fun setClickListener(views: Array<View>, textViews: Array<TextView>) {
            for (i in views.indices) {
                val view = views[i]
                val textView = textViews[i]

                view.setOnClickListener {
                    // 클릭한 뷰의 칭호를 선택하도록 기록
                    selectedTitle = textView.text.toString()

                    // 선택한 뷰의 시각적 표시 변경 (예: 배경 변경)
                    toggleBackground(view)
                }
            }
        }

        // 프론트 뷰들에 클릭 리스너 설정
        setClickListener(frontViews, frontTextViews)

        // 백엔드 뷰들에 클릭 리스너 설정
        setClickListener(backViews, backTextViews)

        // "btn_check" 버튼 클릭 시 서버로 선택한 칭호를 보내는 함수
        val btnCheck: Button = findViewById(R.id.btn_check) // "btn_check" 버튼의 ID를 설정
        btnCheck.setOnClickListener {
            if (selectedTitle != null) {
                // 선택한 칭호를 서버로 전송
                sendSelectedTitleToServer(selectedTitle!!)
                // 버튼 배경 & 텍스트 바꾸기
                btnCheck.setBackgroundResource(R.drawable.act_btn_check2)
                btnCheck.text = "선택 완료"
                btnCheck.setTextColor(Color.parseColor("#FF9C46"))
            } else {
                // 사용자에게 메시지 표시 또는 처리
            }
        }

        // 이전에 선택된 뷰의 ID를 복원
        selectedViewId = sharedPreferences.getInt("selected_view_id", 0)
        if (selectedViewId != 0) {
            val selectedView = findViewById<View>(selectedViewId)
            selectedView.setBackgroundResource(R.drawable.act_btn_click)
        }
        getSelectedTitleAndUpdateUI()
    }

    private fun toggleBackground(view: View) {
        if (selectedViewId != 0) {
            // 이전에 선택된 뷰가 있으면 배경을 복원
            val selectedView = findViewById<View>(selectedViewId)
            selectedView.setBackgroundResource(R.drawable.act_btn)
        }

        if (selectedViewId == view.id) {
            // 같은 뷰를 다시 클릭하면 선택 해제
            selectedViewId = 0
        } else {
            // 다른 뷰를 클릭하면 배경 변경
            view.setBackgroundResource(R.drawable.act_btn_click)
            selectedViewId = view.id
        }

        // 선택된 뷰의 ID를 SharedPreferences에 저장
        sharedPreferences.edit().putInt("selected_view_id", selectedViewId).apply()
    }

    fun getName(): String {
        val pref = getSharedPreferences("userName", 0)
        return pref.getString("name", "").toString()
    }

    //사용자가 가진 칭호 보내기
    fun sendSelectedTitleToServer(value: String) {
        val titleThread = Thread {
            try {
                val outputjson = JSONObject()
                outputjson.put("user_name", getName()) // 사용자 이름
                outputjson.put("value", value.replace("\n", " "))  // value 값

                val jsonObject =
                    Request().reqpost("http://dmumars.kro.kr/api/setusertitle", outputjson)

                val title = jsonObject.getString("title")
                println("사용자의 칭호: $title")
            } catch (e: UnknownServiceException) {
                println(e.message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        titleThread.start() // 쓰레드 시작
        titleThread.join() // 쓰레드 종료될 때까지 대기
    }

    // 사용자가 가지고 있는 칭호를 가져와서 UI 업데이트
    fun getSelectedTitleAndUpdateUI() {
        val titleThread = Thread {
            try {
                val jsonObject =
                    Request().reqget("http://dmumars.kro.kr/api/usergettitle/${getName()}")

                // 사용자 칭호 추출
                val resultsArray = jsonObject.getJSONArray("results")
                val userTitles = mutableListOf<String>()

                for (i in 0 until resultsArray.length()) {
                    val userTitle = resultsArray.getString(i)
                    userTitles.add(userTitle)
                }

                // UI 업데이트 함수 호출
                updateUserTitlesUI(userTitles)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        titleThread.start()
    }

    fun updateUserTitlesUI(userTitles: List<String>) {

        // 칭호와 그에 해당하는 view를 매핑
        val titleToViewMap = mapOf(
            //프
            "초보 프냥이" to binding.vf1,
            "HTML 마스터" to binding.vf2,
            "CSS 마법사" to binding.vf3,
            "자바스크립트 냥이" to binding.vf4,
            "프론트엔트 냥스터" to binding.vf5,
            "프론트엔트 마에스트냥" to binding.vf6,
            //백
            "초보 백엔드 냥이" to binding.vb1,
            "백엔드 탐험가 냥이" to binding.vb2,
            "자바스크립트 백냥이" to binding.vb3,
            "백엔드 엔지니어" to binding.vb4,
            "백엔드의 냥스터" to binding.vb5,
            "백엔드 마에스트냥" to binding.vb6,

        )

        //사용자 미보유 칭호
        for (view in titleToViewMap.values) {
            view.alpha = 0.2f // 불투명도를 0.2로 설정

        }
        
        // 사용자가 보유 칭호
        for (userTitle in userTitles) {
            val view = titleToViewMap[userTitle]
            view?.alpha = 1.0f
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}