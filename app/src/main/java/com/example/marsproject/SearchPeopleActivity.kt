package com.example.marsproject

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.example.marsproject.databinding.ActivityFriendDialogBinding
import com.example.marsproject.databinding.ActivitySearchPeopleBinding
import org.json.JSONObject
import java.net.UnknownServiceException

// 권한 오류 방지
@SuppressLint("MissingPermission")
class SearchPeopleActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchPeopleBinding
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothsearch: BluetoothSearch

    private lateinit var statusTextView: TextView
    private lateinit var findUser: ImageView
    private lateinit var fadeInAnimation: Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchPeopleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar) // 툴바 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize) // 뒤로가기 버튼 이미지 설정
        supportActionBar?.title = "주변 친구 찾기" // 타이틀 지정

        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothsearch = BluetoothSearch(bluetoothManager)

        statusTextView = binding.searchingText
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        // 애니메이션을 텍스트 뷰에 적용
        statusTextView.startAnimation(fadeInAnimation)
        statusTextView.setOnClickListener {}
        statusTextView.visibility = TextView.VISIBLE

        // 사용자 찾기 버튼 클릭 리스너
        if (!bluetoothsearch.bluetoothAdapter.isEnabled) {
            // 블루투스 활성화 안될때 활성화 시키기
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            resultLaunch.launch(enableBtIntent) // 이 함수를 사용하면 resultLaunch 메소드가 실행됨
        } else {
            // 2분동안 다른 블루투스 장치를 찾음
            bluetoothsearch.startbluetoothSearch(bluetoothSearchCallback, 2)
        }
    }

    // 블루투스가 활성화 되어있지 않을때 사용자로 부터 요청을 받음
    private val resultLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // 사용자가 블루투스 사용을 허용 했을때
        if (result.resultCode == -1) {
            // 2분동안 다른 블루투스 장치를 찾음
            if (bluetoothsearch.startbluetoothSearch(bluetoothSearchCallback, 2)) {
                statusTextView = binding.searchingText
                fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

                // 애니메이션을 텍스트 뷰에 적용
                statusTextView.startAnimation(fadeInAnimation)
                statusTextView.visibility = TextView.VISIBLE
            }
        } else {
            // 사용자가 블루투스 허용 안하면 메인으로
            finish()
        }
    }

    private val bluetoothSearchCallback = object : ScanCallback() {
        private val isdup = ArrayList<String>() // 장치 중복 제거용 ArrayList

        // 블루투스가 장치를 찾을 때 마다 해당 함수를 실행함(비동기임)
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            val devicename = result?.device?.name
            val devicemac = result?.device?.address
            val deviceuuid = result?.scanRecord?.serviceUuids?.get(0) // 이 uuid를 db에서 스켄 해야함

            // 장치 검색 결과 중복 제거 및 uuid null 로 나오는거 제거
            if (isdup.contains(deviceuuid.toString()) || deviceuuid == null) {
                return
            }
            isdup.add(deviceuuid.toString())

            Log.d("블루투스", "장치이름 : $devicename")
            Log.d("블루투스", "mac 주소 : $devicemac")
            Log.d("블루투스", "UUID : $deviceuuid")

            Thread {
                try {
                    // 찾은 사용자 데이터 가져오기
                    val frienddata = Request().reqget("http://dmumars.kro.kr/api/getbtuserdata/${deviceuuid}")

                    // Thread 안에서 수행할때 오류가 나는 코드들을 runOnUiThread 로 감싸서 수행
                    runOnUiThread {
                        findUser = binding.friend1
                        findUser.startAnimation(fadeInAnimation) // 이미지 뷰에 애니메이션 적용

                        val screenWidth = 409
                        val screenHeight = 673

                        val randomX = (0..screenWidth).random()
                        val randomY = (0..screenHeight).random()

                        findUser.x = randomX.toFloat()
                        findUser.y = randomY.toFloat()

                        findUser.visibility = ImageView.VISIBLE

                        // 찾은 사용자 다이얼로그로 보여주기
                        findUser.setOnClickListener {
                            // 친구추가 다이얼 로그 생성
                            val dialog = FriendDialog(frienddata)

                            //알림창이 띄워져있는 동안 배경 클릭 막기
                            dialog.isCancelable = false

                            // 친구추가 다이얼 로그 띄우기
                            dialog.show(supportFragmentManager, "FriendDialog")

                            findUser.visibility = ImageView.INVISIBLE
                        }
                    }
                } catch (e: UnknownServiceException) {
                    Log.d("블루투스", "해당 uuid는 앱 사용자가 아님")
                }
            }.start()
        }
    }


    // 친구추가 다이얼 로그 구성
    class FriendDialog(val FriendData: JSONObject) : DialogFragment() {
        private lateinit var binding: ActivityFriendDialogBinding

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = ActivityFriendDialogBinding.inflate(inflater, container, false)
            val view = binding.root

            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            binding.friendName.text = FriendData.getString("user_name")

            // 친구 추가 버튼 이벤트 처리
            binding.addFriendBtn.setOnClickListener {
                Thread {
                    try {
                        // 저장된 닉네임 가져오기
                        val pref = activity?.getSharedPreferences("userName", 0)
                        val userName = pref?.getString("name", "").toString()

                        // 찾은 유저의 닉네임 가져오기
                        val friendName = FriendData.getString("user_name")
                        Log.d("JSON Response", FriendData.toString())

                        val jsonObject = JSONObject() //json 초기화
                        jsonObject.put("user_name", userName)
                        jsonObject.put("friend", friendName)

                        Request().reqpost("http://dmumars.kro.kr/api/setfriend", jsonObject)
                    } catch (e: UnknownServiceException) {
                        println(e.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()

                val sendMessageThread = Thread {
                    try {
                        // 저장된 닉네임 가져오기
                        val pref = activity?.getSharedPreferences("userName", 0)
                        val userName = pref?.getString("name", "").toString()

                        // 찾은 유저의 닉네임 가져오기
                        val friendName = FriendData.getString("user_name")
                        Log.d("JSON Response", FriendData.toString())

                        //보낼 메세지 내용
                        val message = "${userName}님이 친구요청을 보내셨습니다."

                        // 다른유저에게 메세지 전송
                        val sendUserMsg = JSONObject() //json 생성
                        sendUserMsg.put("user_name", userName)
                        sendUserMsg.put("from_user", friendName)
                        sendUserMsg.put("messge", message)

                        // 유저에게 메세지 전송
                        Request().reqpost("http://dmumars.kro.kr/api/pushuserchat", sendUserMsg)

                        Log.d("SDSAFGDADG", "메세지 보냄")

                    } catch (e: UnknownServiceException) {
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                sendMessageThread.start()
                dismiss()
            }

            binding.finishBtn.setOnClickListener {
                dismiss()
            }
            return view
        }
    }

    // 옵션 메뉴 클릭 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> { // 뒤로 가기 버튼 눌렀을 때
                bluetoothsearch.stopbluetoothSearch(bluetoothSearchCallback)
                finish() // 액티비티 종료
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 엑티비티 종료시 유저찾기 종료되게
    override fun onDestroy() {
        super.onDestroy()
        bluetoothsearch.stopbluetoothSearch(bluetoothSearchCallback)
    }
}