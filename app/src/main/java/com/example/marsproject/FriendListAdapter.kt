package com.example.marsproject

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.util.ULocale.getName
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.json.JSONObject

// FriendInfo 모델 클래스 정의
data class FriendInfo(
    val nickname: String,
    val title: String,
    val profileImageUrl: String,
    val isFriend: Boolean
)


class FriendListAdapter(
    private val getUsername: () -> String,
    private val friendList: MutableList<FriendInfo>,
    private val isFriendList: Boolean = true
) :
    RecyclerView.Adapter<FriendListAdapter.FriendViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.friend_item_layout, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friendInfo = friendList[position]
        holder.bind(friendInfo)
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    fun addFriend(friend: FriendInfo): Boolean {
        // 이미 목록에 있는 친구인지 확인
        val isFriendAdded = friendList.none { it.nickname == friend.nickname }

        if (isFriendAdded) {
            // 목록에 추가되지 않은 친구라면 추가
            friendList.add(friend)
        }

        return isFriendAdded
    }


    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
        private val nicknameTextView: TextView = itemView.findViewById(R.id.nicknameTextView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val someButton: ImageButton = itemView.findViewById(R.id.someButton)
        private val touchuserdata: LinearLayout = itemView.findViewById(R.id.touchuserdata)

        @SuppressLint("ResourceType")
        fun bind(friendInfo: FriendInfo) {
            val username = getUsername()
            // 친구 정보를 뷰에 바인딩
            nicknameTextView.text = friendInfo.nickname
            titleTextView.text = friendInfo.title

            Glide.with(itemView.context)
                .load(friendInfo.profileImageUrl)
                .placeholder(Color.parseColor("#00000000"))
                .error(R.drawable.profileimage)
                .skipMemoryCache(true)
                .into(profileImageView)

            // isFriend 값에 따라 이미지 버튼 이미지 설정
            if (friendInfo.isFriend) {
                someButton.setImageResource(R.drawable.minus_background)
                someButton.setOnClickListener {
                    // 친구 삭제 로직을 구현, 현재 항목을 친구 목록에서 제거하고 RecyclerView 갱신
                    val context = itemView.context

                    val alertDialog = AlertDialog.Builder(context)
                        .setTitle("친구 삭제")
                        .setMessage("${friendInfo.nickname} 님을 친구 목록에서 삭제하시겠습니까?")
                        .setPositiveButton("확인") { _, _ ->
                            // 사용자가 확인을 누른 경우
                            // 친구 삭제 요청을 디비에 저장하는 쓰레드를 실행합니다.

                            Thread {
                                val outputjson = JSONObject() // JSON 생성
                                outputjson.put("user_name", username) // 아이디
                                outputjson.put("friend", friendInfo.nickname) // 친구 닉네임

                                // 서버에 친구 삭제 요청 보내기
                                val response = Request().reqpost(
                                    "http://dmumars.kro.kr/api/delfriend",
                                    outputjson
                                )

                                itemView.post {
                                    if (response != null) {
                                        // 친구 삭제 요청이 성공적으로 디비에 저장되면 성공 메시지
                                        Toast.makeText(
                                            context,
                                            "친구 삭제 요청을 보냈습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // 친구 목록에서 해당 친구를 삭제하고 RecyclerView 갱신
                                        val position = friendList.indexOf(friendInfo)
                                        if (position != -1) {
                                            friendList.removeAt(position)
                                            notifyItemRemoved(position)
                                            notifyItemChanged(position, friendList.size)
                                        }
                                    } else {
                                        // 친구 삭제 요청이 실패한 경우 실패 메시지
                                        Toast.makeText(
                                            context,
                                            "친구 삭제 요청에 실패했습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }.start()
                        }
                        .setNegativeButton("취소") { _, _ -> }.create()
                    alertDialog.show()
                }
            } else {
                someButton.setImageResource(R.drawable.plus_background)

                // 친구 추가 로직을 구현
                // 친구 추가 요청을 디비에 저장
                someButton.setOnClickListener {
                    var isAddingFriend = false
                    isAddingFriend = true // 친구 추가 중 상태로 변경
                    someButton.isEnabled = false // 버튼 비활성화

                    val context = itemView.context

                    val alertDialog = AlertDialog.Builder(context)
                        .setTitle("친구 추가")
                        .setMessage("${friendInfo.nickname} 님을 친구로 추가하시겠습니까?")
                        .setPositiveButton("확인") { _, _ ->
                            // 사용자가 확인을 누른 경우
                            // 친구 추가 요청을 디비에 저장하는 쓰레드를 실행합니다.

                            Thread {
                                val outputjson = JSONObject() // JSON 생성
                                outputjson.put("user_name", username) // 아이디
                                outputjson.put("friend", friendInfo.nickname) // 친구 닉네임

                                // 서버에 친구 추가 요청 보내기
                                val response = Request().reqpost(
                                    "http://dmumars.kro.kr/api/setfriend",
                                    outputjson
                                )

                                itemView.post {
                                    isAddingFriend = false // 친구 추가 완료 후 상태 변경
                                    someButton.isEnabled = true // 버튼 활성화

                                    if (response != null) {
                                        // 친구 추가 요청이 성공적으로 디비에 저장되면 성공 메시지
                                        Toast.makeText(
                                            context,
                                            "친구 추가 요청을 보냈습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        // 친구 추가 요청이 실패한 경우 실패 메시지
                                        Toast.makeText(
                                            context,
                                            "친구 추가 요청에 실패했습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }.start()
                        }
                        .setNegativeButton("취소") { _, _ ->
                            // 사용자가 취소를 누른 경우
                            isAddingFriend = false // 친구 추가 취소 시 상태 변경
                            someButton.isEnabled = true // 버튼 활성화
                        }
                        .create()

                    alertDialog.show()
                }

            }

            // 닉네임 텍스트뷰 클릭 이벤트 처리
            touchuserdata.setOnClickListener {
                // 다른 액티비티로 이동하는 코드 작성
                val intent = Intent(itemView.context, SendMessageActivity::class.java)
                // 닉네임, 프로필 이미지 URL, 칭호 데이터를 인텐트에 추가하여 다른 액티비티로 전달
                intent.putExtra("nickname", friendInfo.nickname)
                intent.putExtra("profileImageUrl", friendInfo.profileImageUrl)
                intent.putExtra("title", friendInfo.title)
                itemView.context.startActivity(intent)
            }


            // isFriendList값으로 각각의 이미지에 클릭에 따라 친구추가 로직과 친구삭제 로직 구현


        }

    }
}


