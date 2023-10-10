package com.example.marsproject

// 일일 목표 등록할 때 리스트의 각 아이템의 내용이 담길 데이터 클래스
// 체크 유무, 목표 내용
data class ObjectiveItem (val id: Int, var check: String, val content: String)