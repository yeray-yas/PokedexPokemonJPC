package com.yerayyas.pokedexpokemonjpc.di

import com.yerayyas.pokedexpokemonjpc.data.remote.PokeApi
import com.yerayyas.pokedexpokemonjpc.data.remote.repository.PokemonRepository
import com.yerayyas.pokedexpokemonjpc.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePokemonRepository(
        api: PokeApi
    ) = PokemonRepository(api)

    @Provides
    @Singleton
    fun providePokeApi(): PokeApi {
        return Retrofit
            .Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PokeApi::class.java)
    }
}
