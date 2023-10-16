package com.example.marsproject

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.marsproject.databinding.ActivityChangeTitleBinding
import org.json.JSONObject
import java.net.UnknownServiceException

class ChangeTitleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeTitleBinding
    //private lateinit var sharedPreferences: SharedPreferences
    //private var selectedViewId: Int = 0 // 이전에 선택된 칭호를 나타내기 위한 변수
    private var selectedTitle: String = ""
    private var DBtitle: String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeTitleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize)
        supportActionBar?.title = "내 칭호"

        //우선 다 반투명으로 하기
        ViewOpacity(1,"")

        //DB에서 사용자의 칭호 가지고 오기
        runOnUiThread { DBTitle() }

        //디비에서 사용자가 가지고 있는 칭호가져오기 -> 가지고 있는 칭호 불투명으롭 변경
        getSelectedTitleAndUpdateUI()

    }

    //사용자 칭호를 디비에서 가지고 오기
    fun DBTitle(){
        val titleToViewMap = mapOf(
            //프
            "초보 프냥이" to binding.vf1,
            "초보 프백냥이" to binding.vf2,
            "자바스크립트 프냥이" to binding.vf3,
            "프론트엔드 마법사냥" to binding.vf4,
            "프론트엔드 냥스터" to binding.vf5,
            "프론트엔드 마에스트냥" to binding.vf6,
            //백
            "초보 백냥이" to binding.vb1,
            "초보 백프냥이" to binding.vb2,
            "자바스크립트 백냥이" to binding.vb3,
            "백엔드 냥지니어" to binding.vb4,
            "백엔드 냥스터" to binding.vb5,
            "백엔드 마에스트냥" to binding.vb6
        )
        Thread {
            try {
                val jsonObject = Request().reqget("http://dmumars.kro.kr/api/getuserdata/${getName()}") //get요청\
                DBtitle= jsonObject.getString("user_title") // 칭호
                if(DBtitle != "새싹") {
                    runOnUiThread {
                        ChangeDBTitle(titleToViewMap[DBtitle] as ConstraintLayout)
                    }
                }
            } catch (e: UnknownServiceException) {
                // API 사용법에 나와있는 모든 오류응답은 여기서 처리
                println(e.message)
                // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
    private fun ChangeDBTitle(view: ConstraintLayout) {
        val AllViews = arrayOf(binding.f1, binding.f2, binding.f3, binding.f4, binding.f5, binding.f6,
            binding.b1, binding.b2, binding.b3, binding.b4, binding.b5, binding.b6)

        for(a in AllViews){
            if(a.parent==view){
                if (a.background.constantState == ContextCompat.getDrawable(this, R.drawable.act_btn)?.constantState) {
                    a.setBackgroundResource(R.drawable.act_btn_click)
                    break
                }

            }
        }
    }
    fun ViewOpacity(num:Int, userTitle: String){
        val titleToViewMap = mapOf(
            //프
            "초보 프냥이" to binding.vf1,
            "초보 프백냥이" to binding.vf2,
            "자바스크립트 프냥이" to binding.vf3,
            "프론트엔드 마법사냥" to binding.vf4,
            "프론트엔드 냥스터" to binding.vf5,
            "프론트엔드 마에스트냥" to binding.vf6,
            //백
            "초보 백냥이" to binding.vb1,
            "초보 백프냥이" to binding.vb2,
            "자바스크립트 백냥이" to binding.vb3,
            "백엔드 냥지니어" to binding.vb4,
            "백엔드 냥스터" to binding.vb5,
            "백엔드 마에스트냥" to binding.vb6
        )
        if(num==1){
            for (view in titleToViewMap.values) {
                view.alpha = 0.2f // 반투명
            }
        }
        else if(num==2){
            //선택한 칭호는 불투명으로
            runOnUiThread {
                titleToViewMap[userTitle]?.alpha = 1f //사용자가 가지고 있는 칭호창은 불투며으로 변환
            }
        }

    }

    // 사용자가 가지고 있는 칭호들를 가져와서 UI 업데이트
    fun getSelectedTitleAndUpdateUI() {
        val titleThread = Thread {
            try {
                val jsonObject = Request().reqget("http://dmumars.kro.kr/api/usergettitle/${getName()}")
                // 사용자 칭호 추출
                val resultsArray = jsonObject.getJSONArray("results")
                val userTitles = mutableListOf<String>()

                for (i in 0 until resultsArray.length()) {
                    val userTitle = resultsArray.getString(i)
                    userTitles.add(userTitle)

                }
                // 가지고 있는 칭호 불투명하게 하기
                runOnUiThread { updateUserTitlesUI(userTitles) }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        titleThread.start()
    }

    // view에 클릭 리스너 설정 함수
    fun setClickListener(selectedView: ConstraintLayout?, selectedText: String) {
        selectedView?.setOnClickListener {
            // 선택한 뷰의 시각적 표시 변경 (예: 배경 변경)
            //toggleBackground
            toggleBackground(selectedView)

            val dlg = ChangeTitleDialog(this) // 커스텀 다이얼로그 객체 저장
            // 예 버튼 클릭 시 실행
            dlg.setOnOKClickedListener{
                //사용자 칭호 보내기
                val changeTitle = Thread {
                    val selectedTitle = selectedText

                    try {
                        val changeTitleJson = JSONObject()
                        changeTitleJson.put("user_name", getName()) // 사용자 이름
                        changeTitleJson.put("value", selectedTitle.replace("\n", " "))  // value 값

                        Request().reqpost("http://dmumars.kro.kr/api/setusertitle", changeTitleJson)
                    } catch (e: UnknownServiceException) {
                        println(e.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                changeTitle.start()
                changeTitle.join()
            }
            Log.d("dsfdsfsfsdfs", selectedTitle)
            // 아니오 버튼 클릭 시 실행
            dlg.setOnNOClickedListener {}
            dlg.show("칭호를 변경하시겠습니까?") // 다이얼로그 내용에 담을 텍스트
        }
    }
    private fun toggleBackground(view: ConstraintLayout) {
        val AllViews = arrayOf(binding.f1, binding.f2, binding.f3, binding.f4, binding.f5, binding.f6,
            binding.b1, binding.b2, binding.b3, binding.b4, binding.b5, binding.b6)

        for(a in AllViews){
            if(a.parent==view){
                if (a.background.constantState == ContextCompat.getDrawable(this, R.drawable.act_btn)?.constantState) {
                    for(b in AllViews)
                        b.setBackgroundResource(R.drawable.act_btn)
                    a.setBackgroundResource(R.drawable.act_btn_click)

                } else {
                    a.setBackgroundResource(R.drawable.act_btn)
                }
                break
            }
        }
    }

    //내가 가지고 있는 칭호들 불투명으로 바꾸고 클릭이벤트 넣기
    fun updateUserTitlesUI(userTitle: MutableList<String>){
        val titleToViewMap = mapOf(
            //프
            "초보 프냥이" to binding.vf1,
            "초보 프백냥이" to binding.vf2,
            "자바스크립트 프냥이" to binding.vf3,
            "프론트엔드 마법사냥" to binding.vf4,
            "프론트엔드 냥스터" to binding.vf5,
            "프론트엔드 마에스트냥" to binding.vf6,
            //백
            "초보 백냥이" to binding.vb1,
            "초보 백프냥이" to binding.vb2,
            "자바스크립트 백냥이" to binding.vb3,
            "백엔드 냥지니어" to binding.vb4,
            "백엔드 냥스터" to binding.vb5,
            "백엔드 마에스트냥" to binding.vb6
        )
        for(title in userTitle){
            ViewOpacity(2,title)
            setClickListener(titleToViewMap[title],title)
            //binding.vf1,"초보 프냥이"
        }
    }

    fun getName(): String {
        val pref = getSharedPreferences("userName", 0)
        return pref.getString("name", "").toString()
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