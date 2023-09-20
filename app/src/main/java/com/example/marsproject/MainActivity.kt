package com.example.marsproject

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.example.marsproject.databinding.ActivityMainBinding
import com.kakao.sdk.user.Constants
import org.json.JSONObject
import java.net.UnknownServiceException
import java.util.UUID


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var uuid: ParcelUuid   //블루투스 장치 식별용 랜덤 값
    private lateinit var savedID: String // 저장된 아이디
    private lateinit var savedPW: String // 저장된 비번
    var launcher: ActivityResultLauncher<Intent>? = null
    private var skill: String = ""
    private var login: String = "ok"
    private val PERMISSIONS_REQUEST = 1 // 권한 요청 레벨

    // 권한 체크가 비동기 이므로 onCreate 에서는 권한 체크만하고 onRequestPermissionsResult 콜백 함수에서 나머지 실행
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 블루투스 사용시 위치 및 블루투스 권한허용을 해야함
        val permissionlist: Array<String>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // 안드로이드 12 이후 권한
            permissionlist = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE
            )
        } else {
            // 안드로이드 12 이전 권한
            permissionlist = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
        if (!checkpermission(this, permissionlist)) {
            requestPermissions(permissionlist, PERMISSIONS_REQUEST)
            // 권한 체크 하고 사용자로 부터 권한 받아오기
        } else {
            // 처음 시작 할때는 onRequestPermissionsResult 에서 권한 확인 받고 findlogin 함수가 실행
            Thread {
                Looper.prepare()
                findlogin()
            }.start()
        }
        /* 권한 확인 끝 */

        // ActivityCallback 등록
        val contract = ActivityResultContracts.StartActivityForResult()
        launcher = registerForActivityResult(contract, callback)
    }

    // 로그인 정보 불러오는 함수 (사실상 앱에 스타트)
    fun findlogin() {
        val pref = getSharedPreferences("userLogin", 0)
        savedID = pref.getString("id", "").toString()
        savedPW = pref.getString("pw", "").toString()

        Log.d(Constants.TAG, "로그인 아이디 : $savedID")

        if (savedID == "" && savedPW == "") {
            uuid = ParcelUuid.fromString(UUID.randomUUID().toString())
            Log.e("블루투스", "발급된 uuid: $uuid")
            // 최초로그인 시 블루투스를 식별하는 값 발급
            // TODO: 해당 uuid를 api 에 /setuserbtuuid 넘기는 작업 필요

            // 로컬에 uuid 저장
            val save = getSharedPreferences("bt_uuid", MODE_PRIVATE).edit()
            save.putString("uuid", uuid.toString()) // 값 넣기
            save.apply() // 적용하기

            // 로그인 페이지로 가도록
            login = "login"
        } else {
            try {
                // 로컬에서 저장한 uuid 갖고오기
                val getuuid = getSharedPreferences("bt_uuid", 0)
                uuid = ParcelUuid.fromString(getuuid.getString("uuid", "").toString())
                Log.e("블루투스", "가져온 UUID: $uuid")

                val outputjson = JSONObject() //json 생성
                outputjson.put("id", savedID) // 아이디
                outputjson.put("passwd", savedPW) // 비밀번호

                val jsonObject = Request().reqpost("http://dmumars.kro.kr/api/login", outputjson)
                // jsonObject 변수에는 정상응답 json 객체가 저장되어있음

                // getter는 자료형 별로 getint getJSONArray 이런것들이 있으니 결과 값에 따라 메소드를 변경해서 쓸것
            } catch (e: UnknownServiceException) {
                // API 사용법에 나와있는 모든 오류응답은 여기서 처리

                if (e.message == "is_new") {
                    // 설정 페이지로 가도록
                    login = "is_new"
                }
                println(e.message)
                // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        Log.d(Constants.TAG, "로그인 변수 : $login")
        when (login) {
            "login" -> {
                // 사용자가 로그아웃 된 상태라면 [LoginActivity] 실행
                val intentL = Intent(this, LoginActivity::class.java)
                launcher?.launch(intentL)
            }
            "is_new" -> {
                // 사용자가 로그인 되었지만 닉네임 정보를 입력하지 않으면 [SettingNameActivity] 실행
                val intentN = Intent(this, SettingNameActivity::class.java)
                intentN.putExtra("email", savedID)
                launcher?.launch(intentN)
            }
            else -> {
                // 제대로 로그인 된 상태면 [ListenerRegistration] 함수 실행
                // 엑티비티 시작은 비동기 이므로 인텐트 callback 함수에서 ListenerRegistration 메소드 실행
                ListenerRegistration()
            }
        }
    }

    // 엑티비티 callback 처리 (메인 코드에 함수화로 ActivityResultCallback을 함수 안에 선언 불가)
    val callback = object : ActivityResultCallback<ActivityResult> {
        override fun onActivityResult(result: ActivityResult?) {
            // callback 함수는 메인쓰레드에서 실행함으로 새로운 쓰레드 생성
            Thread {
                Looper.prepare()

                // LoginActivity 에서 넘어온 이후 작업
                if (login == "login") {
                    if (result?.resultCode == RESULT_OK) {
                        var id = ""
                        var pw = "qwer1234"
                        id = result.data?.getStringExtra("id") ?: ""
                        Log.d(Constants.TAG, "사용자 이메일 : $id")

                        // 회원가입 & 로그인
                        try {
                            val outputjson = JSONObject() //json 생성
                            outputjson.put("id", id) // 아이디
                            outputjson.put("passwd", pw) // 비밀번호

                            Request().reqpost("http://dmumars.kro.kr/api/setperson", outputjson)
                            // jsonObject 변수에는 정상응답 json 객체가 저장되어있음

                            // getter는 자료형 별로 getint getJSONArray 이런것들이 있으니 결과 값에 따라 메소드를 변경해서 쓸것
                        } catch (e: UnknownServiceException) {
                            // API 사용법에 나와있는 모든 오류응답은 여기서 처리

                            if (e.message == "ER_DUP_ENTRY") { /* 중복오류 발생 예외처리구문 */
                            }
                            println(e.message)
                            // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        // 로그인 정보 저장
                        // 추후에 로그인 페이지에 사용
                        saveLogin(id, pw)

                        // 로그인 정보 불러오기
                        try {
                            val pref = getSharedPreferences("userLogin", 0)
                            savedID = pref.getString("id", "").toString()
                            savedPW = pref.getString("pw", "").toString()

                            val outputjson = JSONObject() //json 생성
                            outputjson.put("id", savedID) // 아이디
                            outputjson.put("passwd", savedPW) // 비밀번호

                            val jsonObject =
                                Request().reqpost("http://dmumars.kro.kr/api/login", outputjson)
                            // jsonObject 변수에는 정상응답 json 객체가 저장되어있음

                            saveName(jsonObject.getString("user_name"))
                            // getter는 자료형 별로 getint getJSONArray 이런것들이 있으니 결과 값에 따라 메소드를 변경해서 쓸것
                        } catch (e: UnknownServiceException) {
                            // API 사용법에 나와있는 모든 오류응답은 여기서 처리
                            println(e.message)
                            // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    finish() //인텐트 종료

                    overridePendingTransition(0, 0) //인텐트 효과 없애기
                    val intent = intent //인텐트
                    startActivity(intent) //액티비티 열기
                    overridePendingTransition(0, 0) //인텐트 효과 없애기
                }

                // SettingNameActivity 에서 넘어온 이후 작업
                else if (login == "is_new") {
                    if (result?.resultCode == RESULT_OK) {  // 닉네임, 아바타, 목표 설정이 끝났을 때
                        // 닉네임 정보 저장하기
                        try {
                            val outputjson = JSONObject() //json 생성
                            outputjson.put("id", savedID) // 아이디
                            outputjson.put("passwd", savedPW) // 비밀번호

                            val jsonObject =
                                Request().reqpost("http://dmumars.kro.kr/api/login", outputjson)
                            // jsonObject 변수에는 정상응답 json 객체가 저장되어있음

                            saveName(jsonObject.getString("user_name"))
                            // getter는 자료형 별로 getint getJSONArray 이런것들이 있으니 결과 값에 따라 메소드를 변경해서 쓸것
                        } catch (e: UnknownServiceException) {
                            // API 사용법에 나와있는 모든 오류응답은 여기서 처리

                            println(e.message)
                            // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        changeFragment(MainHomeFragment())
                    }
                }
                // ListenerRegistration 실행
                ListenerRegistration()
            }.start()
        }
    }

    // 리스너 설정 및 메인 화면 구성
    @SuppressLint("ResourceType")
    fun ListenerRegistration() {
        val menuBottomNavigation = binding.menuBottomNavigation

        // 하단 탭 클릭 리스너 설정
        menuBottomNavigation.setOnItemSelectedListener { item ->
            changeFragment(
                when (item.itemId) {
                    R.id.menu_home -> { // 홈 탭 클릭
                        menuBottomNavigation.itemIconTintList =
                            ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        menuBottomNavigation.itemTextColor =
                            ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        MainHomeFragment()
                    }

                    R.id.menu_objective -> { // 목표 탭 클릭
                        menuBottomNavigation.itemIconTintList =
                            ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        menuBottomNavigation.itemTextColor =
                            ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        MainObjectiveFragment()
                    }

                    else -> { // 마이 탭 클릭
                        menuBottomNavigation.itemIconTintList =
                            ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        menuBottomNavigation.itemTextColor =
                            ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                        MainMypageFragment()
                    }
                }
            )
            true
        }

        // 검색 가능 모드 활성화 (이제 다른 사용자가 해당 사용자 식별 가능)
        // 성공하면 로그에 성공적으로 광고 뜸
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        BluetoothSearch(bluetoothManager).BluetoothDiscoverable(uuid)

        // 초기 화면 홈으로 설정
        menuBottomNavigation.selectedItemId = R.id.menu_home
    }

    // 프래그먼트 변경 함수
    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.menu_frame_layout, fragment)
            .commit()
    }

    // 클릭 시 프래그먼트 변경 함수
    @SuppressLint("ResourceType")
    fun clickchangeFragment(index: Int) {
        when (index) {
            1 -> { // 홈 프래그먼트에서 사용
                supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
                val menuBottomNavigation = binding.menuBottomNavigation
                menuBottomNavigation.selectedItemId = R.id.menu_objective
                menuBottomNavigation.itemIconTintList =
                    ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                menuBottomNavigation.itemTextColor =
                    ContextCompat.getColorStateList(this, R.drawable.menu_item_color)
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.menu_frame_layout, MainObjectiveFragment())
                    .commit()
            }

            2 -> { // 목표 프래그먼트에서 사용
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.menu_frame_layout, MainDetailStudyFragment())
                    .addToBackStack(null)
                    .commit()
            }

            3 -> { // 로그아웃 사용
                finish() //인텐트 종료
                overridePendingTransition(0, 0) //인텐트 효과 없애기
                val intent = intent //인텐트
                startActivity(intent) //액티비티 열기
                overridePendingTransition(0, 0) //인텐트 효과 없애기
            }
        }
    }

    // 로그인 정보를 저장하는 함수
    fun saveLogin(loginID: String, loginPW: String) {
        val pref = getSharedPreferences("userLogin", MODE_PRIVATE) //shared key 설정
        val edit = pref.edit() // 수정모드
        edit.putString("id", loginID) // 값 넣기
        edit.putString("pw", loginPW) // 값 넣기
        edit.apply() // 적용하기
    }

    // 로그인 정보를 삭제하는 함수
    fun clearLogin() {
        val pref = getSharedPreferences("userLogin", MODE_PRIVATE) //shared key 설정
        val edit = pref.edit() // 수정모드
        edit.clear() // 삭제하기
        edit.apply() // 적용하기
    }

    // 닉네임 정보를 저장하는 함수
    fun saveName(userName: String) {
        val pref = getSharedPreferences("userName", MODE_PRIVATE) //shared key 설정
        val edit = pref.edit() // 수정모드
        edit.putString("name", userName) // 값 넣기
        edit.apply() // 적용하기
    }

    fun clearName() {
        val pref = getSharedPreferences("userName", MODE_PRIVATE) //shared key 설정
        val edit = pref.edit() // 수정모드
        edit.clear() // 삭제하기
        edit.apply() // 적용하기
    }

    // 닉네임 정보를 가져오는 함수
    fun getName(): String {
        val pref = getSharedPreferences("userName", 0)
        return pref.getString("name", "").toString()
    }

    // 목표에서 선택한 이름을 저장하는 함수
    fun setSkill(name: String) {
        skill = name
    }

    // 상세 목표에서 선택한 이름을 가져오는 함수
    fun getSkill(): String {
        return skill
    }

    // 사용자가 권한을 수락하면 비로소 여기서 액티비티가 실행됨
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Thread {
                Looper.prepare()
                findlogin()
            }.start()
        } else {
            // 하나라도 수락안하면 다시 할수 있게
            requestPermissions(permissions, PERMISSIONS_REQUEST)
            Toast.makeText(this, "권한을 수락해야 합니다!", Toast.LENGTH_SHORT).show()
        }
    }

    // 권한 확인 하는 함수
    fun checkpermission(context: Context, list: Array<String>): Boolean {
        for (permission in list) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }
}