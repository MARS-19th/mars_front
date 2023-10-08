package com.example.marsproject

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.marsproject.databinding.ActivityDialogNoticeBinding
import org.json.JSONObject
import java.net.UnknownServiceException

class NoticeDialog : DialogFragment() {
    private lateinit var binding: ActivityDialogNoticeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityDialogNoticeBinding.inflate(inflater, container, false)
        val view = binding.root

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 친구 추가 버튼 이벤트 처리
        binding.sendNotification.setOnClickListener {
           dismiss()
        }

        binding.finishBtn.setOnClickListener {
            dismiss()
        }
        return view
    }
}