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
    var launcher: ActivityResultLauncher<Intent>? = null
    private lateinit var email: String
    private lateinit var name: String

    private var animal: String = "cat"
    private var equipmentAdapter: EquipmentAdapter? = null
    private var equipmentItems: MutableList<EquipmentItem>? = null

    private var selectedExpression: String = "테스트 표정 1" // 기본 표정 값
    private var face: String = "테스트 표정 1"
    private var appearance: String = "테스트 외형 1"

    // 항목을 클릭하여 선택된 표정을 업데이트하는 함수
    override fun onItemClick(expression: String) {
        selectedExpression = expression
        // 여기에서 아이템 클릭 이벤트를 처리하고 필요에 따라 'face' 변수를 업데이트합니다.
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
        // 아이템 클릭과 관련된 다른 메서드나 작업들을 이곳에서 처리할 수 있습니다.
    }


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
        name = intent.getStringExtra("name").toString()

        val contract = ActivityResultContracts.StartActivityForResult()
        val callback = object: ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult?) {
                if(result?.resultCode == RESULT_OK) {
                    //val intentR = result?.data
                    //val no = intentR?.getIntExtra("no", -1)
                    //val detail = intentR?.getStringExtra("detail")

                    // 완료 결과 보내기
                    val intentN = Intent()
                    setResult(RESULT_OK, intentN)
                    finish()
                } else {
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

        // 초기에 "상의"에 해당하는 장비 아이템들을 설정합니다.
        updateEquipmentItemsForTops()
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
                val equipmentItem = EquipmentItem(expressionName, drawable)
                equipmentItems?.add(equipmentItem)

//                // 현재 선택된 표정이면 face 변수에 해당 인덱스를 저장
//                if (selectedExpression == expressionName) {
//                    face = i + 1
//                }
            }
        }

        equipmentAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateEquipmentItemsForBottoms() {
        equipmentItems?.clear()
        // Here you can add equipment items for "하의"
        // Add more items as needed
        ContextCompat.getDrawable(this, com.example.marsproject.R.drawable.list)?.let {
            EquipmentItem(
                "테스트 외형 1",
                it
            )
        }?.let {
            equipmentItems?.add(
                it
            )
        }
        ContextCompat.getDrawable(this, com.example.marsproject.R.drawable.list)?.let {
            EquipmentItem(
                "테스트 외형 2",
                it
            )
        }?.let {
            equipmentItems?.add(
                it
            )
        }
        ContextCompat.getDrawable(this, com.example.marsproject.R.drawable.list)?.let {
            EquipmentItem(
                "테스트 외형 3",
                it
            )
        }?.let {
            equipmentItems?.add(
                it
            )
        }
        equipmentAdapter?.notifyDataSetChanged()
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

    fun onTopsButtonClick(view: View?) {
        updateEquipmentItemsForTops()
        binding.topsButton.setImageResource(com.example.marsproject.R.drawable.icon_face)
        binding.bottomsButton.setImageResource(com.example.marsproject.R.drawable.icon_color2)
    }

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
                intentO.putExtra("name", name)
                intentO.putExtra("animal", animal)
                intentO.putExtra("face", face)
                intentO.putExtra("color", "색상1")
                launcher?.launch(intentO)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}