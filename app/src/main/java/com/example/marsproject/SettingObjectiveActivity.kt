package com.example.marsproject

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.marsproject.databinding.ActivitySettingObjectiveBinding


class SettingObjectiveActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingObjectiveBinding
    var launcher: ActivityResultLauncher<Intent>? = null
    private lateinit var email: String
    private lateinit var name: String
    private lateinit var animal: String
    private lateinit var face: String
    private lateinit var color: String
    private var category: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingObjectiveBinding.inflate(layoutInflater)
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

        val contract = ActivityResultContracts.StartActivityForResult()
        val callback = object: ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult?) {
                if(result?.resultCode == RESULT_OK) {
                    //val intentR = result?.data
                    //val no = intentR?.getIntExtra("no", -1)
                    //val detail = intentR?.getStringExtra("detail")

                    // 완료 결과 보내기
                    val intentA = Intent()
                    setResult(RESULT_OK, intentA)
                    finish()
                } else {
                }
            }
        }
        launcher = registerForActivityResult(contract, callback)

        val clkListener = View.OnClickListener { p0 ->
            when(p0?.id) {
                com.example.marsproject.R.id.studyButton -> {
                    binding.studyButton.setBackgroundResource(com.example.marsproject.R.drawable.button_clicked)
                    binding.exerciseButton.setBackgroundResource(com.example.marsproject.R.drawable.button_background)
                    category = "공부"
                }
                com.example.marsproject.R.id.exerciseButton -> {
                    binding.studyButton.setBackgroundResource(com.example.marsproject.R.drawable.button_background)
                    binding.exerciseButton.setBackgroundResource(com.example.marsproject.R.drawable.button_clicked)
                    category = "운동"
                }
            }
        }
        binding.studyButton.setOnClickListener(clkListener)
        binding.exerciseButton.setOnClickListener(clkListener)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(com.example.marsproject.R.menu.toolbar_menu1, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.home -> { // 뒤로 가기 버튼 눌렀을 때
                finish()
            }
            com.example.marsproject.R.id.action_next -> { // 다음 버튼 눌렀을 때
                // 목표 상세 설정 액티비티 시작
                if(category == "") {
                    Toast.makeText(baseContext, "하나를 선택해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    val intentD = Intent(this, SettingDetailObjectiveActivity::class.java)
                    intentD.putExtra("email", email)
                    intentD.putExtra("name", name)
                    intentD.putExtra("animal", animal)
                    intentD.putExtra("face", face)
                    intentD.putExtra("color", color)
                    intentD.putExtra("category", category)
                    launcher?.launch(intentD)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}