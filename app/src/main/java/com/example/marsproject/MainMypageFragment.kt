package com.example.marsproject

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.marsproject.databinding.FragmentMainMypageBinding
import com.kakao.sdk.user.UserApiClient
import org.json.JSONObject
import java.net.UnknownServiceException

class MainMypageFragment : Fragment() {
    private lateinit var binding: FragmentMainMypageBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private lateinit var savedname: String // 저장된 닉네임
    private var name: String = "닉네임" // 닉네임
    private var id: String = "아이디" // 아이디

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainMypageBinding.inflate(inflater)

        val contract = ActivityResultContracts.StartActivityForResult()
        val callback = object : ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult?) {
                if (result?.resultCode == AppCompatActivity.RESULT_OK) {
                    (activity as MainActivity).clickchangeFragment(4)
                }
            }
        }
        launcher = registerForActivityResult(contract, callback)

        // 닉네임 정보 불러오기
        savedname = (activity as MainActivity).getName()

        // 유저 데이터 불러오는 쓰레드 생성
        val MypageThread = Thread {
            try {
                val jsonObject = Request().reqget("http://dmumars.kro.kr/api/getuserdata/${savedname}") //get요청

                name = jsonObject.getString("user_name") // 닉네임
                id = jsonObject.getString("user_id") // 아이디
            } catch (e: UnknownServiceException) {
                // API 사용법에 나와있는 모든 오류응답은 여기서 처리
                println(e.message)
                // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        MypageThread.start() // 쓰레드 시작
        MypageThread.join() // 쓰레드 종료될 때까지 대기

        // 유저 데이터 변경
        binding.userNameText.text = name
        binding.userIdText.text = id

        // 항목별 페이지로 이동하는 클릭 리스너 설정
        setMoveClickListener()

        return binding.root
    }

    // 페이지로 이동하는 클릭 리스너 설정
    private fun setMoveClickListener() {
        // 클릭 시 내 칭호로 이동 리스너
        binding.titleLayout.setOnClickListener {
            activity?.let{
                // 인텐트 생성 후 액티비티 생성
                val intent = Intent(context, ChangeTitleActivity::class.java) // 내 칭호 페이지로 설정
                startActivity(intent) // 액티비티 생성
            }
        }

        // 클릭 시 닉네임 변경으로 이동 리스너
        binding.nameLayout.setOnClickListener {
            val intent = Intent(context, ChangeNameActivity::class.java) // 닉네임 변경 페이지로 설정
            launcher?.launch(intent)
        }

        // 클릭 시 상점으로 이동 리스너
        binding.storeLayout.setOnClickListener {
            activity?.let{
                // 인텐트 생성 후 액티비티 생성
                val intent = Intent(context, StoreActivity::class.java) // 상점 페이지로 설정
                startActivity(intent) // 액티비티 생성
            }
        }

        // 클릭 시 친구 목록으로 이동하는 리스너
        binding.friendLayout.setOnClickListener {
            activity?.let{
                // 인텐트 생성 후 액티비티 생성
                val intent = Intent(context, FriendListActivity::class.java) // 친구 목록 페이지로 설정
                startActivity(intent) // 액티비티 생성
            }
        }

        // 클릭 시 로그아웃 및 화면 전환 리스너
        binding.logoutLayout.setOnClickListener{
            val dlg = MyDialog(context as AppCompatActivity) // 커스텀 다이얼로그 객체 저장
            // 예 버튼 클릭 시 실행
            dlg.setOnOKClickedListener{
                // 카카오 계정 연동 해제
                UserApiClient.instance.logout { err ->
                    if (err == null) {
                        Log.d(ContentValues.TAG, "로그아웃 성공")
                    }
                    else {
                        Log.d(ContentValues.TAG, "로그아웃 실패")
                    }
                }

                (activity as MainActivity).clearLogin() // 로그인 정보 삭제
                (activity as MainActivity).clearName() // 닉네임 정보 삭제
                (activity as MainActivity).clickchangeFragment(3) // 홈 프래그먼트로 전환
            }
            // 아니오 버튼 클릭 시 실행
            dlg.setOnNOClickedListener {}
            dlg.show("로그아웃 하시겠습니까?") // 다이얼로그 내용에 담을 텍스트
        }

        // 클릭 시 회원탈퇴 및 화면 전환 리스너
        binding.deleteLayout.setOnClickListener{
            val dlg = MyDialog(context as AppCompatActivity) // 커스텀 다이얼로그 객체 저장
            // 예 버튼 클릭 시 실행
            dlg.setOnOKClickedListener{
                // 회원탈퇴 쓰레드 생성
                val delThread = Thread {
                    try {
                        // 닉네임으로 아이디, 비번 가져오기
                        val idpwjson = JSONObject() //json 생성
                        idpwjson.put("user_name", savedname) // 닉네임

                        // 닉네임으로 아이디, 비번 가져오기
                        val jsonidpw = Request().reqpost("http://dmumars.kro.kr/api/getuseridpd", idpwjson)

                        // 받아온 아이디 저장
                        val id = jsonidpw.getString("id")
                        val pw = jsonidpw.getString("passwd")

                        // 아이디, 비번으로 회원탈퇴
                        val deljson = JSONObject() //json 생성
                        deljson.put("id", id) // 아이디
                        deljson.put("passwd", pw) // 패스워드

                        // 닉네임으로 아이디, 비번 가져오기
                        Request().reqpost("http://dmumars.kro.kr/api/deluser", deljson)

                    } catch (e: UnknownServiceException) {
                        // 해당 유저가 없을 때
                        if (e.message == "empty") {
                            Log.e(ContentValues.TAG, "회원탈퇴 유저 찾기 실패")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                delThread.start() // 쓰레드 실행
                delThread.join() // 쓰레드 종료될 때까지 대기

                // 카카오 계정 연동 해제
                UserApiClient.instance.unlink { err ->
                    if (err == null) {
                        Log.d(ContentValues.TAG, "연동해제 성공")
                    }
                    else {
                        Log.d(ContentValues.TAG, "연동해제 실패")
                    }
                }

                (activity as MainActivity).clearLogin() // 로그인 정보 삭제
                (activity as MainActivity).clearName() // 닉네임 정보 삭제
                (activity as MainActivity).clickchangeFragment(3) // 홈 프래그먼트로 전환
            }
            // 아니오 버튼 클릭 시 실행
            dlg.setOnNOClickedListener {}
            dlg.show("회원탈퇴 하시겠습니까?") // 다이얼로그 내용에 담을 텍스트
        }
    }
}