// StoreActivity.kt
package com.example.marsproject

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.marsproject.databinding.ActivityStoreBinding
import java.net.UnknownServiceException

class StoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize)
        supportActionBar?.title = "상점"

        // 카테고리 클릭 리스너 설정
        categoryClickListener()

        val viewList = arrayOf(binding.item1, binding.item2, binding.item3,
            binding.item4, binding.item5, binding.item6, binding.item7)
        val userMoneyTextViews = arrayOf(binding.userMoney1, binding.userMoney2, binding.userMoney3,
            binding.userMoney4, binding.userMoney5, binding.userMoney6, binding.userMoney7)
        val imgList = arrayOf(R.drawable.bag, R.drawable.fish, R.drawable.cap,
            R.drawable.glasses, R.drawable.meat, R.drawable.wind, R.drawable.fork)
        val userMoneyTextView =

        /*
        fun getName(): String {
            val pref = getSharedPreferences("userName", 0)
            return pref.getString("name", "").toString()
        }

        Thread {
            try {
                val jsonObject =
                    Request().reqget("http://dmumars.kro.kr/api/getuseravatar/${getName()}") //get요청

                val color = jsonObject.getString("color")
                val emotion = jsonObject.getString("look")
                val imgPath = "${color}_${emotion}"
                // UI 업데이트를 위해 메인 스레드에서 실행
                runOnUiThread {


                    val imageView = binding.StoreAvatarImage
                    val resID = resources.getIdentifier(imgPath, "drawable", packageName)
                    imageView.setImageResource(resID)

                }

            } catch (e: UnknownServiceException) {
                // API 사용법에 나와있는 모든 오류응답은 여기서 처리
                println(e.message)
                // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
*/

        Thread {
            try {
                val jsonObject = Request().reqget("http://dmumars.kro.kr/api/getshopitemid") //get요청

                for(i in 0 until jsonObject.getJSONArray("results").length()) {
                    val id = jsonObject.getJSONArray("results").getJSONObject(i).getInt("object_id")
                    val name = jsonObject.getJSONArray("results").getJSONObject(i).getString("item_name")
                    val price = jsonObject.getJSONArray("results").getJSONObject(i).getInt("price")
                    userMoneyTextViews[i].text = price.toString()

                    viewList[i].setOnClickListener {
                        val storeDialog = StoreDialog(this, imgList[i], price)
                        storeDialog.show()
                    }
                }
            } catch (e: UnknownServiceException) {
                // API 사용법에 나와있는 모든 오류응답은 여기서 처리
                println(e.message)
                // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun categoryClickListener() {
        val clklistener = View.OnClickListener {
            when (it.id) {
                R.id.headImage -> {
                    binding.headView.setBackgroundColor(Color.parseColor("#FF9C46")) // 머리 밑줄 색상 변경
                    binding.bodyView.setBackgroundColor(Color.parseColor("#DDDDDD")) // 몸통 밑줄 색상 변경
                    binding.etcView.setBackgroundColor(Color.parseColor("#DDDDDD")) // 기타 밑줄 색상 변경
                }
                R.id.bodyImage -> {
                    binding.headView.setBackgroundColor(Color.parseColor("#DDDDDD")) // 머리 밑줄 색상 변경
                    binding.bodyView.setBackgroundColor(Color.parseColor("#FF9C46")) // 몸통 밑줄 색상 변경
                    binding.etcView.setBackgroundColor(Color.parseColor("#DDDDDD")) // 기타 밑줄 색상 변경
                }
                R.id.etcImage -> {
                    binding.headView.setBackgroundColor(Color.parseColor("#DDDDDD")) // 머리 밑줄 색상 변경
                    binding.bodyView.setBackgroundColor(Color.parseColor("#DDDDDD")) // 몸통 밑줄 색상 변경
                    binding.etcView.setBackgroundColor(Color.parseColor("#FF9C46")) // 기타 밑줄 색상 변경
                }
            }
        }

        binding.headImage.setOnClickListener(clklistener)
        binding.bodyImage.setOnClickListener(clklistener)
        binding.etcImage.setOnClickListener(clklistener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
