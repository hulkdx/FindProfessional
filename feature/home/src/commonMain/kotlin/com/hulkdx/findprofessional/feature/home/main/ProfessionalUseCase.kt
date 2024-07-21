package com.hulkdx.findprofessional.feature.home.main

import com.hulkdx.findprofessional.core.model.pro.Professional
import com.hulkdx.findprofessional.core.utils.generalError
import com.hulkdx.findprofessional.feature.home.main.api.ProfessionalApi


class ProfessionalUseCase(
    private val api: ProfessionalApi,
) {
    suspend fun findAll() = try {
        val response = api.findAll()
        response to null
    } catch (e: Exception) {
        emptyList<Professional>() to e.generalError()
    }
}
