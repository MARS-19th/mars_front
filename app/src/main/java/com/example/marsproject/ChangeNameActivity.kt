package com.example.marsproject

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.marsproject.databinding.ActivityChangeNameBinding
import org.json.JSONObject
import java.net.UnknownServiceException
import java.util.regex.Pattern

class ChangeNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeNameBinding
    private lateinit var savedname: String // 저장된 닉네임
    private var checkName: String = "" // 닉네임 유효성 검사 체크 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 닉네임 정보 불러오기
        savedname = getName()

        // 툴바 설정
        setSupportActionBar(binding.toolbar) // 툴바 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize) // 뒤로가기 버튼 이미지 설정
        supportActionBar?.title = "닉네임 변경" // 타이틀 지정

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
        binding.changeButton.setOnClickListener {
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

            val dlg = MyDialog(this) // 커스텀 다이얼로그 객체 저장
            // 예 버튼 클릭 시 실행
            dlg.setOnOKClickedListener{
                println(1)
                val name = binding.editName.text.toString() // 닉네임 저장

                // 닉네임 변경 쓰레드 생성
                val changeThread = Thread {
                    try {
                        // 기존 닉네임과 바꿀 닉네임을 보내서 변경
                        val changenamejson = JSONObject() //json 생성
                        changenamejson.put("curname", savedname) // 기존 닉네임
                        changenamejson.put("newname", name) // 바꿀 닉네임

                        // 닉네임 변경
                        Request().reqpost("http://dmumars.kro.kr/api/setname", changenamejson)

                    } catch (e: UnknownServiceException) {
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                changeThread.start() // 쓰레드 실행
                changeThread.join() // 쓰레드 종료될 때까지 대기

                clearName() // 기존 닉네임 정보 삭제
                saveName(name) // 바뀐 닉네임 정보 저장

                // 완료 결과 보내기
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish() // 액티비티 종료
            }
            // 아니오 버튼 클릭 시 실행
            dlg.setOnNOClickedListener {}
            dlg.show("닉네임을 변경하시겠습니까?") // 다이얼로그 내용에 담을 텍스트

        }
    }

    // 사용 가능한 닉네임인지 체크하는 함수
    fun checkName1(): Boolean {
        val name = binding.editName.text.toString().trim() // 닉네임 입력란 값 저장

        val charset = "euc-kr" // 인코딩 설정
        val length = name.toByteArray(charset(charset)).size // 한글은 2 영문, 숫자는 1

        val regex = "^[가-힣a-zA-Z0-9]*$".toRegex() // 표현식(완성된 한글, 영문, 숫자)
        // 길이가 4 ~ 12이며 표현식에 어긋나지 않을 때
        return if (length in 4..12 && regex.matches(name)) {
            binding.guideText1.setTextColor(Color.parseColor("#FF9C46")) // 안내 문구 주황색으로 변경
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
            binding.guideText2.setTextColor(Color.parseColor("#FF9C46")) // 안내 문구 주황색으로 변경
            binding.guideText2.text = "v 중복확인" // 안내 문구 텍스트 변경
            true
        } else {
            binding.guideText2.setTextColor(Color.parseColor("#ff0000")) // 안내 문구 빨간색으로 변경
            binding.guideText2.text = "* 중복확인" // 안내 문구 텍스트 변경
            false
        }
    }

    // 닉네임 정보를 저장하는 함수
    fun saveName(userName: String) {
        val pref = getSharedPreferences("userName", MODE_PRIVATE) //shared key 설정
        val edit = pref.edit() // 수정모드
        edit.putString("name", userName) // 값 넣기
        edit.apply() // 적용하기
    }

    fun clearName() {
        val pref = getSharedPreferences("userName", MODE_PRIVATE) //shared key 설정
        val edit = pref.edit() // 수정모드
        edit.clear() // 삭제하기
        edit.apply() // 적용하기
    }

    // 닉네임 정보를 가져오는 함수
    fun getName(): String {
        val pref = getSharedPreferences("userName", 0)
        return pref.getString("name", "").toString()
    }

    // 옵션 메뉴 클릭 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> { // 뒤로 가기 버튼 눌렀을 때
                finish() // 액티비티 종료
            }
        }
        return super.onOptionsItemSelected(item)
    }
}