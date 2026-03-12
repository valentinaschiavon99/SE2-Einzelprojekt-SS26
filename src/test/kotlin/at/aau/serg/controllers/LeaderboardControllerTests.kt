package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.web.server.ResponseStatusException
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock(GameResultService::class.java)
        controller = LeaderboardController(mockedService)
    }

    @Test
    fun `getLeaderboard without rank calls sorted leaderboard`() {
        val results = listOf(GameResult(1, "Player", 100, 10.0))
        whenever(mockedService.getSortedLeaderboard()).thenReturn(results)

        val response = controller.getLeaderboard(null)

        verify(mockedService).getSortedLeaderboard()
        assertEquals(results, response)
    }

    @Test
    fun `getLeaderboard with valid rank calls rank filtering`() {
        val rank = 5
        val filteredResults = listOf(GameResult(1, "Player", 100, 10.0))
        whenever(mockedService.getLeaderboardWithRank(rank)).thenReturn(filteredResults)

        val response = controller.getLeaderboard(rank)

        verify(mockedService).getLeaderboardWithRank(rank)
        assertEquals(filteredResults, response)
    }

    @Test
    fun `getLeaderboard with invalid rank throws 400 Bad Request`() {
        val invalidRank = -1
        // Simulate the service throwing an exception for invalid rank
        whenever(mockedService.getLeaderboardWithRank(invalidRank))
            .thenThrow(IllegalArgumentException("Rank out of bounds"))

        // Task 2.2.2: Verify that the controller maps this to a ResponseStatusException (HTTP 400)
        assertThrows<ResponseStatusException> {
            controller.getLeaderboard(invalidRank)
        }
    }
}