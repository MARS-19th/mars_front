package com.example.marsproject

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.marsproject.databinding.ActivitySearchPeopleBinding

@SuppressLint("MissingPermission")
class SearchPeopleActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchPeopleBinding
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothsearch: BluetoothSearch
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchPeopleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_left_resize)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothsearch = BluetoothSearch(bluetoothManager)
        // 사용자 찾기 버튼 클릭 리스너
        binding.searchButton.setOnClickListener {
            if (!bluetoothsearch.bluetoothAdapter.isEnabled) {
                // 블루투스 활성화 안될때 활성화 시키기
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                resultLaunch.launch(enableBtIntent) // 이 함수를 사용하면 resultLaunch 메소드가 실행됨
            } else {
                // 2분동안 다른 블루투스 장치를 찾음
                if (bluetoothsearch.startbluetoothSearch(2)) {
                    Toast.makeText(applicationContext, "다른 플레이어를 찾는중....", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 블루투스가 활성화 되어있지 않을때 사용자로 부터 요청을 받음
    val resultLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // 사용자가 블루투스 사용을 허용 했을때
        if (result.resultCode == -1) {
            // 2분동안 다른 블루투스 장치를 찾음
            if (bluetoothsearch.startbluetoothSearch(2)) {
                Toast.makeText(applicationContext, "다른 플레이어를 찾는중....", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val bluetoothSearchCallback = object : ScanCallback() {
        private val isdup = ArrayList<String>() // 장치 중복 제거용 ArrayList

        // 블루투스가 장치를 찾을 때 마다 해당 함수를 실행함(비동기임)
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            val name = result?.device?.name
            val uuid = result?.scanRecord?.serviceUuids?.get(0).toString() // 이 uuid를 db에서 스켄 해야함
            val mac = result?.device?.address

            // 장치 검색 결과 중복 제거 및 uuid null 로 나오는거 제거
            if (!isdup.contains(mac.toString()) && uuid != "null") {
                isdup.add(mac.toString())

                // TODO: 이제 여기다 블루투스 장치를 찾을때 마다 api /getbtuserdata/[uuid] 에서 uuid 보내서 유저 정보 얻는거 만들면 됨
                // mac 주소를 보내고 싶으나 안드로이드 보안 정책상 자기 블루트스 mac 주소를 얻을 수 없음
                // 그래서 로그인 할 때 랜덤 UUID 를 발급하여 그걸로 장치를 식별 하기로 함

                Log.d("로그", "장치이름 : $name")
                // Advertiser의 31 바이트 제한으로 장치이름은 보내지 않음 즉 null로 표시
                Log.d("로그", "mac 주소 : $mac")
                Log.d("로그", "UUID : $uuid")
                // 일반적인 블루투스장치의 경우 uuid는 null로 뜰꺼임
                // 앱을 킨 사람은 ble로 uuid를 넣어서 계속 해서 신호를 보니기 때문에 정상적으로 출력 될꺼임
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> { // 뒤로 가기 버튼 눌렀을 때
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}