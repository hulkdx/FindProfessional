package com.hulkdx.findprofessional.core.storage

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.hulkdx.findprofessional.core.config.PlatformSpecific
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module

private const val FILE_NAME = "datastore_pref.preferences_pb"

val datastoreModule: Module
    get() = module {
        single {
            val platformSpecific = get<PlatformSpecific>()
            val appDirectoryPath = platformSpecific.appDirectoryPath()
            val file = "$appDirectoryPath/$FILE_NAME"
            PreferenceDataStoreFactory.createWithPath(produceFile = { file.toPath() })
        }
    }
