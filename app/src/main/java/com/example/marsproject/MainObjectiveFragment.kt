package com.example.marsproject

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.marsproject.databinding.FragmentMainObjectiveBinding
import com.kakao.sdk.user.UserApiClient
import org.json.JSONObject
import java.net.UnknownServiceException

class MainObjectiveFragment : Fragment() {
    private lateinit var binding: FragmentMainObjectiveBinding
    private lateinit var savedname: String // 저장된 닉네임

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainObjectiveBinding.inflate(inflater)

        // 닉네임 정보 불러오기
        savedname = (activity as MainActivity).getName()


        // 버튼 클릭 리스너 지정
        objectiveButton()

        // 스킬트리 설정 함수
        skilltree()

        return binding.root
    }

    // 스킬트리 설정 함수
    private fun skilltree(){
        // 스킬트리 진행 상황 불러오기
        val skillThread = Thread {
            try {
                val jsonObject = Request().reqget("http://dmumars.kro.kr/api/getuserskill/${savedname}") //get요청

                // 기본 스킬 4개 클리어를 안했을 때 javascript 버튼 클릭 시 토스트 메시지 출력
                if(jsonObject.getJSONArray("results").length() < 4) {
                    binding.javascriptButton.setOnClickListener{
                        Toast.makeText(context, "위 4개의 스킬을 클리어하셔야합니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                // javascript까지 클리어 했을 때 frontend, backend 표시
                if (jsonObject.getJSONArray("results").length() >= 5) {
                    changeVisibleLine(binding.choiceline1) // 프론트엔드, 백엔드 중간 선 활성화로 변경
                    changeVisibleLine(binding.frontendline1) // 프론트엔드 선 활성화로 변경
                    changeVisibleLine(binding.frontendline2) // 프론트엔드 선 활성화로 변경
                    changeVisibleButton(binding.frontendButton) // 프론트엔드 버튼 활성화로 변경
                    changeVisibleLine(binding.backendline1) // 백엔드 선 활성화로 변경
                    changeVisibleLine(binding.backendline2) // 백엔드 선 활성화로 변경
                    changeVisibleButton(binding.backendButton) // 백엔드 버튼 활성화로 변경
                }

                var choice = "" // 프론트엔드인지 백엔드인지 저장
                for(i in 0 until jsonObject.getJSONArray("results").length()) {
                    // 클리어한 스킬들 클리어로 변경
                    when(jsonObject.getJSONArray("results").getString(i)) {
                        "html" -> changeClearButton(binding.htmlButton) // html 버튼 클리어로 변경
                        "css" -> changeClearButton(binding.cssButton) // css 버튼 클리어로 변경
                        "java" -> changeClearButton(binding.javaButton) // java 버튼 클리어로 변경
                        "python" -> changeClearButton(binding.pythonButton) // python 버튼 클리어로 변경
                        "js" -> changeClearButton(binding.javascriptButton) // javascript 버튼 클리어로 변경
                        "frontend" -> {
                            choice = "frontend" // frontend 저장
                            changeClearButton(binding.frontendButton) // frontend 버튼 클리어로 변경
                            binding.frontendButton.isClickable = false // frontend 버튼 클릭 비활성화로 변경
                            changeInvisibleButton(binding.backendButton) // backend 버튼 비활성화로 변경
                            binding.backendButton.isClickable = false // backend 버튼 클릭 비활성화로 변경
                            changeInvisibleLine(binding.backendline1) // backend 선 비활성화로 변경
                            changeInvisibleLine(binding.backendline2) // backend 선 비활성화로 변경
                            changeVisibleLine(binding.frontexamline) // frontexam 선 활성화로 변경
                            changeVisibleButton(binding.frontexamButton) // frontexam 버튼 활성화로 변경
                            changeInvisibleLine(binding.backexamline) // backexam 선 비활성화로 변경
                            changeInvisibleButton(binding.backexamButton) // backexam 버튼 비활성화로 변경
                            binding.backexamButton.isClickable = false // backexam 버튼 클릭 비활성화로 변경
                        }
                        "backend" -> {
                            choice = "backend" // backend 저장
                            changeClearButton(binding.backendButton)  // backend 버튼 클리어로 변경
                            binding.backendButton.isClickable = false // backend 버튼 클릭 비활성화로 변경
                            changeInvisibleButton(binding.frontendButton) // frontend 버튼 비활성화로 변경
                            binding.frontendButton.isClickable = false // frontend 버튼 클릭 비활성화로 변경
                            changeInvisibleLine(binding.frontendline1) // frontend 선 비활성화로 변경
                            changeInvisibleLine(binding.frontendline2) // frontend 선 비활성화로 변경
                            changeVisibleLine(binding.backexamline) // backexam 선 활성화로 변경
                            changeVisibleButton(binding.backexamButton) // backexam 버튼 활성화로 변경
                            changeInvisibleLine(binding.frontexamline) // frontexam 선 비활성화로 변경
                            changeInvisibleButton(binding.frontexamButton) // frontexam 버튼 비활성화로 변경
                            binding.frontexamButton.isClickable = false // frontexam 버튼 클릭 비활성화로 변경
                        }
                        "중간시험" -> {
                            if(choice == "frontend") {
                                changeClearButton(binding.frontexamButton) // frontexam 버튼 클리어로 변경
                                changeVisibleLine(binding.choiceline2) // JSP, React 중간 선 활성화로 변경
                                changeVisibleLine(binding.jspline1) // JSP 선 활성화로 변경
                                changeVisibleLine(binding.jspline2) // JSP 선 활성화로 변경
                                changeVisibleButton(binding.jspButton) // JSP 버튼 활성화로 변경
                                changeVisibleLine(binding.reactline1) // React 선 활성화로 변경
                                changeVisibleLine(binding.reactline2) // React 선 활성화로 변경
                                changeVisibleButton(binding.reactButton) // React 버튼 활성화로 변경
                                changeInvisibleLine(binding.choiceline3) // Node, Spring 중간 선 비활성화로 변경
                                changeInvisibleLine(binding.nodeline1) // Node 선 비활성화로 변경
                                changeInvisibleLine(binding.nodeline2) // Node 선 비활성화로 변경
                                changeInvisibleButton(binding.nodeButton) // Node 버튼 비활성화로 변경
                                binding.nodeButton.isClickable = false // Node 버튼 클릭 비활성화로 변경
                                changeInvisibleLine(binding.springline1) // Spring 선 비활성화로 변경
                                changeInvisibleLine(binding.springline2) // Spring 선 비활성화로 변경
                                changeInvisibleButton(binding.springButton) // Spring 버튼 비활성화로 변경
                                binding.springButton.isClickable = false // Spring 버튼 클릭 비활성화로 변경
                            } else {
                                changeClearButton(binding.backexamButton) // backexam 버튼 클리어로 변경
                                changeVisibleLine(binding.choiceline3) // Node, Spring 중간 선 활성화로 변경
                                changeVisibleLine(binding.nodeline1) // Node 선 활성화로 변경
                                changeVisibleLine(binding.nodeline2) // Node 선 활성화로 변경
                                changeVisibleButton(binding.nodeButton) // Node 버튼 활성화로 변경
                                changeVisibleLine(binding.springline1) // Spring 선 활성화로 변경
                                changeVisibleLine(binding.springline2) // Spring 선 활성화로 변경
                                changeVisibleButton(binding.springButton) // Spring 버튼 활성화로 변경
                                changeInvisibleLine(binding.choiceline2) // JSP, React 중간 선 비활성화로 변경
                                changeInvisibleLine(binding.jspline1) // JSP 선 비활성화로 변경
                                changeInvisibleLine(binding.jspline2) // JSP 선 비활성화로 변경
                                changeInvisibleButton(binding.jspButton) // JSP 버튼 비활성화로 변경
                                binding.jspButton.isClickable = false // JSP 버튼 클릭 비활성화로 변경
                                changeInvisibleLine(binding.reactline1) // React 선 비활성화로 변경
                                changeInvisibleLine(binding.reactline2) // React 선 비활성화로 변경
                                changeInvisibleButton(binding.reactButton) // React 버튼 비활성화로 변경
                                binding.reactButton.isClickable = false // React 버튼 클릭 비활성화로 변경
                            }
                        }
                        "jsp" -> changeClearButton(binding.jspButton) // jsp 버튼 클리어로 변경
                        "react" -> changeClearButton(binding.reactButton) // react 버튼 클리어로 변경
                        "spring" -> changeClearButton(binding.springButton) // spring 버튼 클리어로 변경
                        "node" -> changeClearButton(binding.nodeButton) // node 버튼 클리어로 변경
                    }
                }
            } catch (e: UnknownServiceException) {
                // API 사용법에 나와있는 모든 오류응답은 여기서 처리
                println(e.message)
                if(e.message == "empty") {
                    binding.javascriptButton.setOnClickListener{
                        Toast.makeText(context, "위 4개의 스킬을 클리어하셔야합니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        skillThread.start() // 쓰레드 시작
        skillThread.join() // 쓰레드 종료될 때까지 대기
    }

    // 스킬트리 선 활성화
    private fun changeVisibleLine(view: View) {
        view.setBackgroundColor(Color.parseColor("#FF9C46")) // 색상 변경
        view.isVisible = true // 보이게 변경
    }

    // 스킬트리 선 비활성화
    private fun changeInvisibleLine(view: View) {
        view.setBackgroundColor(Color.parseColor("#F0F1F4")) // 색상 변경
        view.isVisible = true // 보이게 변경
    }

    // 스킬트리 버튼 활성화
    private fun changeVisibleButton(button: Button) {
        button.setBackgroundResource(R.drawable.objective_button_ok) // 백그라운드 변경
        button.setTextColor(Color.parseColor("#FF8922")) // 색상 변경
        button.isVisible = true // 보이게 변경
    }

    // 스킬트리 버튼 비활성화
    private fun changeInvisibleButton(button: Button) {
        button.setBackgroundResource(R.drawable.objective_button_no) // 백그라운드 변경
        button.setTextColor(Color.parseColor("#C1C3C7")) // 색상 변경
        button.isVisible = true // 보이게 변경
    }

    // 스킬트리 버튼 클리어로 변경
    private fun changeClearButton(button: Button) {
        button.setBackgroundResource(R.drawable.objective_button_clear)
        button.setTextColor(Color.WHITE)
        button.isVisible = true
    }

    // 상세 목표 버튼 클릭 설정 함수
    private fun objectiveButton(){
        // 클릭 시 일일 목표로 이동하는 리스너
        binding.dailyObjectiveLayout.setOnClickListener{
            activity?.let{
                // 인텐트 생성 후 액티비티 생성
                val intent = Intent(context, DailyObjectiveActivity::class.java) // 일일 목표 페이지로 설정
                startActivity(intent) // 액티비티 생성
            }
        }

        // 클릭 시 선택한 스킬이 무엇인지 저장하고 해당하는 상세 목표로 이동하는 리스너
        val skillClkListener = View.OnClickListener { p0 ->
            when(p0?.id) {
                R.id.htmlButton -> (activity as MainActivity).setSkill("HTML") // 저장
                R.id.cssButton -> (activity as MainActivity).setSkill("CSS") // 저장
                R.id.javaButton -> (activity as MainActivity).setSkill("Java") // 저장
                R.id.pythonButton -> (activity as MainActivity).setSkill("Python") // 저장
                R.id.javascriptButton -> (activity as MainActivity).setSkill("js") // 저장
                R.id.jspButton -> (activity as MainActivity).setSkill("JSP") // 저장
                R.id.reactButton -> (activity as MainActivity).setSkill("React") // 저장
                R.id.nodeButton -> (activity as MainActivity).setSkill("Node") // 저장
                R.id.springButton -> (activity as MainActivity).setSkill("Spring") // 저장
            }
            (activity as MainActivity).clickchangeFragment(2) // 이동
        }
        // 클릭 리스너 지정
        binding.htmlButton.setOnClickListener(skillClkListener)
        binding.cssButton.setOnClickListener(skillClkListener)
        binding.javaButton.setOnClickListener(skillClkListener)
        binding.pythonButton.setOnClickListener(skillClkListener)
        binding.javascriptButton.setOnClickListener(skillClkListener)
        binding.jspButton.setOnClickListener(skillClkListener)
        binding.reactButton.setOnClickListener(skillClkListener)
        binding.nodeButton.setOnClickListener(skillClkListener)
        binding.springButton.setOnClickListener(skillClkListener)

        // 클릭 시 프론트엔드인지 백엔드인지에 따라 스킬트리 변경
        val choiceClickListener = View.OnClickListener {
            var choice = "" // 무엇을 선택했는지 저장하는 변수
            // 클릭한 버튼에 따른 값 변경
            when(it.id) {
                R.id.frontendButton -> choice = "frontend" // 프론트엔드
                R.id.backendButton -> choice = "backend" // 백엔드
            }

            val dlg = MyDialog(context as AppCompatActivity) // 커스텀 다이얼로그 객체 저장
            // 예 버튼 클릭 시 실행
            dlg.setOnOKClickedListener{
                // 선택한 스킬을 추가하는 쓰레드 생성
                val choiceThread = Thread {
                    try {
                        // 스킬 추가
                        val addskilljson = JSONObject() // json 생성
                        addskilljson.put("user_name", savedname) // 사용자 닉네임
                        addskilljson.put("skill", choice) // 추가할 스킬

                        // 스킬 추가
                        Request().reqpost("http://dmumars.kro.kr/api/setuserskill", addskilljson)

                    } catch (e: UnknownServiceException) {
                        // API 사용법에 나와있는 모든 오류응답은 여기서 처리
                        println(e.message)
                        // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                choiceThread.start() // 쓰레드 실행
                choiceThread.join() // 쓰레드 종료될 때까지 대기

                // 화면 새로고침
                (activity as MainActivity).clickchangeFragment(1)
            }
            // 아니오 버튼 클릭 시 실행
            dlg.setOnNOClickedListener{}

            // 클릭한 버튼에 따른 메시지 변경
            if (choice == "frontend") {
                dlg.show("프론트엔드로 하시겠습니까?") // 다이얼로그 내용에 담을 텍스트
            } else {
                dlg.show("백엔드로 하시겠습니까?") // 다이얼로그 내용에 담을 텍스트
            }
        }

        // 클릭 리스너 지정
        binding.frontendButton.setOnClickListener(choiceClickListener)
        binding.backendButton.setOnClickListener(choiceClickListener)

        // 클릭 시 VR 기기 여부 묻는 다이얼로그 띄우기
        val examClickListener = View.OnClickListener {
            var choice = "" // 무엇을 선택했는지 저장하는 변수

            // 클릭한 버튼에 따른 값 변경
            when(it.id) {
                R.id.frontexamButton -> choice = "frontend" // 프론트엔드
                R.id.backexamButton -> choice = "backend" // 백엔드
            }

            val dlg = MyDialog(context as AppCompatActivity) // 커스텀 다이얼로그 객체 저장

            // 예 버튼 클릭 시 실행
            dlg.setOnOKClickedListener{
                // 추후 삭제
                Toast.makeText(context, "VR 우편함으로 전송했습니다.", Toast.LENGTH_SHORT).show()

                // 클릭한 버튼에 따른 시험 변경
                if (choice == "frontend") {
                    // VR 우편함에 시험 넣기 추가
                } else {
                    // VR 우편함에 시험 넣기 추가
                }
            }

            // 아니오 버튼 클릭 시 실행
            dlg.setOnNOClickedListener {
                // 추후 삭제
                Toast.makeText(context, "앱에서 시험보기 추가예정",Toast.LENGTH_SHORT).show()
                // 임시로 시험 통과 시 스킬 추가하는것 작성
                // 선택한 스킬을 추가하는 쓰레드 생성
                val choiceThread = Thread {
                    try {
                        // 스킬 추가
                        val addskilljson = JSONObject() // json 생성
                        addskilljson.put("user_name", savedname) // 사용자 닉네임
                        addskilljson.put("skill", "중간시험") // 추가할 스킬

                        // 스킬 추가
                        Request().reqpost("http://dmumars.kro.kr/api/setuserskill", addskilljson)

                    } catch (e: UnknownServiceException) {
                        // API 사용법에 나와있는 모든 오류응답은 여기서 처리
                        println(e.message)
                        // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                choiceThread.start() // 쓰레드 실행
                choiceThread.join() // 쓰레드 종료될 때까지 대기

                // 화면 새로고침
                (activity as MainActivity).clickchangeFragment(1)

                // 클릭한 버튼에 따른 시험 변경
                if (choice == "frontend") {
                    // 유저의 해당 스킬 클리어 처리 쓰레드 생성
                    val titleThread = Thread {
                        try {
                            // 칭호 부여 및 변경
                            val titlejson = JSONObject()
                            titlejson.put("user_name", savedname) // 닉네임
                            titlejson.put("value", "프론트엔드 마법사냥") // 칭호

                            Request().reqpost("http://dmumars.kro.kr/api/setusertitle", titlejson)

                        } catch (e: UnknownServiceException) {
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    titleThread.start() // 쓰레드 실행
                    titleThread.join() // 쓰레드 종료될 때까지 대기
                    // 앱에서 시험 보기 추가
                } else {
                    // 유저의 해당 스킬 클리어 처리 쓰레드 생성
                    val titleThread = Thread {
                        try {
                            // 칭호 부여 및 변경
                            val titlejson = JSONObject()
                            titlejson.put("user_name", savedname) // 닉네임
                            titlejson.put("value", "백엔드 냥지니어") // 칭호

                            Request().reqpost("http://dmumars.kro.kr/api/setusertitle", titlejson)

                        } catch (e: UnknownServiceException) {
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    titleThread.start() // 쓰레드 실행
                    titleThread.join() // 쓰레드 종료될 때까지 대기
                    // 앱에서 시험 보기 추가
                }
            }

            dlg.show("VR기기를 소지하고 있습니까?") // 다이얼로그 내용에 담을 텍스트
        }

        // 클릭 리스너 지정
        binding.frontexamButton.setOnClickListener(examClickListener)
        binding.backexamButton.setOnClickListener(examClickListener)
    }
}