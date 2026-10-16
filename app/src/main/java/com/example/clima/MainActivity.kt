package com.example.clima

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clima.presentacion.ClimaViewModel
import com.example.clima.ui.ClimaApp
import com.example.clima.ui.ClimaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClimaTheme {
                val climaViewModel: ClimaViewModel = viewModel()
                ClimaApp(climaViewModel)
            }
        }
    }
}
