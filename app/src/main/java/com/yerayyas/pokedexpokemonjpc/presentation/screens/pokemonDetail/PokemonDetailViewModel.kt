package com.yerayyas.pokedexpokemonjpc.presentation.screens.pokemonDetail

import androidx.lifecycle.ViewModel
import com.yerayyas.pokedexpokemonjpc.data.remote.repository.PokemonRepository
import com.yerayyas.pokedexpokemonjpc.data.remote.responses.Pokemon
import com.yerayyas.pokedexpokemonjpc.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
): ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        return repository.getPokemonInfo(pokemonName)
    }
}