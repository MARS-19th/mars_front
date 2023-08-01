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
    private lateinit var email: String
    private var checkName: String = ""
    private var selectedImageUri: Uri? = null

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

        val filterAlphaNumSpace = InputFilter { source, start, end, dest, dstart, dend ->
            val ps = Pattern.compile("^[ㄱ-ㅣ가-힣a-zA-Z0-9]+$")
            if (!ps.matcher(source).matches()) {
                ""
            } else source
        }
        binding.editName.filters = arrayOf(filterAlphaNumSpace)

        binding.editName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
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

        binding.startButton.setOnClickListener {
            if (!checkName1()) {
                checkName2()
                Toast.makeText(applicationContext, "이 이름은 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!checkName2()) {
                checkName1()
                Toast.makeText(applicationContext, "이 이름은 현재 사용중입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val name = binding.editName.text.toString()
            val profile = selectedImageUri?.toString() ?: "null"

            val intentA = Intent(this, SettingAvatarActivity::class.java)
            intentA.putExtra("email", email)
            intentA.putExtra("profile", profile)
            intentA.putExtra("name", name)
            launcher?.launch(intentA)
        }

        binding.userImage.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    override fun onBackPressed() {}

    fun checkName1(): Boolean {
        val name = binding.editName.text.toString().trim()

        val charset = "euc-kr"
        val length = name.toByteArray(charset(charset)).size

        val regex = "^[가-힣a-zA-Z0-9]*$".toRegex()
        return if (length in 4..12 && regex.matches(name)) {
            binding.guideText1.setTextColor(Color.parseColor("#00ff00"))
            binding.guideText1.text = "v 한글 2~6자 입력가능"
            true
        } else {
            binding.guideText1.setTextColor(Color.parseColor("#ff0000"))
            binding.guideText1.text = "* 한글 2~6자 입력가능"
            false
        }
    }

    fun checkName2(): Boolean {
        val name = binding.editName.text.toString().trim()
        var result = "false"

        val checkThread = Thread {
            try {
                val outputjson = JSONObject()
                outputjson.put("user_name", name)

                val jsonObject = Request().reqpost("http://dmumars.kro.kr/api/checkname", outputjson)
                result = jsonObject.getString("results")
            } catch (e: UnknownServiceException) {
                println(e.message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        checkThread.start()
        checkThread.join()

        return if (result == "true") {
            binding.guideText2.setTextColor(Color.parseColor("#00ff00"))
            binding.guideText2.text = "v 중복확인"
            true
        } else {
            binding.guideText2.setTextColor(Color.parseColor("#ff0000"))
            binding.guideText2.text = "* 중복확인"
            false
        }
    }
}
