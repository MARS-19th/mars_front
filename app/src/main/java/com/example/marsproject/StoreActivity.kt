package com.example.marsproject

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.marsproject.databinding.ActivityStoreBinding
import org.json.JSONObject
import java.net.UnknownServiceException

class StoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoreBinding
    //내가 현재 착용중인 아이디 저장
    var currentItem: Int? = null // 초기에는 착용한 아이템이 없으므로 null로 초기화
    //내가 구매했던 아이템의 아이디 저장
    val buyItem = ArrayList<Int>(0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        load()
    }

    fun load() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize)
        supportActionBar?.title = "상점"
        //DB에서 고양이 색상, 표정, 아이템여부를 가지고 와서 백그라운드 생성
        runOnUiThread { setBackGround()}

        runOnUiThread {UserInventory()}
        runOnUiThread { getMoney()}

        categoryClickListener()

    }

    private fun getMoney() {
        Thread {
            val viewList = arrayOf(
                binding.item1, binding.item2, binding.item3,
                binding.item4, binding.item5, binding.item6, binding.item7
            )
            val userMoneyTextViews = arrayOf(
                binding.userMoney1, binding.userMoney2, binding.userMoney3,
                binding.userMoney4, binding.userMoney5, binding.userMoney6, binding.userMoney7
            )
            val imgList = arrayOf(
                R.drawable.bag, R.drawable.fish, R.drawable.cap,
                R.drawable.glasses, R.drawable.pen, R.drawable.wind, R.drawable.fork
            )

            try {
                val jsonObject = Request().reqget("http://dmumars.kro.kr/api/getshopitemid")
                val jsonObject1 = Request().reqget("http://dmumars.kro.kr/api/getuserdata/${getName()}")

                val money = jsonObject1.getInt("money").toString()
                runOnUiThread {
                    val current_priceTextView = findViewById<TextView>(R.id.current_price)
                    current_priceTextView.text = money
                }

                for (i in 0 until jsonObject.getJSONArray("results").length()) {
                    val id = jsonObject.getJSONArray("results").getJSONObject(i).getInt("object_id")//1,2,3,4,5,6,7
                    val name = jsonObject.getJSONArray("results").getJSONObject(i).getString("item_name")
                    val price = jsonObject.getJSONArray("results").getJSONObject(i).getInt("price")
                    userMoneyTextViews[i].text = price.toString()
                    runOnUiThread {
                        for(item in buyItem){
                            if(id == item){
                                viewList[item-1].isEnabled = false
                            }
                        }
                        viewList[i].setOnClickListener {
                            val storeDialog = StoreDialog(this, this, imgList[i], price, money, getName(), id)
                            storeDialog.show()
                        }
                    }
                }
            } catch (e: UnknownServiceException) {
                println(e.message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun UserInventory() {
        val buyItemThread = Thread {
            val itemIdViewMap = mapOf(
                1 to binding.item1,
                2 to binding.item2,
                3 to binding.item3,
                4 to binding.item4,
                5 to binding.item5,
                6 to binding.item6,
                7 to binding.item7
            )

            val yesBtnViewMap = mapOf(
                1 to binding.btnyes1,
                2 to binding.btnyes2,
                3 to binding.btnyes3,
                4 to binding.btnyes4,
                5 to binding.btnyes5,
                6 to binding.btnyes6,
                7 to binding.btnyes7
            )

            val noBtnViewMap = mapOf(
                1 to binding.btnno1,
                2 to binding.btnno2,
                3 to binding.btnno3,
                4 to binding.btnno4,
                5 to binding.btnno5,
                6 to binding.btnno6,
                7 to binding.btnno7
            )

            try {
                val jsonObject = Request().reqget("http://dmumars.kro.kr/api/getuserinventory/${getName()}")
                for (i in 0 until jsonObject.getJSONArray("results").length()) {
                    val objectId = jsonObject.getJSONArray("results").getJSONObject(i)
                        .getInt("object_id") //내가 구매한 아이템들
                    buyItem.add(objectId)

                    getCurrentItem()
                    Log.d("구매한 아이템","${objectId}")
                    Log.d("착용중인 아이템","${currentItem}")
                    runOnUiThread {
                        if (currentItem != null && currentItem == objectId) {
                            Log.d("ksdjflksjfklsef","$currentItem")

                            //사용자가 구매한 아이템중 착용한 아이템
                            yesBtnViewMap[objectId]?.visibility = View.VISIBLE
                            noBtnViewMap[objectId]?.visibility = View.INVISIBLE

                            yesBtnViewMap[objectId]?.setOnClickListener{
                                val itemThread = Thread {
                                    // 서버로 보낼 JSON 객체 생성
                                    val requestData = JSONObject()
                                    requestData.put("user_name", getName()) // 사용자 이름 또는 식별자
                                    requestData.put("moun_shop", null) // 아이템 아이디 (null로 설정하여 착용 해제)

                                    try {
                                        Request().reqpost(
                                            "http://dmumars.kro.kr/api/setuserfititem", requestData
                                        )
                                        load()
                                        // 서버 응답을 기다리지 않고 바로 UI를 업데이트할 수 있다면 여기에서 UI 업데이트 코드를 추가할 수 있습니다.
                                    } catch (e: Exception) {
                                        // 예외 처리 코드 추가
                                        e.printStackTrace()
                                    }
                                }
                                itemThread.start()
                                itemThread.join()
                            }

                        } else {
                            //사용자가 구매한 아이템중 미착용 아이템
                            yesBtnViewMap[objectId]?.visibility = View.INVISIBLE
                            noBtnViewMap[objectId]?.visibility = View.VISIBLE

                            noBtnViewMap[objectId]?.setOnClickListener {

                                Thread{
                                    // 서버로 보낼 JSON 객체 생성
                                    val requestData = JSONObject()
                                    requestData.put("user_name", getName()) // 사용자 이름 또는 식별자
                                    requestData.put("moun_shop", objectId) // 아이템 아이디 (null로 설정하여 착용 해제)

                                    try {
                                        Request().reqpost("http://dmumars.kro.kr/api/setuserfititem", requestData)
                                        // 서버 응답을 기다리지 않고 바로 UI를 업데이트할 수 있다면 여기에서 UI 업데이트 코드를 추가할 수 있습니다.
                                        load()
                                    } catch (e: Exception) {
                                        // 예외 처리 코드 추가
                                        e.printStackTrace()
                                    }
                                }.start()
                            }
                        }
                        itemIdViewMap[objectId]?.setBackgroundResource(R.drawable.act_btn_click)
                        //yesBtnViewMap[objectId]?.setBackgroundResource(R.drawable.wear_yes)
                        //noBtnViewMap[objectId]?.setBackgroundResource(R.drawable.wear_no)
                    }

                }
            } catch (e: UnknownServiceException) {
                println(e.message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        buyItemThread.start()
        buyItemThread.join()
    }

    private fun getCurrentItem(){

        val mythread=
            Thread {
                try {
                    val jsonObject = Request().reqget("http://dmumars.kro.kr/api/getuserfititem/${getName()}")
                    currentItem = jsonObject.getInt("moun_shop")
                } catch (e: UnknownServiceException) {
                    println(e.message)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        mythread.start()
        mythread.join()

    }


    private fun categoryClickListener() {
        val clklistener = View.OnClickListener {
            when (it.id) {
                R.id.headImage -> {
                    binding.headView.setBackgroundColor(Color.parseColor("#FF9C46"))
                    binding.bodyView.setBackgroundColor(Color.parseColor("#DDDDDD"))
                    binding.etcView.setBackgroundColor(Color.parseColor("#DDDDDD"))
                }
                R.id.bodyImage -> {
                    binding.headView.setBackgroundColor(Color.parseColor("#DDDDDD"))
                    binding.bodyView.setBackgroundColor(Color.parseColor("#FF9C46"))
                    binding.etcView.setBackgroundColor(Color.parseColor("#DDDDDD"))
                }
                R.id.etcImage -> {
                    binding.headView.setBackgroundColor(Color.parseColor("#DDDDDD"))
                    binding.bodyView.setBackgroundColor(Color.parseColor("#DDDDDD"))
                    binding.etcView.setBackgroundColor(Color.parseColor("#FF9C46"))
                }
            }
        }

        binding.headImage.setOnClickListener(clklistener)
        binding.bodyImage.setOnClickListener(clklistener)
        binding.etcImage.setOnClickListener(clklistener)
    }

    //DB에서 고양이 색상, 표정, 아이템여부를 가지고 와서 백그라운드 생성
    fun setBackGround(){
        Thread {
            Log.d("고양이 배경넣기","ㅇ")
            val emotion: String
            val color: String
            val moun_shop : Int?
            try {
                val jsonObject = Request().reqget("http://dmumars.kro.kr/api/getuseravatar/${getName()}")
                //색상, 표정, 아이템 여부
                emotion = jsonObject.getString("look")
                color = jsonObject.getString("color")
                moun_shop = jsonObject.optInt("moun_shop", -1) // -1은 기본값으로 사용될 값입니다.

                val imgPath = if (moun_shop > -1) {
                    val item = moun_shop.toString()
                    "${color}_${emotion}_${item}"
                } else {
                    "${color}_${emotion}"
                }
                Log.d("adsfdsfadsfdadfs",imgPath)
                // UI 업데이트를 위해 메인 스레드에서 실행
                runOnUiThread {
                    val imageView = binding.StoreAvatarImage
                    val resID = resources.getIdentifier(imgPath, "drawable", packageName)
                    imageView.setImageResource(resID)
                }
            } catch (e: UnknownServiceException) {
                println(e.message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun getName(): String {
        val pref = getSharedPreferences("userName", 0)
        return pref.getString("name", "").toString()
    }
}