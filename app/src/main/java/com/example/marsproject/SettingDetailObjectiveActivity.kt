package com.example.marsproject

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.example.marsproject.databinding.ActivitySettingDetailObjectiveBinding

class SettingDetailObjectiveActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingDetailObjectiveBinding
    var launcher: ActivityResultLauncher<Intent>? = null
    private lateinit var email: String
    private lateinit var name: String
    private lateinit var animal: String
    private lateinit var face: String
    private lateinit var color: String
    private lateinit var category: String
    private var objective: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingDetailObjectiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(com.example.marsproject.R.drawable.icon_left_resize)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 액티비티 이동하면서 넘어온 값 받아오기
        email = intent.getStringExtra("email").toString()
        name = intent.getStringExtra("name").toString()
        animal = intent.getStringExtra("animal").toString()
        face = intent.getStringExtra("face").toString()
        color = intent.getStringExtra("color").toString()
        category = intent.getStringExtra("category").toString()

        if(category == "공부") {
            val clkListener = View.OnClickListener { p0 ->
                when(p0?.id) {
                    com.example.marsproject.R.id.objectiveButton -> {
                        binding.objectiveButton.setBackgroundResource(com.example.marsproject.R.drawable.button_clicked)
                        objective = "프로그래밍"
                    }
                }
            }
            binding.objectiveButton.setOnClickListener(clkListener)
        } else {
            binding.objectiveButton.text = "등산"
            binding.objectiveButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, com.example.marsproject.R.drawable.climb_resize)
            val clkListener = View.OnClickListener { p0 ->
                when(p0?.id) {
                    com.example.marsproject.R.id.objectiveButton -> {
                        binding.objectiveButton.setBackgroundResource(com.example.marsproject.R.drawable.button_clicked)
                        objective = "등산"
                    }
                }
            }
            binding.objectiveButton.setOnClickListener(clkListener)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        binding.toolbar.inflateMenu(com.example.marsproject.R.menu.toolbar_menu2)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.home -> { // 뒤로 가기 버튼 눌렀을 때
                finish()
            }
            com.example.marsproject.R.id.action_ok -> { // 완료 버튼 눌렀을 때
                if(objective == "") {
                    Toast.makeText(baseContext, "하나를 선택해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    // api를 통해서 db에 저장하는 구문 넣기
                    var msg = "$email, $name, $animal, $face\n$color, $category, $objective"
                    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()

                    // 완료 결과 보내기
                    val intentO = Intent()
                    setResult(RESULT_OK, intentO)
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}