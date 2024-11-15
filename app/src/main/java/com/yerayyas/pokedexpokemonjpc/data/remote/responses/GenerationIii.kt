package com.yerayyas.pokedexpokemonjpc.data.remote.responses

import com.google.gson.annotations.SerializedName

data class GenerationIii(
    @SerializedName("firered-leafgreen") val fireredLeafgreen: FireredLeafgreen,
    @SerializedName("ruby-sapphire") val rubySapphire: RubySapphire,
    val emerald: Emerald
)
