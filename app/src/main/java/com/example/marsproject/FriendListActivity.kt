package com.example.marsproject

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.marsproject.databinding.ActivityFriendListBinding
import org.json.JSONObject
import java.net.UnknownServiceException

class FriendListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendListBinding
    private lateinit var friendListAdapter: FriendListAdapter
    private lateinit var friendRequestAdapter: FriendRequestAdapter
    private var friendList = mutableListOf<FriendInfo>() // 친구 목록을 저장할 리스트
    private var friendRequestList = mutableListOf<FriendInfo>() // 친구 신청 목록을 저장할 리스트
    private var searchResultList = mutableListOf<FriendInfo>() // 검색 결과를 저장할 리스트
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
        friendListAdapter = FriendListAdapter({ getName() }, friendList) // 기본값은 친구 목록
        friendRequestAdapter = FriendRequestAdapter({ getName() }, friendRequestList)

        binding.friendRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.friendRecyclerView.adapter = friendListAdapter

        // 검색 버튼 클릭 이벤트 처리
        binding.searchButton.setOnClickListener {
            val friendCode = binding.friendCodeEditText.text.toString()
            if (friendCode.isNotEmpty()) {
                // 여기에서 실제 친구를 검색하고 결과를 searchResultList에 추가하는 로직을 구현
                searchFriend(friendCode)
                binding.friendCodeEditText.text.clear()
            }

        }

          // 친구목록 텍스트뷰를 누를 때
           friendListTextView.setOnClickListener {
            friendListTextView.setTextColor(Color.parseColor("#FF9C46")) // 친구목록 텍스트뷰 선택
            friendRequestTextView.setTextColor(Color.GRAY) // 친구신청 텍스트뷰는 기본 색상으로 변경

            // 친구 목록 리사이클러뷰를 업데이트
            binding.friendRecyclerView.adapter = FriendListAdapter({ getName() }, friendList)
        }

        // 초기 친구 목록을 추가
        addInitialFriends()

        // 친구신청 목록을 가져오고 업데이트
        FriendReq()
    }

    //닉네임 값 가져오기
    fun getName(): String {
        val pref = getSharedPreferences("userName", 0)
        return pref.getString("name", "").toString()
    }

    private fun searchFriend(friendCode: String) {
        // 검색 결과를 초기화
        searchResultList.clear()

        // 검색할 URL을 생성
        val searchUrl = "http://dmumars.kro.kr/api/getuserdata/${friendCode}"
        println("에디트 텍스트 값 : $friendCode")

        val searchThread = Thread {
            try {
                val jsonObject = Request().reqget(searchUrl) // GET 요청
                println("JSON 응답: $jsonObject")

                val nickname = jsonObject.getString("user_name") // 닉네임
                val title = jsonObject.getString("user_title") // 칭호
                val profileImageUrl = jsonObject.getString("profile_local") // 프로필

                // 여기에서 친구 목록에 해당 친구가 있는지 확인
                val isFriend = checkIfFriendExists(nickname) // 이 함수는 친구 목록에서 해당 닉네임을 찾아서 있는지 확인하는 로직입니다.

                // FriendInfo 객체를 생성하여 리스트에 추가
                val friendInfo = FriendInfo(nickname, title, profileImageUrl, isFriend)
                searchResultList.add(friendInfo)

                // RecyclerView 업데이트
                runOnUiThread {
                    if (searchResultList.isEmpty()) {
                        // 검색 결과가 없는 경우 RecyclerView를 비워줌
                        binding.friendRecyclerView.adapter = null
                    } else {
                        // 검색 결과가 있을 때 검색한 친구의 목록을 표시
                        binding.friendRecyclerView.adapter = FriendListAdapter({ getName() }, searchResultList)
                    }
                }

            } catch (e: UnknownServiceException) {
                // API 사용법에 나와있는 모든 오류응답은 여기서 처리
                println(e.message)
                // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장된 상태, 예: {err: "type_err"}
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        searchThread.start()
    }

    // 친구 목록에서 해당 닉네임을 찾아서 있는지 확인하는 함수
    private fun checkIfFriendExists(nickname: String): Boolean {
        // 여기에 친구 목록에서 해당 닉네임을 찾아서 있는지 확인하는 로직을 구현
        // 만약 친구 목록에 해당 닉네임을 찾으면 true를 반환하고, 그렇지 않으면 false를 반환
        for (friend in friendList) {
            if (friend.nickname == nickname) {
                return true
            }
        }
        return false
    }


    private fun addInitialFriends() {
        // 초기 친구 목록을 추가합니다. 해당 유저 친구 목록 불러오는 쓰레드 생성

        val HomeThread = Thread {
            try {
                val jsonObject = Request().reqget("http://dmumars.kro.kr/api/getfriend/${getName()}") // GET 요청

                val jsonArray = jsonObject.getJSONArray("results")

                // JSONArray를 Kotlin 리스트로 변환하여 친구 목록으로 추가
                val friendListFromJson = mutableListOf<FriendInfo>()

                // 각 친구에 대한 데이터를 추출하여 리스트에 추가
                for (i in 0 until jsonArray.length()) {
                    val friendData = jsonArray.getJSONObject(i)
                    val friendCode = friendData.getString("friend")
                    val isAccept = friendData.optBoolean("isaccept", false)

                    // "isaccept" 플래그가 true인 경우에만 친구 목록에 추가
                    if (isAccept) {
                        val friendInfo = getFriendInfo(friendCode) // 친구 정보 가져오기
                        friendListFromJson.add(friendInfo)
                    }
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
    }

    // 친구 정보를 가져오는 함수
    private fun getFriendInfo(friendCode: String): FriendInfo {
        val friendData = Request().reqget("http://dmumars.kro.kr/api/getuserdata/${friendCode}") // GET 요청

        val title = friendData.getString("user_title")
        val userName = friendData.getString("user_name")
        val profile = friendData.getString("profile_local")

        // FriendInfo 객체를 생성하여 반환
        return FriendInfo(userName, title, profile, isFriend = true)
    }

    private fun FriendReq() {
        val username = getName()
        Thread {
            try {
                val friendRequestsJSON =
                    Request().reqget("http://dmumars.kro.kr/api/getreqfriend/${username}") // GET 요청
                val friendNicknamesArray = friendRequestsJSON.getJSONArray("results") // 닉네임 배열

                val friendListFromReq = mutableListOf<FriendInfo>() // 친구 정보를 저장할 리스트

                for (i in 0 until friendNicknamesArray.length()) {
                    val friendNickname = friendNicknamesArray.getString(i)
                    Log.d("FriendRequest", "Friend Nickname[$i]: $friendNickname") // 로그로 닉네임 출력 확인
                    val friendInfoJSON = Request().reqget("http://dmumars.kro.kr/api/getuserdata/${friendNickname}")

                    val nickname = friendInfoJSON.getString("user_name") // 닉네임
                    val title = friendInfoJSON.getString("user_title") // 칭호
                    val profileImageUrl = friendInfoJSON.getString("profile_local") // 프로필 이미지 URL

                    // FriendInfo 객체를 생성하여 리스트에 추가
                    val friendInfo = FriendInfo(nickname, title, profileImageUrl, isFriend = true)
                    friendListFromReq.add(friendInfo)
                }

                // 친구 요청 목록에 친구 정보를 추가하고, RecyclerView 업데이트
                runOnUiThread {
                    friendRequestList.clear()
                    friendRequestList.addAll(friendListFromReq)
                    friendRequestAdapter.notifyDataSetChanged() // RecyclerView 업데이트
                }

            } catch (e: UnknownServiceException) {
                // API 사용법에 나와있는 모든 오류응답은 여기서 처리
                println(e.message)
                // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장된 상태, 예: {err: "type_err"}
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
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

