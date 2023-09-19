package com.example.marsproject

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.marsproject.databinding.FragmentMainMypageBinding

class MainMypageFragment : Fragment() {
    private lateinit var binding: FragmentMainMypageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainMypageBinding.inflate(inflater)

        // 클릭 시 로그아웃 리스너
        binding.logout.setOnClickListener{
            (activity as MainActivity).clearLogin()
            (activity as MainActivity).clearName()
            (activity as MainActivity).clickchangeFragment(3)
        }

        // 클릭 시 상점으로 이동 리스너
        binding.store.setOnClickListener {
            activity?.let{
                val intent = Intent(context, StoreActivity::class.java)
                startActivity(intent)
            }
        }

        return binding.root
    }
}