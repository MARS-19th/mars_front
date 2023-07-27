package com.example.marsproject

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.marsproject.databinding.ActivitySettingNameBinding
import java.util.regex.Pattern

class SettingNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingNameBinding
    var launcher: ActivityResultLauncher<Intent>? = null
    private lateinit var email: String
    private var checkName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 액티비티 이동하면서 넘어온 값 받아오기
        email = intent.getStringExtra("email").toString()

        val contract = ActivityResultContracts.StartActivityForResult()
        val callback = object: ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult?) {
                if(result?.resultCode == RESULT_OK) {
                    //val intentR = result?.data
                    //val no = intentR?.getIntExtra("no", -1)
                    //val detail = intentR?.getStringExtra("detail")

                    // 완료 결과 보내기
                    val intentM = Intent()
                    setResult(RESULT_OK, intentM)
                    finish()
                } else {
                }
            }
        }
        launcher = registerForActivityResult(contract, callback)

        // 문자열 필터
        var filterAlphaNumSpace = InputFilter { source, start, end, dest, dstart, dend ->
            val ps = Pattern.compile("^[ㄱ-ㅣ가-힣a-zA-Z0-9]+$")
            if (!ps.matcher(source).matches()) {
                ""
            } else source
        }
        binding.editName.filters = arrayOf(filterAlphaNumSpace)

        // 에디트 텍스트 변경 리스너
        binding.editName.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 텍스트가 변경되기 전
            }

            override fun afterTextChanged(p0: Editable?) {
                // 텍스트가 변경된 후
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 텍스트가 변경될 때마다
                val charset = "euc-kr"
                val length = binding.editName.text.toString().toByteArray(charset(charset)).size

                if (length == 12) {
                    checkName = binding.editName.text.toString()
                }

                if (length > 12) {
                    binding.editName.setText(checkName)
                    binding.editName.setSelection(binding.editName.length())
                }
                checkName1()
                checkName2()
            }
        })

        binding.startButton.setOnClickListener{
            // 유효성 검사
            if(!checkName1()) {
                checkName2()
                Toast.makeText(applicationContext, "이 이름은 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(!checkName2()) {
                checkName1()
                Toast.makeText(applicationContext, "이 이름은 현재 사용중입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 닉네임 가져오기
            val name = binding.editName.text.toString()

            // 아바타 설정 액티비티 시작
            val intentA = Intent(this, SettingAvatarActivity::class.java)
            intentA.putExtra("email", email)
            intentA.putExtra("name", name)
            launcher?.launch(intentA)
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    // 한글 2~6자 입력가능 체크
    fun checkName1():Boolean{
        var name = binding.editName.text.toString().trim()

        val charset = "euc-kr"
        val length = name.toByteArray(charset(charset)).size

        val regex = "^[가-힣a-zA-Z0-9]*$".toRegex()
        if (length in 4..12 && regex.matches(name)) {
            binding.guideText1.setTextColor(Color.parseColor("#00ff00"))
            binding.guideText1.text = "v 한글 2~6자 입력가능"
            return true
        } else {
            binding.guideText1.setTextColor(Color.parseColor("#ff0000"))
            binding.guideText1.text = "* 한글 2~6자 입력가능"
            return false
        }
    }

    // 중복확인
    fun checkName2():Boolean{
        var name = binding.editName.text.toString().trim()
        val testName = "테스트"
        // 추후에 db 중복 확인
        if (name != testName) {
            binding.guideText2.setTextColor(Color.parseColor("#00ff00"))
            binding.guideText2.text = "v 중복확인"
            return true
        } else {
            binding.guideText2.setTextColor(Color.parseColor("#ff0000"))
            binding.guideText2.text = "* 중복확인"
            return false
        }
    }
}