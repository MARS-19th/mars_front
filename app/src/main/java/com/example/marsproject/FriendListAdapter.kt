package com.example.marsproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide  // Glide 라이브러리를 사용하기 위해 추가해야 합니다.

class FriendListAdapter(private val friendList: List<String>) :
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

        fun bind(friendInfo: String) {
            // friendInfo는 "닉네임|칭호|프로필_이미지_URL"와 같은 형식
            val parts = friendInfo.split("|")

            // 닉네임, 칭호, 프로필 이미지 URL 추출
            val nickname = parts[0]
            val title = parts[1]
            val profileImageUrl = parts[2]

            // 추출한 데이터를 뷰에 넣기
            nicknameTextView.text = nickname
            titleTextView.text = title

            // Glide를 사용하여 프로필 이미지를 로드하고 이미지뷰에 표시
            Glide.with(itemView.context)
                .load(profileImageUrl)
                .into(profileImageView)
        }
    }
}