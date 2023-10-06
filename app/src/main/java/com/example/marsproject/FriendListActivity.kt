package com.example.marsproject

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marsproject.databinding.ActivityFriendListBinding
import org.json.JSONObject
import java.net.UnknownServiceException

class FriendListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendListBinding
    private lateinit var friendListAdapter: FriendListAdapter
    private lateinit var friendRequestAdapter: FriendRequestAdapter
    private var friendList = mutableListOf<String>() // 친구 목록을 저장할 리스트
    private var friendRequestList = mutableListOf<String>() // 친구 신청 목록을 저장할 리스트

    // 검색해야 되는 유저의 데이터를 가져오기 위해 사용하는 변수
    private var name: String = "닉네임" // 닉네임
    private var id: String = "아이디" // 아이디
    private var title: String = "칭호" // 칭호
    private lateinit var profile: String // 프로필
    private lateinit var level: String // 레벨

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize)
        supportActionBar?.title = "친구"

        // 텍스트뷰 변수
        val friendListTextView = findViewById<TextView>(R.id.friendListButton)
        val friendRequestTextView = findViewById<TextView>(R.id.friendRequestButton)

        // 친구 목록 텍스트뷰를 초기에 선택된 상태로 설정
        friendListTextView.setTextColor(Color.parseColor("#FF9C46"))

        // 친구목록 텍스트뷰를 누를 때
        friendListTextView.setOnClickListener {
            friendListTextView.setTextColor(Color.parseColor("#FF9C46")) // 친구목록 텍스트뷰 선택
            friendRequestTextView.setTextColor(Color.GRAY) // 친구신청 텍스트뷰는 기본 색상으로 변경

            // 친구 목록 리사이클러뷰를 업데이트
            binding.friendRecyclerView.adapter = friendListAdapter
        }

        // 친구신청 텍스트뷰를 누를 때
        friendRequestTextView.setOnClickListener {
            friendListTextView.setTextColor(Color.GRAY) // 친구목록 텍스트뷰는 기본 색상으로 변경
            friendRequestTextView.setTextColor(Color.parseColor("#FF9C46")) // 친구신청 텍스트뷰 선택

            // 친구 신청 리사이클러뷰를 업데이트
            binding.friendRecyclerView.adapter = friendRequestAdapter
        }

        // RecyclerView 설정
        friendListAdapter = FriendListAdapter(friendList) // 기본값은 친구 목록
        friendRequestAdapter = FriendRequestAdapter(friendRequestList)

        binding.friendRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.friendRecyclerView.adapter = friendListAdapter

        // 검색 버튼 클릭 이벤트 처리
        binding.searchButton.setOnClickListener {
            val friendCode = binding.friendCodeEditText.text.toString()
            if (friendCode.isNotEmpty()) {
                // 여기에서 실제 친구를 검색하고 결과를 friendList에 추가하는 로직을 구현
                val isFriendFound = searchFriend(friendCode)
                if (!isFriendFound) {
                    // 검색 결과가 없는 경우 메시지를 표시
                    friendList.clear()
                    friendListAdapter.notifyDataSetChanged() // RecyclerView 갱신
                }
                binding.friendCodeEditText.text.clear()
            }
        }

        // 초기 친구 목록을 추가
        addInitialFriends()
    }

    //닉네임 값 가져오기
    private fun getName(): String {
        val pref = getSharedPreferences("userName", 0)
        return pref.getString("name", "").toString()
    }

    private fun searchFriend(friendCode: String): Boolean {
        // 여기에서 실제 친구 검색 로직을 구현
        // 검색 결과가 있으면 friendList에 추가하고 true를 반환하고,
        // 검색 결과가 없으면 false를 반환
        // 실제 데이터 검색 및 추가 로직을 구현

        // 검색할 URL을 생성
        val searchUrl = "http://dmumars.kro.kr/api/getuserdata/${friendCode}"
        println("에디트 텍스트 값 : $friendCode")
        // 검색할 URL로 요청을 보내고 결과를 처리하는 코드를 추가
        val isFriendFound = sendHttpRequestAndProcessResult(searchUrl)

        return isFriendFound
    }

    private fun sendHttpRequestAndProcessResult(searchUrl: String): Boolean {
        println("url 값 : $searchUrl")
        try {
            val jsonObject = Request().reqget(searchUrl) // GET 요청

            // 검색 결과를 파싱하여 유저 데이터를 가져옴
            name = jsonObject.getString("user_name") // 닉네임
            title = jsonObject.getString("user_title") // 칭호
            profile = jsonObject.getString("profile_local") // 프로필

            // 검색 결과가 있으면 friendList에 추가
            friendList.add(name) // 예시로 닉네임을 추가, 원하는 데이터를 추가하세요.


            println("user_name 값 : $name")
            println("user_title 값 : $title")
            println("profile_local 값 : $profile")

            // RecyclerView 업데이트
            runOnUiThread {
                friendListAdapter.notifyDataSetChanged()
            }

            return true
        } catch (e: UnknownServiceException) {
            // API 사용법에 나와있는 모든 오류응답은 여기서 처리
            println(e.message)
            // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장된 상태, 예: {err: "type_err"}
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 검색 결과가 없는 경우
        return false
    }

    private fun addInitialFriends() {
        // 초기 친구 목록을 추가합니다. 해당 유저 친구 목록 불러오는 쓰레드 생성

        val HomeThread = Thread {
            try {
                val jsonObject = Request().reqget("http://dmumars.kro.kr/api/getfriend/${getName()}") // GET 요청

                val jsonArray = jsonObject.getJSONArray("results")

                // JSONArray를 Kotlin 리스트로 변환하여 친구 목록으로 추가
                val friendListFromJson = mutableListOf<String>()

                // 각 친구에 대한 데이터를 추출하여 리스트에 추가
                for (i in 0 until jsonArray.length()) {
                    val friendCode = jsonArray.getString(i)

                    val friendData = Request().reqget("http://dmumars.kro.kr/api/getuserdata/${friendCode}") // GET 요청

                    // 필요한 데이터 추출
                    val title = friendData.getString("user_title")
                    val userName = friendData.getString("user_name")
                    val profile = friendData.getString("profile_local")

                    // 데이터를 한 문자열로 묶어서 리스트에 추가
                    val friendInfo = "$userName|$title|$profile"
                    friendListFromJson.add(friendInfo)
                }


                // friendList에 친구를 추가하거나 다른 작업 수행
                friendList.addAll(friendListFromJson)

                // RecyclerView 업데이트 (친구 목록 누를 때마다 갱신)
                runOnUiThread {
                    friendListAdapter.notifyDataSetChanged()
                }
            } catch (e: UnknownServiceException) {
                // API 사용법에 나와있는 모든 오류응답은 여기서 처리
                println(e.message)
                // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장된 상태, 예: {err: "type_err"}
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        HomeThread.start() // 쓰레드 시작
        HomeThread.join() // 쓰레드 종료될 때까지 대기

        // 로그 추가
        println("친구 목록 개수: ${friendList.size}")
        for (friendInfo in friendList) {
            println("친구 정보: $friendInfo")
        }
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
