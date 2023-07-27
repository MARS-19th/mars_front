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
        binding.htmlButton.setOnClickListener{
            (activity as MainActivity).setSkill("html") // 저장
            (activity as MainActivity).clickchangeFragment(2) // 이동
        }

        return binding.root
    }
}