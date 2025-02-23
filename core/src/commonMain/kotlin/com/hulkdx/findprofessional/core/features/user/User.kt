package com.hulkdx.findprofessional.core.features.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("normal")
data class User(
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val profileImage: String? = null,
    val skypeId: String? = null,
) : UserType() {
    val fullName: String
        get() = "$firstName $lastName"
}
