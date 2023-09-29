package com.example.marsproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.example.marsproject.databinding.ActivityDailyObjectiveBinding
import org.json.JSONObject
import java.net.UnknownServiceException

class DailyObjectiveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDailyObjectiveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyObjectiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar) // 툴바 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize) // 뒤로가기 버튼 이미지 설정
        supportActionBar?.title = "일일 목표" // 타이틀 지정


        // 클릭 시 목표 등록창 다이얼로그 띄우기
        binding.addLayout.setOnClickListener{
            val dlg = ObjectiveDialogCustom(this) // 커스텀 다이얼로그 객체 저장
            // 예 버튼 클릭 시 실행
            dlg.setOnOKClickedListener{
                // 목표 추가 쓰레드 실행
                Thread {
                    try {
                        // json 추가

                    } catch (e: UnknownServiceException) {
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()

                // 추후에 화면 리프레쉬로 변경
                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
            }
            dlg.show("목표 등록") // 다이얼로그 내용에 담을 텍스트
        }

    }

    // 옵션 메뉴 클릭 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> { // 뒤로 가기 버튼 눌렀을 때
                finish() // 액티비티 종료
            }
        }
        return super.onOptionsItemSelected(item)
    }
}