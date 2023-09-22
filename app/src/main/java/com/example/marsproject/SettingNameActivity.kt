package com.example.marsproject

import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import org.json.JSONObject
import java.net.UnknownServiceException
import java.util.regex.Pattern

class SettingNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingNameBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private lateinit var email: String // 이메일
    private var checkName: String = "" // 닉네임 유효성 검사 체크 변수
    private var selectedImageUri: Uri? = null // 선택한 이미지 uri 변수

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            binding.userImage.setImageURI(uri)
            selectedImageUri = uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 액티비티 이동하면서 넘어온 값 받아오기
        email = intent.getStringExtra("email").toString()

        val contract = ActivityResultContracts.StartActivityForResult()
        val callback = object : ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult?) {
                if (result?.resultCode == RESULT_OK) {
                    val intentM = Intent()
                    setResult(RESULT_OK, intentM)
                    finish()
                } else {
                }
            }
        }
        launcher = registerForActivityResult(contract, callback)

        // 닉네임 설정 시 유효성 검사 필터 생성
        val filterAlphaNumSpace = InputFilter { source, start, end, dest, dstart, dend ->
            val ps = Pattern.compile("^[ㄱ-ㅣ가-힣a-zA-Z0-9]+$") // 한글, 영문, 숫자 가능
            // 다른 문자가 입력된다면 삭제
            if (!ps.matcher(source).matches()) {
                ""
            } else source
        }
        binding.editName.filters = arrayOf(filterAlphaNumSpace) // 닉네임 입력란 필터에 적용

        // 닉네임 입력란 텍스트 변경 리스너
        binding.editName.addTextChangedListener(object : TextWatcher {
            // 작성하고 있을 때
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            // 입력이 끝났을 때
            override fun afterTextChanged(p0: Editable?) {}
            // 입력란에 변화가 있을 때
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val charset = "euc-kr" // 인코딩 설정
                val length = binding.editName.text.toString().toByteArray(charset(charset)).size // 한글은 2 영문, 숫자는 1

                // 최대 길이 입력했을 때
                if (length == 12) {
                    checkName = binding.editName.text.toString() // 변수에 저장
                }

                // 최대 길이 초과 입력했을 때
                if (length > 12) {
                    binding.editName.setText(checkName) // 저장해둔 닉네임으로 변경
                    binding.editName.setSelection(binding.editName.length()) // 맨 끝으로 커서 위치 이동
                }
                checkName1() // 사용 가능한 닉네임인지 체크
                checkName2() // 닉네임이 중복인지 체크
            }
        })

        // 시작 버튼 클릭 리스너
        binding.startButton.setOnClickListener {
            // 사용 불가능한 닉네임일 때
            if (!checkName1()) {
                checkName2() // 닉네임이 중복인지 체크
                Toast.makeText(applicationContext, "이 이름은 사용할 수 없습니다.", Toast.LENGTH_SHORT).show() // 토스트 메시지 출력
                return@setOnClickListener // 리스너 종료
            }
            // 닉네임이 중복일 때
            if (!checkName2()) {
                checkName1() // 사용 가능한 닉네임인지 체크
                Toast.makeText(applicationContext, "이 이름은 현재 사용중입니다.", Toast.LENGTH_SHORT).show() // 토스트 메시지 출력
                return@setOnClickListener // 리스너 종료
            }

            val name = binding.editName.text.toString() // 닉네임 저장
            val profile = selectedImageUri?.toString() ?: "null" // 프로필 이미지 저장

            // 인텐트 생성 후 액티비티 생성
            val intentA = Intent(this, SettingAvatarActivity::class.java) // 아바타 설정 페이지로 설정
            intentA.putExtra("email", email) // 값 저장
            intentA.putExtra("profile", profile) // 값 저장
            intentA.putExtra("name", name) // 값 저장
            launcher?.launch(intentA) // 액티비티 생성
        }

        // 프로필 이미지 클릭 리스너
        binding.userImage.setOnClickListener {
            openGallery()
        }
    }

    // 갤러리 여는 함수
    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    // 뒤로가기 비활성화
    override fun onBackPressed() {}

    // 사용 가능한 닉네임인지 체크하는 함수
    fun checkName1(): Boolean {
        val name = binding.editName.text.toString().trim() // 닉네임 입력란 값 저장

        val charset = "euc-kr" // 인코딩 설정
        val length = name.toByteArray(charset(charset)).size // 한글은 2 영문, 숫자는 1

        val regex = "^[가-힣a-zA-Z0-9]*$".toRegex() // 표현식(완성된 한글, 영문, 숫자)
        // 길이가 4 ~ 12이며 표현식에 어긋나지 않을 때
        return if (length in 4..12 && regex.matches(name)) {
            binding.guideText1.setTextColor(Color.parseColor("#00ff00")) // 안내 문구 초록색으로 변경
            binding.guideText1.text = "v 한글 2~6자 입력가능" // 안내 문구 텍스트 변경
            true
        } else {
            binding.guideText1.setTextColor(Color.parseColor("#ff0000")) // 안내 문구 빨간색으로 변경
            binding.guideText1.text = "* 한글 2~6자 입력가능" // 안내 문구 텍스트 변경
            false
        }
    }

    // 닉네임이 중복인지 체크하는 함수
    fun checkName2(): Boolean {
        val name = binding.editName.text.toString().trim() // 닉네임 입력란 값 저장
        var result = "false" // false 저장

        // 닉네임 중복 체크 쓰레드 생성
        val checkThread = Thread {
            try {
                val outputjson = JSONObject() // JSON 생성
                outputjson.put("user_name", name) // 닉네임

                // 닉네임 중복 체크하기
                val jsonObject = Request().reqpost("http://dmumars.kro.kr/api/checkname", outputjson)
                // 중복이 아니면 true 저장
                result = jsonObject.getString("results")
            } catch (e: UnknownServiceException) {
                println(e.message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        checkThread.start() // 쓰레드 시작
        checkThread.join() // 쓰레드 종료될 때까지 대기

        // 닉네임이 중복이 아닐 때
        return if (result == "true") {
            binding.guideText2.setTextColor(Color.parseColor("#00ff00")) // 안내 문구 초록색으로 변경
            binding.guideText2.text = "v 중복확인" // 안내 문구 텍스트 변경
            true
        } else {
            binding.guideText2.setTextColor(Color.parseColor("#ff0000")) // 안내 문구 빨간색으로 변경
            binding.guideText2.text = "* 중복확인" // 안내 문구 텍스트 변경
            false
        }
    }
}
