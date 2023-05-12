package com.youthworkhub.model

data class JobsModel(
    var id: String,
    var description: String?,
    var location: String?,
    var owner: UserModel?,
    var timestamp: Long?,
    var title: String?,
    var price: String?,
    var skills: String?,
    var image: String? = null,
    var saved: Boolean = false
) {
}