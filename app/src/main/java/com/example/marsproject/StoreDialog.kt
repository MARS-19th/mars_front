//StoreDialog.kt
package com.example.marsproject

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.marsproject.databinding.StoreDialogBinding

class StoreDialog(
    private val context: AppCompatActivity,
    private val imageResource: Int,
    private val itemPrice: Int // 아이템 가격을 추가합니다.
) {

    private lateinit var binding: StoreDialogBinding
    private var dlg = Dialog(context)

    fun show() {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView: View = inflater.inflate(R.layout.store_dialog, null)
        builder.setView(dialogView)

        dlg = builder.create()
        dlg?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg?.setCancelable(false)

        // store_dialog.xml에서 ImageView와 TextView를 찾습니다.
        val dialogImageView = dialogView.findViewById<ImageView>(R.id.item_dialog)
        val dialogPriceTextView = dialogView.findViewById<TextView>(R.id.dialog_price)

        // 이미지 리소스와 아이템 가격 설정
        dialogImageView.setImageResource(imageResource)
        dialogPriceTextView.text = "${itemPrice}"

        val okButton = dialogView.findViewById<View>(R.id.buy)
        val noButton = dialogView.findViewById<View>(R.id.close)

        okButton.setOnClickListener {
            // 여기에서 OK 버튼을 눌렀을 때 수행할 동작을 추가하세요.
            dlg?.dismiss() // 다이얼로그 닫기
        }

        noButton.setOnClickListener {
            dlg?.dismiss()
        }

        dlg?.show()
    }
}
