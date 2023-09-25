package com.example.marsproject

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.example.marsproject.databinding.ActivityDialogCustomBinding

class MyDialog(private val context : AppCompatActivity) {

    private lateinit var binding : ActivityDialogCustomBinding
    private val dlg = Dialog(context) // 부모 액티비티의 context 가 들어감

    private lateinit var listener : MyDialogOKClickedListener

    fun show(content : String) {
        binding = ActivityDialogCustomBinding.inflate(context.layoutInflater)

        dlg?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 레이아웃 배경을 투명하게
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE) // 타이틀 제거
        dlg.setContentView(binding.root) // 다이얼로그에 사용할 xml 파일을 불러오기
        dlg.setCancelable(false) // 바깥 화면을 눌렀을 때 닫히지 않게 하기


        binding.dialogText.text = content // 부모 액티비티에서 전달 받은 텍스트 설정

        // 예 버튼 클릭 리스너
        binding.okButton.setOnClickListener {
            listener.onOKClicked("확인을 눌렀습니다.") // 예 버튼을 눌렀다는 함수
            dlg.dismiss()
        }

        // 아니오 버튼 클릭 리스너
        binding.noButton.setOnClickListener {
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