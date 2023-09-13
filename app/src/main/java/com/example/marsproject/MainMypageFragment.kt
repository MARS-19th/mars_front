package com.example.marsproject

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

        binding.logout.setOnClickListener{
            (activity as MainActivity).clearLogin()
            (activity as MainActivity).clickchangeFragment(3)
        }

        return binding.root
    }
}