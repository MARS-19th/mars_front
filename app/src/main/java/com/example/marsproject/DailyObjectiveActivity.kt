package com.example.marsproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
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

        // 리사이클러뷰 객체 저장
        val itemView = binding.itemView

        // ObjectiveItem으로 ArrayList 생성
        val itemList = ArrayList<ObjectiveItem>()

        // 샘플 데이터 추후 삭제
        itemList.add(ObjectiveItem("no", "html 마스터 칭호 얻기"))
        itemList.add(ObjectiveItem("ok", "html 바보 칭호 얻기"))

        // ArrayList를 사용해 Adapter 생성
        val objectiveAdapter = ObjectiveAdapter(itemList)

        // 어댑터와 리사이클러뷰 갱신
        objectiveAdapter.notifyDataSetChanged()

        // 리사이클러뷰에 어댑터 붙여주기
        itemView.adapter = objectiveAdapter

        // 레이아웃 매니저 설정
        itemView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // 아이템 클릭 시 체크 변경
        objectiveAdapter.itemClickListener = object : ObjectiveAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val item = itemList[position]
                // 체크 유무 변경
                if(item.check == "ok") {
                    item.check = "no" // 로컬 값 변경 추후 삭제
                    // 목표 체크 변경 쓰레드 (추가 예정)
                } else {
                    item.check = "ok" // 로컬 값 변경 추후 삭제
                    // 목표 체크 변경 쓰레드 (추가 예정)
                }

                // 어댑터와 리사이클러뷰 갱신
                objectiveAdapter.notifyDataSetChanged()
            }
        }

        // 클릭 시 목표 등록창 다이얼로그 띄우기
        binding.addLayout.setOnClickListener{
            val dlg = ObjectiveDialogCustom(this) // 커스텀 다이얼로그 객체 저장
            // 예 버튼 클릭 시 실행
            dlg.setOnOKClickedListener{
                // 로컬 임시 추가 추후 삭제
                itemList.add(ObjectiveItem("no", it))

                // 목표 추가 쓰레드 실행
                Thread {
                    try {
                        // json 추가

                    } catch (e: UnknownServiceException) {
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()

                // 어댑터와 리사이클러뷰 갱신
                objectiveAdapter.notifyDataSetChanged()

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