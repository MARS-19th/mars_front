package com.example.marsproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendRequestAdapter(private val friendRequestList: List<String>) :
    RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder>() {

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

        fun bind(friendRequest: String) {
            // 친구 신청 아이템을 설정하는 코드
            // friendRequest 데이터를 이용하여 각 뷰에 내용을 설정합니다.
            // 예를 들어, 닉네임 설정: nicknameTextView.text = friendRequest.nickname
            // 프로필 이미지 설정: profileImageView.setImageResource(friendRequest.profileImageResId)

            // 수락 버튼 클릭 이벤트 설정
            acceptButton.setOnClickListener {
                // 수락 버튼을 클릭한 경우의 처리 코드를 작성하세요.
            }

            // 거절 버튼 클릭 이벤트 설정
            declineButton.setOnClickListener {
                // 거절 버튼을 클릭한 경우의 처리 코드를 작성하세요.
            }
        }
    }
}
