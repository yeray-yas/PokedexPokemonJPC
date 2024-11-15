package com.yerayyas.pokedexpokemonjpc.data.remote.responses

import com.google.gson.annotations.SerializedName

data class Stat(
    @SerializedName("base_stat") val baseStat: Int,
    val effort: Int,
    @SerializedName("stat") val stat: StatX
)
