package com.example.marsproject

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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

class SettingAvatarActivity : AppCompatActivity(), EquipmentAdapter.OnItemClickListener {
    private lateinit var binding: ActivitySettingAvatarBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private lateinit var email: String
    private lateinit var profile: String
    private lateinit var name: String

    private var animal: String = "cat"
    private var equipmentAdapter: EquipmentAdapter? = null
    private var equipmentItems: MutableList<EquipmentItem>? = null

    private var face: String = "테스트 표정 1"
    private var appearance: String = "테스트 외형 1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingAvatarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(com.example.marsproject.R.drawable.icon_left_resize)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 액티비티 이동하면서 넘어온 값 받아오기
        email = intent.getStringExtra("email").toString()
        profile = intent.getStringExtra("profile").toString()
        name = intent.getStringExtra("name").toString()

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

        binding.avatarImageView.setImageResource(com.example.marsproject.R.drawable.cat_avatar)

        val equipmentLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 3)
        binding.equipmentRecyclerView.layoutManager = equipmentLayoutManager

        equipmentItems = mutableListOf()
        equipmentAdapter = EquipmentAdapter(equipmentItems as MutableList<EquipmentItem>, this) // 리스너로 'this'를 전달합니다.
        binding.equipmentRecyclerView.adapter = equipmentAdapter // RecyclerView에 어댑터를 설정합니다.

        // 표정 탭 초기화
        updateEquipmentItemsForTops()
    }

    // 항목을 클릭하여 선택된 표정을 업데이트하는 함수
    override fun onItemClick(expression: String, expressionAppearance: String) {
        when (expression) {
            "테스트 표정 1" -> face = "테스트 표정 1"
            "테스트 표정 2" -> face = "테스트 표정 2"
            "테스트 표정 3" -> face = "테스트 표정 3"
            "테스트 표정 4" -> face = "테스트 표정 4"
            "테스트 표정 5" -> face = "테스트 표정 5"
            "테스트 표정 6" -> face = "테스트 표정 6"
            "테스트 표정 7" -> face = "테스트 표정 7"
            "테스트 표정 8" -> face = "테스트 표정 8"
            "테스트 표정 9" -> face = "테스트 표정 9"
            // 필요한 경우 나머지 표정에 대한 케이스를 추가합니다.
        }

        when (expressionAppearance) {
            "테스트 외형 1" -> appearance = "테스트 외형 1"
            "테스트 외형 2" -> appearance = "테스트 외형 2"
            "테스트 외형 3" -> appearance = "테스트 외형 3"
            "테스트 외형 4" -> appearance = "테스트 외형 4"
            "테스트 외형 5" -> appearance = "테스트 외형 5"
            "테스트 외형 6" -> appearance = "테스트 외형 6"
            "테스트 외형 7" -> appearance = "테스트 외형 7"
            "테스트 외형 8" -> appearance = "테스트 외형 8"
            "테스트 외형 9" -> appearance = "테스트 외형 9"
            // 필요한 경우 나머지 표정에 대한 케이스를 추가합니다.
        }

        // 아이템 클릭과 관련된 다른 메서드나 작업들을 이곳에서 처리할 수 있습니다.
    }

    // 고양이 클릭 아바타 이미지 변경
    fun onCatButtonClick(view: View?) {
        binding.avatarImageView.setImageResource(com.example.marsproject.R.drawable.cat_avatar)
        binding.catButton.setBackgroundResource(com.example.marsproject.R.drawable.avatar_button_clicked)
        binding.monkeyButton.setBackgroundResource(com.example.marsproject.R.drawable.avatar_button_background)
        animal = "cat"
    }

    // 원숭이 클릭 아바타 이미지 변경
    fun onMonkeyButtonClick(view: View?) {
        binding.avatarImageView.setImageResource(com.example.marsproject.R.drawable.monkey_avatar)
        binding.monkeyButton.setBackgroundResource(com.example.marsproject.R.drawable.avatar_button_clicked)
        binding.catButton.setBackgroundResource(com.example.marsproject.R.drawable.avatar_button_background)
        animal = "monkey"
    }

    // 상단 표정 선택
    fun onTopsButtonClick(view: View?) {
        updateEquipmentItemsForTops()
        binding.topsButton.setImageResource(com.example.marsproject.R.drawable.icon_face)
        binding.bottomsButton.setImageResource(com.example.marsproject.R.drawable.icon_color2)
    }

    // 하단 외형 선택
    fun onBottomsButtonClick(view: View?) {
        updateEquipmentItemsForBottoms()
        binding.topsButton.setImageResource(com.example.marsproject.R.drawable.icon_face2)
        binding.bottomsButton.setImageResource(com.example.marsproject.R.drawable.icon_color)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        binding.toolbar.inflateMenu(com.example.marsproject.R.menu.toolbar_menu1)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.home -> { // 뒤로 가기 버튼 눌렀을 때
                finish()
            }
            com.example.marsproject.R.id.action_next -> { // 다음 버튼 눌렀을 때
                // 목표 설정 액티비티 시작
                val intentO = Intent(this, SettingObjectiveActivity::class.java)
                intentO.putExtra("email", email)
                intentO.putExtra("profile", profile)
                intentO.putExtra("name", name)
                intentO.putExtra("animal", animal)
                intentO.putExtra("face", face) // 선택한 표정 값
                intentO.putExtra("appearance", appearance) // 선택한 외형 값
                launcher?.launch(intentO)
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
            "테스트 표정 5",
            "테스트 표정 6",
            "테스트 표정 7",
            "테스트 표정 8",
            "테스트 표정 9"
        )

        for (i in 0 until expressions.size) {
            ContextCompat.getDrawable(this, resources.getIdentifier("f${i + 1}", "drawable", packageName))?.let { drawable ->
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
            "테스트 외형 3",
            "테스트 외형 4",
            "테스트 외형 5",
            "테스트 외형 6",
            "테스트 외형 7",
            "테스트 외형 8",
            "테스트 외형 9"
        )

        for (i in equipmentNames.indices) {
            ContextCompat.getDrawable(this, com.example.marsproject.R.drawable.list)?.let { drawable ->
                val equipmentName = equipmentNames[i]
                val equipmentItem = EquipmentItem(equipmentName, drawable, equipmentName) // 선택한 외형 값으로 수정
                equipmentItems?.add(equipmentItem)
            }
        }

        equipmentAdapter?.notifyDataSetChanged()
    }

}
