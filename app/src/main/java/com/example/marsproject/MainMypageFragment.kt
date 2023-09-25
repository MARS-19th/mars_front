package com.example.marsproject

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.marsproject.databinding.FragmentMainMypageBinding
import com.kakao.sdk.user.UserApiClient

class MainMypageFragment : Fragment() {
    private lateinit var binding: FragmentMainMypageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainMypageBinding.inflate(inflater)

        // 클릭 시 로그아웃 및 화면 전환 리스너
        binding.logoutLayout.setOnClickListener{
            // 카카오 계정 연동 해제
            UserApiClient.instance.logout { err ->
                if (err == null) {
                    Log.d(ContentValues.TAG, "로그아웃 성공")
                }
                else {
                    Log.d(ContentValues.TAG, "로그아웃 실패")
                }
            }

            (activity as MainActivity).clearLogin() // 로그인 정보 삭제
            (activity as MainActivity).clearName() // 닉네임 정보 삭제
            (activity as MainActivity).clickchangeFragment(3) // 홈 프래그먼트로 전환
        }

//        // 클릭 시 상점으로 이동 리스너
//        binding.titleLayout.setOnClickListener {
//            activity?.let{
//                // 인텐트 생성 후 액티비티 생성
//                val intent = Intent(context, StoreActivity::class.java) // 상점 페이지로 설정
//                startActivity(intent) // 액티비티 생성
//            }
//        }

        return binding.root
    }
}