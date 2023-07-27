package com.example.marsproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.example.marsproject.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var launcher: ActivityResultLauncher<Intent>? = null
    private var skill: String = ""

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(user.check == 0) { // 처음 실행인 지 확인 // 추후에 로그인 유무에 따른 로그인 페이지로 이동 or 메인 페이지로 이동
            val contract = ActivityResultContracts.StartActivityForResult()
            val callback = object: ActivityResultCallback<ActivityResult> {
                override fun onActivityResult(result: ActivityResult?) {
                    if(result?.resultCode == RESULT_OK) { // 닉네임, 아바타, 목표 설정이 끝났을 때
                    }
                }
            }
            launcher = registerForActivityResult(contract, callback)
            user.check++ // 다음에 실행 안 하도록 증가
            // 닉네임 설정 액티비티 시작
            val intentN = Intent(this, SettingNameActivity::class.java)
            intentN.putExtra("email", "t@t.t")
            launcher?.launch(intentN)
        }

        val menuBottomNavigation = binding.menuBottomNavigation

        // 하단 탭 클릭 리스너 설정
        menuBottomNavigation.setOnItemSelectedListener { item ->
            changeFragment(
                when (item.itemId) {
                    R.id.menu_home -> { // 홈 탭 클릭
                        menuBottomNavigation.itemIconTintList = ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        menuBottomNavigation.itemTextColor = ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        MainHomeFragment()
                    }
                    R.id.menu_objective -> { // 목표 탭 클릭
                        menuBottomNavigation.itemIconTintList = ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        menuBottomNavigation.itemTextColor = ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        MainObjectiveFragment()
                    }
                    else -> { // 마이 탭 클릭
                        menuBottomNavigation.itemIconTintList = ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        menuBottomNavigation.itemTextColor = ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        MainMypageFragment()
                    }
                }
            )
            true
        }

        // 초기 화면 홈으로 설정
        menuBottomNavigation.selectedItemId = R.id.menu_home
    }

    // 프래그먼트 변경 함수
    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.menu_frame_layout, fragment)
            .commit()
    }

    // 클릭 시 프래그먼트 변경 함수
    @SuppressLint("ResourceType")
    fun clickchangeFragment(index: Int) {
        when(index) {
            1 -> { // 홈 프래그먼트에서 사용
                supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
                val menuBottomNavigation = binding.menuBottomNavigation
                menuBottomNavigation.selectedItemId = R.id.menu_objective
                menuBottomNavigation.itemIconTintList = ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                menuBottomNavigation.itemTextColor = ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.menu_frame_layout, MainObjectiveFragment())
                    .commit()
            }
            2 -> { // 목표 프래그먼트에서 사용
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.menu_frame_layout, MainDetailStudyFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    // 목표에서 선택한 이름을 저장하는 함수
    fun setSkill(name: String) {
        skill = name
    }

    // 상세 목표에서 선택한 이름을 가져오는 함수
    fun getSkill(): String {
        return skill
    }
}