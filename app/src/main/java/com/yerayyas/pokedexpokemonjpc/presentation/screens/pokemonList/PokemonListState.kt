package com.yerayyas.pokedexpokemonjpc.presentation.screens.pokemonList

import com.yerayyas.pokedexpokemonjpc.data.models.PokedexListEntry

data class PokemonListState(
    val pokemonList: List<PokedexListEntry> = emptyList(),
    val loadError: String = "",
    val isLoading: Boolean = false,
    val endReached: Boolean = false,
    val isSearching: Boolean = false,
    val query: String = ""
)
