package com.example.marsproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.marsproject.databinding.FragmentMainDetailStudyBinding
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

//        // 강의 불러오기
//        val skillThread = Thread {
//            try {
//                val jsonObject = Request().reqget("http://dmumars.kro.kr/api/getdetailmark/${skill.lowercase()}") //get요청
//
//                // 스킬의 강의 수만큼 for문 실행
//                for(i in 0 until jsonObject.getJSONArray("results").length()) {
//                    // 강의의 유튜브 링크 또는 과제 가져오기
//                    val jsonObject2 = Request().reqget("http://dmumars.kro.kr/api/getmoredata/${jsonObject.getJSONArray("results").getJSONObject(i).getInt("mark_id")}") //get요청
//
//                    // 클릭 시 유튜브로 연결 리스너
//                    var clicklistener1 = View.OnClickListener { p0 ->
//                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${jsonObject2.getJSONArray("results").getJSONObject(0).getString("info_data")}"))
//                        startActivity(intent)
//                    }
//
//                    // 중간 시험은 클릭 시 토스트 출력 리스너
//                    if(skill == "중간시험") {
//                        clicklistener1 = View.OnClickListener { p0 ->
//                            Toast.makeText(context, jsonObject2.getJSONArray("results").getString(0), Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    when(i) {
//                        0 -> {
//                            binding.title1.text = jsonObject.getJSONArray("results").getJSONObject(i).getString("mark_list")
//                            binding.title1.setOnClickListener(clicklistener1)
//                            binding.goalButton1.setOnClickListener(clicklistener1)
//                            binding.constraintLayout1.setOnClickListener(clicklistener1)
//                        }
//                        1 -> {
//                            binding.title2.text = jsonObject.getJSONArray("results").getJSONObject(i).getString("mark_list")
//                            binding.title2.setOnClickListener(clicklistener1)
//                            binding.goalButton2.setOnClickListener(clicklistener1)
//                            binding.constraintLayout2.setOnClickListener(clicklistener1)
//                        }
//                    }
//                }
//            } catch (e: UnknownServiceException) {
//                // API 사용법에 나와있는 모든 오류응답은 여기서 처리
//                println(e.message)
//                // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//        skillThread.start()
//        skillThread.join()
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