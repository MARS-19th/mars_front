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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.marsproject.databinding.StoreDialogBinding
import org.json.JSONObject
import java.net.UnknownServiceException

class StoreDialog(
    private val context: AppCompatActivity,
    private val activity: StoreActivity,
    private val imageResource: Int,
    private val itemPrice: Int, // 아이템 가격을 추가합니다.
    private val diaprice: String, //다이얼로그 현재 가격
    private var userName: String,
    private val id: Int,
    private var currentItem: Int?= null

) {
    private lateinit var binding: StoreDialogBinding
    private var dlg = Dialog(context)

    fun show() {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView: View = inflater.inflate(R.layout.store_dialog, null)
        builder.setView(dialogView)

        dlg = builder.create()
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setCancelable(false)

        // store_dialog.xml에서 ImageView와 TextView를 찾습니다.
        val dialogImageView = dialogView.findViewById<ImageView>(R.id.item_dialog)
        val dialogPriceTextView = dialogView.findViewById<TextView>(R.id.dialog_price)
        val diapriceTextView = dialogView.findViewById<TextView>(R.id.dia_price)
        val minji:Int = diaprice.toInt()-itemPrice //현재코인에서 아이템 가격 빼기

        // 이미지 리소스와 아이템 가격 설정
        dialogImageView.setImageResource(imageResource)
        dialogPriceTextView.text = "$itemPrice"
        diapriceTextView.text = diaprice //다이얼로그 현재 코인 부분

        val okButton = dialogView.findViewById<View>(R.id.buy) //구매하기
        val noButton = dialogView.findViewById<View>(R.id.close) //닫기버튼


        okButton.setOnClickListener {
            // 여기에서 구매하기 버튼을 눌렀을 때 수행할 동작
            // (http://dmumars.kro.kr/api/setuserinventory)

            if(minji >= 0) {
                //아이템 인벤토리에 유저네임, object id 디비에 저장
                Thread {
                    try {
                        val outputjson = JSONObject() //json 생성
                        outputjson.put("user_name", userName) //getName()
                        outputjson.put("object_id", id)

                        Request().reqpost("http://dmumars.kro.kr/api/setuserinventory", outputjson)
                    } catch (e: UnknownServiceException) {
                        println(e.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()

                //현재 가지고 있는 코인에서 선택한 아이템을 사면 현재 코인-아이템 가격 값으로 바뀜
                Thread {
                    try {
                        val outputjson = JSONObject() //json 생성
                        outputjson.put("user_name", userName) //getName()
                        outputjson.put("value", minji)

                        Request().reqpost("http://dmumars.kro.kr/api/setmoney", outputjson)
                    } catch (e: UnknownServiceException) {
                        println(e.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()


                // 아이템을 착용했을 때 아이템 아이디를 서버로 보내는 함수
                Thread {
                    try {
                        val outputjson = JSONObject() //json 생성
                        outputjson.put("user_name", userName) //getName()
                        outputjson.put("moun_shop", id)

                        Request().reqpost("http://dmumars.kro.kr/api/setuserfititem", outputjson)

                        //현재 착용 중인 아이템의 아이디를 id(object id)로 변경
                        currentItem = id

                        //미착용 버튼 클릭시 착용중으로 변경(디비에서 마운샵을 해당 아이디로 변경)


                        //착용중 버튼 ㅋ틀릭시 미착용으로 변경(디비에서 마운샵을 널로 변경)
                    } catch (e: UnknownServiceException) {
                        println(e.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            } else {
                Toast.makeText(context, "재화가 부족합니다", Toast.LENGTH_SHORT).show()
            }

            activity.load()
            dlg.dismiss()// 다이얼로그 닫기
        }

        noButton.setOnClickListener {
            dlg.dismiss()

        }
        dlg.show()
    }
}