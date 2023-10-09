package com.example.marsproject

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.marsproject.databinding.FragmentMainMypageBinding
import com.kakao.sdk.user.UserApiClient
import org.json.JSONObject
import java.net.UnknownServiceException

class MainMypageFragment : Fragment() {
    private lateinit var binding: FragmentMainMypageBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private var savedName: String = "닉네임" // 닉네임
    private var savedID: String = "아이디" // 아이디

    // 프사 선택 후 앱으로 돌아올때 콜백
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            // 파일 uri 값 얻기
            val data = it.data?.data!!

            Thread {
                try {
                    //파일 이름 얻기
                    val filename =  getFilename(data, activity?.applicationContext!!)

                    // 가져온 파일을 inputStream으로 리턴
                    val fileInputStream = activity?.contentResolver?.openInputStream(data)!!

                    // 프사 json 생성
                    val profilejson =  JSONObject()
                    profilejson.put("user_name", savedName)

                    // 파일 보내기
                    Request().fileupload("http://dmumars.kro.kr/api/uploadprofile", profilejson, filename, fileInputStream)

                    // 기존 프로필 사진 디스크 케쉬 지우기
                    Glide.get(requireActivity()).clearDiskCache()

                    // 가저온 이미지로 프사 변경 변경하기
                    activity?.runOnUiThread {
                        binding.userImage.setImageURI(data)
                        Toast.makeText(activity?.applicationContext!!, "프로필 사진 업로드 완료!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    activity?.runOnUiThread {
                        Toast.makeText(activity?.applicationContext!!, "프로필 사진을 업로드 하는 중에 문제가 생겼습니다!", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }
    }

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
        val pref = (activity as MainActivity).getSharedPreferences("userLogin", 0)
        savedID = pref.getString("id", "").toString()
        savedName = (activity as MainActivity).getName()

        // 유저 데이터 변경
        binding.userNameText.text = savedName
        binding.userIdText.text = savedID

        // 서버에서 프사 이미지 가져와서 userImage에 적용하기
        Glide.with(this)
            .load("http://dmumars.kro.kr/api/getprofile/${savedName}")
            .placeholder(R.drawable.profileimage)
            .error(R.drawable.profileimage)
            .skipMemoryCache(true)
            .into(binding.userImage)

        // 항목별 페이지로 이동하는 클릭 리스너 설정
        setMoveClickListener()

        return binding.root
    }

    // 페이지로 이동하는 클릭 리스너 설정
    private fun setMoveClickListener() {
        // 프로필 이미지 클릭시 프사 변경 하는 리스너
        binding.userImage.setOnClickListener {
            // 갤러리 Intent 실행
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryLauncher.launch(intent)
        }

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
                        idpwjson.put("user_name", savedName) // 닉네임

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

    // 파일 이름 얻기
    private fun getFilename(path: Uri?, context: Context): String {
        val c = context.contentResolver.query(path!!, null, null, null, null)
        val index = c?.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
        c?.moveToFirst()
        val result = c?.getString(index!!)
        c?.close()

        return result!!
    }
}