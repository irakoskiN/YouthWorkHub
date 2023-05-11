package com.youthworkhub.model

import com.google.firebase.firestore.DocumentReference

data class CreateJobModel(
    var description: String?,
    var location: String?,
    var owner: DocumentReference?,
    var timestamp: Long?,
    var title: String?,
    var price: String?,
    var skills: String?,
    var image: String? = null
) {

}