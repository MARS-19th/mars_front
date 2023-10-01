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
    private lateinit var savedname: String // 저장된 닉네임
    private var count = 0; // 등록된 일일 목표 개수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyObjectiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savedname = getName() // 저장된 닉네임 가져오기

        // 툴바 설정
        setSupportActionBar(binding.toolbar) // 툴바 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize) // 뒤로가기 버튼 이미지 설정
        supportActionBar?.title = "일일 목표" // 타이틀 지정

        // 리사이클러뷰 객체 저장
        val itemView = binding.itemView

        // ObjectiveItem으로 ArrayList 생성
        val itemList = ArrayList<ObjectiveItem>()

        // 유저의 일일 목표 데이터 불러오는 쓰레드 생성
        val dailyThread = Thread {
            try {
                // 일일 목표 불러오기
                val jsonObject = Request().reqget("http://dmumars.kro.kr/api/getuserdatemark/${savedname}/day")

                // 일일 목표 개수 저장
                count = jsonObject.getJSONArray("results").length()

                // 일일 목표 불러와서 ArrayList에 추가
                for(i in 0 until jsonObject.getJSONArray("results").length()) {
                    var check = "no"
                    if(jsonObject.getJSONArray("results").getJSONObject(i).getBoolean("is_clear")) {
                        check = "ok"
                    }
                    itemList.add(ObjectiveItem(jsonObject.getJSONArray("results").getJSONObject(i).getInt("mark_id"), check, jsonObject.getJSONArray("results").getJSONObject(i).getString("mark_list")))
                }

            } catch (e: UnknownServiceException) {
                println(e.message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        dailyThread.start() // 쓰레드 시작
        dailyThread.join() // 쓰레드 종료될 때까지 대기

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
                    item.check = "no" // 로컬 값 변경
                    // 목표 체크 변경 쓰레드 (추가 예정)
                    Thread {
                        try {
                            // json 추가
                            val outputjson = JSONObject() // JSON 생성
                            outputjson.put("user_name", savedname) // 닉네임
                            outputjson.put("mark_id", item.id) // 목표 id
                            outputjson.put("mark_list", item.content) // 목표 내용
                            outputjson.put("is_clear", item.check != "no") // 클리어 여부

                            // 목표 추가하기
                            Request().reqpost("http://dmumars.kro.kr/api/upuserdatemark", outputjson)

                        } catch (e: UnknownServiceException) {
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.start()
                } else {
                    item.check = "ok" // 로컬 값 변경
                    // 목표 체크 변경 쓰레드 (추가 예정)
                    Thread {
                        try {
                            // json 추가
                            val outputjson = JSONObject() // JSON 생성
                            outputjson.put("user_name", savedname) // 닉네임
                            outputjson.put("mark_id", item.id) // 목표 id
                            outputjson.put("mark_list", item.content) // 목표 내용
                            outputjson.put("is_clear", item.check != "no") // 클리어 여부

                            // 목표 추가하기
                            Request().reqpost("http://dmumars.kro.kr/api/upuserdatemark", outputjson)

                        } catch (e: UnknownServiceException) {
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.start()
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
                var id = 0
                // 목표 추가 쓰레드 생성
                val addThread = Thread {
                    try {
                        // json 추가
                        val outputjson = JSONObject() // JSON 생성
                        outputjson.put("user_name", savedname) // 닉네임
                        outputjson.put("mark_list", it) // 목표 내용

                        // 목표 추가하기
                        val jsonObject = Request().reqpost("http://dmumars.kro.kr/api/setuserdatemark", outputjson)

                        // 추가한 목표의 id 받아오기
                        id = jsonObject.getInt("results")
                    } catch (e: UnknownServiceException) {
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                addThread.start() // 쓰레드 실행
                addThread.join() // 쓰레드 종료될 때까지 대기

                itemList.add(ObjectiveItem(id, "no", it))

                // 어댑터와 리사이클러뷰 갱신
                objectiveAdapter.notifyDataSetChanged()

                // 토스트 메시지 출력
                Toast.makeText(applicationContext, "등록된 목표는 24시간 이후에 삭제됩니다", Toast.LENGTH_SHORT).show()
            }
            dlg.show("목표 등록") // 다이얼로그 내용에 담을 텍스트
        }

    }

    // 닉네임 정보를 가져오는 함수
    fun getName(): String {
        val pref = getSharedPreferences("userName", 0)
        return pref.getString("name", "").toString()
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