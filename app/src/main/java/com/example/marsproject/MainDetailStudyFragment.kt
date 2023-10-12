package com.example.marsproject

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.example.marsproject.databinding.FragmentMainDetailStudyBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.net.UnknownServiceException


class MainDetailStudyFragment : Fragment() {
    private lateinit var binding: FragmentMainDetailStudyBinding
    private lateinit var skill: String
    private lateinit var savedname: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainDetailStudyBinding.inflate(inflater)

        // 선택한 스킬이 무엇인지 가져오기
        skill = (activity as MainActivity).getSkill()

        // 타이틀을 선택한 스킬로 변경
        if(skill == "js") {
            binding.skillTitle.text = "Javascript"
        } else {
            binding.skillTitle.text = skill
        }

        // 클릭 시 뒤로 이동하는 함수
        binding.backImage.setOnClickListener{(activity as MainActivity).clickchangeFragment(1)}

        // 닉네임 정보 불러오기
        savedname = (activity as MainActivity).getName()

        // 뷰 객체들 담을 리스트 생성
        val itemList:Array<View> = arrayOf(binding.studyView1, binding.studyView2, binding.studyView3,
            binding.studyView4, binding.studyView5, binding.studyView6, binding.studyView7,
            binding.studyView8, binding.studyView9, binding.studyView10, binding.studyView11,
            binding.studyView12, binding.studyView13, binding.studyView14, binding.studyView15)
        val viewList:Array<View> = arrayOf(binding.view1, binding.view2, binding.view3,
            binding.view4, binding.view5, binding.view6, binding.view7,
            binding.view8, binding.view9, binding.view10, binding.view11,
            binding.view12, binding.view13, binding.view14)

