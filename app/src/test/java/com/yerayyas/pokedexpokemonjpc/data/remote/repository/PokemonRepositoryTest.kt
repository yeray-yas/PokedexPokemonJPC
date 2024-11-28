package com.yerayyas.pokedexpokemonjpc.data.remote.repository

import com.yerayyas.pokedexpokemonjpc.data.remote.PokeApi
import com.yerayyas.pokedexpokemonjpc.data.remote.responses.PokemonList
import com.yerayyas.pokedexpokemonjpc.data.remote.responses.Result
import com.yerayyas.pokedexpokemonjpc.util.Resource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class PokemonRepositoryTest {

    private lateinit var repository: PokemonRepository

    @RelaxedMockK
    private lateinit var mockApi: PokeApi

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository =
            PokemonRepository(mockApi)
    }

    @Test
    fun `getPokemonList returns success when API call is successful`() = runBlocking {
        // Given
        val mockResults = listOf(
            Result(
                name = "bulbasaur",
                url = "https://pokeapi.co/api/v2/pokemon/1/"
            ),
            Result(
                name = "ivysaur",
                url = "https://pokeapi.co/api/v2/pokemon/2/"
            )
        )
        val mockResponse = PokemonList(
            count = 2,
            next = "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
            previous = "previous_url",
            results = mockResults
        )

        coEvery { mockApi.getPokemonList(any(), any()) } returns mockResponse

        // When
        val result = repository.getPokemonList(0, 20)

        // Then
        assert(result is Resource.Success)
        assert((result as Resource.Success).data == mockResponse)
        coVerify(exactly = 1) { mockApi.getPokemonList(0, 20) }
    }

    @Test
    fun `getPokemonList returns error when API call fails`() = runBlocking {
        // Given
        coEvery { mockApi.getPokemonList(any(), any()) } throws Exception("Network error")

        // When
        val result = repository.getPokemonList(0, 20)

        // Then
        assert(result is Resource.Error)
        assert((result as Resource.Error).message == "An unknown error has occurred.")
        coVerify(exactly = 1) { mockApi.getPokemonList(0, 20) }
    }

    @Test
    fun `getPokemonList returns correct result for different limits`() = runBlocking {
        // Given
        val mockResults = List(5) { Result(name = "pokemon$it", url = "url$it") }
        val mockResponse = PokemonList(
            count = 20,
            next = "next_url",
            previous = "previous_url",
            results = mockResults
        )

        coEvery { mockApi.getPokemonList(0, 5) } returns mockResponse

        // When
        val result = repository.getPokemonList(0, 5)

        // Then
        assert(result is Resource.Success)
        val successResult = (result as Resource.Success).data
        assert(successResult?.results?.size == 5)
        coVerify(exactly = 1) { mockApi.getPokemonList(0, 5) }
    }

    @Test
    fun `getPokemonList handles unexpected API response format`() = runBlocking {
        // Given
        val invalidResponse = PokemonList(
            count = 20,
            next = "next_url",
            previous = "previous_url",
            results = emptyList()
        )

        coEvery { mockApi.getPokemonList(0, 20) } returns invalidResponse

        // When
        val result = repository.getPokemonList(0, 20)

        //Then
        assert(result is Resource.Success)
        val successResult = (result as Resource.Success).data
        successResult?.results?.isEmpty()?.let { assert(it) }
        coVerify(exactly = 1) { mockApi.getPokemonList(0, 20) }
    }
}
