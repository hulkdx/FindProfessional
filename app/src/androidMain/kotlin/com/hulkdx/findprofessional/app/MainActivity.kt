package com.hulkdx.findprofessional.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hulkdx.findprofessional.libs.navigation.decompose.getRootComponent

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val root = getRootComponent()
        setContent {
            App(root)
        }
    }
}
