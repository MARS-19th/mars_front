package com.example.marsproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FriendListAdapter(private val friendList: List<String>, private val isFriendList: Boolean = true) :
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

        fun bind(friendInfo: String) {
            val parts = friendInfo.split("|")
            val nickname = parts[0]
            val title = parts[1]
            val profileImageUrl = parts[2]

            nicknameTextView.text = nickname
            titleTextView.text = title

            Glide.with(itemView.context)
                .load(profileImageUrl)
                .into(profileImageView)

            // 친구 목록에 있는 경우 이미지 버튼을 minus.png로 설정
            // 친구 목록에 없는 경우 이미지 버튼을 plus.png로 설정
            if (isFriendList) {
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
