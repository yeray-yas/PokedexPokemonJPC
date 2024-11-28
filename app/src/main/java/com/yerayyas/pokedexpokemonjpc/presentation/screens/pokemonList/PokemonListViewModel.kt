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
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private var curPage = 0

    private val _pokemonList = MutableLiveData<List<PokedexListEntry>>(emptyList())
    val pokemonList: LiveData<List<PokedexListEntry>> get() = _pokemonList

    private val _loadError = MutableLiveData("")
    val loadError: LiveData<String> get() = _loadError

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _endReached = MutableLiveData(false)
    val endReached: LiveData<Boolean> get() = _endReached

    init {
        loadPokemonPaginated()
    }

    @SuppressLint("LogNotTimber")
    fun loadPokemonPaginated() {
        Log.d("PokemonListViewModel", "Cargando Pokémon...")
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
