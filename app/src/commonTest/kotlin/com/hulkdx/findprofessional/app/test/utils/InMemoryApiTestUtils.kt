package com.hulkdx.findprofessional.app.test.utils

import com.hulkdx.findprofessional.core.features.pro.model.ProfessionalAvailability
import com.hulkdx.findprofessional.core.features.user.ProUser
import com.hulkdx.findprofessional.core.features.user.Token
import com.hulkdx.findprofessional.core.features.user.User
import com.hulkdx.findprofessional.core.features.user.UserData
import com.hulkdx.findprofessional.core.features.user.UserType
import com.hulkdx.findprofessional.feature.developer.InMemoryApi

val inMemoryApi by lazy { get<InMemoryApi>() }

fun setUser(email: String, password: String) {
    setUser(User(email = email, firstName = "", lastName = ""))
}

fun setProUser(email: String, password: String) {
    setUser(ProUser(email = email))
}

fun setProAvailability(availability: List<ProfessionalAvailability>) =
    inMemoryApi.setProAvailability(availability)

fun setUser(user: UserType) {
    inMemoryApi.setUserData(
        UserData(
            Token(
                "uiTestAccessToken",
                "uiTestRefreshToken",
            ),
            user,
        )
    )
}
