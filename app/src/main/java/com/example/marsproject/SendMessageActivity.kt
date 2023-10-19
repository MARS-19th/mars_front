package com.example.marsproject

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.marsproject.databinding.ActivitySendMessageBinding
import com.google.android.material.internal.ToolbarUtils
import org.json.JSONException
import org.json.JSONObject
import java.net.UnknownServiceException

class SendMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySendMessageBinding

    private lateinit var type: String // type 변수 선언
    private lateinit var face: String // face 변수 선언
    private lateinit var appearance: String // appearance 변수 선언
    private lateinit var moun_shop: String // moun_shop 변수 선언
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar) // 툴바 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize) // 뒤로가기 버튼 이미지 설정
        supportActionBar?.title = "친구프로필" // 타이틀 지정

        // 이전 액티비티에서 전달된 데이터 추출
        val nickname = intent.getStringExtra("nickname")
        val title = intent.getStringExtra("title")

        binding.friendName.text = nickname!!.toString()

        // 버튼에 대한 클릭 리스너 설정
        binding.sendMsg1.setOnClickListener {
            sendMessage(nickname, "지금 뭐해?")
            Log.d("sdjgksgdjdks", "메세지 보내기")
        }

        binding.sendMsg2.setOnClickListener {
            sendMessage(nickname,"공부해라")
        }

        binding.sendMsg3.setOnClickListener {
            sendMessage(nickname,"안녕!!")
        }

        binding.sendMsg4.setOnClickListener {
            sendMessage(nickname,"열공해!!")
        }


        //아바타 이미지 가져오기
        Thread {
            try {
                val jsonObject = Request().reqget("http://dmumars.kro.kr/api/getuseravatar/${nickname}") // GET 요청


                    //type = jsonObject.getString("type")
                    face = jsonObject.getString("look")
                    appearance = jsonObject.getString("color")
                    moun_shop = jsonObject.getString("moun_shop") //장비 장착시 이미지 변경 하도록 구현해야함

                runOnUiThread {
                    // UI 업데이트 및 Glide를 사용하여 이미지 로드
                    val avatarimg = "set_${appearance}_${face}"
                    Log.d("AvatarImage", "Avatar Image: $avatarimg")

                    val avatarResourceId = resources.getIdentifier(avatarimg, "drawable", packageName)
                    val avatarImageView = findViewById<ImageView>(R.id.friendimage)

                    Glide.with(this@SendMessageActivity)
                        .load(avatarResourceId)
                        .placeholder(Color.parseColor("#00000000"))
                        .error(R.drawable.profileimage)
                        .skipMemoryCache(true)
                        .into(avatarImageView)

                    // 나머지 UI 업데이트
                    binding.friendName.text = nickname
                    binding.friendtitle.text = title
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                // JSON 파싱 오류에 대한 처리
            }
        }.start()

    }

    fun sendMessage(nickname: String, message : String) {
        Log.d("SDSAFGDADG", "sendMessage 함수 실행")
        val sendMessageThread = Thread {
            try {
                // 다른유저에게 메세지 전송
                val sendUserMsg = JSONObject() //json 생성
                sendUserMsg.put("user_name", getName())
                sendUserMsg.put("from_user", nickname)
                sendUserMsg.put("messge", message)

                // 유저에게 메세지 전송
                Request().reqpost("http://dmumars.kro.kr/api/pushuserchat", sendUserMsg)

                Log.d("SDSAFGDADG", "메세지 보냄")
                runOnUiThread {
                    Toast.makeText(applicationContext,"${nickname}에게 메세지를 보냈습니다.", Toast.LENGTH_LONG).show()
                }

            } catch (e: UnknownServiceException) {
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        sendMessageThread.start()
    }

    // 닉네임 정보를 가져오는 함수
    fun getName(): String {
        val pref = getSharedPreferences("userName", 0)
        return pref.getString("name", "").toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> { // 뒤로 가기 버튼 눌렀을 때
                finish() // 액티비티 종료
            }
        }
        return super.onOptionsItemSelected(item)
    }
}