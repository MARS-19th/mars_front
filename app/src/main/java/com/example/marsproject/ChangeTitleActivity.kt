package com.example.marsproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.example.marsproject.databinding.ActivityChangeTitleBinding
import android.widget.ImageView
import android.widget.TextView
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownServiceException

class ChangeTitleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeTitleBinding
    private var selectedView: View? = null // 선택된 뷰를 추적하는 변수
    val name = "fdsfsdf" // 사용자 이름 가져오기
    private var text: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeTitleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize)
        supportActionBar?.title = "내 칭호"

        val f1 = findViewById<View>(R.id.f1)
        val tf1 = findViewById<TextView>(R.id.t_f1)

        val f2 = findViewById<View>(R.id.f2)
        val tf2 = findViewById<TextView>(R.id.t_f2)

        val f3 = findViewById<View>(R.id.f3)
        val tf3 = findViewById<TextView>(R.id.t_f3)

        val f4 = findViewById<View>(R.id.f4)
        val tf4 = findViewById<TextView>(R.id.t_f4)

        val f5 = findViewById<View>(R.id.f5)
        val tf5 = findViewById<TextView>(R.id.t_f5)

        val f6 = findViewById<View>(R.id.f6)
        val tf6 = findViewById<TextView>(R.id.t_f6)



        f1.setOnClickListener {
            // f1 클릭 시 tf_1의 텍스트를 로그에 출력
            text = tf1.text.toString()
            toggleBackground(f1)
            Log.d("YourActivity", "tf_1 텍스트: $text")
            sendSelectedTitleToServer(text)
        }

        f2.setOnClickListener {
            // f2 클릭 시 tf_2의 텍스트를 로그에 출력
            text = tf2.text.toString()
            toggleBackground(f2)
            Log.d("YourActivity", "tf_2 텍스트: $text")
            sendSelectedTitleToServer(text)
        }


        f3.setOnClickListener {
            // f3 클릭 시 tf_3의 텍스트를 로그에 출력
            text = tf3.text.toString()
            toggleBackground(f3)
            Log.d("YourActivity", "tf_3 텍스트: $text")
            sendSelectedTitleToServer(text)
        }


        f4.setOnClickListener {
            // f4 클릭 시 tf_4의 텍스트를 로그에 출력
            text = tf4.text.toString()
            toggleBackground(f4)
            Log.d("YourActivity", "tf_4 텍스트: $text")
            sendSelectedTitleToServer(text)
        }


        f5.setOnClickListener {
            // f5 클릭 시 tf_5의 텍스트를 로그에 출력
            text = tf5.text.toString()
            toggleBackground(f5)
            Log.d("YourActivity", "tf_5 텍스트: $text")
            sendSelectedTitleToServer(text)
        }


        f6.setOnClickListener {
            // f6 클릭 시 tf_6의 텍스트를 로그에 출력
            text = tf6.text.toString()
            toggleBackground(f6)
            Log.d("YourActivity", "tf_6 텍스트: $text")
            sendSelectedTitleToServer(text)
        }
    }

//아직 백엔드 부분은 안햇어여

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

    fun sendSelectedTitleToServer(value: String) {
        val titleThread = Thread {
            try {
                Log.d("d",value)
                val outputjson = JSONObject() // JSON 생성
                outputjson.put("user_name", name) // 사용자 이름
                outputjson.put("value", value)  // value 값

                // 서버로 데이터를 전송하고 응답을 받는 코드
                val jsonObject = Request().reqpost("http://dmumars.kro.kr/api/setusertitle", outputjson)

                // 서버 응답 처리
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
}
