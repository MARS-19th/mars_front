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

class MainDetailStudyFragment : Fragment() {
    private lateinit var binding: FragmentMainDetailStudyBinding
    private lateinit var skill: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainDetailStudyBinding.inflate(inflater)

        // 선택한 스킬이 무엇인지 가져오기
        skill = (activity as MainActivity).getSkill()

        // 툴바
        val toolbar: Toolbar = binding.toolbar
        toolbar.setNavigationIcon(R.drawable.icon_left_resize) // 뒤로가기 아이콘 생성
        toolbar.setNavigationOnClickListener{ // 뒤로가기 아이콘 클릭 리스너
            (activity as MainActivity).clickchangeFragment(1) // 뒤로 이동하는 함수
        }

        
        // 일단 그냥 해둔거고 api로 바꿔야함
        // 클릭 시 유튜브로 연결
        val clicklistener1 = View.OnClickListener { p0 ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"))
            startActivity(intent)
        }

        // 클릭 시 완료로 변경
        val clicklistener2 = View.OnClickListener { p0 ->
            when(p0?.id) {
                R.id.completeButton1 -> {
                    if(binding.completeButton1.text.toString() == "완료") {
                        Toast.makeText(context, "xxxx년 xx월 xx일 완료", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.completeButton1.text = "완료"
                        binding.completeButton1.setBackgroundResource(R.drawable.main_detail_btn_complete)
                    }
                }
                R.id.completeButton2 -> {
                    if(binding.completeButton2.text.toString() == "완료") {
                        Toast.makeText(context, "xxxx년 xx월 xx일 완료", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.completeButton2.text = "완료"
                        binding.completeButton2.setBackgroundResource(R.drawable.main_detail_btn_complete)
                    }
                }
            }
        }
        binding.title1.setOnClickListener(clicklistener1)
        binding.goalButton1.setOnClickListener(clicklistener1)
        binding.constraintLayout1.setOnClickListener(clicklistener1)
        binding.completeButton1.setOnClickListener(clicklistener2)

        binding.title2.setOnClickListener(clicklistener1)
        binding.goalButton2.setOnClickListener(clicklistener1)
        binding.constraintLayout2.setOnClickListener(clicklistener1)
        binding.completeButton2.setOnClickListener(clicklistener2)
        //----------------------------------------------------------------------

        return binding.root
    }
}