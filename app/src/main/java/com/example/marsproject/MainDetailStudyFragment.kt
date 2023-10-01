package com.example.marsproject

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.marsproject.databinding.FragmentMainDetailStudyBinding
import com.kakao.sdk.user.UserApiClient
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
        val itemList = ArrayList<View>()

        // 리스트에 값 넣기
        itemList.add(binding.studyView1)
        itemList.add(binding.studyView2)
        itemList.add(binding.studyView3)
        itemList.add(binding.studyView4)
        itemList.add(binding.studyView5)
        itemList.add(binding.studyView6)
        itemList.add(binding.studyView7)
        itemList.add(binding.studyView8)
        itemList.add(binding.studyView9)
        itemList.add(binding.studyView10)
        itemList.add(binding.studyView11)
        itemList.add(binding.studyView12)
        itemList.add(binding.studyView13)
        itemList.add(binding.studyView14)
        itemList.add(binding.studyView15)

        // 강의 정보 불러오기
        val skillThread = Thread {
            try {
                // 스킬의 강의 리스트들을 불러오기
                val lectureObject = Request().reqget("http://dmumars.kro.kr/api/getdetailmark/${skill.lowercase()}") //get요청

                // 스킬의 강의 수만큼 for문 실행
                for(i in 0 until lectureObject.getJSONArray("results").length()) {
                    // 클릭시 해당 일차 강의 띄우기
                    itemList[i].setOnClickListener{
                        var progress = 0 // 유저의 해당 강의 진행도
                        var lectureid = lectureObject.getJSONArray("results").getJSONObject(i).getInt("mark_id") // 강의 번호

                        // 유저의 해당 강의 진행도를 가져오는 쓰레드 생성
                        val progressThread = Thread {
                            try {
                                val userObject = Request().reqget(
                                    "http://dmumars.kro.kr/api/getusermark/${savedname}/${skill.lowercase()}/${
                                        lectureObject.getJSONArray("results").getJSONObject(i)
                                            .getInt("mark_id")
                                    }"
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
                            // 유저의 강의 진행도 저장 쓰레드 생성
                            val changeThread = Thread {
                                try {
                                    // 닉네임과 강의 번호와 진행도 담아서 보내기
                                    val saveDatajson = JSONObject() //json 생성
                                    saveDatajson.put("user_name", savedname) // 유저 닉네임
                                    saveDatajson.put("mark_id", lectureid) // 강의 번호
                                    saveDatajson.put("progress", 100) // 진행도

                                    // 강의 진행도 저장
                                    Request().reqpost("http://dmumars.kro.kr/api/setuserdetailskill", saveDatajson)

                                } catch (e: UnknownServiceException) {
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            changeThread.start() // 쓰레드 실행
                            changeThread.join() // 쓰레드 종료될 때까지 대기
                        }
                        dlg.show(lectureName, lectureLink, progress) // 다이얼로그 내용에 담을 텍스트
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
        skillThread.start()
        skillThread.join()
//
//        // 클릭 시 완료로 변경
//        val clicklistener2 = View.OnClickListener { p0 ->
//            when(p0?.id) {
//                R.id.completeButton1 -> {
//                    if(binding.completeButton1.text.toString() == "완료") {
//                        Toast.makeText(context, "xxxx년 xx월 xx일 완료", Toast.LENGTH_SHORT).show()
//                    } else {
//                        binding.completeButton1.text = "완료"
//                        binding.completeButton1.setBackgroundResource(R.drawable.main_detail_btn_complete)
//                    }
//                }
//                R.id.completeButton2 -> {
//                    if(binding.completeButton2.text.toString() == "완료") {
//                        Toast.makeText(context, "xxxx년 xx월 xx일 완료", Toast.LENGTH_SHORT).show()
//                    } else {
//                        binding.completeButton2.text = "완료"
//                        binding.completeButton2.setBackgroundResource(R.drawable.main_detail_btn_complete)
//                    }
//                }
//            }
//        }
//
//        binding.completeButton1.setOnClickListener(clicklistener2)
//        binding.completeButton2.setOnClickListener(clicklistener2)

        return binding.root
    }
}