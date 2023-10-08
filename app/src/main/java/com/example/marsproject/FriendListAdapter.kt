package com.example.marsproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// FriendInfo 모델 클래스 정의
data class FriendInfo(val nickname: String, val title: String, val profileImageUrl: String, val isFriend: Boolean)


class FriendListAdapter(private val friendList: List<FriendInfo>, private val isFriendList: Boolean = true) :
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

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
        private val nicknameTextView: TextView = itemView.findViewById(R.id.nicknameTextView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val someButton: ImageButton = itemView.findViewById(R.id.someButton)

        fun bind(friendInfo: FriendInfo) {
            // 친구 정보를 뷰에 바인딩
            nicknameTextView.text = friendInfo.nickname
            titleTextView.text = friendInfo.title

            Glide.with(itemView.context)
                .load(friendInfo.profileImageUrl)
                .into(profileImageView)

            // isFriend 값에 따라 이미지 버튼 이미지 설정
            if (friendInfo.isFriend) {
                someButton.setImageResource(R.drawable.minus)
            } else {
                someButton.setImageResource(R.drawable.plus)
            }

            // isFriendList값으로 각각의 이미지에 따라 친구추가 로직과 친구삭제 로직 구현
            someButton.setOnClickListener {

                if (isFriendList) {
                    // 친구 삭제 로직을 구현
                    // 현재 항목을 친구 목록에서 제거하고 RecyclerView 갱신

                } else {
                    // 친구 추가 로직을 구현
                    // 현재 항목을 친구 목록에 추가하고 RecyclerView 갱신

                }
            }

        }
    }
}

