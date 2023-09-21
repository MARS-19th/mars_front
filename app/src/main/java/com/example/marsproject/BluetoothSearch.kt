/*블루투스 사용을 위한 클래스*/
package com.example.marsproject

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import androidx.core.os.HandlerCompat

// 권한 오류 방지
@SuppressLint("MissingPermission")
class BluetoothSearch(bluetoothManager: BluetoothManager) {
    private val mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    private val advertiser: BluetoothLeAdvertiser
    private val scanner: BluetoothLeScanner
    private var isDiscoverable = false // 블루투스 검색 가능 모드 상태
    private var isScan = false // 블루투스 스캔 모드 상태
    var bluetoothAdapter: BluetoothAdapter

    // 블루투스 초기화
    init {
        bluetoothAdapter = bluetoothManager.adapter
        advertiser = bluetoothAdapter.bluetoothLeAdvertiser
        scanner = bluetoothAdapter.bluetoothLeScanner
    }

    // 다른 장치가 식별 할 수 있도록 검색 가능 모드를 켜주는 함수
    fun BluetoothDiscoverable(randomuuid: ParcelUuid) {
        if (isDiscoverable) {
            Log.d("블루투스", "이미 검색 가능 모드임")
            return
        }
        isDiscoverable = true
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setConnectable(true)
            .setTimeout(0)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .build()
        // 광고모드 설정
        val data = AdvertiseData.Builder()
            .addServiceUuid(randomuuid)
            .build()
        // 보낼 데이터

        // 검색 가능 모드 시작과 결과 리턴
        advertiser.startAdvertising(settings, data, object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                Log.d("블루투스", "성공적으로 광고")
                super.onStartSuccess(settingsInEffect)
            }

            override fun onStartFailure(errorCode: Int) {
                Log.d("블루투스", "광고 실패 code: $errorCode")
                super.onStartFailure(errorCode)
            }
        })
    }

    // 블루투스로 유저 찾기 시작
    fun startbluetoothSearch(scanCallback: ScanCallback, minute: Int): Boolean {
        if (!isScan) {
            // 중복 스캔이 아닌경우
            isScan = true
            scanner.startScan(scanCallback)
            Log.d("블루투스", "스캔이 시작됨")

            mainThreadHandler.postDelayed({
                scanner.stopScan(scanCallback)
                isScan = false
                Log.d("블루투스", "스캔이 종료됨")
            }, (minute * 60 * 1000).toLong())

            return true
        } else {
            // 중복 스캔일 경우
            return false
        }
    }
}