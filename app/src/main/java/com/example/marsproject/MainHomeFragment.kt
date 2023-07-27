package com.example.marsproject

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.marsproject.databinding.FragmentMainHomeBinding

class MainHomeFragment : Fragment() {
    private lateinit var binding: FragmentMainHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainHomeBinding.inflate(inflater)

        // 클릭 시 내 주변 사람 찾기로 이동 리스너
        binding.bluetoothImage.setOnClickListener {
            activity?.let{
                val intent = Intent(context, SearchPeopleActivity::class.java)
                startActivity(intent)
            }
        }

        // 클릭 시 목표 탭으로 이동 리스너
        binding.objectiveName.setOnClickListener{
            (activity as MainActivity).clickchangeFragment(1)
        }
        binding.progressBar.setOnClickListener{
            (activity as MainActivity).clickchangeFragment(1)
        }

        return binding.root
    }
}