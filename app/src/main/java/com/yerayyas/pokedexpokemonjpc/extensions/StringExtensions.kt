package com.yerayyas.pokedexpokemonjpc.extensions

fun String.extractPokemonNumber(): Int {
    return this.substringAfterLast("/").toIntOrNull() ?: 0
}