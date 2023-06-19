package com.hulkdx.findprofessional.tests.screen.login

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.hulkdx.findprofessional.common.config.storage.AccessTokenStorage
import com.hulkdx.findprofessional.resources.MR
import com.hulkdx.findprofessional.utils.Rule
import com.hulkdx.findprofessional.utils.assertNodeIsDisplayed
import com.hulkdx.findprofessional.utils.get
import com.hulkdx.findprofessional.utils.onNodeWithTagRes
import com.hulkdx.findprofessional.utils.onNodeWithTextRes
import kotlinx.coroutines.runBlocking

fun launchLoginScreen(
    rule: Rule,
    block: LoginDsl.() -> Unit,
): LoginDsl {
    runBlocking {
        get<AccessTokenStorage>().remove()
    }
    return LoginDsl(rule).apply(block)
}

class LoginDsl(
    private val rule: Rule,
) {
    fun typeEmail(email: String) {
        rule.onNodeWithTextRes(MR.strings.email.resourceId)
            .performTextInput(email)
    }

    fun typePassword(password: String) {
        rule.onNodeWithTextRes(MR.strings.password.resourceId)
            .performTextInput(password)
    }

    fun pressSignUpButton() {
        rule.onNodeWithTagRes(MR.strings.signUp.resourceId)
            .performClick()
    }

    fun pressSignInButton() {
        rule.onNodeWithTextRes(MR.strings.signIn.resourceId)
            .performClick()
    }

    fun pressDeveloperButton() {
        rule.onNodeWithText("dev")
            .performClick()
    }

    fun verify(block: LoginVerify.() -> Unit) = LoginVerify(rule).apply(block)
}

class LoginVerify(
    private val rule: Rule,
) {
    fun signupScreenShown() {
        rule.assertNodeIsDisplayed("SignUpScreen")
    }

    fun homeScreenShown() {
        rule.assertNodeIsDisplayed("HomeScreen")
    }

    fun developerScreenShown() {
        rule.assertNodeIsDisplayed("DeveloperScreen")
    }
}
