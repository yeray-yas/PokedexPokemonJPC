package com.yerayyas.pokedexpokemonjpc.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.yerayyas.pokedexpokemonjpc.presentation.navigation.NavigationWrapper
import com.yerayyas.pokedexpokemonjpc.ui.theme.PokedexPokemonJPCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navHostController = rememberNavController()
            PokedexPokemonJPCTheme {
                NavigationWrapper(navHostController)
            }
        }
    }
}
