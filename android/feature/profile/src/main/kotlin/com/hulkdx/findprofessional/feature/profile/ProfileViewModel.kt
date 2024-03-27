package com.hulkdx.findprofessional.feature.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hulkdx.findprofessional.common.feature.authentication.logout.LogoutUseCase
import com.hulkdx.findprofessional.common.navigation.NavigationScreen
import com.hulkdx.findprofessional.common.navigation.Navigator
import com.hulkdx.findprofessional.common.utils.StringOrRes
import kotlinx.coroutines.launch


class ProfileViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val logoutUseCase: LogoutUseCase,
    private val navigator: Navigator,
) : ViewModel() {
    val error = savedStateHandle.getStateFlow<StringOrRes?>("error", null)

    fun onLogoutClicked() = viewModelScope.launch {
        logoutUseCase.logout()
    }

    fun onBecomeCoachClicked() {
        navigator.navigate(NavigationScreen.SignUpPro)
    }

    fun setError(error: StringOrRes?) {
        savedStateHandle["error"] = error
    }
}
