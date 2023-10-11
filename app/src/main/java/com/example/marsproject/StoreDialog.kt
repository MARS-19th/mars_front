//StoreDialog.kt
package com.example.marsproject

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.marsproject.databinding.StoreDialogBinding
import org.json.JSONObject
import java.net.UnknownServiceException

class StoreDialog(
    private val context: AppCompatActivity,
    private val imageResource: Int,
    private val itemPrice: Int, // 아이템 가격을 추가합니다.
    private val diaprice: String, //다이얼로그 현재 가격
    private var userName: String,
    private val id: Int
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
        val diapriceTextView = dialogView.findViewById<TextView>(R.id.dia_price)

        // 이미지 리소스와 아이템 가격 설정
        dialogImageView.setImageResource(imageResource)
        dialogPriceTextView.text = "${itemPrice}"
        diapriceTextView.text = "${diaprice}" //다이얼로그 현재 코인 부분

        val okButton = dialogView.findViewById<View>(R.id.buy) //구매하기
        val noButton = dialogView.findViewById<View>(R.id.close) //닫기버튼

        okButton.setOnClickListener {
            // 여기에서 구매하기 버튼을 눌렀을 때 수행할 동작
            // (http://dmumars.kro.kr/api/setuserinventory)
            Thread {
                try {
                    val outputjson = JSONObject() //json 생성
                    outputjson.put("user_name", userName) //getName()
                    outputjson.put("object_id", id)

                    Request().reqpost("http://dmumars.kro.kr/api/setuserinventory", outputjson)
                    Log.d("왜실행안되냐고", "userName: ${userName}, id: ${id}")

                } catch (e: UnknownServiceException) {
                    println(e.message)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            dlg?.dismiss() // 다이얼로그 닫기
        }

        noButton.setOnClickListener {
            dlg?.dismiss()
        }

        dlg?.show()
    }
}
