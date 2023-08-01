package com.example.marsproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.marsproject.databinding.FragmentMainObjectiveBinding

class MainObjectiveFragment : Fragment() {
    private lateinit var binding: FragmentMainObjectiveBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainObjectiveBinding.inflate(inflater)

        // 클릭 시 선택한 스킬이 무엇인지 저장하고 해당하는 상세 목표로 이동하는 리스너
        val skillClkListener = View.OnClickListener { p0 ->
            when(p0?.id) {
                R.id.htmlButton -> (activity as MainActivity).setSkill("html") // 저장
                R.id.cssButton -> (activity as MainActivity).setSkill("css") // 저장
                R.id.javaButton -> (activity as MainActivity).setSkill("java") // 저장
                R.id.pythonButton -> (activity as MainActivity).setSkill("python") // 저장
                R.id.javascriptButton -> (activity as MainActivity).setSkill("js") // 저장
                R.id.frontendButton -> (activity as MainActivity).setSkill("frontend") // 저장
                R.id.backendButton -> (activity as MainActivity).setSkill("backend") // 저장
                R.id.frontexamButton -> (activity as MainActivity).setSkill("중간시험") // 저장
                R.id.backexamButton -> (activity as MainActivity).setSkill("중간시험") // 저장
                R.id.jspButton -> (activity as MainActivity).setSkill("jsp") // 저장
                R.id.reactButton -> (activity as MainActivity).setSkill("react") // 저장
                R.id.nodeButton -> (activity as MainActivity).setSkill("node") // 저장
                R.id.djangoButton -> (activity as MainActivity).setSkill("django") // 저장
                R.id.springButton -> (activity as MainActivity).setSkill("spring") // 저장
            }
            (activity as MainActivity).clickchangeFragment(2) // 이동
        }
        binding.htmlButton.setOnClickListener(skillClkListener)
        binding.cssButton.setOnClickListener(skillClkListener)
        binding.javaButton.setOnClickListener(skillClkListener)
        binding.pythonButton.setOnClickListener(skillClkListener)
        binding.javascriptButton.setOnClickListener(skillClkListener)
        binding.frontendButton.setOnClickListener(skillClkListener)
        binding.backendButton.setOnClickListener(skillClkListener)
        binding.frontexamButton.setOnClickListener(skillClkListener)
        binding.backexamButton.setOnClickListener(skillClkListener)
        binding.jspButton.setOnClickListener(skillClkListener)
        binding.reactButton.setOnClickListener(skillClkListener)
        binding.nodeButton.setOnClickListener(skillClkListener)
        binding.djangoButton.setOnClickListener(skillClkListener)
        binding.springButton.setOnClickListener(skillClkListener)

        return binding.root
    }
}