package com.example.appdanini.data.model.request.invite

// 피공유자가 그룹 아이디를 받을 수 있는 시점
data class UpdateFamliyNameResponse(
    val groupId : Long,
    val groupName : String,
    val updatedAt : String
)
