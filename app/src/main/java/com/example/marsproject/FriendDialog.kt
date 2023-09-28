package com.example.marsproject

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.marsproject.databinding.DialogFriendBinding

class FriendDialog(id: String) : DialogFragment() {

    //뷰 바인딩 정의
    private lateinit var binding : DialogFriendBinding

    private var id: String? = null

    init {
        this.id = id
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFriendBinding.inflate(inflater, container, false)
        val view = binding.root

        //레이아웃 배경을 투명하게 해줌
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.addFriendBtn.setOnClickListener{
            //친구추가 알림 친구에게 날리기
            //000님이 친구추가 요청을 보내셨습니다. 수락하시겠습니까?
        }

        binding.finishBtn.setOnClickListener{
            dismiss()
        }
        return view
    }
}
