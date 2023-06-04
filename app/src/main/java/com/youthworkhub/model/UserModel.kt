package com.youthworkhub.model

data class UserModel(
    var id: String,
    var email: String,
    var username: String,
    var firstname: String? = null,
    var lastname: String? = null,
    var image: String? = null,
)