        // 강의 정보 불러오기
        val skillThread = Thread {
            try {
                // 스킬의 강의 리스트들을 불러오기
                val lectureObject = Request().reqget("http://dmumars.kro.kr/api/getdetailmark/${skill.lowercase()}") //get요청

                // 스킬의 강의 수만큼 for문 실행
                for(i in 0 until lectureObject.getJSONArray("results").length()) {
                    // 클릭시 해당 일차 강의 띄우기
                    val lecturelistener = View.OnClickListener {
                        var progress = 0 // 유저의 해당 강의 진행도
                        var lectureid = lectureObject.getJSONArray("results").getJSONObject(i).getInt("mark_id") // 강의 번호

                        // 유저의 해당 강의 진행도를 가져오는 쓰레드 생성
                        val progressThread = Thread {
                            try {
                                val userObject = Request().reqget(
                                    "http://dmumars.kro.kr/api/getusermark/${savedname}/${skill.lowercase()}/${i + 1}"
                                ) //get요청

                                progress = userObject.getJSONArray("results").getJSONObject(0)
                                    .getInt("progress")
                            } catch (e: UnknownServiceException) {
                                // API 사용법에 나와있는 모든 오류응답은 여기서 처리
                                println(e.message)
                                // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        progressThread.start() // 쓰레드 실행
                        progressThread.join() // 쓰레드 종료될 때까지 대기

                        // 강의 제목 저장
                        val lectureName = lectureObject.getJSONArray("results").getJSONObject(i).getString("mark_list")

                        // 링크 저장
                        var lectureLink = ""

                        // 강의의 유튜브 링크 또는 과제 가져오는 쓰레드 생성
                        val infoThread = Thread {
                            val infoObject = Request().reqget(
                                "http://dmumars.kro.kr/api/getmoredata/${lectureid}"
                            ) //get요청
                            lectureLink = infoObject.getJSONArray("results").getJSONObject(0).getString("info_data")
                        }
                        infoThread.start() // 쓰레드 실행
                        infoThread.join() // 쓰레드 종료될 때까지 대기

                        val dlg = LectureDialogCustom(context as AppCompatActivity) // 커스텀 다이얼로그 객체 저장
                        // 예 버튼 클릭 시 실행
                        dlg.setOnOKClickedListener{
                            runBlocking {async{delay(1000)}} // 1초 대기

                            // 유저의 강의 진행도 저장 쓰레드 생성
                            val saveThread = Thread {
                                try {
                                    // 닉네임과 강의 번호와 진행도 담아서 보내기
                                    val saveDatajson = JSONObject() //json 생성
                                    saveDatajson.put("user_name", savedname) // 유저 닉네임
                                    saveDatajson.put("mark_id", lectureid) // 강의 번호
                                    saveDatajson.put("progress", 100) // 진행도

                                    // 강의 진행도 저장
                                    Request().reqpost("http://dmumars.kro.kr/api/setuserdetailskill", saveDatajson)

                                    // 처음만 재화 지급
                                    if(progress == 0) {
                                        // 사용자의 재화 가져오기
                                        val jsonObject =
                                            Request().reqget("http://dmumars.kro.kr/api/getuserdata/${savedname}") //get요청

                                        // 강의 하나 들을때마다 20원
                                        var money = jsonObject.getInt("money") // 재화
                                        money += 20 // 20원 추가

                                        // 사용자의 재화 저장
                                        val moneyjson = JSONObject()
                                        moneyjson.put("user_name", savedname) // 닉네임
                                        moneyjson.put("value", money) // 재화

                                        Request().reqpost(
                                            "http://dmumars.kro.kr/api/setmoney",
                                            moneyjson
                                        )
                                    }

                                } catch (e: UnknownServiceException) {
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            saveThread.start() // 쓰레드 실행
                            saveThread.join() // 쓰레드 종료될 때까지 대기

                            // 화면 새로고침
                            (activity as MainActivity).clickchangeFragment(2)
                        }
                        dlg.show(lectureName, lectureLink, progress) // 다이얼로그 내용에 담을 텍스트
                    }
                    itemList[i].setOnClickListener(lecturelistener)
                    viewList[i].setOnClickListener(lecturelistener)
                }

                // 들은 강의의 수를 저장
                var count = 0

                // 스킬의 강의 수만큼 for문 실행
                for(i in 0 until lectureObject.getJSONArray("results").length()) {
                    var progress = 0 // 유저의 해당 강의 진행도
                    // 유저의 해당 강의 진행도를 가져오는 쓰레드 생성
                    val progressThread = Thread {
                        try {
                            val userObject = Request().reqget(
                                "http://dmumars.kro.kr/api/getusermark/${savedname}/${skill.lowercase()}/${i + 1}"
                            ) //get요청

                            progress = userObject.getJSONArray("results").getJSONObject(0)
                                .getInt("progress")
                        } catch (e: UnknownServiceException) {
                            // API 사용법에 나와있는 모든 오류응답은 여기서 처리
                            println(e.message)
                            // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    progressThread.start() // 쓰레드 실행
                    progressThread.join() // 쓰레드 종료될 때까지 대기
                    if(progress == 100) {
                        itemList[i].setBackgroundResource(R.drawable.circle_clear)
                        count++
                    }

                    // 안내 말풍선 텍스트 변경
                    if (count == 14) {
                        binding.signText.text = "클리어"
                    } else {
                        binding.signText.text = "${count + 1}일차"
                    }

                    // 안내 말풍선 위치 조정
                    val constraints = ConstraintSet()
                    constraints.clone(binding.constraintLayout)
                    constraints.connect(
                        binding.signLayout.id,
                        ConstraintSet.BOTTOM,
                        itemList[count].id,
                        ConstraintSet.BOTTOM
                    )
                    if(count == 14) {
                        constraints.connect(
                            binding.signLayout.id,
                            ConstraintSet.BOTTOM,
                            itemList[count].id,
                            ConstraintSet.BOTTOM,
                            convertDpToPixel(70f, context as AppCompatActivity)
                        )
                    }
                    constraints.connect(
                        binding.signLayout.id,
                        ConstraintSet.END,
                        itemList[count].id,
                        ConstraintSet.END
                    )
                    constraints.connect(
                        binding.signLayout.id,
                        ConstraintSet.START,
                        itemList[count].id,
                        ConstraintSet.START
                    )
                    constraints.applyTo(binding.constraintLayout)
                }

                Log.d("count", count.toString())

                // 스킬의 강의 수만큼 for문 실행
                for(i in count + 1 until 15) {
                    itemList[i].setOnClickListener{
                        Toast.makeText(context, "이전 강의를 시청하세요", Toast.LENGTH_SHORT).show()
                    }
                    viewList[i].setOnClickListener{
                        Toast.makeText(context, "이전 강의를 시청하세요", Toast.LENGTH_SHORT).show()
                    }
                }

                // 모든 강의를 들었을 때 해당 스킬 클리어 처리
                if (count == 14) {
                    binding.studyView15.setOnClickListener {
                        // 유저의 해당 스킬 클리어 처리 및 재화 지급 쓰레드 생성
                        val saveThread = Thread {
                            try {
                                // 닉네임과 스킬 이 담아서 보내기
                                val saveDatajson = JSONObject() //json 생성
                                saveDatajson.put("user_name", savedname) // 유저 닉네임
                                saveDatajson.put("skill", skill.lowercase()) // 스킬 이름

                                // 유저의 해당 스킬 클리어 처리
                                Request().reqpost("http://dmumars.kro.kr/api/setuserskill", saveDatajson)

                                // 사용자의 재화 가져오기
                                val jsonObject =
                                    Request().reqget("http://dmumars.kro.kr/api/getuserdata/${savedname}") //get요청

                                // 스킬 클리어시 300원
                                var money = jsonObject.getInt("money") // 재화
                                money += 300 // 300원 추가

                                // 사용자의 재화 저장
                                val moneyjson = JSONObject()
                                moneyjson.put("user_name", savedname) // 닉네임
                                moneyjson.put("value", money) // 재화

                                Request().reqpost(
                                    "http://dmumars.kro.kr/api/setmoney",
                                    moneyjson
                                )

                            } catch (e: UnknownServiceException) {
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        saveThread.start() // 쓰레드 실행
                        saveThread.join() // 쓰레드 종료될 때까지 대기

                        // 칭호 부여
                        giveTitle(skill.lowercase())

                        // 토스트 메시지 출력
                        if(skill == "js") {
                            Toast.makeText(context, "JavaScript 스킬을 마스터하셨습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "$skill 스킬을 마스터하셨습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            } catch (e: UnknownServiceException) {
                // API 사용법에 나와있는 모든 오류응답은 여기서 처리
                println(e.message)
                // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        skillThread.start() // 쓰레드 실행
        skillThread.join() // 쓰레드 종료될 때까지 대기

        return binding.root
    }

    // dp를 픽셀로 변환
    private fun convertDpToPixel(dp: Float, context: Context): Int {
        return (dp * (context.resources
            .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }

    // 칭호 부여
    private fun giveTitle(skill: String) {
        var title = ""
        when (skill) {
            "js" -> title = "자바스크립트 프냥이"
            "jsp" -> title = "프론트엔드 냥스터"
            "react" -> title = "프론트엔드 마에스트냥"
            "spring" -> title = "백엔드 냥스터"
            "node" -> title = "백엔드 마에스트냥"
            "css" -> {
                // html을 클리어했는지 확인 후 클리어했다면 칭호 부여
                val checkThread = Thread {
                    try {
                        val jsonObject =
                            Request().reqget("http://dmumars.kro.kr/api/getuserskill/${savedname}")

                        // 사용자 클리어 한 스킬 담기
                        val resultsArray = jsonObject.getJSONArray("results")

                        for (i in 0 until resultsArray.length()) {
                            if (resultsArray.getString(i) == "html") title = "초보 백프냥이"
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                checkThread.start() // 쓰레드 실행
                checkThread.join() // 쓰레드 종료될 때까지 대기
            }
            "html" -> {
                // css를 클리어했는지 확인 후 클리어했다면 칭호 부여
                val checkThread = Thread {
                    try {
                        val jsonObject =
                            Request().reqget("http://dmumars.kro.kr/api/getuserskill/${savedname}")

                        // 사용자 클리어 한 스킬 담기
                        val resultsArray = jsonObject.getJSONArray("results")

                        for (i in 0 until resultsArray.length()) {
                            if (resultsArray.getString(i) == "css") title = "초보 백프냥이"
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                checkThread.start() // 쓰레드 실행
                checkThread.join() // 쓰레드 종료될 때까지 대기
            }
            "java" -> {
                // python을 클리어했는지 확인 후 클리어했다면 칭호 부여
                val checkThread = Thread {
                    try {
                        val jsonObject =
                            Request().reqget("http://dmumars.kro.kr/api/getuserskill/${savedname}")

                        // 사용자 클리어 한 스킬 담기
                        val resultsArray = jsonObject.getJSONArray("results")

                        for (i in 0 until resultsArray.length()) {
                            if (resultsArray.getString(i) == "python") title = "초보 프백냥이"
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                checkThread.start() // 쓰레드 실행
                checkThread.join() // 쓰레드 종료될 때까지 대기
            }
            "python" -> {
                // java를 클리어했는지 확인 후 클리어했다면 칭호 부여
                val checkThread = Thread {
                    try {
                        val jsonObject =
                            Request().reqget("http://dmumars.kro.kr/api/getuserskill/${savedname}")

                        // 사용자 클리어 한 스킬 담기
                        val resultsArray = jsonObject.getJSONArray("results")

                        for (i in 0 until resultsArray.length()) {
                            if (resultsArray.getString(i) == "java") title = "초보 프백냥이"
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                checkThread.start() // 쓰레드 실행
                checkThread.join() // 쓰레드 종료될 때까지 대기
            }
        }
        if (title != "") {
            // 유저의 해당 스킬 클리어 처리 쓰레드 생성
            val titleThread = Thread {
                try {
                    // 칭호 부여 및 변경
                    val titlejson = JSONObject()
                    titlejson.put("user_name", savedname) // 닉네임
                    titlejson.put("value", title) // 칭호

                    Request().reqpost("http://dmumars.kro.kr/api/setusertitle", titlejson)

                    // 자바스크립트이면 자바스크립트 프냥이, 백냥이 둘 다 부여
                    if (skill == "js") {
                        // 칭호 부여 및 변경
                        val titlejson = JSONObject()
                        titlejson.put("user_name", savedname) // 닉네임
                        titlejson.put("value", "자바스크립트 백냥이") // 칭호

                        Request().reqpost("http://dmumars.kro.kr/api/setusertitle", titlejson)
                    }
                    // html, css 둘 다 클리어했다면 초보 프냥이, 초보 백프냥이 둘 다 부여
                    if (title == "초보 백프냥이") {
                        // 칭호 부여 및 변경
                        val titlejson = JSONObject()
                        titlejson.put("user_name", savedname) // 닉네임
                        titlejson.put("value", "초보 프냥이") // 칭호

                        Request().reqpost("http://dmumars.kro.kr/api/setusertitle", titlejson)
                    }
                    // java, python 둘 다 클리어했다면 초보 백냥이, 초보 프백냥이 둘 다 부여
                    if (title == "초보 프백냥이") {
                        // 칭호 부여 및 변경
                        val titlejson = JSONObject()
                        titlejson.put("user_name", savedname) // 닉네임
                        titlejson.put("value", "초보 백냥이") // 칭호

                        Request().reqpost("http://dmumars.kro.kr/api/setusertitle", titlejson)
                    }

                } catch (e: UnknownServiceException) {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            titleThread.start() // 쓰레드 실행
            titleThread.join() // 쓰레드 종료될 때까지 대기
        }
    }
}