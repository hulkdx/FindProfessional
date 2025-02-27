package com.hulkdx.findprofessional.feature.pro.profile

import com.hulkdx.findprofessional.feature.pro.profile.edit.EditProProfileViewModel
import com.hulkdx.findprofessional.feature.pro.profile.edit.SaveProUserUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val proProfileModule: Module
    get() = module {
        viewModelOf(::ProProfileViewModel)
        viewModelOf(::EditProProfileViewModel)

        factoryOf(::SaveProUserUseCase)
    }
