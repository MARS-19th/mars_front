package com.example.marsproject

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marsproject.databinding.ActivitySettingAvatarBinding
import org.json.JSONObject
import java.net.UnknownServiceException

class SettingAvatarActivity : AppCompatActivity(), EquipmentAdapter.OnItemClickListener {
    private lateinit var binding: ActivitySettingAvatarBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private lateinit var email: String // 이메일
    private lateinit var profile: String // 프로필
    private lateinit var name: String // 닉네임

    private var animal: String = "cat" // 동물 종류 (기본값 - 고양이)
    private var equipmentAdapter: EquipmentAdapter? = null
    private var equipmentItems: MutableList<EquipmentItem>? = null

    private var face: String = "emo1" // 얼굴 (기본값 - 1번째)
    private var appearance: String = "cat1" // 외형 (기본값 - 1번째)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingAvatarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar) // 툴바 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(com.example.marsproject.R.drawable.icon_left_resize) // 뒤로가기 버튼 이미지 설정
        supportActionBar?.title = "아바타 설정" // 타이틀 지정

        // 액티비티 이동하면서 넘어온 값 받아오기
        email = intent.getStringExtra("email").toString() // 이메일
        profile = intent.getStringExtra("profile").toString() // 프로필
        name = intent.getStringExtra("name").toString() // 닉네임

        val contract = ActivityResultContracts.StartActivityForResult()
        val callback = object: ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult?) {
                if(result?.resultCode == RESULT_OK) {
                    // 완료 결과 보내기
                    val intentN = Intent()
                    setResult(RESULT_OK, intentN)
                    finish()
                }
            }
        }
        launcher = registerForActivityResult(contract, callback)

        // 기본 이미지를 고양이 아바타로 설정
        binding.avatarImage.setImageResource(com.example.marsproject.R.drawable.set_cat1_emo1)

        val equipmentLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 3)
        binding.equipmentRecyclerView.layoutManager = equipmentLayoutManager

        equipmentItems = mutableListOf()
        equipmentAdapter = EquipmentAdapter(equipmentItems as MutableList<EquipmentItem>, this) // 리스너로 'this'를 전달합니다.
        binding.equipmentRecyclerView.adapter = equipmentAdapter // RecyclerView에 어댑터를 설정합니다.

        // 표정 탭 초기화
        updateEquipmentItemsForTops()
    }


    fun getName(): String {
        val pref = getSharedPreferences("userName", 0)
        return pref.getString("name", "").toString()
    }


    override fun onItemClick(expression: String, expressionAppearance: String) {
        when (expression) {
            "테스트 표정 1" -> face = "emo1"
            "테스트 표정 2" -> face = "emo2"
            "테스트 표정 3" -> face = "emo3"
            "테스트 표정 4" -> face = "emo4"
            "테스트 표정 5" -> face = "emo5"
            // 필요한 경우 나머지 표정에 대한 케이스를 추가합니다.
        }

        when (expressionAppearance) {
            "테스트 외형 1" -> appearance = "cat1"
            "테스트 외형 2" -> appearance = "cat2"
            "테스트 외형 3" -> appearance = "cat3"
            // 필요한 경우 나머지 외형에 대한 케이스를 추가합니다.
        }

        // 로그로 마지막으로 선택한 표정과 외형 값을 출력
        Log.d("AvatarSelection", "마지막으로 선택한 표정: $face, 외형: $appearance")

        // 아이템 클릭과 관련된 다른 메서드나 작업들을 이곳에서 처리할 수 있습니다.

        // 기본 이미지를 appearance와 face 값에 따라 설정
        val resourceId = resources.getIdentifier("set_${appearance}_$face", "drawable", packageName)
        val defaultImage = ContextCompat.getDrawable(this, resourceId)
        binding.avatarImage.setImageDrawable(defaultImage)

        Thread {
            try {
                val outputjson = JSONObject()
                outputjson.put("user_name", getName())
                outputjson.put("type", "cat")
                outputjson.put("look", "$face")
                outputjson.put("color", "$appearance")

                // 서버 디비에 설정 아바타 정보 저장
                val response = Request().reqpost("http://dmumars.kro.kr/api/setuseravatar", outputjson)
            } catch (e: UnknownServiceException) {
                println(e.message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }


    // 상단 표정 선택
    fun onTopsButtonClick(view: View?) {
        updateEquipmentItemsForTops() // 아이템 그리기
        binding.topsView.setBackgroundColor(Color.parseColor("#FF9C46")) // 표정 밑줄 색상 변경
        binding.bottomsView.setBackgroundColor(Color.parseColor("#DDDDDD")) // 표정 밑줄 색상 변경
    }

    // 하단 외형 선택
    fun onBottomsButtonClick(view: View?) {
        updateEquipmentItemsForBottoms() // 아이템 그리기
        binding.topsView.setBackgroundColor(Color.parseColor("#DDDDDD")) // 표정 밑줄 색상 변경
        binding.bottomsView.setBackgroundColor(Color.parseColor("#FF9C46")) // 표정 밑줄 색상 변경
    }

    // 툴바에 옵션 메뉴 생성
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        binding.toolbar.inflateMenu(com.example.marsproject.R.menu.toolbar_menu1) // 다음 버튼 생성
        return true
    }

    // 옵션 메뉴 클릭 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.home -> { // 뒤로 가기 버튼 눌렀을 때
                finish() // 액티비티 종료
            }
            com.example.marsproject.R.id.action_next -> { // 다음 버튼 눌렀을 때
                // 인텐트 생성 후 액티비티 생성
                val intentO = Intent(this, SettingObjectiveActivity::class.java) // 목표 설정 페이지로 설정
                intentO.putExtra("email", email) // 이메일
                intentO.putExtra("profile", profile) // 프로필
                intentO.putExtra("name", name) // 닉네임
                intentO.putExtra("animal", animal) // 동물 종류
                intentO.putExtra("face", face) // 선택한 표정 값
                intentO.putExtra("appearance", appearance) // 선택한 외형 값
                launcher?.launch(intentO) // 액티비티 생성
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun updateEquipmentItemsForTops() {
        equipmentItems?.clear()

        val expressions = arrayOf(
            "테스트 표정 1",
            "테스트 표정 2",
            "테스트 표정 3",
            "테스트 표정 4",
            "테스트 표정 5"
        )

        for (i in 0 until expressions.size) {
            ContextCompat.getDrawable(this, resources.getIdentifier("emo${i + 1}", "drawable", packageName))?.let { drawable ->
                val expressionName = expressions[i]
                val equipmentItem = EquipmentItem(expressionName, drawable, appearance) // appearance 값을 넣도록 수정
                equipmentItems?.add(equipmentItem)
            }
        }

        equipmentAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateEquipmentItemsForBottoms() {
        equipmentItems?.clear()

        val equipmentNames = arrayOf(
            "테스트 외형 1",
            "테스트 외형 2",
            "테스트 외형 3"
        )

        for (i in 0 until equipmentNames.size) {
            ContextCompat.getDrawable(this, resources.getIdentifier("cat${i + 1}", "drawable", packageName))?.let { drawable ->
                val equipmentName = equipmentNames[i]
                val equipmentItem = EquipmentItem(equipmentName, drawable, equipmentName) // 선택한 외형 값으로 수정
                equipmentItems?.add(equipmentItem)
            }
        }

        equipmentAdapter?.notifyDataSetChanged()
    }

}