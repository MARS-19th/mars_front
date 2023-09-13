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

            // 이메일 로그인 콜백
            val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e(TAG, "로그인 실패 $error")
                } else if (token != null) {
                    Log.e(TAG, "로그인 성공 ${token.accessToken}")
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
                            // 완료 결과 보내기
                            val intentM = Intent()
                            intentM.putExtra("id", user.kakaoAccount?.email)
                            setResult(RESULT_OK, intentM)
                            finish()
                        }
                    }
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
                                // 완료 결과 보내기
                                val intentM = Intent()
                                intentM.putExtra("id", user.kakaoAccount?.email)
                                setResult(RESULT_OK, intentM)
                                finish()
                            }
                        }
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(
                    this,
                    callback = mCallback
                ) // 카카오 이메일 로그인
            }
        }
    }
}