package com.yerayyas.pokedexpokemonjpc.presentation.screens.pokemonList

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import coil.request.SuccessResult
import com.yerayyas.pokedexpokemonjpc.data.models.PokedexListEntry
import com.yerayyas.pokedexpokemonjpc.data.remote.repository.PokemonRepository
import com.yerayyas.pokedexpokemonjpc.util.Constants.PAGE_SIZE
import com.yerayyas.pokedexpokemonjpc.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private var curPage = 0

    private val _pokemonList = MutableLiveData<List<PokedexListEntry>>(emptyList())
    val pokemonList: LiveData<List<PokedexListEntry>> = _pokemonList

    private val _loadError = MutableLiveData("")
    val loadError: LiveData<String> = _loadError

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _endReached = MutableLiveData(false)
    val endReached: LiveData<Boolean> = _endReached

    private var cachedPokemonList = listOf<PokedexListEntry>()
    private var isSearchStarting = true

    var _isSearching = MutableLiveData(false)
    val isSearching: LiveData<Boolean> = _isSearching

    init {
        loadPokemonPaginated()
    }

    fun searchPokemonList(query: String) {
        val listToSearch = if (isSearchStarting) {
            _pokemonList.value
        } else {
            cachedPokemonList
        }
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                withContext(Dispatchers.Main) {
                    _pokemonList.value = cachedPokemonList
                    _isSearching.value = false
                }
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch?.filter {
                it.pokemonName.contains(query.trim(), ignoreCase = true) ||
                        it.number.toString() == query.trim()
            }
            if (isSearchStarting) {
                cachedPokemonList = _pokemonList.value ?: emptyList()
                isSearchStarting = false
            }
            withContext(Dispatchers.Main) {
                _pokemonList.value = results ?: emptyList()
                _isSearching.value = true
            }
        }
    }

    @SuppressLint("LogNotTimber")
    fun loadPokemonPaginated() {
        Log.d("PokemonListViewModel", "Loading Pokémon...")
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getPokemonList(PAGE_SIZE, curPage * PAGE_SIZE)) {
                is Resource.Success -> {
                    _endReached.value = curPage * PAGE_SIZE >= result.data!!.count
                    val pokedexEntries = result.data.results.mapIndexed { index, entry ->
                        val number = if (entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }
                        val url =
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokedexListEntry(entry.name.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                        }, url, number.toInt())
                    }
                    curPage++

                    _loadError.value = ""
                    _isLoading.value = false
                    _pokemonList.value = _pokemonList.value?.plus(pokedexEntries) ?: pokedexEntries
                    Log.d("PokemonListViewModel", "loadPokemonPaginated: ${_pokemonList.value}")
                }

                is Resource.Error -> {
                    _loadError.value = result.message ?: "Unknown error"
                    _isLoading.value = false
                    Log.d("PokemonListViewModel", "loadPokemonPaginated: ${_loadError.value}")
                }

                is Resource.Loading -> TODO()
            }
        }
    }

    fun calcDominantColor(drawable: SuccessResult, onFinish: (Color) -> Unit) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}
