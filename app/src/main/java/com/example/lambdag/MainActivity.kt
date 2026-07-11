package com.example.lambdag

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.lambdag.navigation.NavGraph
import com.example.lambdag.ui.theme.LambdaGTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LambdaGTheme {
                NavGraph()
            }
        }
    }
}