package com.example.marsproject

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class LoginApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들

        // Kakao SDK 초기화
        KakaoSdk.init(this, "3defac72ba0c94d5ae9acfc439136a87")
    }
}