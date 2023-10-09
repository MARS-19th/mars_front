package com.example.marsproject

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.net.UnknownServiceException

class FriendRequestAdapter(private val getUsername: () -> String, private val friendRequestList: List<FriendInfo>) :
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

        fun bind(friendRequest: FriendInfo) {
            val username = getUsername()

            // 프로필 이미지를 Glide를 사용하여 로드
            Glide.with(itemView.context)
                .load(friendRequest.profileImageUrl)
                .into(profileImageView)

            // 닉네임 텍스트뷰에 닉네임 설정
            nicknameTextView.text = friendRequest.nickname

            // 칭호 텍스트뷰에 칭호 설정
            titleTextView.text = friendRequest.title

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

