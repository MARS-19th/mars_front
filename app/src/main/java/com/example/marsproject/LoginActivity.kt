package com.example.marsproject


import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.marsproject.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //kakaohashkey 불러오기
        var keyHash = Utility.getKeyHash(this)
        Log.i(ContentValues.TAG, "keyhash : $keyHash")



        binding.kakaoLoginBtn.setOnClickListener {

            if (AuthApiClient.instance.hasToken()) {
                UserApiClient.instance.accessTokenInfo { _, error ->
                    if (error != null) {
                        if (error is KakaoSdkError && error.isInvalidTokenError() == true) {
                            // 로그인이 필요합니다. (토큰이 유효하지 않음)
                            // 여기서 로그인 화면으로 이동하거나 로그인을 재시도하는 로직을 구현할 수 있습니다.
                            // 예를 들어, 다음과 같이 로그인 화면으로 이동할 수 있습니다.
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish() // 현재 액티비티를 종료하거나 다른 로그인 관련 로직을 수행할 수 있습니다.
                        } else {
                            Log.e(TAG, "토큰이 유효합니다.")
                            // 기타 에러가 발생했습니다. 에러 처리 로직을 추가하세요.
                            // 예를 들어, 에러 메시지를 표시하거나 다른 조치를 취할 수 있습니다.
                            // error.message를 사용하여 에러 메시지를 가져올 수 있습니다.
                        }
                    } else {
                        // 토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                        // 여기에서 토큰 유효성 체크가 성공한 경우 필요한 추가 작업을 수행할 수 있습니다.
                        // 예를 들어, 사용자 정보 요청 등의 작업을 진행할 수 있습니다.
                    }
                }
            } else {
                // 로그인이 필요합니다. (토큰이 없음)
                // 여기에서 로그인 화면으로 이동하거나 로그인을 유도하는 로직을 추가하세요.
                // 예를 들어, 다음과 같이 로그인 화면으로 이동할 수 있습니다.
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // 현재 액티비티를 종료하거나 다른 로그인 관련 로직을 수행할 수 있습니다.
            }


            // 이메일 로그인 콜백
            val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e(TAG, "로그인 실패 $error")
                } else if (token != null) {
                    Log.e(TAG, "로그인 성공 ${token.accessToken}")
                    // 완료 결과 보내기
                    val intentM = Intent()
                    setResult(RESULT_OK, intentM)
                    finish()
                }
            }

            // 카카오톡 설치 확인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                // 카카오톡 로그인
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    // 로그인 실패 부분
                    if (error != null) {
                        Log.e(TAG, "로그인 실패 $error")
                        // 사용자가 취소
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        // 다른 오류
                        else {
                            UserApiClient.instance.loginWithKakaoAccount(
                                this,
                                callback = mCallback
                            ) // 카카오 이메일 로그인
                        }
                    }
                    // 로그인 성공 부분
                    else if (token != null) {
                        Log.e(TAG, "로그인 성공 ${token.accessToken}")
                        // 완료 결과 보내기
                        val intentM = Intent()
                        setResult(RESULT_OK, intentM)
                        finish()
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(
                    this,
                    callback = mCallback
                ) // 카카오 이메일 로그인
            }

            // 사용자 정보 요청 (기본)
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(TAG, "사용자 정보 요청 실패", error)
                } else if (user != null) {
                    Log.i(
                        TAG, "사용자 정보 요청 성공" +
                                "\n회원번호: ${user.id}" +
                                "\n이메일: ${user.kakaoAccount?.email}" +
                                "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                    )
                }
            }
        }
    }
}