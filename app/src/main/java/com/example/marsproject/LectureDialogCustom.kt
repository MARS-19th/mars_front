package com.example.marsproject

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.view.Window
import android.widget.Toast
import com.example.marsproject.databinding.ActivityLectureDialogCustomBinding

class LectureDialogCustom(private val context : AppCompatActivity) {

    private lateinit var binding : ActivityLectureDialogCustomBinding
    private val dlg = Dialog(context) // 부모 액티비티의 context 가 들어감

    private lateinit var listener : MyDialogOKClickedListener

    fun show(title : String, link : String, progress : Int) {
        binding = ActivityLectureDialogCustomBinding.inflate(context.layoutInflater)

        dlg?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 레이아웃 배경을 투명하게
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE) // 타이틀 제거
        dlg.setContentView(binding.root) // 다이얼로그에 사용할 xml 파일을 불러오기

        // 강의 제목 변경
        binding.lectureTitle.text = title

        // 처음 보는 것이 아니라면
        if (progress != 0) {
            binding.playButton.text = "다시 듣기" // 텍스트 변경
            binding.clearButton.text = "완료" // 텍스트 변경
            binding.dialogLayout.setBackgroundResource(R.drawable.dialog_clear) // 배경 변경
            binding.playButton.setBackgroundResource(R.drawable.playbutton_clear) // 배경 변경
        }

        // 강의 듣기 버튼 클릭 리스너
        binding.playButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link)) // 유튜브 링크 연결
            context.startActivity(intent) // 인텐트 실행
            listener.onOKClicked("강의 봤어용") // 강의 듣기 버튼을 눌렀다는 함수
            dlg.dismiss()
        }

        dlg.show()
    }

    // 부모 액티비티에서 예 버튼 클릭 시 호출되는 함수
    fun setOnOKClickedListener(listener: (String) -> Unit) {
        this.listener = object: MyDialogOKClickedListener {
            override fun onOKClicked(content: String) {
                listener(content)
            }
        }
    }

    interface MyDialogOKClickedListener {
        fun onOKClicked(content : String)
    }

}