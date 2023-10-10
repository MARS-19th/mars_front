package com.example.marsproject

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.view.Window
import android.widget.Toast
import com.example.marsproject.databinding.ActivityObjectiveDialogCustomBinding

class ObjectiveDialogCustom(private val context : AppCompatActivity) {

    private lateinit var binding : ActivityObjectiveDialogCustomBinding
    private val dlg = Dialog(context) // 부모 액티비티의 context 가 들어감

    private lateinit var listener : MyDialogOKClickedListener

    fun show(content : String) {
        binding = ActivityObjectiveDialogCustomBinding.inflate(context.layoutInflater)

        dlg?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 레이아웃 배경을 투명하게
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE) // 타이틀 제거
        dlg.setContentView(binding.root) // 다이얼로그에 사용할 xml 파일을 불러오기
        dlg.setCancelable(false) // 바깥 화면을 눌렀을 때 닫히지 않게 하기

        // 예 버튼 클릭 리스너
        binding.okButton.setOnClickListener {
            if(binding.dialogEditText.text.toString().trim() == "") {
                Toast.makeText(context, "목표를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                listener.onOKClicked(binding.dialogEditText.text.toString()) // 예 버튼을 눌렀다는 함수
                dlg.dismiss()
            }
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