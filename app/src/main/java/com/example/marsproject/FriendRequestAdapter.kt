package com.example.marsproject

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.json.JSONObject
import java.net.UnknownServiceException

class FriendRequestAdapter(
    private val getUsername: () -> String,
    private val friendRequestList: MutableList<FriendInfo>,
    private val friendListAdapter: FriendListAdapter // FriendListAdapter 추가
) : RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.friend_request_item_layout, parent, false)
        return FriendRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int) {
        val friendRequest = friendRequestList[position]
        holder.bind(friendRequest)
    }

    override fun getItemCount(): Int {
        return friendRequestList.size
    }

    inner class FriendRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
        private val nicknameTextView: TextView = itemView.findViewById(R.id.nicknameTextView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val acceptButton: Button = itemView.findViewById(R.id.acceptButton)
        private val declineButton: Button = itemView.findViewById(R.id.declineButton)
        private val touchuserdata: LinearLayout = itemView.findViewById(R.id.touchuserdata)

        @SuppressLint("ResourceType")
        fun bind(friendRequest: FriendInfo) {
            val username = getUsername()

            // 프로필 이미지를 Glide를 사용하여 로드
            Glide.with(itemView.context)
                .load(friendRequest.profileImageUrl)
                .placeholder(Color.parseColor("#00000000"))
                .error(R.drawable.profileimage)
                .skipMemoryCache(true)
                .into(profileImageView)

            // 닉네임 텍스트뷰에 닉네임 설정
            nicknameTextView.text = friendRequest.nickname

            // 칭호 텍스트뷰에 칭호 설정
            titleTextView.text = friendRequest.title

            // 닉네임 텍스트뷰 클릭 이벤트 설정
            touchuserdata.setOnClickListener {
                // SendMessageActivity로 데이터를 전달하는 Intent 생성
                val intent = Intent(itemView.context, SendMessageActivity::class.java)
                intent.putExtra("nickname", friendRequest.nickname)
                intent.putExtra("title", friendRequest.title)
                intent.putExtra("profileImageUrl", friendRequest.profileImageUrl)

                // 액티비티 시작
                itemView.context.startActivity(intent)
            }

            acceptButton.setOnClickListener {
                // 수락 버튼을 클릭한 경우의 처리 코드를 작성
                Thread {
                    try {
                        val outputjson = JSONObject() // JSON 생성
                        outputjson.put("user_name", username) // 아이디
                        outputjson.put("friend", friendRequest.nickname) // 친구 닉네임

                        // 서버에 친구 추가 요청 보내기
                        val response = Request().reqpost("http://dmumars.kro.kr/api/setfriend", outputjson)

                        itemView.post {
                            if (response != null) {
                                // 친구 추가 성공 시 FriendListAdapter에 해당 친구 추가
                                val isFriendAdded = friendListAdapter.addFriend(friendRequest)

                                if (isFriendAdded) {
                                    // 친구 신청 목록에서 해당 친구 제거
                                    val position = friendRequestList.indexOf(friendRequest)
                                    if (position != -1) {
                                        friendRequestList.removeAt(position)
                                        // RecyclerView에서 해당 아이템 제거 및 변경 알림
                                        notifyItemRemoved(position)
                                        notifyItemChanged(position, friendRequestList.size)
                                    }

                                    // 친구 목록이 업데이트되었음을 알리기 위해 notifyDataSetChanged 호출
                                    friendListAdapter.notifyDataSetChanged()
                                    // 친구 목록에 추가되었습니다 토스트 메시지 표시
                                    Toast.makeText(itemView.context, "친구 목록에 추가되었습니다.", Toast.LENGTH_SHORT).show()

                                }
                            } else {
                                Toast.makeText(itemView.context, "친구 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
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

            // 거절 버튼 클릭 이벤트 설정
            declineButton.setOnClickListener {
                // 거절 버튼을 클릭한 경우의 처리 코드를 작성
                Thread {
                    try {
                        val outputjson = JSONObject() // JSON 생성
                        outputjson.put("user_name", username) // 아이디
                        outputjson.put("friend", friendRequest.nickname) // 친구 닉네임

                        // 서버에 친구 거절 요청 보내기
                        val response = Request().reqpost("http://dmumars.kro.kr/api/delfriend", outputjson)

                        itemView.post {
                            if (response != null) {
                                // 거절 성공 시 친구 신청 목록에서 해당 친구 제거
                                val position = friendRequestList.indexOf(friendRequest)
                                if (position != -1) {
                                    friendRequestList.removeAt(position)
                                    // RecyclerView에서 해당 아이템 제거 및 변경 알림
                                    notifyItemRemoved(position)
                                    notifyItemChanged(position, friendRequestList.size)
                                }

                                // 거절 메시지 표시
                                Toast.makeText(itemView.context, "친구 요청을 거절했습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(itemView.context, "친구 요청 거절에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
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
        }
    }
}

